package com.example.objectionapp.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("CenterHorizontally")
data class CenterHorizontalView(
	val styles: List<Style>? = null, val content: View
) : View()

@Composable
fun CenterHorizontalRender(view: CenterHorizontalView) {
	Row(
		compileStyles(view.styles).fillMaxWidth(),
		horizontalArrangement = Arrangement.Center,
	) {
		ViewRender(view.content)
	}
}

@Serializable
@SerialName("CenterVertically")
data class CenterVerticalView(
	val styles: List<Style>? = null, val content: View
) : View()

@Composable
fun CenterVerticalViewRender(view: CenterVerticalView) {
	Column(
		compileStyles(view.styles).fillMaxHeight(),
		verticalArrangement = Arrangement.Center,
	) {
		ViewRender(view.content)
	}
}
