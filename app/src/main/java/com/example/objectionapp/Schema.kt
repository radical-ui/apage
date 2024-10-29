package com.example.objectionapp

import androidx.compose.ui.graphics.Color
import kotlin.reflect.KClass
import kotlin.reflect.full.createType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@SerialInfo
@Target(AnnotationTarget.PROPERTY)
annotation class Description(val content: String)

@OptIn(ExperimentalSerializationApi::class)
@SerialInfo
@Target(AnnotationTarget.PROPERTY)
annotation class ObjectReference(val expectedTopLevelVariant: KClass<*>)

@OptIn(ExperimentalSerializationApi::class)
@SerialInfo
@Target(AnnotationTarget.PROPERTY)
annotation class AnyObjectReference

@OptIn(ExperimentalSerializationApi::class)
@SerialInfo
@Target(AnnotationTarget.CLASS)
private annotation class IsColor

@OptIn(ExperimentalSerializationApi::class)
@SerialInfo
@Target(AnnotationTarget.CLASS)
private annotation class IsBinding

@OptIn(ExperimentalSerializationApi::class)
@SerialInfo
@Target(AnnotationTarget.CLASS)
annotation class ContentKey(val key: String)

@Serializable
@IsBinding
data class Binding<T>(val key: String, val child: T)

@Serializable
@IsColor
data class ColorData(val red: Int, val green: Int, val blue: Int, val alpha: Int) {
	fun intoColor(): Color {
		return Color(red = red, green = green, blue = blue, alpha = alpha)
	}
}

class SchemaBuilder {
	private val types: HashMap<String, ItemSchema?> = hashMapOf()

	fun build(klass: KClass<*>): Schema {
		val rootItem = this.getItemSchema(
			Params(
				descriptor = serialDescriptor(klass.createType()),
				annotations = listOf(),
			)
		)

		val goodTypes = hashMapOf<String, ItemSchema>()

		for ((key, value) in types) {
			if (value == null) throw Exception("Found a null type. This is a logical bug in the schema builder")

			goodTypes[key] = value
		}

		return Schema(rootItem, types = goodTypes)
	}

	private data class Params(
		val descriptor: SerialDescriptor,
		val annotations: List<Annotation>,
	)

	@OptIn(ExperimentalSerializationApi::class)
	private fun getItemSchema(params: Params): ItemSchema {
		val schema = when (params.descriptor.kind) {
			StructureKind.CLASS -> getClassSchema(params)
			PrimitiveKind.STRING -> getStringSchema(params)
			PrimitiveKind.DOUBLE -> ItemSchema.NumberSchema
			PrimitiveKind.FLOAT -> ItemSchema.NumberSchema
			PrimitiveKind.INT -> ItemSchema.NumberSchema
			PrimitiveKind.BOOLEAN -> ItemSchema.BooleanSchema
			StructureKind.LIST -> getListSchema(params)
			PolymorphicKind.SEALED -> getSealedSchema(params)
			SerialKind.ENUM -> throw Exception(
				"Use a sealed class with objects instead of an enum. Failed at: ${params.descriptor}"
			)

			else -> throw Exception("unknown item at ${params.descriptor}: ${params.descriptor.kind}")
		}

		return if (params.descriptor.isNullable) ItemSchema.OptionalSchema(child = schema) else schema
	}

	@OptIn(ExperimentalSerializationApi::class)
	private fun getStringSchema(params: Params): ItemSchema {
		for (annotation in params.annotations) {
			when (annotation) {
				is ObjectReference -> return ItemSchema.ReferenceSchema(
					expectedTopLevelVariant = serialDescriptor(
						annotation.expectedTopLevelVariant.createType()
					).serialName
				)

				is AnyObjectReference -> return ItemSchema.ReferenceSchema(expectedTopLevelVariant = null)
			}
		}

		return ItemSchema.StringSchema
	}

