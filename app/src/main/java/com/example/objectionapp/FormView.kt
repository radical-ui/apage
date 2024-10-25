package com.example.objectionapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun FormViewRender(view: View.FormView, scrollBehavior: TopAppBarScrollBehavior) {


	val controller = useController()
	Column(
		modifier = Modifier
			.fillMaxHeight()
			.padding(20.dp),
		verticalArrangement = if (view.actionStyle is FormActionStyle.Relative) Arrangement.spacedBy(
			10.dp
		) else if (view.actionStyle is FormActionStyle.BottomFixed) Arrangement.SpaceBetween else Arrangement.Top


	) {
		LazyColumn(
			Modifier
				.fillMaxWidth()
				.nestedScroll(scrollBehavior.nestedScrollConnection),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
				view.items.map {
					item {
						FormItemRender(it)
					}
				}
		}
		Box {
			if (view.actionStyle is FormActionStyle.Relative || view.actionStyle is FormActionStyle.BottomFixed) actionLayout(
				view
			)
		}
	}
}

@Composable
fun actionLayout(view: View.FormView) {

	val buttonSize = if (view.actionStyle !is FormActionStyle.TopBar) Modifier.fillMaxWidth()
	else Modifier.wrapContentWidth()

	val weight = if (view.actionStyle !is FormActionStyle.TopBar) 1f else null

	Row(
		modifier = buttonSize, horizontalArrangement = Arrangement.spacedBy(20.dp)
	) {
		view.actions.secondaryAction?.let {
			Box(modifier = weight?.let { Modifier.weight(it) } ?: Modifier) {
				OutlinedButton(modifier = buttonSize, onClick = {
					println("clicked")
				}) {
					Text(it.title)
				}
			}
		}

		view.actions.primaryAction.let {

			Box(modifier = weight?.let { Modifier.weight(it) } ?: Modifier) {
				ElevatedButton(modifier = buttonSize, colors = ButtonColors(
					containerColor = MaterialTheme.colorScheme.primary,
					contentColor = MaterialTheme.colorScheme.onPrimary,
					disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
					disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer
				), onClick = {
					println("clicked")
				})

				{
					Text(it.title)
				}
			}
		}

	}
}


@Composable
fun FormItemRender(item: FormItem) {
	when (item.input) {
		is Input.Text -> TextFieldRender(item = item, input = item.input, mode = item.input.mode)
		is Input.Switch -> SwitchRender(item = item, input = item.input)
	}
}

@Composable
fun SwitchRender(item: FormItem, input: Input.Switch) {
	val controller = useController()
	var checked by remember { mutableStateOf(input.switched.child) }

	Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
		Column {
			item.label?.let { Text(it, fontSize = MaterialTheme.typography.bodyLarge.fontSize) }
			item.description?.let {
				Text(
					it,
					fontSize = MaterialTheme.typography.labelSmall.fontSize
				)
			}
		}

		Switch(checked = checked, onCheckedChange = {
			checked = it
			controller.bridge.emitBindingUpdate(input.switched.key, Json.encodeToJsonElement(it)) {}
		})
	}
}

@Composable
fun TextFieldRender(
	item: FormItem, input: Input.Text, mode: InputMode
) {
	val controller = useController()
	var text by remember { mutableStateOf(input.startingValue.child) }
	var hiddenState by remember { mutableStateOf(true) }

	val multiline = if (mode is InputMode.Text) mode.multiline
	else false
	val trailingIcon = if (mode is InputMode.Text) mode.trailingIcon else null
	val password = mode is InputMode.Password

	val description = item.description
	val status = input.fieldStatus
	val label = item.label
	val placeholder = input.placeholder
	val readOnly = input.readOnly

	val focusManager = LocalFocusManager.current


	OutlinedTextField(singleLine = !multiline,
		readOnly = readOnly,
		supportingText = { description?.let { Text(it) } },
		isError = status == TextFieldStatus.Error,
		enabled = status != TextFieldStatus.Disabled,
		value = text,
		placeholder = { placeholder?.let { Text(it) } },
		keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
		onValueChange = {
			text = it
			controller.bridge.emitBindingUpdate(
				input.startingValue.key, Json.encodeToJsonElement(it)
			) {}
		},
		visualTransformation = if (password && hiddenState) PasswordVisualTransformation() else VisualTransformation.None,
		label = { label?.let { Text(it) } },
		modifier = Modifier.fillMaxWidth(),
		keyboardOptions = KeyboardOptions(
			keyboardType = mode.getKeyboardType()
		),
		trailingIcon = {
			if (password) {
				IconButton(modifier = Modifier.wrapContentHeight(), onClick = {
					hiddenState = !hiddenState
				}) {
					if (hiddenState) StandardIcon("VisibilityOff")
					else (StandardIcon("Visibility"))
				}
			}

			trailingIcon?.let { StandardIcon(it) }
		})
}

@Preview
@Composable
fun FormViewTest() {
	val controller = Controller.fromConstants()

	controller.objectStore.preload(
		"theme_default", Object.Theme(Theme(iconPack = IconPack.Rounded))
	)

	controller.objectStore.preload(
		"layout_default", Object.Layout(
			Layout(
				tabBar = TabBar(
					useSheet = true, stupid = false, buttons = listOf(
						TabBarButton("Products", "ShoppingBasket"),
					)
				)
			)
		)
	)

	controller.objectStore.preload(
		"Products", Object.Page(
			Page(
				title = "Products",
				type = PageType.Plain("ShoppingBasket"),
				subtitle = "Hello darkness my old friend, I've come to talk with you again",
				view = View.FormView(
					items = listOf(
						FormItem(
							submitStrategy = SubmitStrategy.OnSubmit,
							label = "Text Input",
							description = "some supporting text",
							input = Input.Text(
								startingValue = Binding("start", ""),
								placeholder = "not a little note",
								mode = InputMode.Text(trailingIcon = "Bolt"),
							)
						),
						FormItem(
							submitStrategy = SubmitStrategy.OnSubmit,
							label = "Password Input",
							description = "Your password is too gay. Try again.",
							input = Input.Text(
								fieldStatus = TextFieldStatus.Error,
								startingValue = Binding("start", ""),
								placeholder = "not a little note",
								mode = InputMode.Password,
							)
						),
						FormItem(
							submitStrategy = SubmitStrategy.OnSubmit,
							label = "Switch Input",
							description = "A quick description of what is getting switched.",
							input = Input.Switch(switched = Binding("key", true))
						),
					), actionStyle = FormActionStyle.TopBar, actions = FormActions(
						primaryAction = FormAction(
							title = "Login", link = Link("Products")
						), secondaryAction = FormAction(
							title = "Sign up", link = Link("Products")
						)
					)
				)
			)
		)
	)
	TestProvider(controller)
}