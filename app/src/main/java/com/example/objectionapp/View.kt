package com.example.objectionapp

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavController
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("$")
@Serializable
sealed class View {
	@Serializable
	@SerialName("ListView")
	data class ListView(
		val items: List<List<ListViewItem>>, val trailingIcon: String?
	) : View()

	@Serializable
	@SerialName("FormView")
	data class FormView(
		val items: List<FormItem>, val actionStyle: FormActionStyle, val actions: FormActions
	) : View()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewRender(view: View, scrollBehavior: TopAppBarScrollBehavior) {
	when (view) {
		is CardView -> CardViewRender(view, scrollBehavior)
		is View.ListView -> ListView(view, scrollBehavior)
		is View.FormView -> FormViewRender(view, scrollBehavior)
		is ContentView -> ContentViewRender(view, scrollBehavior.nestedScrollConnection)
	}
}

@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("$")
@Serializable
sealed class FormActionStyle {
	data object BottomFixed : FormActionStyle()
	data object Relative : FormActionStyle()
	data object TopBar : FormActionStyle()
}

@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("$")
@Serializable
data class Link(
	@ObjectReference(Page::class) val pageId: String, val useSheet: Boolean = false
) {
	fun follow(navController: NavController) {
		navController.navigate(
			if (useSheet) encodeObjectIdIntoSheetRoute(pageId)
			else encodeObjectIdIntoPageRoute(pageId)
		)
	}
}

@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("$")
@Serializable
data class FormActions(
	val primaryAction: FormAction,
	val secondaryAction: FormAction? = null,
)

@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("$")
@Serializable
data class FormAction(
	val title: String, val link: Link?
)

@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("$")
@Serializable
sealed class FormActionColor {
	data object Primary : FormActionColor()
	data object Secondary : FormActionColor()
	data object Error : FormActionColor()
	data object Success : FormActionColor()
	data object Warning : FormActionColor()
}

@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("$")
@Serializable
sealed class FormActionLocation {
	data object TopBar : FormActionLocation()
	data object Page : FormActionLocation()
}


@Serializable
data class FormItem(
	val submitStrategy: SubmitStrategy,
	val label: String?,
	val description: String?,
	val input: Input
)

@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("$")
@Serializable
sealed class InputMode() {
	data class Text(val multiline: Boolean = false, val trailingIcon: String? = null) : InputMode()
	data object Ascii : InputMode()
	data object Number : InputMode()
	data object Phone : InputMode()
	data object Uri : InputMode()
	data object Email : InputMode()
	data object Password : InputMode()
	data object NumberPassword : InputMode()
	data object Decimal : InputMode()

	fun getKeyboardType(): KeyboardType {
		return when (this) {
			is Text -> KeyboardType.Text
			is Ascii -> KeyboardType.Ascii
			is Number -> KeyboardType.Number
			is Phone -> KeyboardType.Phone
			is Uri -> KeyboardType.Uri
			is Email -> KeyboardType.Email
			is Password -> KeyboardType.Password
			is NumberPassword -> KeyboardType.NumberPassword
			is Decimal -> KeyboardType.Decimal
		}
	}
}

@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("$")
@Serializable
sealed class Input() {
	@Serializable
	data class Text(
		var startingValue: Binding<String>,
		var readOnly: Boolean = false,
		val fieldStatus: TextFieldStatus? = null,
		val placeholder: String? = null,
		val mode: InputMode
	) : Input()

//	@Serializable
//	data object File : Input()
//
//	@Serializable
//	data object ProfilePicture : Input()

	@Serializable
	data class Switch(
		val switched: Binding<Boolean>,
	) : Input()
}

@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("$")
@Serializable
sealed class TextFieldStatus {
	data object Error : TextFieldStatus()
	data object Disabled : TextFieldStatus()
}

@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("$")
@Serializable
sealed class SubmitStrategy {
	data object OnBlur : SubmitStrategy()
	data object OnKeyUp : SubmitStrategy()
	data object OnSubmit : SubmitStrategy()
}


@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("$")
sealed class CardContainer {
	@Serializable
	@SerialName("SingularCardContainer")
	data class SingularCardContainer(
		@ObjectReference(Page::class) val objectId: String,
	) : CardContainer()

	@Serializable
	@SerialName("PluralCardContainer")
	data class PluralCardContainer(
		@ObjectReference(Page::class) val objectIds: List<String>,
		val title: String? = null,
	) : CardContainer()

	@Serializable
	@SerialName("CustomCardContainer")
	data class CustomCardContainer(
		@ObjectReference(Page::class) val objectId: String? = null,
		val title: String? = null,
		val icon: String? = null,
		val imageUrl: String? = null
	) : CardContainer()
}