	@OptIn(ExperimentalSerializationApi::class)
	private fun getSealedSchema(params: Params): ItemSchema {
		val variants = mutableListOf<EnumVariantSchema>()
		val descriptor = params.descriptor.getElementDescriptor(1)
		var discriminatorKey: String? = null
		var contentKey: String? = null

		for (annotation in params.descriptor.annotations) {
			if (annotation is JsonClassDiscriminator) {
				discriminatorKey = annotation.discriminator
			}
			if (annotation is ContentKey) {
				contentKey = annotation.key
			}
		}

		if (!this.types.containsKey(params.descriptor.serialName)) {
			// we start the reference so if there is a recursive reference, we won't loop infinitely
			this.types[params.descriptor.serialName] = null

			for (childIndex in 0..<descriptor.elementsCount) {
				val variant = descriptor.getElementDescriptor(childIndex)
				val variantAnnotations = descriptor.getElementAnnotations(childIndex)

				variants.add(
					EnumVariantSchema(
						name = variant.serialName,
						description = getDescription(variantAnnotations),
						type = if (variant.kind == StructureKind.OBJECT) null
						else getItemSchema(
							Params(
								descriptor = if (contentKey == null) variant else extractContentKey(
									contentKey, variant
								),
								annotations = if (contentKey == null) listOf() else variant.getElementAnnotations(
									variant.getElementIndex(contentKey)
								),
							)
						)
					)
				)
			}

			if (discriminatorKey == null) {
				throw Exception(
					"All sealed classes must have a JsonDiscriminatorKey annotation. Failed at: $descriptor"
				)
			}

			this.types[params.descriptor.serialName] =
				ItemSchema.EnumSchema(discriminatorKey, contentKey, variants)
		}

		return ItemSchema.Type(params.descriptor.serialName)
	}

	@OptIn(ExperimentalSerializationApi::class)
	private fun extractContentKey(
		contentKey: String, descriptor: SerialDescriptor
	): SerialDescriptor {
		if (descriptor.kind is StructureKind.CLASS) {
			val index = descriptor.getElementIndex(contentKey)
			if (index == CompositeDecoder.UNKNOWN_NAME) throw Exception("Property $contentKey (referenced by @ContentKey) does not exist on ${descriptor.serialName}")

			return descriptor.getElementDescriptor(index)
		}

		throw Exception("Expected the child of a contentKey enum descriptor to be a class")
	}

	@OptIn(ExperimentalSerializationApi::class)
	private fun getListSchema(params: Params): ItemSchema {
		if (params.descriptor.elementsCount != 1) {
			throw Exception("A list must have exactly on child element")
		}

		for (annotation in params.annotations) {
			when (annotation) {
				is ObjectReference -> return ItemSchema.ListSchema(
					item = ItemSchema.ReferenceSchema(
						expectedTopLevelVariant = serialDescriptor(
							annotation.expectedTopLevelVariant.createType()
						).serialName
					)
				)

				is AnyObjectReference -> return ItemSchema.ListSchema(
					item = ItemSchema.ReferenceSchema(
						expectedTopLevelVariant = null
					)
				)
			}
		}

		return ItemSchema.ListSchema(
			item = getItemSchema(
				Params(
					descriptor = params.descriptor.getElementDescriptor(
						0
					),
					annotations = listOf(),
				)
			)
		)
	}

