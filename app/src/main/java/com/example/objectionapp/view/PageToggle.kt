package com.example.objectionapp.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.objectionapp.ContentKey
import com.example.objectionapp.ObjectReference
import com.example.objectionapp.Page
import com.example.objectionapp.decodeObjectIdFromRouteArgs
import com.example.objectionapp.useNavController
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@ContentKey("def")
@JsonClassDiscriminator("$")
sealed class PageToggleFilter {
	@Serializable
	@SerialName("SpecificPage")
	data class SpecificPage(@ObjectReference(Page::class) val def: String) : PageToggleFilter()

	@Serializable
	@SerialName("NotSpecificPage")
	data class NotSpecificPage(@ObjectReference(Page::class) val def: String) : PageToggleFilter()

	fun isOk(pageId: String): Boolean? {
		return when (this) {
			is SpecificPage -> {
				if (pageId == this.def) true
				else null
			}

			is NotSpecificPage -> {
				if (pageId == this.def) false
				else null
			}
		}
	}
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("$")
sealed class PageToggleViewDefaultBehavior {
	@Serializable
	@SerialName("Include")
	data object Include : PageToggleViewDefaultBehavior()

	@Serializable
	@SerialName("Exclude")
	data object Exclude : PageToggleViewDefaultBehavior()
}

@Serializable
@SerialName("PageToggle")
data class PageToggleView(
	val filters: List<PageToggleFilter>? = null,
	val content: View? = null,
	val defaultBehavior: PageToggleViewDefaultBehavior? = null
) : View()

private fun isFiltersOk(filters: List<PageToggleFilter>, pageId: String): Boolean? {
	var resolution: Boolean? = null

	for (filter in filters) {
		resolution = filter.isOk(pageId)
		if (resolution !== null) return resolution
	}

	return null
}

@Composable
fun PageToggleViewRender(view: PageToggleView) {
	val controller = useNavController()
	val stack by controller.currentBackStackEntryAsState()
	val arguments = stack?.arguments ?: return
	val objectId = decodeObjectIdFromRouteArgs(arguments)
	val resolution = isFiltersOk((view.filters ?: listOf()), objectId)

	if (resolution == true || resolution == null && view.defaultBehavior !is PageToggleViewDefaultBehavior.Exclude) {
		view.content?.let { ViewRender(it) }
	}
}
