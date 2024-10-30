//package com.example.objectionapp
//
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.NavigationBar
//import androidx.compose.material3.NavigationBarItem
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Tab
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.DisposableEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.currentBackStackEntryAsState
//import kotlinx.serialization.ExperimentalSerializationApi
//import kotlinx.serialization.SerialName
//import kotlinx.serialization.Serializable
//import kotlinx.serialization.json.JsonClassDiscriminator
//
//@OptIn(ExperimentalSerializationApi::class)
//@Serializable
//@JsonClassDiscriminator("$")
//sealed class SearchEmbedStrategy {
//	@Serializable
//	@SerialName("sheet")
//	data object Sheet : SearchEmbedStrategy()
//
//	@Serializable
//	@SerialName("page")
//	data object Page : SearchEmbedStrategy()
//}
//
//@Serializable
//data class TabBar(
//	val floating: Boolean = false,
//	@ObjectReference(Page::class) val pages: List<String>,
//	val searchEmbedStrategy: SearchEmbedStrategy? = null
//)
//
//@Composable
//fun TabBarRender(
//	tabBar: TabBar,
//) {
//	val navController = useNavController()
//	val currentBackStackEntry = navController.currentBackStackEntryAsState()
//	val currentPageId =
//		currentBackStackEntry.value?.arguments?.let { decodeObjectIdFromRouteArgs(it) }
//
//	Column(
//		horizontalAlignment = Alignment.CenterHorizontally,
//		verticalArrangement = Arrangement.spacedBy(20.dp),
//	) {
//		val searchPage = usePage(currentPageId)?.searchPageId
//
//		if (tabBar.floating) {
//			if (searchPage != null && tabBar.searchEmbedStrategy != null) FloatingSearchRender(
//				searchPage, tabBar.searchEmbedStrategy
//			)
//			FloatingNavigationRender(tabBar)
//		} else {
//			if (searchPage != null && tabBar.searchEmbedStrategy != null) SearchRender(
//				searchPage, tabBar.searchEmbedStrategy
//			)
//			NavigationRender(tabBar)
//		}
//	}
//
//}
//
//@Composable
//private fun SearchRender(currentPageId: String, searchEmbedStrategy: SearchEmbedStrategy) {
//	val page = usePage(currentPageId)
//
//	// TODO actually open the page when clicked
//
//	if (page != null) {
//		Column {
//			Box(
//				modifier = Modifier
//					.fillMaxWidth()
//					.padding(horizontal = 16.dp)
//					.padding(top = 16.dp)
//			) {
//				Box(
//					Modifier
//						.fillMaxWidth()
//						.clip(RoundedCornerShape(50))
//						.padding(vertical = 8.dp, horizontal = 16.dp)
//				) {
//					Row(
//						verticalAlignment = Alignment.CenterVertically,
//						horizontalArrangement = Arrangement.spacedBy(10.dp)
//					) {
//						RenderIcon(page)
//						StandardIcon(
//							"Search",
//							modifier = Modifier.size(30.dp),
//						)
//
//						Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
//							page.title?.let {
//								Text(
//									it,
//									color = MaterialTheme.colorScheme.onBackground,
//									fontSize = 16.sp,
//									fontWeight = FontWeight.Bold
//								)
//							}
//
//							page.subtitle?.let {
//								Text(
//									it,
//									color = MaterialTheme.colorScheme.onBackground,
//									fontSize = 14.sp,
//								)
//							}
//						}
//					}
//				}
//			}
//		}
//	}
//}
//
//@Composable
//private fun FloatingSearchRender(
//	searchPageId: String, searchEmbedStrategy: SearchEmbedStrategy
//) {
//	val navController = useNavController()
//	val searchPage = usePage(searchPageId)
//
//	if (searchPage != null) {
//		Row {
//			Surface(
//				onClick = {
//					openSearch(navController, searchPageId, searchEmbedStrategy)
//				},
//				shadowElevation = 5.dp,
//				modifier = Modifier
//					.clip(RoundedCornerShape(60))
//					.height(50.dp)
//					.fillMaxWidth(),
//				color = MaterialTheme.colorScheme.surfaceVariant,
//			) {
//				Row(
//					verticalAlignment = Alignment.CenterVertically,
//					horizontalArrangement = Arrangement.spacedBy(10.dp),
//					modifier = Modifier.padding(horizontal = 10.dp)
//				) {
//					RenderIcon(searchPage, modifier = Modifier.size(30.dp))
//
//					searchPage.title?.let {
//						Text(
//							it,
//							color = MaterialTheme.colorScheme.onBackground,
//							fontSize = 16.sp,
//							fontWeight = FontWeight.Bold
//						)
//					}
//
//					searchPage.subtitle?.let {
//						Text(
//							it,
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
//
//private fun openSearch(
//	navController: NavHostController, pageId: String, searchEmbedStrategy: SearchEmbedStrategy
//) {
//	when (searchEmbedStrategy) {
//		is SearchEmbedStrategy.Sheet -> navController.navigate(
//			route = encodeObjectIdIntoSheetRoute(
//				pageId
//			)
//		)
//
//		is SearchEmbedStrategy.Page -> navController.navigate(
//			route = encodeObjectIdIntoPageRoute(
//				pageId
//			)
//		)
//	}
//}
//
//@Composable
//private fun RenderIcon(page: Page, modifier: Modifier = Modifier) {
//	when (page.type) {
//		is PageType.Plain -> page.type.icon?.let { StandardIcon(it, modifier) }
//		else -> {}
//	}
//}
//
//@Composable
//private fun NavigationRender(tabBar: TabBar) {
//	NavigationBar {
//		NavigableTabBar(buttons = tabBar.pages.map { id ->
//			NavButton(pageId = id) { didClick, isActive ->
//				val page = usePage(id);
//
//				if (page != null) {
//					NavigationBarItem(
//						selected = isActive,
//						icon = { RenderIcon(page) },
//						onClick = { didClick() },
//					)
//				}
//			}
//		})
//	}
//}
//
//@Composable
//private fun FloatingNavigationRender(tabBar: TabBar) {
//	Column(
//		modifier = Modifier
//			.fillMaxWidth()
//			.padding(20.dp),
//		horizontalAlignment = Alignment.CenterHorizontally,
//	) {
//		Surface(
//			modifier = Modifier
//				.padding(0.dp)
//				.clip(RoundedCornerShape(60))
//				.height(50.dp),
//			color = MaterialTheme.colorScheme.inverseSurface,
//		) {
//			Row(
//				verticalAlignment = Alignment.CenterVertically,
//				horizontalArrangement = Arrangement.SpaceEvenly,
//				modifier = Modifier.padding(0.dp),
//			) {
//				NavigableTabBar(buttons = tabBar.pages.map { pageId ->
//					NavButton(pageId = pageId) { didClick, isActive ->
//						val page = usePage(pageId)
//
//						if (page !== null) {
//							Button(colors = ButtonDefaults.buttonColors(
//								containerColor = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.inverseSurface,
//								contentColor = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.inverseOnSurface,
//							),
//								modifier = Modifier.fillMaxHeight(),
//								shape = RoundedCornerShape(0),
//								onClick = { didClick() },
//								content = {
//									RenderIcon(page, modifier = Modifier.size(30.dp))
//								})
//						}
//					}
//				})
//			}
//		}
//	}
//}
//
//private data class NavButton(
//	val pageId: String,
//	val component: @Composable (() -> Unit, Boolean) -> Unit,
//)
//
//@Composable
//private fun NavigableTabBar(buttons: List<NavButton>) {
//	val navController = useNavController()
//	var currentButton by remember { mutableStateOf<String?>(null) }
//
//	DisposableEffect(Unit) {
//		val listener = NavController.OnDestinationChangedListener { _, _, arguments ->
//			val pageId = decodeObjectIdFromRouteArgs(arguments)
//
//			if (buttons.find { button -> button.pageId == pageId } != null) currentButton = pageId
//		}
//
//		navController.addOnDestinationChangedListener(listener)
//
//		onDispose {
//			navController.removeOnDestinationChangedListener(listener)
//		}
//	}
//
//	for (button in buttons) {
//		val isActive = currentButton == button.pageId
//		val onDidClick = {
//			navController.navigate(route = encodeObjectIdIntoPageRoute(button.pageId)) {
//				popUpTo(button.pageId)
//				launchSingleTop = true
//			}
//		}
//
//		button.component(onDidClick, isActive)
//	}
//}
//
//@Preview
//@Composable
//private fun FloatingTabBarTest() {
//	val controller = Controller.fromConstants()
//
//	controller.objectStore.preload(defaultThemeId, Theme())
//	controller.objectStore.preload(
//		defaultLayoutId, Layout(
//			tabBar = TabBar(
//				floating = true,
//				pages = listOf("some_page", "other_page"),
//				searchEmbedStrategy = SearchEmbedStrategy.Sheet
//			)
//		)
//	)
//
//	controller.objectStore.preload(
//		"some_page", Page(
//			title = "Hello there",
//			searchPageId = "search_page",
//			type = PageType.Plain(icon = "Lightbulb")
//		)
//	)
//	controller.objectStore.preload(
//		"other_page", Page(
//			title = "Other",
//			type = PageType.Plain(icon = "Home")
//		)
//	)
//	controller.objectStore.preload(
//		"search_page", Page(title = "Boston", type = PageType.Plain(icon = "Search"))
//	)
//
//	TestProvider(controller)
//}
