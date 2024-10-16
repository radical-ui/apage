package com.example.objectionapp

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@Serializable
data class Theme(
	@Description("Unless otherwise specified, the supplied light and dark color schemes will be overridden with the android-supplied color scheme, which is computed from the home screen background.")
	val disableDynamicTheme: Boolean = false,

	@Description("The color scheme that will be applied when the system is in light mode, when the dynamic theme is disabled")
	val lightColorScheme: ColorScheme = ColorScheme(),

	@Description("The color scheme that will be applied when the system is in dark mode, when the dynamic theme is disabled")
	val darkColorScheme: ColorScheme = ColorScheme(),

	@Description("The type of icons that will be displayed in other parts of the app")
	val iconPack: IconPack,
)

@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("$")
@Serializable
sealed class IconPack {
	@Serializable
	data object Rounded : IconPack()

	@Serializable
	data object Sharp : IconPack()

	@Serializable
	data object Filled : IconPack()

	fun getJavaName(): String {
		return when (this) {
			is Filled -> "filled"
			is Sharp -> "sharp"
			is Rounded -> "rounded"
		}
	}

	fun getIcons(): Any {
		return when (this) {
			is Filled -> Icons.Filled
			is Sharp -> Icons.Sharp
			is Rounded -> Icons.Rounded
		}
	}
}

@Serializable
data class SurfaceTheme(
	@SerialName("background_color_1") val backgroundColor1: ColorData,
	@SerialName("background_color_2") val backgroundColor2: ColorData,
	@SerialName("background_color_3") val backgroundColor3: ColorData,
	@SerialName("background_color_4") val backgroundColor4: ColorData,

	@SerialName("foreground_color_1") val foregroundColor1: ColorData,
	@SerialName("foreground_color_2") val foregroundColor2: ColorData,
	@SerialName("foreground_color_3") val foregroundColor3: ColorData,
	@SerialName("foreground_color_4") val foregroundColor4: ColorData,

	@SerialName("primary_color_1") val primaryColor1: ColorData,
	@SerialName("primary_color_2") val primaryColor2: ColorData,
	@SerialName("primary_color_3") val primaryColor3: ColorData,
	@SerialName("primary_color_4") val primaryColor4: ColorData,

	@SerialName("glow_color") val glowColor: ColorData?
)

@Composable
fun RenderTheme(content: @Composable () -> Unit) {
	val theme = useDefaultTheme()
	val isDarkTheme = isSystemInDarkTheme()
	val supportsDynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
	val shouldDoDynamicTheme = theme.disableDynamicTheme

	val colorScheme = when {
		supportsDynamicColor && isDarkTheme && shouldDoDynamicTheme -> dynamicDarkColorScheme(
			LocalContext.current
		)

		supportsDynamicColor && !isDarkTheme && shouldDoDynamicTheme -> dynamicLightColorScheme(
			LocalContext.current
		)

		isDarkTheme -> fillDarkDefaults(theme.darkColorScheme)
		else -> fillLightDefaults(theme.lightColorScheme)
	}

	val typography = Typography(
		displaySmall = TextStyle(
			fontWeight = FontWeight.W100,
			fontSize = 96.sp
		),
		labelLarge = TextStyle(fontWeight = FontWeight.W600, fontSize = 14.sp)
	)

	val shapes = Shapes(extraSmall = RoundedCornerShape(3.0.dp), small = RoundedCornerShape(6.0.dp))

	MaterialTheme(
		colorScheme = colorScheme,
		typography = typography,
		shapes = shapes,
		content = content
	)
}
