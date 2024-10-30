package com.example.objectionapp.view

import com.example.objectionapp.useDefaultTheme
import com.example.objectionapp.useLogger
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.QuestionMark
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector

@Serializable
@SerialName("Icon")
data class IconView(
	val styles: List<Style>? = null, val name: String
) : View()

@Composable
fun IconViewRender(view: IconView) {
	val logger = useLogger("StandardIcon")
	val theme = useDefaultTheme()
	val icon: ImageVector? = remember(view.name) {
		try {
			val cl =
				Class.forName("androidx.compose.material.icons.${theme.iconPack.getJavaName()}.${view.name}Kt")

			val method = cl.declaredMethods.first()
			method.invoke(null, theme.iconPack.getIcons()) as ImageVector
		} catch (err: Throwable) {
			logger.error("failed to load icon: $err")
			null
		}
	}

	val modifier = compileStyles(view.styles)

	if (icon == null) Icon(Icons.Rounded.QuestionMark, "no icon", modifier = modifier)
	else Icon(icon, "${icon.name} icon", modifier = modifier)
}
