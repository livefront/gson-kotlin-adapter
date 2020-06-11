package com.livefront.gsonkotlinadapter

val FROM_JSON_BOOLEAN_DATA_JSON: String = """
        {
          "nonnullBoolean": true,
          "nullableBoolean1": false,
          "nullableBoolean2": null
        }
        """.trimIndent()

val TO_JSON_BOOLEAN_DATA_JSON: String = """
        {
          "nonnullBoolean": true,
          "nullableBoolean1": false
        }
        """.trimIndent()

fun booleanDataObject() = BooleanData(
    nonnullBoolean = true,
    nullableBoolean1 = false,
    nullableBoolean2 = null,
    missingBoolean = null
)

const val FROM_JSON_DOUBLE_DATA_JSON: String = """
        {
          "nonnullDouble": 1.0,
          "nullableDouble1": 2.0,
          "nullableDouble2": null
        }
        """

val TO_JSON_DOUBLE_DATA_JSON: String = """
        {
          "nonnullDouble": 1.0,
          "nullableDouble1": 2.0
        }
        """.trimIndent()

fun doubleDataObject() = DoubleData(
    nonnullDouble = 1.0,
    nullableDouble1 = 2.0,
    nullableDouble2 = null,
    missingDouble = null
)

val FROM_JSON_GENERIC_DATA_JSON: String = """
        {
          "value": {
            "nonnullBoolean": true,
            "nullableBoolean1": false
          }
        }
        """.trimIndent()

val TO_JSON_GENERIC_DATA_JSON: String = """
        {
          "value": {
            "nonnullBoolean": true,
            "nullableBoolean1": false
          }
        }
        """.trimIndent()

fun genericDataObject(): GenericData<BooleanData> = GenericData(
    value = BooleanData(
        nonnullBoolean = true,
        nullableBoolean1 = false,
        nullableBoolean2 = null,
        missingBoolean = null
    )
)

val FROM_JSON_INT_DATA_JSON: String = """
        {
          "nonnullInt": 1,
          "nullableInt1": 2,
          "nullableInt2": null
        }
        """.trimIndent()

val TO_JSON_INT_DATA_JSON: String = """
        {
          "nonnullInt": 1,
          "nullableInt1": 2
        }
        """.trimIndent()

fun intDataObject() = IntData(
    nonnullInt = 1,
    nullableInt1 = 2,
    nullableInt2 = null,
    missingInt = null
)

val FROM_JSON_LONG_DATA_JSON: String = """
        {
          "nonnullLong": 1,
          "nullableLong1": 2,
          "nullableLong2": null
        }
        """.trimIndent()

val TO_JSON_LONG_DATA_JSON: String = """
        {
          "nonnullLong": 1,
          "nullableLong1": 2
        }
        """.trimIndent()

fun longDataObject() = LongData(
    nonnullLong = 1L,
    nullableLong1 = 2L,
    nullableLong2 = null,
    missingLong = null
)

val FROM_JSON_NON_CONSTRUCTOR_DATA_OBJECT = """
        {
          "valueA": true,
          "valueB": true,
          "valueC": true
        }
        """.trimIndent()

val TO_JSON_NON_CONSTRUCTOR_DATA_OBJECT = """
        {
          "valueA": true,
          "valueB": false
        }
        """.trimIndent()

fun nonConstructorDataObject() = NonConstructorData(
    true
)

val FROM_JSON_OBJECT_DATA_JSON: String = """
        {
          "nonnullObject": {
            "nonnullString": "foo",
            "nullableString1": "bar",
            "nullableString2": null
          },
          "nullableObject1": {
            "nonnullString": "foo",
            "nullableString1": "bar",
            "nullableString2": null
          },
          "nullableObject2": null
        }
        """.trimIndent()

val TO_JSON_OBJECT_DATA_JSON: String = """
        {
          "nonnullObject": {
            "nonnullString": "foo",
            "nullableString1": "bar"
          },
          "nullableObject1": {
            "nonnullString": "foo",
            "nullableString1": "bar"
          }
        }
        """.trimIndent()

fun objectDataObject() = ObjectData(
    nonnullObject = StringData(
        nonnullString = "foo",
        nullableString1 = "bar",
        nullableString2 = null,
        missingString = null
    ),
    nullableObject1 = StringData(
        nonnullString = "foo",
        nullableString1 = "bar",
        nullableString2 = null,
        missingString = null
    ),
    nullableObject2 = null,
    missingObject = null
)

val SERIALIZABLE_NAME_ALTERNATE_DATA_JSON: String = """
        {
          "nonnullString": "value",
          "foo": "value",
          "nullableString2": null
        }
        """.trimIndent()

fun serializableNameAlternateDataObject() = SerializableNameAlternateData(
    nonnullString = "value",
    nullableString1 = "value",
    nullableString2 = null,
    missingString = null
)

val SERIALIZABLE_NAME_ALTERNATE_WITH_DUPLICATE_VALUE_DATA_JSON: String = """
        {
          "nonnullString": "value",
          "foo": "value",
          "bar": "value",
          "nullableString2": null
        }
        """.trimIndent()

val SERIALIZABLE_NAME_DATA_JSON: String = """
        {
          "nonnullString": "value",
          "bar": "value",
          "nullableString2": null
        }
        """.trimIndent()

fun serializableNameDataObject() = SerializableNameData(
    nonnullString = "value",
    nullableString1 = "value",
    nullableString2 = null,
    missingString = null
)

val FROM_JSON_STRING_DATA_JSON: String = """
        {
          "nonnullString": "foo",
          "nullableString1": "bar",
          "nullableString2": null
        }
        """.trimIndent()

val TO_JSON_STRING_DATA_JSON: String = """
        {
          "nonnullString": "foo",
          "nullableString1": "bar"
        }
        """.trimIndent()

fun stringDataObject() = StringData(
    nonnullString = "foo",
    nullableString1 = "bar",
    nullableString2 = null,
    missingString = null
)

val STRING_DATA_WITH_DEFAULTS_JSON: String = """
        { }
        """.trimIndent()

fun stringDataWithDefaultsObject() = StringDataWithDefaults()

val STRING_DATA_WITH_DELEGATE_JSON: String = """
        {
          "nonnullString": "foo",
          "nullString": null
        }
        """.trimIndent()

fun stringDataWithDelegateObject() = StringDataWithDelegate(
    nonnullString = "foo",
    nullString = null
)

val FROM_JSON_TRANSIENT_DATA_JSON: String = """
        {
          "transientString1": "foo",
          "string": "bar"
        }
        """.trimIndent()

fun fromJsonTransientStringDataObject() = TransientStringData(
    string = "bar"
)

val TO_JSON_TRANSIENT_DATA_JSON: String = """
        {
          "string": "bar"
        }
        """.trimIndent()

fun toJsonTransientStringDataObject() = TransientStringData(
    transientString1 = "foo",
    transientString2 = "foo",
    string = "bar"
)

fun toJsonInvalidTransientStringDataObject() = InvalidTransientStringData(
    transientString1 = "foo",
    transientString2 = "foo",
    string = "bar"
)
