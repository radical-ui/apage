package com.example.objectionapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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

@Serializable
@SerialName("ContentView")
data class ContentView(
	val spacing: Int = 0, val items: List<Content> = listOf()
) : View()

@Composable
fun ContentViewRender(view: ContentView) {
	val arrangement = Arrangement.spacedBy(view.spacing.dp)
	val modifier = Modifier
		.padding(horizontal = 16.dp)
		.fillMaxWidth()

	if (view.items.size > 3) {
		LazyColumn(verticalArrangement = arrangement, modifier = modifier) {
			for (content in view.items) {
				item { ContentRender(content) }
			}
		}
	} else {
		Column(
			verticalArrangement = arrangement,
			modifier = modifier.verticalScroll(state = rememberScrollState())
		) {
			for (content in view.items) {
				ContentRender(content)
			}
		}
	}
}

@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("$")
@Serializable
sealed class Content {
	@Serializable
	@SerialName("Button")
	data class Button(
		val text: String,
		val link: Link,
		val color: ThemeColor,
		val large: Boolean = false,
		val icon: String? = null,
		val stretch: Boolean = false
	) : Content()

	@Serializable
	@SerialName("Paragraph")
	data class Paragraph(
		val text: String
	) : Content()

	@Serializable
	@SerialName("Center")
	data class Center(
		val content: Content
	) : Content()

	@Serializable
	@SerialName("Gap")
	data class Gap(
		val size: Int = 10
	) : Content()

	@Serializable
	@SerialName("Row")
	data class Row(
		val spacing: Int = 10, val items: List<Item> = listOf()
	) : Content() {
		@Serializable
		data class Item(val expand: Boolean = false, val content: Content)
	}

	@Serializable
	@SerialName("Card")
	data class Card(
		val link: Link? = null,
		val color: ThemeColor,
//		val imageUrl: String? = null,
//		val title: String? = null,
//		val subtitle: String? = null,
//		val footerIcon: String? = null,
//		val footerText: String? = null,
//		val isPrimary: Boolean = false,
//		val options: CardViewOptions = CardViewOptions(),
//		val spacer: Int = 10,
	) : Content()

	@Serializable
	@SerialName("Image")
	data class Image(
		val url: String
	) : Content()
}

@Composable
private fun ContentRender(content: Content) {
	when (content) {
		is Content.Button -> {
			val navController = useNavController()
			val modifier = if (content.stretch) Modifier.fillMaxWidth() else Modifier

			Button(modifier = modifier,
				onClick = { content.link.follow(navController) },
				colors = ButtonColors(
					contentColor = if (content.primary) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground,
					containerColor = if (content.primary) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer,
					disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
					disabledContainerColor = MaterialTheme.colorScheme.onSurface
				),
				content = {
					Row(horizontalArrangement = Arrangement.spacedBy(if (content.large) 10.dp else 5.dp)) {
						content.icon?.let {
							StandardIcon(it, Modifier.size(if (content.large) 30.dp else 22.dp))
						}

						Text(text = content.text, fontSize = if (content.large) 24.sp else 16.sp)
					}
				})
		}

		is Content.Paragraph -> {
			Text(content.text, color = MaterialTheme.colorScheme.onBackground)
		}

		is Content.Center -> {
			Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
				ContentRender(content.content)
			}
		}

		is Content.Gap -> {
			Box(
				Modifier
					.fillMaxWidth()
					.height(content.size.dp)
			)
		}

		is Content.Row -> {
			Row(
				Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(content.spacing.dp),
			) {
				for (child in content.items) {
					Box(if (child.expand) Modifier.weight(1F) else Modifier) {
						ContentRender(child.content)
					}
				}
			}
		}

		is Content.CustomCard -> {
			val navController = useNavController()

			Card(
				modifier = if (content.options.excessiveRounding) Modifier.clip(
					RoundedCornerShape(
						20.dp
					)
				) else Modifier,
				elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp),
				colors = CardColors(
					containerColor = if (content.isPrimary) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer,
					contentColor = if (content.isPrimary) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
					disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
					disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
				),
				onClick = {
					if (content.link != null) content.link.follow(navController)
				},
			) {
				Column(verticalArrangement = Arrangement.spacedBy(content.spacer.dp)) {
					content.imageUrl?.let {
						val modifier =
							if (content.options.useCardPadding) Modifier.padding(content.spacer.dp) else Modifier

						AsyncImage(
							model = it,
							contentDescription = "An image",
							clipToBounds = true,
							contentScale = ContentScale.Crop,
							modifier = modifier
								.fillMaxWidth()
								.height(200.dp)
								.clip(RoundedCornerShape(12.dp))
						)
					}

					Column(
						modifier = if (content.options.useCardPadding) Modifier.padding(horizontal = content.spacer.dp) else Modifier,
						verticalArrangement = Arrangement.spacedBy(content.spacer.dp)
					) {
						content.title?.let {
							Text(it, fontSize = 30.sp)
						}

						content.subtitle?.let {
							Text(it)
						}
					}

					content.footerText?.let { text ->
						Row(
							Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.tertiary)
								.padding(content.spacer.dp)
						) {
							val color = MaterialTheme.colorScheme.onTertiary

							content.footerIcon?.let {
								StandardIcon(it, tint = color)
							}

							Text(text, color = color)
						}
					}
				}
			}
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
			title = "Hello There", type = PageType.Plain(), view = ContentView(
				spacing = 2, items = listOf(
					Content.Paragraph("Hello world!"), Content.Gap(), Content.Center(
						Content.Button(
							text = "Click me", icon = "Search", link = Link(pageId = "link")
						)
					), Content.Center(
						Content.Button(
							text = "Prime me",
							icon = "Search",
							primary = true,
							large = true,
							link = Link(pageId = "link", useSheet = true)
						)
					), Content.Row(
						items = listOf(
							Content.Center(Content.Paragraph("Hello there")),
							Content.Center(Content.Paragraph("inner"))
						)
					), Content.Row(
						items = listOf(
							Content.Button(
								text = "Foo", link = Link(pageId = "link"), stretch = true
							),
							Content.Button(
								text = "Bar", link = Link(pageId = "link"), stretch = true
							),
						)
					), Content.CustomCard(
						isPrimary = true,
						title = "This is a title",
						subtitle = "Foo * Bar * Bin * Baz",
						imageUrl = "https://picsum.photos/400",
						footerIcon = "Lightbulb",
						footerText = "Hello there",
						options = CardViewOptions(useCardPadding = true, excessiveRounding = true),
						spacer = 30
					)
				)
			)
		)
	)

	controller.objectStore.preload(
		"link", Page(title = "Link", type = PageType.Plain())
	)

	TestProvider(controller)
}
