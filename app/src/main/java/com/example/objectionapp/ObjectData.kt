package com.example.objectionapp

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator


@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("$")
@ContentKey("def")
sealed class Object {
	@Serializable
	@SerialName("Page")
	data class Page(val def: com.example.objectionapp.Page) : Object()

	@Serializable
	@SerialName("Theme")
	data class Theme(val def: com.example.objectionapp.Theme) : Object()

	@Serializable
	@SerialName("Layout")
	data class Layout(val def: com.example.objectionapp.Layout) : Object()
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("$")
sealed class ActionKind {
	@SerialName("danger")
	data object Danger : ActionKind()

	@SerialName("success")
	data object Success : ActionKind()

	@SerialName("normal")
	data object Normal : ActionKind()
}

@Serializable
data class Action(
	@SerialName("kind") val kind: ActionKind,
	@SerialName("title") val title: String,
	@SerialName("icon") val icon: String?,
	@SerialName("process") val process: ActionProcess,
)

@Serializable
data class PerformOperationActionProcess(
	@SerialName("key") val key: String,
)

@Serializable
data class ShowObjectActionProcess(
	@SerialName("id") val id: String,
)

@Serializable
@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("$")
sealed class ActionProcess {
	@Serializable
	@SerialName("perform_operation")
	data class PerformOperation(val def: PerformOperationActionProcess) : ActionProcess()

	@Serializable
	@SerialName("show_object")
	data class ShowObject(val def: ShowObjectActionProcess) : ActionProcess()
}
