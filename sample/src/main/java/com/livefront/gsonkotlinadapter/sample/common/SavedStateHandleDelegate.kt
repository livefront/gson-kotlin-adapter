package com.livefront.gsonkotlinadapter.sample.common

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Returns a delegate provider for a [MutableStateFlow] of type [T] with the given [defaultValue].
 *
 * The key used to store values is taken to be the property name that is using this delegate.
 *
 * The passed [scope] is used to listen to state updates to persist them in the [SavedStateHandle].
 */
fun <T : Any> SavedStateHandle.mutableStateFlow(
    scope: CoroutineScope,
    defaultValue: T,
): PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, MutableStateFlow<T>>> = MutableStateFlowSavedEntryProvider(
    scope = scope,
    savedStateHandle = this,
    defaultValue = defaultValue,
)

/**
 * A delegate provider for a [ReadOnlyProperty] of a [MutableStateFlow] of type [T] that is backed
 * by [SavedStateHandle] and has a default value of [defaultValue].
 */
private class MutableStateFlowSavedEntryProvider<T>(
    private val scope: CoroutineScope,
    private val savedStateHandle: SavedStateHandle,
    private val defaultValue: T,
) : PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, MutableStateFlow<T>>> {
    override fun provideDelegate(
        thisRef: Any?,
        property: KProperty<*>,
    ): ReadOnlyProperty<Any?, MutableStateFlow<T>> = MutableStateFlowSavedEntry(
        scope = scope,
        savedStateHandle = savedStateHandle,
        key = property.name,
        defaultValue = defaultValue,
    )
}

/**
 * A read only property for a [MutableStateFlow] of type [T] that is backed by a [SavedStateHandle]
 * with the given default value and key for saving in the [SavedStateHandle].
 */
private class MutableStateFlowSavedEntry<T>(
    scope: CoroutineScope,
    savedStateHandle: SavedStateHandle,
    key: String,
    defaultValue: T,
) : ReadOnlyProperty<Any?, MutableStateFlow<T>> {
    private val saveableMutableSaveStateFlow = SaveableMutableSaveStateFlow(
        scope = scope,
        savedStateHandle = savedStateHandle,
        key = key,
        mutableStateFlow = MutableStateFlow(savedStateHandle.get<T>(key) ?: defaultValue),
    )

    override fun getValue(
        thisRef: Any?,
        property: KProperty<*>,
    ): MutableStateFlow<T> = saveableMutableSaveStateFlow
}

/**
 * A [MutableStateFlow] that is backed by a [SavedStateHandle].
 */
private class SaveableMutableSaveStateFlow<T>(
    scope: CoroutineScope,
    savedStateHandle: SavedStateHandle,
    key: String,
    mutableStateFlow: MutableStateFlow<T>,
) : MutableStateFlow<T> by mutableStateFlow {
    init {
        // Anytime the mutable state is updated, we update the savedStateHandle.
        onEach { savedStateHandle[key] = it }.launchIn(scope)
    }
}
