package com.example.objectionapp

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Layout")
data class Layout(
	@Description(
		"The tab bar is shown at the bottom of the application. If there is no current page set, the current page will default to the first tab bar item."
	) val tabBar: TabBar? = null,

	@Description("The page that is to be shown by default") @ObjectReference(Page::class) val currentPageId: String? = null,
) : Object() {
	fun getRoots(): List<String> {
		return (tabBar?.pages?.map { it } ?: listOf()) + (currentPageId?.let { listOf(it) }
			?: listOf())
	}

	fun getInitialPageId(): String? {
		return currentPageId ?: tabBar?.pages?.getOrNull(0)
	}
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RenderDefaultLayout() {
	val navController = useNavController()
	val layout = useDefaultLayout()

	Scaffold(bottomBar = { layout.tabBar?.let { TabBarRender(it) } }, content = { padding ->
		val initialObjectId = layout.getInitialPageId()

		if (initialObjectId != null) {
			SharedTransitionLayout {
				NavHost(
					navController = navController,
					startDestination = encodeObjectIdIntoPageRoute(initialObjectId)
				) {
					composable(getObjectIdPageRouteTemplate()) { navBackStackEntry ->
						PageRender(
							id = decodeObjectIdFromRouteArgs(navBackStackEntry.arguments),
							bottomPadding = padding.calculateBottomPadding(),
							animatedVisibilityScope = this,
						)
					}
					dialog(getObjectIdDialogRouteTemplate()) { navBackStackEntry ->
						PageRender(
							id = decodeObjectIdFromRouteArgs(navBackStackEntry.arguments),
							bottomPadding = padding.calculateBottomPadding(),
							animatedVisibilityScope = null,
						)
					}
					dialog(getObjectIdSheetRouteTemplate()) { navBackStackEntry ->
						ModalBottomSheet(onDismissRequest = {
							navController.popBackStack()
						}) {
							PageRender(
								id = decodeObjectIdFromRouteArgs(navBackStackEntry.arguments),
								bottomPadding = padding.calculateBottomPadding(),
								animatedVisibilityScope = null,
							)
						}
					}
				}
			}
		}
	})
}

@Composable
@Preview()
private fun SinglePageTest() {
	val controller = Controller.fromConstants()
	controller.objectStore.preload("theme_default", Theme())
	controller.objectStore.preload("layout_default", Layout(currentPageId = "some_page"))
	controller.objectStore.preload(
		"some_page", Page(
			title = "Some Page", type = PageType.Plain("settings")
		)
	)

	TestProvider(controller)
}

@Preview
@Composable
private fun TabViewTest() {
	val controller = Controller.fromConstants()

	controller.objectStore.preload("theme_default", Theme())
	controller.objectStore.preload(
		"layout_default", Layout(tabBar = TabBar(pages = listOf("darkness", "lightness")))
	)

	controller.objectStore.preload(
		"darkness", Page(
			title = "Darkness",
			type = PageType.Plain("ShoppingBasket"),
			subtitle = "Hello darkness my old friend, I've come to talk with you again"
		)
	)

	controller.objectStore.preload(
		"lightness", Page(
			title = "Lightness",
			type = PageType.Plain("Lightbulb"),
			subtitle = "The light shines, and darkness cannot hide from it"
		)
	)

	TestProvider(controller)
}

// TODO Fixme
@Composable
@Preview()
fun EmptyLayoutTest() {
	val controller = Controller.fromConstants()
	controller.objectStore.preload(
		"theme_default", Theme(iconPack = IconPack.Rounded)
	)
	controller.objectStore.preload("layout_default", Layout(tabBar = TabBar(pages = listOf())))

	TestProvider(controller)
}
