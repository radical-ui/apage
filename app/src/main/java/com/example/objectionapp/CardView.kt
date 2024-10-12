package com.example.objectionapp

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PostCardRender(
	isVertical: Boolean,
	type: PageType.Post,
	objectId: String,
	title: String?,
) {
	val padding = 10.dp
	type.imageUrls?.first()?.let {
		AsyncImage(
			model = it,
			contentDescription = "An image",
			clipToBounds = true,
			contentScale = ContentScale.Crop,
			modifier = Modifier
				.height(200.dp)
				.fillMaxWidth()
				.clip(RoundedCornerShape(12.dp))
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


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CardRender(
	objectId: String,
	isVertical: Boolean,
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
			)
//		is PageType.Profile -> ProfileCardRender(objectId, isVertical)
//		is PageType.Plain -> PlainCardRender(objectId, isVertical)
			else -> return@Card
		}
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
				when (it) {
					is CardContainer.SingularCardContainer -> CardRender(
						it.objectId,
						isVertical = false
					)

					is CardContainer.PluralCardContainer -> {
						Box(
							modifier = Modifier
								.background(color = MaterialTheme.colorScheme.surfaceVariant)
								.fillMaxWidth()
						) {
							LazyRow(
								modifier = Modifier
									.nestedScroll(scrollBehavior.nestedScrollConnection)
									.fillMaxWidth()
							) {
								item {
									it.objectIds.map {
										CardRender(it, isVertical = true)
									}
								}
							}
						}
					}
				}
			}
		}
	}
}