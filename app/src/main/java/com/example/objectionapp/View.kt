package com.example.objectionapp

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.serialization.Serializable

@Serializable
sealed class View {
	class CardView(
		val containers: List<CardContainer>,
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


@Composable
fun ViewRender(view: View) {
	when (view) {
		is View.CardView -> CardViewRender(view)
	}
}

@Composable
fun CardViewRender(view: View.CardView) {
	LazyColumn {
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

@Composable
fun CardRender(objectId: String, isVertical: Boolean) {
	val obj = usePage(objectId) ?: return
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ObjectPreviewView(
	content: ObjectPreview,
	padding: PaddingValues,
	width: Dp? = null,
	animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
	val obj = usePage(content.objectId)
	val navController = useNavController()

	Box(Modifier.padding(padding)) {
		Card(
			modifier = if (width != null) {
				Modifier.width(width)
			} else {
				Modifier
			},
			onClick = {
				navController.navigate(route = encodeObjectIdIntoPageRoute(content.objectId))
			},
//            colors = CardDefaults.cardColors(
//                containerColor = ,
//                contentColor = surface.value.foregroundColor1.intoColor(),
//                disabledContentColor = surface.value.foregroundColor3.intoColor(),
//                disabledContainerColor = surface.value.backgroundColor2.intoColor()
//            )
		) {
//			obj?.imageUrls?.first()?.let {
//				AsyncImage(
//					model = it,
//					contentDescription = "An image",
//					clipToBounds = true,
//					contentScale = ContentScale.Crop,
//					modifier = if (animatedVisibilityScope != null) {
//						Modifier
//							.sharedElement(
//								state = rememberSharedContentState("${content.objectId}/image"),
//								animatedVisibilityScope = animatedVisibilityScope,
//								boundsTransform = { _, _ ->
//									tween(durationMillis = 300)
//								}
//							)
//					} else {
//						Modifier
//					}
//						.height(200.dp)
//						.fillMaxWidth()
//						.clip(RoundedCornerShape(12.dp)),
//					onState = { state ->
//						println(state)
//					}
//				)
//			}

			Column(
				Modifier.padding(horizontal = 10.dp, vertical = 12.dp),
				verticalArrangement = Arrangement.spacedBy(10.dp)
			) {
				Text(
					"${obj?.title}",
//                    color = surface.value.foregroundColor1.intoColor(),
					fontSize = 16.sp,
					fontWeight = FontWeight.Bold,
				)

			}
		}
	}
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ObjectView(
	content: ObjectGroup,
	padding: PaddingValues,
	animatedVisibilityScope: AnimatedVisibilityScope?,
) {
	Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
		Text(
			content.title,
			fontSize = 20.sp,
//            color = surface.value.foregroundColor1.intoColor(),
			modifier = Modifier.padding(padding)
		)

		LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), contentPadding = padding) {
			for (objectId in content.objects) {
				item {
					ObjectPreviewView(
						ObjectPreview(objectId, null, null),
						padding = PaddingValues(0.dp),
						width = 200.dp,
						animatedVisibilityScope = animatedVisibilityScope
					)
				}
			}
		}
	}
}

