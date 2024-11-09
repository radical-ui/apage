import { dtils, getConfigValue } from './deps.ts'

class Color {
	red: number
	green: number
	blue: number

	constructor(red: number, green: number, blue: number) {
		this.red = red
		this.green = green
		this.blue = blue
	}

	static from(text: string) {
		// reference: https://stackoverflow.com/a/5624139
		// Expand shorthand form (e.g. "03F") to full form (e.g. "0033FF")

		const shorthandRegex = /^#?([a-f\d])([a-f\d])([a-f\d])$/i
		text = text.trim()
		text = text.replace(shorthandRegex, function (_, r, g, b) {
			return r + r + g + g + b + b
		})

		const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(text)
		if (!result) throw new Error(`failed to parse hex color: ${text}`)

		return new Color(
			parseInt(result[1], 16),
			parseInt(result[2], 16),
			parseInt(result[3], 16),
		)
	}

	toHex() {
		return '#' + (1 << 24 | this.red << 16 | this.green << 8 | this.blue).toString(16).slice(1)
	}

	toKotlin() {
		return `Color(red = ${this.red}, green = ${this.green}, blue = ${this.blue})`
	}
}

console.log(`
	NOTE: Currently this script does not change the app id (com.example.objectionapp).
	This is somewhat difficult because, in addition to the two necessary substitutions
	app/build.gradle.kt, all the app directories need to be renamed to match.
`)

const icon = getConfigValue('icon')
const backendUrl = getConfigValue('backend_url')
const appName = getConfigValue('app_name')
const noInternetHeader = getConfigValue('no_internet_header')
const noInternetContent = getConfigValue('no_internet_content')
const errorHeader = getConfigValue('error_header')
const lightBackgroundColor = Color.from(getConfigValue('light_background_color'))
const lightForegroundColor = Color.from(getConfigValue('light_foreground_color'))
const darkBackgroundColor = Color.from(getConfigValue('dark_background_color'))
const darkForegroundColor = Color.from(getConfigValue('dark_foreground_color'))
const lightLoaderColor = Color.from(getConfigValue('light_loader_color'))
const darkLoaderColor = Color.from(getConfigValue('dark_loader_color'))

await replaceLine('app/src/main/java/com/example/objectionapp/Constants.kt', [
	{ match: 'const val appName', write: `const val appName = "${appName}"` },
	{ match: 'const val wsUrl', write: `const val wsUrl = "${backendUrl}"` },
	{ match: 'const val noInternetHeader', write: `const val noInternetHeader = "${noInternetHeader}"` },
	{ match: 'const val noInternetContent', write: `const val noInternetContent = "${noInternetContent}"` },
	{ match: 'const val errorHeader', write: `const val errorHeader = "${errorHeader}"` },
	{ match: 'val lightBackgroundColor', write: `val lightBackgroundColor = ${lightBackgroundColor.toKotlin()}` },
	{ match: 'val lightForegroundColor', write: `val lightForegroundColor = ${lightForegroundColor.toKotlin()}` },
	{ match: 'val darkBackgroundColor', write: `val darkBackgroundColor = ${darkBackgroundColor.toKotlin()}` },
	{ match: 'val darkForegroundColor', write: `val darkForegroundColor = ${darkForegroundColor.toKotlin()}` },
	{ match: 'val lightLoaderColor', write: `val lightLoaderColor = ${lightLoaderColor.toKotlin()}` },
	{ match: 'val darkLoaderColor', write: `val darkLoaderColor = ${darkLoaderColor.toKotlin()}` },
])
await replaceLine('app/src/main/res/values/colors.xml', [
	{ match: '<color name="foreground"', write: `<color name="foreground">${lightForegroundColor.toHex()}</color>` },
	{ match: '<color name="background"', write: `<color name="background">${lightBackgroundColor.toHex()}</color>` },
])
await replaceLine('app/src/main/res/values-night/colors.xml', [
	{ match: '<color name="foreground"', write: `<color name="foreground">${darkForegroundColor.toHex()}</color>` },
	{ match: '<color name="background"', write: `<color name="background">${darkBackgroundColor.toHex()}</color>` },
])
await replaceLine('app/src/main/res/values/strings.xml', [
	{ match: '<string name="app_name"', write: `<string name="app_name">${appName}</string>` },
])
await replaceLine('settings.gradle.kts', [
	{ match: 'rootProject.name', write: `rootProject.name = "${appName}"` },
])

await writeIcon(108, 'mipmap-mdpi')
await writeIcon(162, 'mipmap-hdpi')
await writeIcon(216, 'mipmap-xhdpi')
await writeIcon(324, 'mipmap-xxhdpi')
await writeIcon(432, 'mipmap-xxxhdpi')

await dtils.exec(['./gradlew', 'assembleDebug'])

console.log('APK is located at: app/build/outputs/apk/debug/app-debug.apk')

async function writeIcon(height: number, space: string) {
	console.log(`Writing icon to space ${space} with height=${height}`)

	const destFile = `app/src/main/res/${space}/ic_launcher_foreground.png`
	try {
		await Deno.remove(destFile)
	} catch (_) {
		// it's ok if it doesn't exist
	}

	await dtils.exec(['ffmpeg', '-i', icon, '-vf', `scale=-1:${height}`, destFile])
}

interface Replacement {
	match: string
	write: string
}

async function replaceLine(file: string, replacements: Replacement[]) {
	const text = await dtils.readText(file)

	const newText = text.split('\n').map((line) => {
		for (const replacement of replacements) {
			if (line.includes(replacement.match)) {
				return replacement.write
			}
		}

		return line
	}).join('\n')

	await dtils.writeText(file, newText)
}
