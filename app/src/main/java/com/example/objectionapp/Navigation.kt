package com.example.objectionapp

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

var LinkShouldPopUp = compositionLocalOf<Boolean> { false }

@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("$")
@Serializable
data class Link(
	@ObjectReference(Page::class) val pageId: String, val useSheet: Boolean? = null
) {
	fun follow(navController: NavController, popUp: Boolean) {
		navController.navigate(
			route = if (useSheet == true) encodeObjectIdIntoSheetRoute(pageId)
			else encodeObjectIdIntoPageRoute(pageId)
		) {
			if (popUp) {
				popUpTo(pageId)
				launchSingleTop = true
			}
		}
	}
}
