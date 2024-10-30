package com.example.objectionapp.view

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Stack")
data class StackView(
	val styles: List<Style>? = null,
	val items: List<View>? = null,
) : View()

@Composable
fun StackViewRender(view: StackView) {
	Box(modifier = compileStyles(view.styles)) {
		for (child in view.items ?: listOf()) {
			ViewRender(child)
		}
	}
}
