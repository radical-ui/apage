package com.example.objectionapp

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("$")
@Serializable
sealed class View {
	class CardView(
		val containers: List<CardContainer>,
	) : View()

	class ListView(
		val items: List<List<ListViewItem>>,
		val trailingIcon: String?
	) : View()
}

@Serializable
sealed class CardContainer {
	data class SingularCardContainer(
		val objectId: String,
	) : CardContainer()

	data class PluralCardContainer(
		val objectIds: List<String>,
		val title: String? = null,
	) : CardContainer()
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewRender(view: View, scrollBehavior: TopAppBarScrollBehavior) {
	when (view) {
		is View.CardView -> CardViewRender(view, scrollBehavior)
		is View.ListView -> ListView(view, scrollBehavior)
	}
}


