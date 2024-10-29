package com.example.objectionapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
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

fun compileStyles(styles: List<Style>, initialModifier: Modifier = Modifier): Modifier {
	var modifier = initialModifier

	for (style in styles) {
		modifier = style.apply(modifier)
	}

	return modifier
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("$")
sealed class ThemeColor {
	@Serializable
	@SerialName("Primary")
	data object Primary : ThemeColor()

	@Serializable
	@SerialName("Secondary")
	data object Secondary : ThemeColor()

	@Serializable
	@SerialName("Tertiary")
	data object Tertiary : ThemeColor()

	@Serializable
	@SerialName("Surface")
	data object Surface : ThemeColor()

	@Serializable
	@SerialName("Inverse")
	data object Inverse : ThemeColor()

	@Composable
	fun getContainerColor(): Color {
		return when (this) {
			is Primary -> MaterialTheme.colorScheme.primary
			is Secondary -> MaterialTheme.colorScheme.secondary
			is Tertiary -> MaterialTheme.colorScheme.tertiary
			is Surface -> MaterialTheme.colorScheme.surface
			is Inverse -> MaterialTheme.colorScheme.inverseSurface
		}
	}

	@Composable
	fun getContentColor(): Color {
		return when (this) {
			is Primary -> MaterialTheme.colorScheme.onPrimary
			is Secondary -> MaterialTheme.colorScheme.onSecondary
			is Tertiary -> MaterialTheme.colorScheme.onTertiary
			is Surface -> MaterialTheme.colorScheme.onSurface
			is Inverse -> MaterialTheme.colorScheme.inverseOnSurface
		}
	}
}

@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("$")
@Serializable
sealed class ContentView : View() {
	@Serializable
	@SerialName("Column")
	data class Column(
		val styles: List<Style> = listOf(), val spacing: Int = 0, val items: List<Item> = listOf()
	) : ContentView() {
		@Serializable
		data class Item(
			val expand: Boolean = false,
			val content: ContentView,
		)
	}

	@Serializable
	@SerialName("ScrollingColumn")
	data class ScrollingColumn(
		val styles: List<Style> = listOf(),
		val spacing: Int = 0,
		val items: List<ContentView> = listOf()
	) : ContentView()

	@Serializable
	@SerialName("Row")
	data class Row(
		val styles: List<Style> = listOf(), val spacing: Int = 10, val items: List<Item> = listOf()
	) : ContentView() {
		@Serializable
		data class Item(val expand: Boolean = false, val content: ContentView)
	}

	@Serializable
	@SerialName("ScrollingRow")
	data class ScrollingRow(
		val styles: List<Style> = listOf(),
		val spacing: Int = 0,
		val items: List<ContentView> = listOf()
	) : ContentView()

	@Serializable
	@SerialName("Box")
	data class Box(
		val color: ThemeColor? = null,
		val styles: List<Style> = listOf(),
		val items: List<ContentView> = listOf()
	) : ContentView()

	@Serializable
	@SerialName("Button")
	data class Button(
		val link: Link,
		val styles: List<Style> = listOf(),
		val color: ThemeColor,
		val content: ContentView,
	) : ContentView()

	@Serializable
	@SerialName("Paragraph")
	data class Text(
		val text: String,
		val size: Int? = null,
		val weight: Weight? = null,
	) : ContentView() {
		@Serializable
		@JsonClassDiscriminator("$")
		sealed class Weight {
			@Serializable
			@SerialName("Light")
			data object Light : Weight()

			@Serializable
			@SerialName("Normal")
			data object Normal : Weight()

			@Serializable
			@SerialName("SemiBold")
			data object SemiBold : Weight()

			@Serializable
			@SerialName("Bold")
			data object Bold : Weight()

			fun toWeight(): FontWeight {
				return when (this) {
					is Light -> FontWeight.Light
					is Normal -> FontWeight.Normal
					is SemiBold -> FontWeight.SemiBold
					is Bold -> FontWeight.Bold
				}
			}
		}
	}

	@Serializable
	@SerialName("Center")
	data class Center(
		val styles: List<Style>, val content: ContentView
	) : ContentView()

	@Serializable
	@SerialName("Card")
	data class Card(
		val styles: List<Style>,
		val link: Link? = null,
		val color: ThemeColor,
		val content: ContentView,
	) : ContentView()

	@Serializable
	@SerialName("Image")
	data class Image(
		val styles: List<Style> = listOf(), val url: String, val description: String
	) : ContentView()
}

@Composable
fun ContentViewRender(view: ContentView, scrollConnection: NestedScrollConnection?) {
	when (view) {
		is ContentView.Row -> {
			Row(
				modifier = compileStyles(view.styles),
				horizontalArrangement = Arrangement.spacedBy(view.spacing.dp),
			) {
				for (child in view.items) {
					Box(if (child.expand) Modifier.weight(1F) else Modifier) {
						ContentViewRender(child.content, scrollConnection)
					}
				}
			}
		}

		is ContentView.ScrollingRow -> {
			LazyRow(
				modifier = compileStyles(view.styles),
				horizontalArrangement = Arrangement.spacedBy(view.spacing.dp),
			) {
				for (child in view.items) {
					item {
						ContentViewRender(child, scrollConnection)
					}
				}
			}
		}

		is ContentView.Column -> {
			Column(
				verticalArrangement = Arrangement.spacedBy(view.spacing.dp),
				modifier = compileStyles(view.styles)
			) {
				for (item in view.items) {
					Box(if (item.expand) Modifier.weight(1F) else Modifier) {
						ContentViewRender(item.content, scrollConnection)
					}
				}
			}
		}

		is ContentView.ScrollingColumn -> {
			LazyColumn(verticalArrangement = Arrangement.spacedBy(view.spacing.dp),
				modifier = compileStyles(view.styles,
					initialModifier = scrollConnection?.let { Modifier.nestedScroll(it) }
						?: Modifier)) {
				for (child in view.items) {
					item {
						ContentViewRender(child, null)
					}
				}
			}
		}

		is ContentView.Box -> {
			val modifier =
				if (view.color != null) Modifier.background(view.color.getContentColor()) else Modifier

			Box(modifier = compileStyles(view.styles, modifier)) {
				if (view.color != null) {
					CompositionLocalProvider(LocalContentColor provides view.color.getContentColor()) {
						for (child in view.items) {
							ContentViewRender(child, scrollConnection)
						}
					}
				} else {
					for (child in view.items) {
						ContentViewRender(child, scrollConnection)
					}
				}
			}
		}

		is ContentView.Button -> {
			val navController = useNavController()

			Button(modifier = compileStyles(view.styles),
				onClick = { view.link.follow(navController) },
				colors = ButtonColors(
					contentColor = view.color.getContentColor(),
					containerColor = view.color.getContainerColor(),
					disabledContentColor = view.color.getContentColor(),
					disabledContainerColor = view.color.getContainerColor()
				),
				content = {
					ContentViewRender(view.content, scrollConnection)
				})
		}

		is ContentView.Text -> {
			Text(
				view.text,
				color = LocalContentColor.current,
				fontWeight = (view.weight ?: ContentView.Text.Weight.Normal).toWeight(),
				fontSize = (view.size ?: 16).sp
			)
		}

		is ContentView.Center -> {
			Row(
				Modifier.fillMaxSize(),
				horizontalArrangement = Arrangement.Center,
				verticalAlignment = Alignment.CenterVertically
			) {
				ContentViewRender(view.content, scrollConnection)
			}
		}

		is ContentView.Card -> {
			val navController = useNavController()

			Card(
				modifier = compileStyles(view.styles),
				elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp),
				colors = CardColors(
					containerColor = view.color.getContainerColor(),
					contentColor = view.color.getContentColor(),
					disabledContentColor = view.color.getContentColor(),
					disabledContainerColor = view.color.getContainerColor()
				),
				onClick = {
					if (view.link != null) view.link.follow(navController)
				},
			) {
				ContentViewRender(view.content, scrollConnection)
			}
		}

		is ContentView.Image -> {
			AsyncImage(
				view.url,
				contentDescription = view.description,
				modifier = compileStyles(view.styles)
			)
		}
	}
}

