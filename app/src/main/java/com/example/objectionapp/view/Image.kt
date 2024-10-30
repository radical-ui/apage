package com.example.objectionapp.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@Serializable
@SerialName("Image")
data class ImageView(
	val styles: List<Style>? = null,
	val url: String,
	val description: String,
	val contentScale: ImageViewContentScale? = null
) : View()

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("$")
sealed class ImageViewContentScale {
	@Serializable
	@SerialName("Fit")
	data object Fit : ImageViewContentScale()

	@Serializable
	@SerialName("Crop")
	data object Crop : ImageViewContentScale()

	@Serializable
	@SerialName("Inside")
	data object Inside : ImageViewContentScale()

	@Serializable
	@SerialName("FillWidth")
	data object FillWidth : ImageViewContentScale()

	@Serializable
	@SerialName("FillBounds")
	data object FillBounds : ImageViewContentScale()

	@Serializable
	@SerialName("FillHeight")
	data object FillHeight : ImageViewContentScale()

	fun toContentScale(): ContentScale {
		return when (this) {
			is Fit -> ContentScale.Fit
			is Crop -> ContentScale.Crop
			is Inside -> ContentScale.Inside
			is FillWidth -> ContentScale.FillWidth
			is FillBounds -> ContentScale.FillBounds
			is FillHeight -> ContentScale.FillHeight
		}
	}
}

@Composable
fun ImageViewRender(view: ImageView) {
	AsyncImage(
		view.url,
		contentScale = view.contentScale?.toContentScale() ?: ContentScale.Fit,
		contentDescription = view.description,
		modifier = compileStyles(view.styles)
	)
}
