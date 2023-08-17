package com.livefront.gsonkotlinadapter

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory
import com.livefront.gsonkotlinadapter.util.fromJson
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class KotlinReflectiveTypeAdapterFactoryTests {
    private val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapterFactory(KotlinReflectiveTypeAdapterFactory.create(false))
        .create()

    private val gsonWithDefaultPrimitiveValuesEnabled: Gson = GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapterFactory(KotlinReflectiveTypeAdapterFactory.create(true))
        .create()

    @BeforeEach
    fun setup() {
        mockkConstructor(ReflectiveTypeAdapterFactory.Adapter::class)
    }

    @AfterEach
    fun teardown() {
        verify(exactly = 0) {
            anyConstructed<ReflectiveTypeAdapterFactory.Adapter<*, *>>().read(any())
        }
        unmockkAll()
    }

    @Test
    fun fromJson_singletonData() {
        // Should deserialize the JSON to a SingletonData object and make sure it
        // matches the pre-constructed data.
        val expectedData = SingletonData
        val actualData = gson.fromJson<SingletonData>("""{}""")
        assertEquals(expectedData, actualData)
    }

    @Test
    fun toJson_singletonData() {
        // Should serialize the SingletonData object to JSON and make sure it
        // matches the pre-defined data.
        val expectedJson = """{}"""
        val actualJson = gson.toJson(SingletonData)
        assertEquals(expectedJson, actualJson)
    }

    @Test
    fun fromJson_booleanData() {
        // Should deserialize the JSON to a BooleanData object and make sure it
        // matches the pre-constructed data.
        val expectedData = booleanDataObject()
        val actualData = gson.fromJson<BooleanData>(FROM_JSON_BOOLEAN_DATA_JSON)
        assertEquals(expectedData, actualData)
    }

    @Test
    fun toJson_booleanData() {
        // Should serialize the BooleanData object to JSON and make sure it
        // matches the pre-defined data.
        val expectedJson = TO_JSON_BOOLEAN_DATA_JSON
        val actualJson = gson.toJson(booleanDataObject())
        assertEquals(expectedJson, actualJson)
    }

    @Test
    fun fromJson_defaultPrimitiveData_primitiveValuesDisabled() {
        // Should attempt to deserialize the JSON to a DefaultPrimitiveData object and fail.
        assertThrows(IllegalArgumentException::class.java) {
            gson.fromJson<DefaultPrimitiveData>(FROM_JSON_DEFAULT_PRIMITIVE_DATA_JSON)
        }
    }

    @Test
    fun fromJson_defaultPrimitiveData_primitiveValuesEnabled() {
        // Should deserialize the JSON to a DefaultPrimitiveData object and make sure it matches
        // the pre-constructed data and verify that default values were used.
        val expectedData = fromJsonDefaultPrimitiveDataObject()
        val actualData = gsonWithDefaultPrimitiveValuesEnabled.fromJson<DefaultPrimitiveData>(
            FROM_JSON_DEFAULT_PRIMITIVE_DATA_JSON
        )
        assertEquals(expectedData, actualData)
    }

    @Test
    fun fromJson_doubleData() {
        // Should deserialize the JSON to a DoubleData object and make sure it
        // matches the pre-constructed data.
        val expectedData = doubleDataObject()
        val actualData = gson.fromJson<DoubleData>(FROM_JSON_DOUBLE_DATA_JSON)
        assertEquals(expectedData, actualData)
    }

    @Test
    fun toJson_doubleData() {
        // Should serialize the DoubleData object to JSON and make sure it
        // matches the pre-defined data.
        val expectedJson = TO_JSON_DOUBLE_DATA_JSON
        val actualJson = gson.toJson(doubleDataObject())
        assertEquals(expectedJson, actualJson)
    }

    @Test
    fun fromJson_genericData() {
        // Should deserialize the JSON to a GenericData object and make sure it
        // matches the pre-constructed data.
        val expectedData = genericDataObject()
        val actualData = gson.fromJson<GenericData<BooleanData>>(FROM_JSON_GENERIC_DATA_JSON)
        assertEquals(expectedData, actualData)
    }

    @Test
    fun toJson_genericData() {
        // Should serialize the GenericData object to JSON and make sure it
        // matches the pre-defined data.
        val expectedJson = TO_JSON_GENERIC_DATA_JSON
        val actualJson = gson.toJson(genericDataObject())
        assertEquals(expectedJson, actualJson)
    }

    @Test
    fun fromJson_intData() {
        // Should deserialize the JSON to a IntData object and make sure it
        // matches the pre-constructed data.
        val expectedData = intDataObject()
        val actualData = gson.fromJson<IntData>(FROM_JSON_INT_DATA_JSON)
        assertEquals(expectedData, actualData)
    }

    @Test
    fun toJson_intData() {
        // Should serialize the IntData object to JSON and make sure it
        // matches the pre-defined data.
        val expectedJson = TO_JSON_INT_DATA_JSON
        val actualJson = gson.toJson(intDataObject())
        assertEquals(expectedJson, actualJson)
    }

    @Test
    fun fromJson_longData() {
        // Should deserialize the JSON to a LongData object and make sure it
        // matches the pre-constructed data.
        val expectedData = longDataObject()
        val actualData = gson.fromJson<LongData>(FROM_JSON_LONG_DATA_JSON)
        assertEquals(expectedData, actualData)
    }

    @Test
    fun toJson_longData() {
        // Should serialize the LongData object to JSON and make sure it
        // matches the pre-defined data.
        val expectedJson = TO_JSON_LONG_DATA_JSON
        val actualJson = gson.toJson(longDataObject())
        assertEquals(expectedJson, actualJson)
    }

    @Test
    fun fromJson_nonConstructorData() {
        // Should deserialize the JSON to a NonConstructorData object and make sure it matches
        // the pre-constructed data. This should ignore the non-constructor data in the JSON.
        val expectedData = nonConstructorDataObject()
        val actualData = gson.fromJson<NonConstructorData>(FROM_JSON_NON_CONSTRUCTOR_DATA_OBJECT)
        assertEquals(expectedData, actualData)
    }

    @Test
    fun toJson_nonConstructorData() {
        // Should serialize a NonConstructorData object to JSON and make sure it
        // matches the pre-constructed data. This should not ignore the non-constructor data.
        val expectedData = nonConstructorDataObject()
        val actualData = gson.fromJson<NonConstructorData>(TO_JSON_NON_CONSTRUCTOR_DATA_OBJECT)
        assertEquals(expectedData, actualData)
    }

    @Test
    fun fromJson_objectData() {
        // Should deserialize the JSON to a ObjectData object and make sure it
        // matches the pre-constructed data.
        val expectedData = objectDataObject()
        val actualData = gson.fromJson<ObjectData>(FROM_JSON_OBJECT_DATA_JSON)
        assertEquals(expectedData, actualData)
    }

    @Test
    fun toJson_objectData() {
        // Should serialize the NonConstructorData object to JSON and make sure it
        // matches the pre-defined data.
        val expectedJson = TO_JSON_OBJECT_DATA_JSON
        val actualJson = gson.toJson(objectDataObject())
        assertEquals(expectedJson, actualJson)
    }

    @Test
    fun fromJson_serializableNameAlternateData_duplicateValue() {
        // Should throw an IllegalArgumentException caused by JSON having multiple values for the
        // same parameter when deserializing the JSON to a SerializableNameAlternateData object
        assertThrows(IllegalArgumentException::class.java) {
            gson.fromJson<SerializableNameAlternateData>(
                SERIALIZABLE_NAME_ALTERNATE_WITH_DUPLICATE_VALUE_DATA_JSON
            )
        }
    }

    @Test
    fun fromJson_serializableNameAlternateData() {
        // Should deserialize the JSON to a SerializableNameAlternateData object and make sure it
        // matches the pre-constructed data.
        val expectedData = serializableNameAlternateDataObject()
        val actualData = gson.fromJson<SerializableNameAlternateData>(
            SERIALIZABLE_NAME_ALTERNATE_DATA_JSON
        )
        assertEquals(expectedData, actualData)
    }

    @Test
    fun fromJson_serializableNameData() {
        // Should deserialize the JSON to a SerializableNameData object and make sure it
        // matches the pre-constructed data.
        val expectedData = serializableNameDataObject()
        val actualData = gson.fromJson<SerializableNameData>(SERIALIZABLE_NAME_DATA_JSON)
        assertEquals(expectedData, actualData)
    }

    @Test
    fun fromJson_stringData() {
        // Should deserialize the JSON to a StringData object and make sure it
        // matches the pre-constructed data.
        val expectedData = stringDataObject()
        val actualData = gson.fromJson<StringData>(FROM_JSON_STRING_DATA_JSON)
        assertEquals(expectedData, actualData)
    }

    @Test
    fun toJson_stringData() {
        // Should serialize StringData object to JSON and make sure it
        // matches the pre-constructed data.
        val expectedData = TO_JSON_STRING_DATA_JSON
        val actualData = gson.toJson(stringDataObject())
        assertEquals(expectedData, actualData)
    }

    @Test
    fun fromJson_stringData_invalidTransient() {
        // Should attempt to deserialize the JSON to a InvalidTransientStringData object and fail
        assertThrows(IllegalArgumentException::class.java) {
            gson.fromJson<InvalidTransientStringData>(FROM_JSON_TRANSIENT_DATA_JSON)
        }
    }

    @Test
    fun toJson_stringData_invalidTransient() {
        // Should serialize InvalidTransientStringData object to JSON and make sure it
        // matches the pre-constructed data.
        val expectedData = TO_JSON_TRANSIENT_DATA_JSON
        val actualData = gson.toJson(toJsonInvalidTransientStringDataObject())
        assertEquals(expectedData, actualData)
    }

    @Test
    fun fromJson_stringData_transient() {
        // Should deserialize the JSON to a TransientStringData object and make sure it
        // matches the pre-constructed data with the default transient values.
        val expectedData = fromJsonTransientStringDataObject()
        val actualData = gson.fromJson<TransientStringData>(FROM_JSON_TRANSIENT_DATA_JSON)
        assertEquals(expectedData, actualData)
    }

    @Test
    fun toJson_stringData_transient() {
        // Should serialize TransientStringData object to JSON and make sure it
        // matches the pre-constructed data.
        val expectedData = TO_JSON_TRANSIENT_DATA_JSON
        val actualData = gson.toJson(toJsonTransientStringDataObject())
        assertEquals(expectedData, actualData)
    }

    @Test
    fun fromJson_stringData_withDefaults() {
        // Should deserialize the JSON to a StringDataWithDefaults object and make sure it
        // matches the pre-constructed data and verify that default values were used.
        val expectedData = stringDataWithDefaultsObject()
        val actualData = gson.fromJson<StringDataWithDefaults>(STRING_DATA_WITH_DEFAULTS_JSON)
        assertEquals(expectedData, actualData)
    }

    @Test
    fun fromJson_stringData_withDelegate() {
        // Should deserialize the JSON to a StringDataWithDelegate object and make sure it
        // matches the pre-constructed data and verify that the delegate is instantiated.
        val expectedData = stringDataWithDelegateObject()
        val actualData = gson.fromJson<StringDataWithDelegate>(STRING_DATA_WITH_DELEGATE_JSON)
        assertEquals(expectedData, actualData)
        assertNotNull(actualData.lazyProperty)
    }
}
