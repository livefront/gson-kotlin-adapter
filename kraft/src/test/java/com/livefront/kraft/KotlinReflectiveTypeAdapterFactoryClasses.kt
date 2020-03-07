package com.livefront.kraft

import com.google.gson.annotations.SerializedName

data class BooleanData(
    val nonnullBoolean: Boolean,
    val nullableBoolean1: Boolean?,
    val nullableBoolean2: Boolean?,
    val missingBoolean: Boolean?
)

data class DoubleData(
    val nonnullDouble: Double,
    val nullableDouble1: Double?,
    val nullableDouble2: Double?,
    val missingDouble: Double?
)

data class IntData(
    val nonnullInt: Int,
    val nullableInt1: Int?,
    val nullableInt2: Int?,
    val missingInt: Int?
)

data class LongData(
    val nonnullLong: Long,
    val nullableLong1: Long?,
    val nullableLong2: Long?,
    val missingLong: Long?
)

data class NonConstructorData(
    val valueA: Boolean
) {
    val valueB: Boolean = false
    val valueC: Boolean = !valueA
}

data class ObjectData(
    val nonnullObject: StringData,
    val nullableObject1: StringData?,
    val nullableObject2: StringData?,
    val missingObject: StringData?
)

data class SerializableNameAlternateData(
    val nonnullString: String,
    @SerializedName("bar", alternate = ["foo"]) val nullableString1: String?,
    val nullableString2: String?,
    @SerializedName("gone") val missingString: String?
)

data class SerializableNameData(
    val nonnullString: String,
    @SerializedName("bar") val nullableString1: String?,
    val nullableString2: String?,
    @SerializedName("gone") val missingString: String?
)

data class StringData(
    val nonnullString: String,
    val nullableString1: String?,
    val nullableString2: String?,
    val missingString: String?
)

data class StringDataWithDefaults(
    val nonnullString: String = "foo",
    val nullableString: String? = "bar"
)

data class StringDataWithDelegate(
    val nonnullString: String,
    val nullString: String?
) {
    val lazyProperty: String by lazy { nullString ?: nonnullString }
}

data class TransientStringData(
    @Transient val transientString: String = "foobar",
    val string: String
)
