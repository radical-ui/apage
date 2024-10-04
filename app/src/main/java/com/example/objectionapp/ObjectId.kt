package com.example.objectionapp

import android.os.Bundle
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

fun getObjectIdPageRouteTemplate(): String {
	return "page/{encodedObjectId}"
}

fun getObjectIdDialogRouteTemplate(): String {
	return "dialog/{encodedObjectId}"
}

fun getObjectIdSheetRouteTemplate(): String {
	return "sheet/{encodedObjectId}"
}

@OptIn(ExperimentalEncodingApi::class)
fun encodeObjectIdIntoPageRoute(objectId: String): String {
	val encodedId = Base64.UrlSafe.encode(objectId.toByteArray())

	return "page/${encodedId}"
}

@OptIn(ExperimentalEncodingApi::class)
fun encodeObjectIdIntoDialogRoute(objectId: String): String {
	val encodedId = Base64.UrlSafe.encode(objectId.toByteArray())

	return "dialog/${encodedId}"
}

@OptIn(ExperimentalEncodingApi::class)
fun encodeObjectIdIntoSheetRoute(objectId: String): String {
	val encodedId = Base64.UrlSafe.encode(objectId.toByteArray())

	return "sheet/${encodedId}"
}

@OptIn(ExperimentalEncodingApi::class)
fun decodeObjectIdFromRouteArgs(args: Bundle?): String {
	val encodedId = args?.getString("encodedObjectId") ?: throw Exception("Invalid bundle")

	return String(bytes = Base64.UrlSafe.decode(encodedId))
}
