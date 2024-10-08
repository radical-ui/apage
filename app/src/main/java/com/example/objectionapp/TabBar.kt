package com.example.objectionapp


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.serialization.Serializable

@Serializable
data class TabBar(
	val stupid: Boolean = false, val buttons: List<TabBarButton>, val useSheet: Boolean
)

@Serializable
data class TabBarButton(
	@ObjectReference(Object.Page::class) val pageId: String,
	val icon: String,
)

@Composable
fun TabBarRender(
	tabBar: TabBar,
) {
	val navController = useNavController()
	val currentBackStackEntry = navController.currentBackStackEntryAsState()
	val currentPageId =
		currentBackStackEntry.value?.arguments?.let { decodeObjectIdFromRouteArgs(it) }

	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(20.dp),
	) {

		val searchPage = usePage(currentPageId)?.searchPageId

		if (tabBar.stupid) {
			if (searchPage != null) StupidSearchRender(currentPageId, tabBar.useSheet)
			StupidNavigationRender(tabBar)
		} else {
			if (searchPage != null) SearchRender(currentPageId, tabBar.useSheet)
			NavigationRender(tabBar)
		}
	}

}

@Composable
private fun SearchRender(currentPageId: String?, useSheet: Boolean) {
	Column {
		currentPageId?.let { pageId ->
			val page = usePage(pageId)

			Box(
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 16.dp)
					.padding(top = 16.dp)
			) {
				Box(
					Modifier
						.fillMaxWidth()
						.clip(RoundedCornerShape(50))
						.padding(vertical = 8.dp, horizontal = 16.dp)
				) {
					Row(
						verticalAlignment = Alignment.CenterVertically,
						horizontalArrangement = Arrangement.spacedBy(10.dp)
					) {
						StandardIcon(
							"Search",
							modifier = Modifier.size(30.dp),
						)

						Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
							page?.title?.let {
								Text(
									it,
									color = MaterialTheme.colorScheme.onBackground,
									fontSize = 16.sp,
									fontWeight = FontWeight.Bold
								)
							}

							page?.title?.let {
								Text(
									it,
									color = MaterialTheme.colorScheme.onBackground,
									fontSize = 14.sp,
								)
							}
						}
					}
				}
			}
		}
	}
}


@Composable
private fun StupidSearchRender(
	currentPageId: String?,
	useSheet: Boolean
) {
	val navController = useNavController()

	Row {
		currentPageId?.let { pageId ->

			val searchPage = usePage(pageId)
			var searching by remember { mutableStateOf(false) }

			Surface(
				onClick = {
					search(navController, pageId, useSheet)
				},
				modifier = Modifier
					.clip(RoundedCornerShape(60))
					.height(50.dp)
					.fillMaxWidth(),
				color = MaterialTheme.colorScheme.surfaceVariant,
			) {
				Row(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(10.dp),
					modifier = Modifier.padding(horizontal = 10.dp)
				) {
					StandardIcon("Search")
					searchPage?.title?.let {
						Text(
							it,
							color = MaterialTheme.colorScheme.onBackground,
							fontSize = 16.sp,
							fontWeight = FontWeight.Bold
						)
					}
					searchPage?.subtitle?.let {
						Text(
							it,
							color = MaterialTheme.colorScheme.onBackground,
							fontSize = 16.sp,
							fontWeight = FontWeight.Bold
						)
					}
				}
			}
		}
	}
}

private fun search(navController: NavHostController, pageId: String, useSheet: Boolean) {
	if (useSheet) navController.navigate(route = encodeObjectIdIntoSheetRoute(pageId))
	else navController.navigate(route = encodeObjectIdIntoPageRoute(pageId))
}

@Composable
private fun NavigationRender(tabBar: TabBar) {
	NavigationBar {
		NavigableTabBar(buttons = tabBar.buttons.map { button ->
			NavButton(pageId = button.pageId) { didClick, isActive ->
				NavigationBarItem(
					selected = isActive,
					icon = { StandardIcon(button.icon) },
					onClick = { didClick() },
				)
			}
		})
	}
}

@Composable
private fun StupidNavigationRender(tabBar: TabBar) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(20.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		Surface(
			modifier = Modifier
				.padding(0.dp)
				.clip(RoundedCornerShape(60))
				.height(50.dp),
			color = MaterialTheme.colorScheme.surfaceVariant,

			) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.SpaceEvenly,
				modifier = Modifier.padding(0.dp),

				) {
				NavigableTabBar(buttons = tabBar.buttons.map { button ->
					NavButton(pageId = button.pageId) { didClick, isActive ->
						val color = if (isActive) MaterialTheme.colorScheme.primary
						else MaterialTheme.colorScheme.surfaceVariant
						Button(colors = ButtonDefaults.buttonColors(
							containerColor = color,
							contentColor = Color.Black,
						),
							modifier = Modifier.fillMaxHeight(),
							shape = RoundedCornerShape(0),
							onClick = { didClick() },
							content = {
								StandardIcon(
									button.icon,
									modifier = Modifier.size(30.dp),
								)
							})
					}
				})
			}
		}
	}
}

data class NavButton(
	val pageId: String,
	val component: @Composable (() -> Unit, Boolean) -> Unit,
)


@Composable
fun NavigableTabBar(buttons: List<NavButton>) {
	val navController = useNavController()
	var currentButton by remember { mutableStateOf<String?>(null) }

	DisposableEffect(Unit) {
		val listener = NavController.OnDestinationChangedListener { _, _, arguments ->
			val pageId = decodeObjectIdFromRouteArgs(arguments)

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