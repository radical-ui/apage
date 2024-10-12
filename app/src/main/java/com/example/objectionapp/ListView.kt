package com.example.objectionapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable

@Serializable
data class ListViewItem(
	val pageId: String,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListView(
	view: View.ListView,
	scrollBehavior: TopAppBarScrollBehavior
) {

	LazyColumn(
		Modifier
			.nestedScroll(scrollBehavior.nestedScrollConnection)
	) {
		val allItems: List<ListViewItem> = view.items.flatten()

		for ((index, item) in allItems.withIndex()) {
			item {
				val page = usePage(item.pageId) ?: return@item
				val title = page.title ?: return@item
				val subtitle = page.subtitle ?: return@item
				val leadingIcon: String

				if (page.type is PageType.Plain) leadingIcon = page.type.icon ?: "QuestionMark"
				else return@item

				ListViewRender(
					title = page.title,
					subtitle = page.subtitle,
					leadingIcon,
					trailingIcon = view.trailingIcon,
				)
				if (index <= (allItems.size - 2)) HorizontalDivider()
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListViewRender(title: String, subtitle: String, leadingIcon: String?, trailingIcon: String?) {
	val navController = useNavController()
	ListItem(
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