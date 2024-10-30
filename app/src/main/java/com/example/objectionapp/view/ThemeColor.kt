package com.example.objectionapp.view

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.objectionapp.Description
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("$")
sealed class ThemeColor {
	@Description("Does not support color intensity")
	@Serializable
	@SerialName("Background")
	data object Background : ThemeColor()

	@Serializable
	@SerialName("Primary")
	data object Primary : ThemeColor()

	@Serializable
	@SerialName("Secondary")
	data object Secondary : ThemeColor()

	@Serializable
	@SerialName("Tertiary")
	data object Tertiary : ThemeColor()

	@Serializable
	@SerialName("Surface")
	data object Surface : ThemeColor()

	@Description("Does not support color intensity")
	@Serializable
	@SerialName("Inverse")
	data object Inverse : ThemeColor()

	@Composable
	fun getContainerColor(intense: Boolean?): Color {
		return when (this) {
			is Background -> MaterialTheme.colorScheme.background
			is Primary -> if (intense == true) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primary
			is Secondary -> if (intense == true) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.secondary
			is Tertiary -> if (intense == true) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.tertiary
			is Surface -> if (intense == true) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.surface
			is Inverse -> MaterialTheme.colorScheme.inverseSurface
		}
	}

	@Composable
	fun getContentColor(intense: Boolean?): Color {
		return when (this) {
			is Background -> MaterialTheme.colorScheme.onBackground
			is Primary -> if (intense == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary
			is Secondary -> if (intense == true) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSecondary
			is Tertiary -> if (intense == true) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onTertiary
			is Surface -> if (intense == true) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface
			is Inverse -> MaterialTheme.colorScheme.inverseOnSurface
		}
	}
}