package com.example.objectionapp

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun FormViewRender(view: View.FormView, scrollBehavior: TopAppBarScrollBehavior) {
	LazyColumn(
		Modifier
			.fillMaxWidth()
			.nestedScroll(scrollBehavior.nestedScrollConnection)
	) {
		item {
			view.items.map {
				FormItemRender(it)
			}
		}
	}
}

@Composable
fun FormItemRender(item: FormItem) {
	when (item.type) {
		FormItemType.Text -> TextFormItemRender(item)
		FormItemType.File -> FileFormItemRender(item)
		FormItemType.ProfilePicture -> ProfilePictureFormItemRender(item)
	}
}

@Composable
fun TextFormItemRender(item: FormItem) {
	val controller = useController()
	var text = item.textValue.child

	OutlinedTextField(
		value = text,
		onValueChange = {
			controller.bridge.emitBindingUpdate(item.textValue.key, it) {
				text = it
			}
		},
		label = { Text(item.label) },
		modifier = Modifier
			.fillMaxWidth()
			.padding(8.dp),
		keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
	)
}

@Composable
fun FileFormItemRender(item: FormItem) {

}

@Composable
fun ProfilePictureFormItemRender(item: FormItem) {

}