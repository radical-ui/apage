package com.example.objectionapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CardViewOptions(
	val excessiveRounding: Boolean = false,
	val useCardPadding: Boolean = false,
)

@Serializable
@SerialName("CardView")
data class CardView(
	val options: CardViewOptions,
	val containers: List<CardContainer> = listOf(),
) : View()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardViewRender(view: CardView, scrollBehavior: TopAppBarScrollBehavior) {
	LazyColumn(
		modifier = Modifier
			.padding(bottom = 90.dp)
			.nestedScroll(scrollBehavior.nestedScrollConnection),
		verticalArrangement = Arrangement.spacedBy(10.dp),
	) {

		view.containers.map {
			item {
				when (it) {
					is CardContainer.SingularCardContainer -> CardRender(
						it.objectId,
						isVertical = false,
						viewOptions = view.options,
					)

					is CardContainer.PluralCardContainer -> {
						Box(
							modifier = Modifier
								.background(color = MaterialTheme.colorScheme.surfaceVariant)
								.fillMaxWidth()
						) {
							LazyRow(
								modifier = Modifier
									.nestedScroll(scrollBehavior.nestedScrollConnection)
									.fillMaxWidth()
							) {
								item {
									it.objectIds.map {
										CardRender(
											it, isVertical = true, viewOptions = view.options
										)
									}
								}
							}
						}
					}

					is CardContainer.CustomCardContainer -> return@item
				}
			}
		}
	}
}

@Composable
private fun CardRender(
	pageId: String,
	isVertical: Boolean,
	viewOptions: CardViewOptions,
) {
	val page = usePage(pageId)
	val navController = useNavController()

	var rootModifier: Modifier = Modifier
	var bodyModifier: Modifier = Modifier

	rootModifier = if (isVertical) rootModifier.width(140.dp) else rootModifier.fillMaxWidth()
	rootModifier =
		if (viewOptions.excessiveRounding) rootModifier.clip(RoundedCornerShape(20.dp)) else rootModifier
	rootModifier = if (viewOptions.useCardPadding) rootModifier.padding(10.dp) else rootModifier

	bodyModifier = if (viewOptions.useCardPadding) bodyModifier else bodyModifier.padding(10.dp)

	Card(
		modifier = rootModifier,
		elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp),
		colors = CardColors(
			containerColor = MaterialTheme.colorScheme.surfaceContainer,
			contentColor = MaterialTheme.colorScheme.onSurface,
			disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
			disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
		),
		onClick = {
			navController.navigate(route = encodeObjectIdIntoPageRoute(pageId))
		},
	) {
		if (page == null) {
			// TODO: skeleton loader here
		} else {
			Row(
				modifier = bodyModifier
					.fillMaxWidth()
					.fillMaxHeight()
					.padding(10.dp),
				horizontalArrangement = Arrangement.spacedBy(10.dp),
				verticalAlignment = Alignment.Top
			) {
				if (page.type is PageType.Post) {
					page.type.imageUrls?.getOrNull(0)?.let {
						AsyncImage(
							model = it,
							contentDescription = "An image",
							clipToBounds = true,
							contentScale = ContentScale.Crop,
							modifier = Modifier
								.width(110.dp)
								.height(110.dp)
								.clip(RoundedCornerShape(12.dp))
						)
					}
				}

				Column(
					verticalArrangement = Arrangement.SpaceAround
				) {
					if (page.type is PageType.Post) {
						page.type.supertitle?.let {
							Text(
								it,
								color = MaterialTheme.colorScheme.onSurface,
								fontSize = 10.sp,
								fontWeight = FontWeight.SemiBold,
							)
						}
					}

					page.title?.let {
						Text(
							it,
							color = MaterialTheme.colorScheme.onSurface,
							fontSize = 16.sp,
						)
					}

					page.subtitle?.let {
						Text(
							it, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary
						)
					}

					if (page.type is PageType.Post) {
						page.type.additionalInfo?.let {
							Text(
								it,
								fontSize = 14.sp,
								color = MaterialTheme.colorScheme.onSurface,
								fontWeight = FontWeight.SemiBold
							)
						}
					}
				}
			}
		}
	}
}

@Preview
@Composable
fun CardViewTest() {
	val controller = Controller.fromConstants()

	controller.objectStore.preload(defaultThemeId, Theme())
	controller.objectStore.preload(defaultLayoutId, Layout(currentPageId = "cards"))

	controller.objectStore.preload(
		"cards", Page(
			title = "Products",
			type = PageType.Plain("ShoppingBasket"),
			subtitle = "Hello darkness my old friend, I've come to talk with you again",
			view = CardView(
				options = CardViewOptions(excessiveRounding = true, useCardPadding = true),
				mutableListOf(
					CardContainer.SingularCardContainer("Hammer_Product"),
					CardContainer.SingularCardContainer("Hammer_Product"),
					CardContainer.SingularCardContainer("Hammer_Product"),
				)
			)
		)

	)

	controller.objectStore.preload(
		"Hammer_Product", Page(
			title = "Premium Hammer", subtitle = "Hammers Inc.", type = PageType.Post(
				supertitle = "PRODUCT", imageUrls = mutableListOf(
					"https://s3-alpha-sig.figma.com/img/c791/197e/dcf10efc9fa151a9e05a3cdb5135450e?Expires=1729468800&Key-Pair-Id=APKAQ4GOSFWCVNEHN3O4&Signature=kztBpUK9GdKSU0bOVEMBlxjzUMbLAcJfGVOqOjvMdvfoLL2d0OsiVej-PUy3yoFxqVSs5L7cShG6wUL7WTQ7DlvI98fcBBPfh5Fz0Ly5Z~4LMywr-nPPV1fIhlnWkwV8ZVhUYDS-4ktZjS1AWVNj0YfnrX4SRXVB4lCgl0uIX93E8zCOkk2tsNBbNtKp2mAZX1Wd7jprwPELnp1cuX-B3lm23BRYxGsq99yI6Ez0UVXEJRoto-f3w0YoWqMSfHTZxGwxaCv8oGhWSb5bR~J4JQL19kERthz38TbtzpUG9DAwW3Mou5swhPbdgi1O7yBJdlAUqN9uv3SEEQPYNzGBGQ__"
				), additionalInfo = "$55"
			)

		)
	)

	TestProvider(controller)
}