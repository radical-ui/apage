package com.example.objectionapp

import androidx.compose.animation.ExperimentalSharedTransitionApi
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

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PostCardRender(
	isVertical: Boolean,
	type: PageType.Post,
	objectId: String,
	title: String?,
	subtitle: String?
) {
	val padding = 10.dp

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.fillMaxHeight()
			.padding(10.dp),
		horizontalArrangement = Arrangement.Start,
		verticalAlignment = Alignment.CenterVertically
	) {
		type.imageUrls?.first()?.let {
			AsyncImage(
				model = it,
				contentDescription = "An image",
				clipToBounds = true,
				contentScale = ContentScale.Crop,
				modifier = Modifier
					.width(90.dp)
					.height(90.dp)
					.clip(RoundedCornerShape(12.dp))
			)
		Column(
			Modifier.padding(horizontal = 10.dp, vertical = 12.dp),
			verticalArrangement = Arrangement.spacedBy(2.dp)
		) {
			type.supertitle?.let {
				Text(
					it,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					fontSize = 10.sp,
					fontWeight = FontWeight.SemiBold,
				)
			}
			title?.let {
				Text(
					it,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					fontSize = 16.sp,
					fontWeight = FontWeight.Bold,
				)
			}
			subtitle?.let {
				Text(
					it,
					fontSize = 12.sp,
					color = MaterialTheme.colorScheme.primary
				)
			}
			type.additionalInfo?.let {
				Text(
					it,
					fontSize = 14.sp,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					fontWeight = FontWeight.Bold
				)
			}

		}
	}
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CardRender(
	objectId: String,
	isVertical: Boolean,
) {
	val obj = usePage(objectId) ?: return
	val navController = useNavController()

	Card(
		modifier = Modifier
			.padding(horizontal = 20.dp, vertical = 10.dp)
			.fillMaxWidth(),
		elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp),
		onClick = {
			navController.navigate(route = encodeObjectIdIntoPageRoute(objectId))
		},
	) {
		when (obj.type) {
			is PageType.Post -> PostCardRender(
				isVertical,
				type = obj.type,
				objectId = objectId,
				title = obj.title,
				subtitle = obj.subtitle
			)
//		is PageType.Profile -> ProfileCardRender(objectId, isVertical)
			is PageType.Plain -> PlainCardRender(objectId, isVertical)
			else -> return@Card
		}
	}


}

@Composable
fun PlainCardRender(objectId: String, isVertical: Boolean) {
	val page = usePage(objectId) ?: return
	val navController = useNavController()

	Column(modifier = Modifier.padding(10.dp)) {
		Text(page.title ?: "No title", fontSize = 16.sp, fontWeight = FontWeight.Bold)
		Text(page.subtitle ?: "", fontSize = 14.sp)
	}

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardViewRender(view: View.CardView, scrollBehavior: TopAppBarScrollBehavior) {
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
						isVertical = false
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
										CardRender(it, isVertical = true)
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun CardViewTest() {

	val controller = Controller.fromConstants()

	controller.objectStore.preload(
		"theme_default", Object.Theme(Theme(iconPack = IconPack.Rounded))
	)

	controller.objectStore.preload(
		"layout_default", Object.Layout(
			Layout(
				tabBar = TabBar(
					useSheet = true,
					stupid = false, buttons = listOf(
						TabBarButton("Products", "ShoppingBasket"),
					)
				)
			)
		)
	)

	controller.objectStore.preload(
		"Products",
		Object.Page(
			Page(
				title = "Products",
				type = PageType.Plain("ShoppingBasket"),
				subtitle = "Hello darkness my old friend, I've come to talk with you again",
				view = View.CardView(
					mutableListOf(
						CardContainer.SingularCardContainer("Hammer_Product"),
						CardContainer.SingularCardContainer("Hammer_Product"),
						CardContainer.SingularCardContainer("Hammer_Product"),
					)
				)
			)
		)
	)

	controller.objectStore.preload(
		"Hammer_Product", Object.Page(
			Page(
				"Premium Hammer", "Hammers Inc.", type = PageType.Post(
					supertitle = "PRODUCT",
					imageUrls = mutableListOf(
						"https://s3-alpha-sig.figma.com/img/c791/197e/dcf10efc9fa151a9e05a3cdb5135450e?Expires=1729468800&Key-Pair-Id=APKAQ4GOSFWCVNEHN3O4&Signature=kztBpUK9GdKSU0bOVEMBlxjzUMbLAcJfGVOqOjvMdvfoLL2d0OsiVej-PUy3yoFxqVSs5L7cShG6wUL7WTQ7DlvI98fcBBPfh5Fz0Ly5Z~4LMywr-nPPV1fIhlnWkwV8ZVhUYDS-4ktZjS1AWVNj0YfnrX4SRXVB4lCgl0uIX93E8zCOkk2tsNBbNtKp2mAZX1Wd7jprwPELnp1cuX-B3lm23BRYxGsq99yI6Ez0UVXEJRoto-f3w0YoWqMSfHTZxGwxaCv8oGhWSb5bR~J4JQL19kERthz38TbtzpUG9DAwW3Mou5swhPbdgi1O7yBJdlAUqN9uv3SEEQPYNzGBGQ__"
					),
					additionalInfo = "$55"
				)
			)
		)
	)

	TestProvider(controller)
}