	@OptIn(ExperimentalSerializationApi::class)
	private fun getClassSchema(params: Params): ItemSchema {
		val items = mutableListOf<StructPropertySchema>()

		for (annotation in params.descriptor.annotations) {
			if (annotation is IsColor) return ItemSchema.ColorSchema
			if (annotation is IsBinding) {
				var childDescriptor: SerialDescriptor? = null

				for (childIndex in 0..<params.descriptor.elementsCount) {
					if (params.descriptor.getElementName(childIndex) === "child") {
						childDescriptor = params.descriptor.getElementDescriptor(childIndex)
					}
				}

				if (childDescriptor == null) throw Exception("Binding class did not have a 'child' property")

				return getItemSchema(
					Params(
						descriptor = childDescriptor, annotations = listOf(),
					)
				)
			}
		}

		if (!this.types.containsKey(params.descriptor.serialName)) {
			// like with the enums, we want to protect ourselves from infinite recursion

			this.types[params.descriptor.serialName] = null

			for (childIndex in 0..<params.descriptor.elementsCount) {
				val child = params.descriptor.getElementDescriptor(childIndex)
				val annotations = params.descriptor.getElementAnnotations(childIndex)

				items.add(
					StructPropertySchema(
						name = params.descriptor.getElementName(childIndex),
						type = getItemSchema(
							Params(
								descriptor = child, annotations = annotations,
							)
						),
						description = getDescription(annotations),
					)
				)
			}

			this.types[params.descriptor.serialName] = ItemSchema.StructSchema(properties = items)
		}

		return ItemSchema.Type(params.descriptor.serialName)
	}

	private fun getDescription(annotations: List<Annotation>): String? {
		for (annotation in annotations) {
			if (annotation is Description) {
				return annotation.content
			}
		}

		return null
	}
}

@OptIn(ExperimentalSerializationApi::class)
private fun getTopLevelVariant(klass: KClass<*>): String? {
	val descriptor = serialDescriptor(klass.createType())

	for (annotation in descriptor.annotations) {
		if (annotation is ObjectReference) {
			return serialDescriptor(annotation.expectedTopLevelVariant.createType()).serialName
		}
	}

	return null
}

@Serializable
data class Schema(
	@SerialName("object") val obj: ItemSchema, val types: HashMap<String, ItemSchema>
) {
	val version = "0.1"

	@SerialName("initial_objects")
	val initialObjects = listOf(
		InitialObject(
			id = "theme_default",
			description = "The theme that will be applied by default to all UI elements",
			expectedTopLevelVariant = getTopLevelVariant(Theme::class)
		),
		InitialObject(
			id = "layout_default",
			description = "The layout that will wrap everything",
			expectedTopLevelVariant = getTopLevelVariant(Theme::class)
		),
	)
}

@Serializable
data class InitialObject(
	val id: String,
	val description: String,
	@SerialName("expected_top_level_variant") val expectedTopLevelVariant: String?,
)

@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("$")
@Serializable
sealed class ItemSchema {
	@Serializable
	@SerialName("struct")
	data class StructSchema(val properties: List<StructPropertySchema>) : ItemSchema()

	@Serializable
	@SerialName("enum")
	data class EnumSchema(
		@SerialName("discriminator_key") val discriminatorKey: String,
		@SerialName("content_key") val contentKey: String?,
		val variants: List<EnumVariantSchema>
	) : ItemSchema()

	@Serializable
	@SerialName("string")
	data object StringSchema : ItemSchema()

	@Serializable
	@SerialName("number")
	data object NumberSchema : ItemSchema()

	@Serializable
	@SerialName("boolean")
	data object BooleanSchema : ItemSchema()

	@Serializable
	@SerialName("color")
	data object ColorSchema : ItemSchema()

	@Serializable
	@SerialName("list")
	data class ListSchema(val item: ItemSchema) : ItemSchema() {
		@SerialName("batch_size")
		val batchSize = 50
	}

	@Serializable
	@SerialName("reference")
	data class ReferenceSchema(
		@SerialName("expected_top_level_variant") val expectedTopLevelVariant: String?
	) : ItemSchema()

	@Serializable
	@SerialName("binding")
	data class BindingSchema(val child: ItemSchema) : ItemSchema()

	@Serializable
	@SerialName("optional")
	data class OptionalSchema(val child: ItemSchema) : ItemSchema()

	@Serializable
	@SerialName("type")
	data class Type(val name: String) : ItemSchema()
}

@Serializable
data class StructPropertySchema(
	val name: String,
	val description: String?,
	val type: ItemSchema,
)

@Serializable
data class EnumVariantSchema(val name: String, val description: String?, val type: ItemSchema?)
