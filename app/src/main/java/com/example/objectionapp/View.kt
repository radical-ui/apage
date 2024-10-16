package com.example.objectionapp

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("$")
@Serializable
sealed class View {
	@Serializable
	@SerialName("CardView")
	class CardView(
		val containers: List<CardContainer>,
	) : View()

	@Serializable
	@SerialName("ListView")
	class ListView(
		val items: List<List<ListViewItem>>,
		val trailingIcon: String?
	) : View()
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("$")
sealed class CardContainer {
	@Serializable
	@SerialName("SingularCardContainer")
	data class SingularCardContainer(
		val objectId: String,
	) : CardContainer()

	@Serializable
	@SerialName("PluralCardContainer")
	data class PluralCardContainer(
		val objectIds: List<String>,
		val title: String? = null,
	) : CardContainer()

	data class CustomCardContainer(
		val objectId: String? = null,
		val title: String? = null,
		val icon: String? = null,
		val imageUrl: String? = null
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


