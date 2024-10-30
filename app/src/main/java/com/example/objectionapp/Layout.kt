package com.example.objectionapp

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.example.objectionapp.view.TabBar
import com.example.objectionapp.view.TabBarRender
import com.example.objectionapp.view.TextView
import com.example.objectionapp.view.View
import com.example.objectionapp.view.ViewRender
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Layout")
data class Layout(
	val topBar: View? = null,

	@Description(
		"The tab bar is shown at the bottom of the application. If there is no current page set, the current page will default to the first tab bar item."
	) val tabBar: TabBar? = null,

	@Description("The page that is to be shown by default") @ObjectReference(Page::class) val currentPageId: String? = null,
) : Object() {
	fun getRoots(): List<String> {
		return (tabBar?.buttons?.map { it.pageId } ?: listOf()) + (currentPageId?.let { listOf(it) }
			?: listOf())
	}

	fun getInitialPageId(): String? {
		return currentPageId ?: tabBar?.buttons?.getOrNull(0)?.pageId
	}
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RenderDefaultLayout() {
	val navController = useNavController()
	val layout = useDefaultLayout()

	Scaffold(topBar = { layout.topBar?.let { ViewRender(it) } },
		bottomBar = { layout.tabBar?.let { TabBarRender(it) } },
		content = { padding ->
			val initialObjectId = layout.getInitialPageId()
			padding

			if (initialObjectId != null) {
				SharedTransitionLayout {
					NavHost(
						navController = navController,
						startDestination = encodeObjectIdIntoPageRoute(initialObjectId)
					) {
						composable(getObjectIdPageRouteTemplate()) { navBackStackEntry ->
							PageRender(
								id = decodeObjectIdFromRouteArgs(navBackStackEntry.arguments!!),
								scrollConnection = null
//							bottomPadding = padding.calculateBottomPadding(),
//							animatedVisibilityScope = this,
							)
						}
						dialog(getObjectIdDialogRouteTemplate()) { navBackStackEntry ->
							PageRender(
								id = decodeObjectIdFromRouteArgs(navBackStackEntry.arguments!!),
								scrollConnection = null
//							bottomPadding = padding.calculateBottomPadding(),
//							animatedVisibilityScope = null,
							)
						}
						dialog(getObjectIdSheetRouteTemplate()) { navBackStackEntry ->
							ModalBottomSheet(
								onDismissRequest = {
									navController.popBackStack()
								},
								containerColor = MaterialTheme.colorScheme.background,
								contentColor = MaterialTheme.colorScheme.onBackground,
							) {
								Box(Modifier.fillMaxSize()) {
									PageRender(
										id = decodeObjectIdFromRouteArgs(navBackStackEntry.arguments!!),
										scrollConnection = null
//								bottomPadding = padding.calculateBottomPadding(),
//								animatedVisibilityScope = null,
									)
								}
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
	controller.objectStore.preload("some_page", Page(view = TextView("Some Page")))

	TestProvider(controller)
}

//@Preview
//@Composable
//private fun TabViewTest() {
//	val controller = Controller.fromConstants()
//
//	controller.objectStore.preload("theme_default", Theme())
//	controller.objectStore.preload(
//		"layout_default", Layout(tabBar = TabBar(pages = listOf("darkness", "lightness")))
//	)
//
//	controller.objectStore.preload(
//		"darkness", Page(
//			title = "Darkness",
//			type = PageType.Plain("ShoppingBasket"),
//			subtitle = "Hello darkness my old friend, I've come to talk with you again"
//		)
//	)
//
//	controller.objectStore.preload(
//		"lightness", Page(
//			title = "Lightness",
//			type = PageType.Plain("Lightbulb"),
//			subtitle = "The light shines, and darkness cannot hide from it"
//		)
//	)
//
//	TestProvider(controller)
//}

// TODO Fixme
@Composable
@Preview()
fun EmptyLayoutTest() {
	val controller = Controller.fromConstants()
	controller.objectStore.preload(
		"theme_default", Theme(iconPack = IconPack.Rounded)
	)
	controller.objectStore.preload("layout_default", Layout(tabBar = TabBar(buttons = listOf())))

	TestProvider(controller)
}
