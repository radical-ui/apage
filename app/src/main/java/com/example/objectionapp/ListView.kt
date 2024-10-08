package com.example.objectionapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp


data class ListViewItem(
	val pageId: String,
	val leadingIcon: String?,
	val trailingIcon: String?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListView(
	view: View.ListView,
	scrollBehavior: TopAppBarScrollBehavior
) {
	Box(
		modifier = Modifier.padding(20.dp)

	) {
		LazyColumn(
			Modifier
				.nestedScroll(scrollBehavior.nestedScrollConnection)
				.clip(RoundedCornerShape(7))
				.nestedScroll(scrollBehavior.nestedScrollConnection)
		) {
			item {
				for ((index, item) in view.items.withIndex()) {
					val page = usePage(item.pageId) ?: return@item
					val title = page.title ?: return@item
					val subtitle = page.subtitle ?: return@item

					ListViewRender(
						title = page.title,
						subtitle = page.subtitle,
						leadingIcon = item.leadingIcon,
						trailingIcon = item.trailingIcon,
					)

					if (index <= (view.items.size - 2)) HorizontalDivider()
				}
			}
		}
	}
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListViewRender(title: String, subtitle: String, leadingIcon: String?, trailingIcon: String?) {
	val navController = useNavController()

	ListItem(
		colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
		headlineContent = { Text(title) },
		supportingContent = { Text(subtitle) },
		leadingContent = {
			leadingIcon?.let {
				Column(
					modifier = Modifier
						.fillMaxHeight()
						.padding(5.dp),
					verticalArrangement = Arrangement.SpaceAround,
				) {
					StandardIcon(it)
				}
			}

		},
		trailingContent = {
			trailingIcon?.let {
				Column(
					modifier = Modifier
						.fillMaxHeight()
						.padding(5.dp),
					verticalArrangement = Arrangement.SpaceAround,
				) {
					StandardIcon(it)
				}
			}
		},
		modifier = Modifier
			.clickable {
				navController.navigate(route = encodeObjectIdIntoPageRoute("Products"))
			}
			.fillMaxHeight()
	)
}