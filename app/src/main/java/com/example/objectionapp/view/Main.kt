package com.example.objectionapp.view

import androidx.compose.runtime.Composable
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("$")
@Serializable
sealed class View

@Composable
fun ViewRender(view: View) {
	when (view) {
		is FloatingBottomSearchBarView -> FloatingBottomSearchBarViewRender(view)
		is BoxView -> BoxViewRender(view)
		is ButtonView -> ButtonViewRender(view)
		is CenterHorizontalView -> CenterHorizontalRender(view)
		is CenterVerticalView -> CenterVerticalViewRender(view)
		is RowView -> RowViewRender(view)
		is ScrollingRowView -> ScrollingRowViewRender(view)
		is ColumnView -> ColumnViewRender(view)
		is ScrollingColumnView -> ScrollingColumnViewRender(view)
		is IconView -> IconViewRender(view)
		is ImageView -> ImageViewRender(view)
		is PageToggleView -> PageToggleViewRender(view)
		is StackView -> StackViewRender(view)
		is SurfaceView -> SurfaceViewRender(view)
		is TextView -> TextViewRender(view)
	}
}
