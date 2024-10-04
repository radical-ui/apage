package com.example.objectionapp

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.serialization.Serializable

@Serializable
data class Page(
	@Description("The page title will be displayed prominently at the top of the screen") val title: String? = null,
	@Description("The page subtitle is displayed directly under any images on the page") val subtitle: String? = null,
	@Description(
		"The page that will be pulled up for a presumed search through the contents of this page"
	) @ObjectReference(Object.Page::class) val searchPageId: String? = null,


	val view: View? = null,

	val type: PageType,
)

@Serializable
sealed class PageType {
	@Serializable
	data class Post(
		@Description("The images will be displayed in carousel form, directly below the title")
		val imageUrls: List<String>? = null,
		@Description("The page supertitle is displayed directly below any images on the page")
		val supertitle: String? = null,
		@Description("The additional info is displayed directly below any images on the page")
		val aditionalInfo: String? = null,
	) : PageType()

	@Serializable
	data class Profile(
		@Description("The the banner image for the user profile")
		val bannerImageUrl: String,
		@Description("The avatar image for the user profile")
		val avatarImageUrl: String,
	) : PageType()


	@Serializable
	data object Plain : PageType()
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.PageRender(
	id: String,
	bottomPadding: Dp,
	animatedVisibilityScope: AnimatedVisibilityScope?,
) {
	val page = usePage(id) ?: return
	val navController = useNavController()
	val layout = useDefaultLayout()
	val isRoot = layout.getRoots().contains(id)

	val scrollBehavior = if (isRoot) {
		TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
	} else {
		TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
	}

	Column(
		Modifier.padding(bottom = bottomPadding)
	) {
		if (isRoot) {
			LargeTopAppBar(
				title = { Text("${page.title}") },
				scrollBehavior = scrollBehavior,
			)
		} else {
			TopAppBar(
				navigationIcon = {
					IconButton(onClick = { navController.popBackStack() }) {
						StandardIcon("ArrowBack")
					}
				},
				title = { Text("${page.title}") },
				scrollBehavior = scrollBehavior,
			)
		}

		when (page.type) {
			is PageType.Plain -> PlainPageRender(
				id,
				bottomPadding,
				animatedVisibilityScope,
				pageType = page.type,
				view = page.view,
				title = page.title,
				searchPageId = page.searchPageId,
				scrollBehavior = scrollBehavior,

				)

			is PageType.Post -> PostPageRender(
				id,
				bottomPadding,
				animatedVisibilityScope,
				pageType = page.type,
				view = page.view,
				title = page.title,
				searchPageId = page.searchPageId,
				scrollBehavior = scrollBehavior,
			)

			is PageType.Profile -> ProfilePageRender(
				id,
				bottomPadding,
				animatedVisibilityScope,
				pageType = page.type,
				view = page.view,
				title = page.title,
				searchPageId = page.searchPageId,
				scrollBehavior = scrollBehavior,
			)
		}

	}

}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ProfilePageRender(
	id: String,
	bottomPadding: Dp,
	animatedVisibilityScope: AnimatedVisibilityScope?,
	pageType: PageType.Profile,
	view: View? = null,
	title: String? = null,
	searchPageId: String? = null,
	scrollBehavior: TopAppBarScrollBehavior,
) {
	return
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.PlainPageRender(
	id: String,
	bottomPadding: Dp,
	animatedVisibilityScope: AnimatedVisibilityScope?,
	pageType: PageType.Plain,
	view: View? = null,
	title: String? = null,
	searchPageId: String? = null,
	scrollBehavior: TopAppBarScrollBehavior,
) {
	if (view != null) ViewRender(view, scrollBehavior)
	else return
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.PostPageRender(
	id: String,
	bottomPadding: Dp,
	animatedVisibilityScope: AnimatedVisibilityScope?,
	pageType: PageType.Post,
	view: View? = null,
	title: String? = null,
	searchPageId: String? = null,
	scrollBehavior: TopAppBarScrollBehavior
) {
	val childPadding = PaddingValues(horizontal = 16.dp)

	// TODO support multiple images, but keep in mind that probably only the first one should take part in
	//  the shared element animation
	pageType.imageUrls?.first()?.let { url ->
		AsyncImage(
			model = url,
			contentDescription = "An image",
			clipToBounds = true,
			contentScale = ContentScale.Crop,
			modifier = if (animatedVisibilityScope != null) {
				Modifier.sharedElement(state = rememberSharedContentState("${id}/image"),
					animatedVisibilityScope = animatedVisibilityScope,
					boundsTransform = { _, _ ->
						tween(durationMillis = 300)
					})
			} else {
				Modifier
			}
				.padding(childPadding)
				.clip(RoundedCornerShape(8))
				.height(300.dp)
				.fillMaxWidth()
		)
	}
}