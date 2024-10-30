package com.example.objectionapp.view

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("$")
sealed class TextViewWeight {
	@Serializable
	data object Light : TextViewWeight()

	@Serializable
	data object Normal : TextViewWeight()

	@Serializable
	data object SemiBold : TextViewWeight()

	@Serializable
	data object Bold : TextViewWeight()

	fun toWeight(): FontWeight {
		return when (this) {
			is Light -> FontWeight.Light
			is Normal -> FontWeight.Normal
			is SemiBold -> FontWeight.SemiBold
			is Bold -> FontWeight.Bold
		}
	}
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("$")
sealed class TextFont {
	@Serializable
	data object SansSerif : TextFont()

	@Serializable
	data object Serif : TextFont()

	@Serializable
	data object Monospace : TextFont()

	fun toFontFamily(): FontFamily {
		return when (this) {
			is SansSerif -> FontFamily.SansSerif
			is Serif -> FontFamily.Serif
			is Monospace -> FontFamily.Monospace
		}
	}
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("$")
sealed class TextViewAlign {
	@Serializable
	@SerialName("Start")
	data object Start : TextViewAlign()

	@Serializable
	@SerialName("End")
	data object End : TextViewAlign()

	@Serializable
	@SerialName("Center")
	data object Center : TextViewAlign()

	@Serializable
	@SerialName("Justify")
	data object Justify : TextViewAlign()

	fun toTextAlign(): TextAlign {
		return when (this) {
			is Start -> TextAlign.Start
			is End -> TextAlign.End
			is Center -> TextAlign.Center
			is Justify -> TextAlign.Justify
		}
	}
}

@Serializable
@SerialName("Text")
data class TextView(
	val text: String,
	val size: Float? = null,
	val weight: TextViewWeight? = null,
	val font: TextFont? = null,
	val align: TextViewAlign? = null,
	val color: ThemeColor? = null,
) : View()

@Composable
fun TextViewRender(view: TextView) {
	Text(
		view.text,
		color = view.color?.getContainerColor(intense = false) ?: LocalContentColor.current,
		fontWeight = (view.weight ?: TextViewWeight.Normal).toWeight(),
		fontSize = (view.size ?: 16F).toInt().sp,
		fontFamily = (view.font ?: TextFont.SansSerif).toFontFamily(),
		textAlign = view.align?.toTextAlign()
	)
}
