package com.livefront.gsonkotlinadapter.util

import com.google.gson.Gson
import com.livefront.gsonkotlinadapter.BooleanData
import com.livefront.gsonkotlinadapter.StringData
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class GsonTests {
    @Test
    fun warmClassCaches() {
        // Should call getAdapter for each class passed in
        val class1 = StringData::class.java
        val class2 = BooleanData::class.java
        val gson = mockk<Gson>()
        every { gson.getAdapter(any<Class<*>>()) } returns mockk()

        gson.warmClassCaches(class1, class2)

        verify {
            gson.getAdapter(class1)
            gson.getAdapter(class2)
        }
    }
}
