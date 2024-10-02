package com.example.objectionapp

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview


data class FormPage(
	val header: FormActions,
	val inputs: List<Input>, // TODO: Add other form variations. Currently there is only the option for a field
)

class FormActions(
	val right: FormAction,
	val left: FormAction,
)

class FormAction(
	val icon: String,
)


enum class FileInputForm {
	button,
	block
}

sealed class Input {
	data class Field(
		val title: String,
		val placeholder: String,
		val label: String,
		var value: String,
	) : Input()

	data class Toggle(
		val title: String,
		val icon: String,
		val toggled: Boolean,
	) : Input()

	data class Checkbox(
		val title: String,
		val checked: Boolean,
	) : Input()

	data class Button(
		val title: String,
		val text: String,
		val icon: String,
	) : Input()
	data class File(
		val form: FileInputForm,
		val title: String,
		val fileType: String,
		val icon: String,
	): Input()
}


@Composable
fun FormPageRenderer(formPage: FormPage) {
	LazyColumn {
		for (input in formPage.inputs) {
			when (input) {
				is Input.Field -> {
					item {
						OutlinedTextField(
							value = input.value,
							onValueChange = { input.value = it },
							label = { Text(input.label) },
							modifier = Modifier
								.fillMaxWidth()
								.padding(8.dp),
							keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
						)
					}
				}

				is Input.Button -> {
//					Button() {

//					}
				}

				is Input.Toggle -> {

				}

				is Input.Checkbox -> {

				}

				is Input.File -> {

				}

			}

		}
	}
}



@Preview
@Composable
fun FormPagePreview() {
	val fp = FormPage(
		header = FormActions(right = FormAction("Check"), left = FormAction("X")),
		inputs = listOf<Input>(
			Input.Field(
				title = "This is a title",
				placeholder = "Hello",
				label = "Hey",
				value = "nothing"
			),
			Input.Field(
				title = "this is a title",
				placeholder = "Hello",
				label = "Hey",
				value = "nothing"
			),
			Input.Button(
				icon = "ArrowUp",
				text = "Upload",
				title = "Just in case..."
			),
			Input.Checkbox(
				"with a title comes....",
				checked = false,
			),
			Input.File(
				icon = "File",
				title = "great responsibility",
				form = FileInputForm.block,
				fileType = "png",
			)
		)
	)
	FormPageRenderer(fp)
}
