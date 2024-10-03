package com.example.objectionapp

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import kotlinx.serialization.Serializable

@Serializable
data class Layout(
	@Description(
		"The tab bar is shown at the bottom of the application. If there is no current page set, the current page will default to the first tab bar item."
	) val tabBar: TabBar? = null,

	@Description("The page that is to be shown by default") @ObjectReference(Object.Page::class) val currentPageId: String? = null,
) {
	fun getRoots(): List<String> {
		return (tabBar?.buttons?.map { it.pageId } ?: listOf()) + (currentPageId?.let { listOf(it) } ?: listOf())
	}

	fun getInitialPageId(): String? {
		return currentPageId ?: tabBar?.buttons?.first()?.pageId
	}
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RenderDefaultLayout() {
	val navController = useNavController()
	val layout = useDefaultLayout()

	Scaffold(
		bottomBar = { layout.tabBar?.let { TabBarRender(it) } },
		content = { padding ->
			val initialObjectId = layout.getInitialPageId()

			if (initialObjectId != null) {
				SharedTransitionLayout {
					NavHost(
						navController = navController, startDestination = encodeObjectIdIntoPageRoute(initialObjectId)
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
					}
				}
			}
		})
}

@Composable
@Preview()
fun SingleLayoutTest() {
	val controller = Controller.fromConstants()
	controller.objectStore.preload("theme_default", Object.Theme(Theme(iconPack = IconPack.Rounded)))
	controller.objectStore.preload(
		"layout_default", Object.Layout(
			Layout(
				tabBar = TabBar(
					stupid = true,
					buttons = listOf(
						TabBarButton("Products", "ShoppingBasket"),
						TabBarButton("Home", "Home"),
						TabBarButton("Services", "Group"),
					)
				)
			)
		)
	)
	controller.objectStore.preload(
		"Services", Object.Page(
			Page(title = "Services", type = PageType.Plain)
		)
	)
	controller.objectStore.preload(
		"Home", Object.Page(
			Page(title = "Home", type = PageType.Plain)
		)
	)
	controller.objectStore.preload(
		"Products", Object.Page(
			Page(title = "Products", type = PageType.Plain)
		)
	)

	TestProvider(controller)
}
