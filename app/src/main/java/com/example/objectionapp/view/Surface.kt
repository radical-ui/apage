package com.example.objectionapp.view

import androidx.compose.foundation.clickable
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.example.objectionapp.Link
import com.example.objectionapp.useNavController
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Surface")
data class SurfaceView(
	val styles: List<Style>? = null,
	val link: Link? = null,
	val intenseColors: Boolean? = null,
	val color: ThemeColor? = null,
	val content: View? = null,
) : View()

@Composable
fun SurfaceViewRender(view: SurfaceView) {
	val navController = useNavController()

	// TODO figure out if we should popUp

	val baseModifier = Modifier.clickable {
		if (view.link != null) view.link.follow(navController, popUp = false)
	}

	val modifier = compileStyles(view.styles, baseModifier)
	val color = view.color ?: ThemeColor.Surface
	val containerColor = color.getContainerColor(view.intenseColors)

	val inner = @Composable {
		CompositionLocalProvider(LocalContentColor.provides(color.getContentColor(view.intenseColors))) {
			view.content?.let { ViewRender(it) }
		}
	}

	view.link?.let { link ->
		Surface(
			modifier = modifier,
			color = containerColor,
			onClick = { link.follow(navController, popUp = false) },
			content = inner
		)
	} ?: Surface(
		modifier = modifier, color = containerColor, content = inner
	)
}
