package com.example.objectionapp

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.serialization.Serializable

@Serializable
sealed class View {
	class CardView(
		val containers: List<CardContainer>,
	) : View()

	class ListView(
		val items: List<ListViewItem>
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardViewRender(view: View.CardView, scrollBehavior: TopAppBarScrollBehavior) {
	LazyColumn(
		modifier = Modifier
			.padding(bottom = 90.dp)
			.nestedScroll(scrollBehavior.nestedScrollConnection),
		verticalArrangement = Arrangement.spacedBy(10.dp),
	) {
		view.containers.map {
			item {
				CardContainerRender(it)
			}
		}
	}
}

@Composable
fun CardContainerRender(container: CardContainer) {

}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.CardRender(
	objectId: String,
	isVertical: Boolean,
	animatedVisibilityScope: AnimatedVisibilityScope?,
) {
	val obj = usePage(objectId) ?: return
	val navController = useNavController()


	Card(
		modifier = Modifier.padding(10.dp),
		onClick = {
			navController.navigate(route = encodeObjectIdIntoPageRoute(objectId))
		},
	) {
		when (obj.type) {
			is PageType.Post -> PostCardRender(
				isVertical,
				type = obj.type,
				objectId = objectId,
				title = obj.title,
				animatedVisibilityScope = animatedVisibilityScope
			)
//		is PageType.Profile -> ProfileCardRender(objectId, isVertical)
//		is PageType.Plain -> PlainCardRender(objectId, isVertical)
			else -> return@Card
		}
	}


}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.PostCardRender(
	isVertical: Boolean,
	type: PageType.Post,
	objectId: String,
	title: String?,
	animatedVisibilityScope: AnimatedVisibilityScope?,
) {
	val padding = 10.dp
	type.imageUrls?.first()?.let {
		AsyncImage(
			model = it,
			contentDescription = "An image",
			clipToBounds = true,
			contentScale = ContentScale.Crop,
			modifier = if (animatedVisibilityScope != null) {
				Modifier
					.sharedElement(
						state = rememberSharedContentState("${objectId}/image"),
						animatedVisibilityScope = animatedVisibilityScope,
						boundsTransform = { _, _ ->
							tween(durationMillis = 300)
						}
					)
			} else {
				Modifier
			}
				.height(200.dp)
				.fillMaxWidth()
				.clip(RoundedCornerShape(12.dp)),
			onState = { state ->
				println(state)
			}
		)
	}

	Column(
		Modifier.padding(horizontal = 10.dp, vertical = 12.dp),
		verticalArrangement = Arrangement.spacedBy(10.dp)
	) {
		Text(
			"$title",
//                    color = surface.value.foregroundColor1.intoColor(),
			fontSize = 16.sp,
			fontWeight = FontWeight.Bold,
		)

	}
}
