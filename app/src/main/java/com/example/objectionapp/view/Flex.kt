package com.example.objectionapp.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.example.objectionapp.ContentKey
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import kotlin.collections.listOf

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("$")
sealed class HorizontalAlignment {
	@Serializable
	data object Center : HorizontalAlignment()

	@Serializable
	data object Start : HorizontalAlignment()

	@Serializable
	data object End : HorizontalAlignment()

	fun toHorizontalAlignment(): Alignment.Horizontal {
		return when (this) {
			is Center -> Alignment.CenterHorizontally
			is Start -> Alignment.Start
			is End -> Alignment.End
		}
	}
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("$")
sealed class VerticalAlignment {
	@Serializable
	data object Center : VerticalAlignment()

	@Serializable
	data object Top : VerticalAlignment()

	@Serializable
	data object Bottom : VerticalAlignment()

	fun toVerticalAlignment(): Alignment.Vertical {
		return when (this) {
			is Center -> Alignment.CenterVertically
			is Top -> Alignment.Top
			is Bottom -> Alignment.Bottom
		}
	}
}

@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("$")
@ContentKey("def")
@Serializable
sealed class ContentPadding {
	@Serializable
	@SerialName("Padding")
	data class All(val def: Float) : ContentPadding()

	@Serializable
	@SerialName("Horizontal")
	data class Horizontal(val def: Float) : ContentPadding()

	@Serializable
	@SerialName("Vertical")
	data class Vertical(val def: Float) : ContentPadding()

	@Serializable
	@SerialName("Top")
	data class Top(val def: Float) : ContentPadding()

	@Serializable
	@SerialName("Bottom")
	data class Bottom(val def: Float) : ContentPadding()

	@Serializable
	@SerialName("Start")
	data class Start(val def: Float) : ContentPadding()

	@Serializable
	@SerialName("End")
	data class End(val def: Float) : ContentPadding()
}

fun compilePaddingValues(padding: List<ContentPadding>): PaddingValues {
	var top = 0F;
	var bottom = 0F;
	var start = 0F;
	var end = 0F;

	for (item in padding) {
		when (item) {
			is ContentPadding.All -> {
				top = item.def
				bottom = item.def
				start = item.def
				end = item.def
			}

			is ContentPadding.Horizontal -> {
				start = item.def
				end = item.def
			}

			is ContentPadding.Vertical -> {
				top = item.def
				bottom = item.def
			}

			is ContentPadding.Top -> top = item.def
			is ContentPadding.Bottom -> bottom = item.def
			is ContentPadding.Start -> start = item.def
			is ContentPadding.End -> end = item.def
		}
	}

	return PaddingValues(top = top.dp, bottom = bottom.dp, start = start.dp, end = end.dp)
}

@Serializable
data class FlexItem(
	val expand: Boolean? = null,
	val content: View,
)

@Serializable
@SerialName("Column")
data class ColumnView(
	val styles: List<Style>? = null,
	val spacing: Float? = null,
	val items: List<FlexItem>? = null,
	val alignment: HorizontalAlignment? = null
) : View()

@Composable
fun ColumnViewRender(view: ColumnView) {
	Column(
		verticalArrangement = Arrangement.spacedBy((view.spacing ?: 0F).dp),
		modifier = compileStyles(view.styles),
		horizontalAlignment = (view.alignment ?: HorizontalAlignment.Start).toHorizontalAlignment()
	) {
		for (item in view.items ?: listOf()) {
			Box(if (item.expand == true) Modifier.weight(1F) else Modifier) {
				ViewRender(item.content)
			}
		}
	}
}

var LocalScrollConnection = compositionLocalOf<NestedScrollConnection?> { null }

@Serializable
@SerialName("ScrollingColumn")
data class ScrollingColumnView(
	val styles: List<Style>? = null,
	val spacing: Float? = null,
	val items: List<View>? = null,
	val contentPadding: List<ContentPadding>? = null
) : View()

@Composable
fun ScrollingColumnViewRender(view: ScrollingColumnView) {
	val modifier = LocalScrollConnection.current?.let { Modifier.nestedScroll(it) } ?: Modifier

	LazyColumn(verticalArrangement = Arrangement.spacedBy((view.spacing ?: 0F).dp),
		contentPadding = compilePaddingValues(view.contentPadding ?: listOf()),
		modifier = compileStyles(view.styles, modifier),
		content = {
			for (child in view.items ?: listOf()) {
				item {
					CompositionLocalProvider(LocalScrollConnection.provides(null)) {
						ViewRender(child)
					}
				}
			}
		})
}

@Serializable
@SerialName("Row")
data class RowView(
	val styles: List<Style>? = null,
	val spacing: Float? = null,
	val items: List<FlexItem>? = null,
	val alignment: VerticalAlignment? = null
) : View()

@Composable
fun RowViewRender(view: RowView) {
	Row(
		modifier = compileStyles(view.styles),
		horizontalArrangement = Arrangement.spacedBy((view.spacing ?: 0.0).toInt().dp),
		verticalAlignment = (view.alignment ?: VerticalAlignment.Top).toVerticalAlignment()
	) {
		for (child in view.items ?: listOf()) {
			Box(if (child.expand == true) Modifier.weight(1F) else Modifier) {
				ViewRender(child.content)
			}
		}
	}
}

@Serializable
@SerialName("ScrollingRow")
data class ScrollingRowView(
	val styles: List<Style>? = null,
	val spacing: Float? = null,
	val items: List<View>? = null,
	val contentPadding: List<ContentPadding>? = null
) : View()

@Composable
fun ScrollingRowViewRender(view: ScrollingRowView) {
	LazyRow(
		modifier = compileStyles(view.styles),
		horizontalArrangement = Arrangement.spacedBy((view.spacing ?: 0F).dp),
		contentPadding = compilePaddingValues(view.contentPadding ?: listOf()),
	) {
		for (child in view.items ?: listOf()) {
			item {
				ViewRender(child)
			}
		}
	}
}
