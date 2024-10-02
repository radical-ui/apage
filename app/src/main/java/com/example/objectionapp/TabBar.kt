package com.example.objectionapp


import android.graphics.drawable.RippleDrawable
import androidx.compose.foundation.border
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.serialization.Serializable

@Serializable
data class TabBar(
	val stupid: Boolean = false,
	val buttons: List<TabBarButton>,
)

@Serializable
data class TabBarButton(
	@ObjectReference(Object.PlainPage::class) val pageId: String,
	val icon: String,
)

@Composable
fun TabBarRender(tabBar: TabBar) {
	val navController = useNavController()
	val currentBackStackEntry = navController.currentBackStackEntryAsState()
	val currentPageId =
		currentBackStackEntry.value?.arguments?.let { decodeObjectIdFromRouteArgs(it) }


	Column {
		currentPageId?.let { pageId ->
			val searchPage = usePage(pageId)

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
									fontSize = 14.sp,
								)
							}
						}
					}
				}
			}
		}
	}

	if (tabBar.stupid) {
		Surface(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp)
				.clip(RoundedCornerShape(60))
				.height(50.dp),
			color = MaterialTheme.colorScheme.surfaceVariant,
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.SpaceEvenly
			) {
				for (button in tabBar.buttons) {
					val page = usePage(button.pageId)
					Surface(
						modifier = Modifier
							.fillMaxWidth()
							.fillMaxHeight(),
						color = selected(button.pageId) ?: Color.Transparent
					) {
						StandardIcon(button.icon)

					}
				}
			}
		}
	} else {

		NavigationBar {
			val history = remember { mutableStateOf<List<String>>(listOf()) }

			DisposableEffect(Unit) {
				val listener = NavController.OnDestinationChangedListener { _, _, arguments ->
					history.value += listOf(decodeObjectIdFromRouteArgs(arguments))
				}

				navController.addOnDestinationChangedListener(listener)

				onDispose {
					navController.removeOnDestinationChangedListener(listener)
				}
			}

			for (button in tabBar.buttons) {
				val page = usePage(button.pageId)

				NavigationBarItem(
					selected = history.value.contains(button.pageId),
					icon = { StandardIcon(button.icon) },
					onClick = {
						history.value = listOf(button.pageId)
						navController.navigate(route = encodeObjectIdIntoPageRoute(button.pageId)) {
							popUpTo(button.pageId)
							launchSingleTop = true
						}
					},
					label = { Text("${page?.title}") },
				)
			}
		}
	}
}


fun selected(pageId: String): Color {
	return if (useDefaultLayout().tabBar?.buttons?.map { it.pageId }?.contains(pageId) == true) {
		MaterialTheme.colorScheme.primary
	} else {
		null
	}
}

