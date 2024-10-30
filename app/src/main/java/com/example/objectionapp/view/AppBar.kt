package com.example.objectionapp.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.objectionapp.Controller
import com.example.objectionapp.Layout
import com.example.objectionapp.Link
import com.example.objectionapp.ObjectReference
import com.example.objectionapp.Page
import com.example.objectionapp.TestProvider
import com.example.objectionapp.Theme
import com.example.objectionapp.decodeObjectIdFromRouteArgs
import com.example.objectionapp.defaultLayoutId
import com.example.objectionapp.defaultThemeId
import com.example.objectionapp.encodeObjectIdIntoPageRoute
import com.example.objectionapp.useNavController
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TabBarButton(
	@ObjectReference(Page::class) val pageId: String,
	val text: String,
	val icon: String,
)

@Serializable
data class TabBar(
	val floating: Boolean = false, val buttons: List<TabBarButton>, val searchView: View? = null
)

@Composable
fun TabBarRender(tabBar: TabBar) {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(20.dp),
	) {
		if (tabBar.floating) {
			tabBar.searchView?.let { ViewRender(it) }
			FloatingNavigationRender(tabBar.buttons)
		} else {
//			tabBar.searchButton?.let { SearchRender(it) }
			NavigationRender(tabBar.buttons)
		}
	}

}

//@Composable
//private fun SearchRender(button: TabBarSearchButton) {
//	// TODO actually open the page when clicked
//
//	Column {
//		Box(
//			modifier = Modifier
//				.fillMaxWidth()
//				.padding(horizontal = 16.dp)
//				.padding(top = 16.dp)
//		) {
//			Box(
//				Modifier
//					.fillMaxWidth()
//					.clip(RoundedCornerShape(50))
//					.padding(vertical = 8.dp, horizontal = 16.dp)
//			) {
//				Row(
//					verticalAlignment = Alignment.CenterVertically,
//					horizontalArrangement = Arrangement.spacedBy(10.dp)
//				) {
//					IconViewRender(IconView(name = button.icon, styles = listOf(Style.Size(30F))))
//
//					Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
//						Text(
//							button.text,
//							color = MaterialTheme.colorScheme.onBackground,
//							fontSize = 16.sp,
//							fontWeight = FontWeight.Bold
//						)
//					}
//				}
//			}
//		}
//	}
//}

@Serializable
@SerialName("FloatingBottomSearchBar")
data class FloatingBottomSearchBarView(
	val icon: String, val text: String, val link: Link
) : View()

@Composable
fun FloatingBottomSearchBarViewRender(view: FloatingBottomSearchBarView) {
	val navController = useNavController()

	Row(Modifier.padding(horizontal = 16.dp)) {
		Surface(
			onClick = {
				view.link.follow(navController, false)
			},
			shadowElevation = 100.dp,
			modifier = Modifier
				.shadow(10.dp)
				.clip(RoundedCornerShape(60))
				.height(50.dp)
				.fillMaxWidth(),
			color = MaterialTheme.colorScheme.inverseSurface,
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(10.dp),
				modifier = Modifier.padding(horizontal = 10.dp)
			) {
				CompositionLocalProvider(LocalContentColor.provides(MaterialTheme.colorScheme.inverseOnSurface)) {
					IconViewRender(IconView(name = view.icon, styles = listOf(Style.Size(30F))))

					Text(
						view.text, fontSize = 16.sp, fontWeight = FontWeight.Bold
					)
				}
			}
		}
	}
}

@Composable
private fun NavigationRender(buttons: List<TabBarButton>) {
	NavigationBar {
		NavigableTabBar(buttons = buttons.map { button ->
			NavButton(pageId = button.pageId) { didClick, isActive ->
				NavigationBarItem(
					selected = isActive,
					icon = { IconViewRender(IconView(name = button.icon)) },
					onClick = { didClick() },
				)
			}
		})
	}
}

@Composable
private fun FloatingNavigationRender(buttons: List<TabBarButton>) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(bottom = 40.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		Surface(
			modifier = Modifier
				.padding(0.dp)
				.shadow(10.dp)
				.clip(RoundedCornerShape(60))
				.height(50.dp),
			color = MaterialTheme.colorScheme.inverseSurface,
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.SpaceEvenly,
				modifier = Modifier.padding(0.dp),
			) {
				NavigableTabBar(buttons = buttons.map { button ->
					NavButton(pageId = button.pageId) { didClick, isActive ->
						Button(colors = ButtonDefaults.buttonColors(
							containerColor = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.inverseSurface,
							contentColor = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.inverseOnSurface,
						),
							modifier = Modifier.fillMaxHeight(),
							shape = RoundedCornerShape(0),
							onClick = { didClick() },
							content = {
								IconViewRender(
									IconView(
										name = button.icon, styles = listOf(Style.Size(30F))
									)
								)
							})
					}
				})
			}
		}
	}
}

private data class NavButton(
	val pageId: String,
	val component: @Composable (() -> Unit, Boolean) -> Unit,
)

@Composable
private fun NavigableTabBar(buttons: List<NavButton>) {
	val navController = useNavController()
	var currentButton by remember { mutableStateOf<String?>(null) }

	DisposableEffect(Unit) {
		val listener = NavController.OnDestinationChangedListener { _, _, arguments ->
			val pageId = decodeObjectIdFromRouteArgs(arguments!!)

			println("clicked $pageId")
			if (buttons.find { button -> button.pageId == pageId } != null) currentButton = pageId
		}

		navController.addOnDestinationChangedListener(listener)

		onDispose {
			navController.removeOnDestinationChangedListener(listener)
		}
	}

	for (button in buttons) {
		val isActive = currentButton == button.pageId
		val onDidClick = {
			navController.navigate(route = encodeObjectIdIntoPageRoute(button.pageId)) {
				popUpTo(button.pageId)
				launchSingleTop = true
			}
		}

		button.component(onDidClick, isActive)
	}
}

@Preview
@Composable
private fun FloatingTabBarTest() {
	val controller = Controller.fromConstants()

	controller.objectStore.preload(defaultThemeId, Theme())
	controller.objectStore.preload(
		defaultLayoutId, Layout(
			tabBar = TabBar(
				floating = true, buttons = listOf(
					TabBarButton(
						pageId = "some_page", text = "Some", icon = "Lightbulb"
					)
				), searchView = FloatingBottomSearchBarView(
					icon = "Search",
					link = Link(pageId = "search_page", useSheet = true),
					text = "Search..."
				)
			)
		)
	)

	controller.objectStore.preload("some_page", Page())
	controller.objectStore.preload("other_page", Page())
	controller.objectStore.preload("search_page", Page())

	TestProvider(controller)
}