@Preview
@Composable
fun ContentPreview() {
	val controller = Controller.fromConstants()

	controller.objectStore.preload(defaultThemeId, Theme())
	controller.objectStore.preload(defaultLayoutId, Layout(currentPageId = "page"))

	controller.objectStore.preload(
		"page", Page(
			title = "Hello There", type = PageType.Plain(), view = ContentView.ScrollingColumn(
				spacing = 2, items = listOf(
//					ContentView.Text(text = "Hello world!"), ContentView.Center(
//						ContentView.Button(
//							text = "Click me", icon = "Search", link = Link(pageId = "link")
//						)
//					), ContentView.Center(
//						ContentView.Button(
//							text = "Prime me",
//							icon = "Search",
//							primary = true,
//							large = true,
//							link = Link(pageId = "link", useSheet = true)
//						)
//					), ContentView.Row(
//						items = listOf(
//							ContentView.Center(ContentView.Paragraph("Hello there")),
//							ContentView.Center(ContentView.Paragraph("inner"))
//						)
//					), ContentView.Row(
//						items = listOf(
//							ContentView.Button(
//								text = "Foo", link = Link(pageId = "link"), stretch = true
//							),
//							ContentView.Button(
//								text = "Bar", link = Link(pageId = "link"), stretch = true
//							),
//						)
//					), ContentView.CustomCard(
//						isPrimary = true,
//						title = "This is a title",
//						subtitle = "Foo * Bar * Bin * Baz",
//						imageUrl = "https://picsum.photos/400",
//						footerIcon = "Lightbulb",
//						footerText = "Hello there",
//						options = CardViewOptions(useCardPadding = true, excessiveRounding = true),
//						spacer = 30
//					)
				)
			)
		)
	)

	controller.objectStore.preload(
		"link", Page(title = "Link", type = PageType.Plain())
	)

	TestProvider(controller)
}
