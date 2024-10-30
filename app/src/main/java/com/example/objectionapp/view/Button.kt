package com.example.objectionapp.view

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.runtime.Composable
import com.example.objectionapp.Link
import com.example.objectionapp.useNavController
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Button")
data class ButtonView(
	val link: Link,
	val content: View? = null,
	val styles: List<Style>? = null,
	val color: ThemeColor? = null,
	val intenseColor: Boolean? = false,
) : View()

@Composable
fun ButtonViewRender(view: ButtonView) {
	val navController = useNavController()
	val color = view.color ?: ThemeColor.Surface

	Button(modifier = compileStyles(view.styles),
		// TODO correct popup so that it is correct
		onClick = { view.link.follow(navController, false) },
		colors = ButtonColors(
			contentColor = color.getContentColor(view.intenseColor),
			containerColor = color.getContainerColor(view.intenseColor),
			disabledContentColor = color.getContentColor(view.intenseColor),
			disabledContainerColor = color.getContainerColor(view.intenseColor)
		),
		content = {
			view.content?.let { ViewRender(it) }
		})
}