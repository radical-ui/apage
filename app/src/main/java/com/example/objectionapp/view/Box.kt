package com.example.objectionapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import com.example.objectionapp.view.Style
import com.example.objectionapp.view.ThemeColor
import com.example.objectionapp.view.compileStyles
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Box")
data class BoxView(
	val color: ThemeColor? = null,
	val intenseColor: Boolean? = null,
	val styles: List<Style>? = null,
	val content: View? = null,
) : View()

@Composable
fun BoxViewRender(view: BoxView) {
	val modifier =
		if (view.color != null) Modifier.background(view.color.getContainerColor(view.intenseColor)) else Modifier

	Box(modifier = compileStyles(view.styles, modifier)) {
		if (view.color != null) {
			CompositionLocalProvider(LocalContentColor provides view.color.getContentColor(view.intenseColor)) {
				view.content?.let { ViewRender(it) }
			}
		} else {
			view.content?.let { ViewRender(it) }
		}
	}
}