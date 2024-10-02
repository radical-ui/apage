package com.example.objectionapp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.QuestionMark
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import java.lang.System.console

@Composable
fun StandardIcon(
    name: String,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    val logger = useController().logger.scope("StandardIcon")
    val theme = useDefaultTheme()
    val icon: ImageVector? = remember(name) {
        try {
            val cl =
                Class.forName("androidx.compose.material.icons.${theme.iconPack.getJavaName()}.${name}Kt")

            val method = cl.declaredMethods.first()
            method.invoke(null, theme.iconPack.getIcons()) as ImageVector
        } catch (err: Throwable) {
            logger.error("failed to load icon: $err")
            null
        }
    }

    if (icon == null) Icon(Icons.Rounded.QuestionMark, "no icon", tint = tint, modifier = modifier)
    else Icon(icon, "$name icon", tint = tint, modifier = modifier)
}
