export * as dtils from 'https://deno.land/x/dtils@2.6.1/mod.ts'

export function getConfigValue(name: string): string {
	const value = prompt(name)
	if (!value) throw new Error(`'${name}' is required`)

	return value
}
