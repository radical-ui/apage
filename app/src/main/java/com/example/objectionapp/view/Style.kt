package com.example.objectionapp.view

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.objectionapp.ContentKey
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("$")
@ContentKey("def")
sealed class Style {
	@Serializable
	@SerialName("Padding")
	data class Padding(val def: Float) : Style()

	@Serializable
	@SerialName("PaddingHorizontal")
	data class PaddingHorizontal(val def: Float) : Style()

	@Serializable
	@SerialName("PaddingVertical")
	data class PaddingVertical(val def: Float) : Style()

	@Serializable
	@SerialName("PaddingTop")
	data class PaddingTop(val def: Float) : Style()

	@Serializable
	@SerialName("PaddingBottom")
	data class PaddingBottom(val def: Float) : Style()

	@Serializable
	@SerialName("PaddingStart")
	data class PaddingStart(val def: Float) : Style()

	@Serializable
	@SerialName("PaddingEnd")
	data class PaddingEnd(val def: Float) : Style()

	@Serializable
	@SerialName("ClipCornerRadius")
	data class ClipCornerRadius(val def: Float) : Style()

	@Serializable
	@SerialName("Width")
	data class Width(val def: Float) : Style()

	@Serializable
	@SerialName("Height")
	data class Height(val def: Float) : Style()

	@Serializable
	@SerialName("Size")
	data class Size(val def: Float) : Style()

	@Serializable
	@SerialName("FillMaxWidth")
	data object FillMaxWidth : Style()

	@Serializable
	@SerialName("FillMaxHeight")
	data object FillMaxHeight : Style()

	@Serializable
	@SerialName("FillMaxSize")
	data object FillMaxSize : Style()

	fun apply(modifier: Modifier): Modifier {
		return when (this) {
			is Padding -> modifier.padding(all = def.dp)
			is PaddingHorizontal -> modifier.padding(horizontal = def.dp)
			is PaddingVertical -> modifier.padding(vertical = def.dp)
			is PaddingTop -> modifier.padding(top = def.dp)
			is PaddingBottom -> modifier.padding(bottom = def.dp)
			is PaddingStart -> modifier.padding(start = def.dp)
			is PaddingEnd -> modifier.padding(end = def.dp)
			is ClipCornerRadius -> modifier.clip(RoundedCornerShape(def.dp))
			is Width -> modifier.width(def.dp)
			is Height -> modifier.height(def.dp)
			is Size -> modifier.size(def.dp)
			is FillMaxWidth -> modifier.fillMaxWidth()
			is FillMaxHeight -> modifier.fillMaxHeight()
			is FillMaxSize -> modifier.fillMaxSize()
		}
	}
}

fun compileStyles(styles: List<Style>?, initialModifier: Modifier = Modifier): Modifier {
	var modifier = initialModifier

	for (style in styles ?: listOf()) {
		modifier = style.apply(modifier)
	}

	return modifier
}
