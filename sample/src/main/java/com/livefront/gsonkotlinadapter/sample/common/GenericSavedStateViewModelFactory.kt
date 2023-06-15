package com.livefront.gsonkotlinadapter.sample.common

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner

/**
 * A generic [AbstractSavedStateViewModelFactory] for creating a subclass of a [BaseViewModel],
 * creating one with the [viewModelFactory] by passing it a [SavedStateHandle].
 *
 * This class should be considered private, and only used via one of the extension methods defined
 * in this file.
 */
class GenericSavedStateViewModelFactory<VM : ViewModel>(
    savedStateRegistryOwner: SavedStateRegistryOwner,
    defaultBundle: Bundle?,
    private val viewModelFactory: BaseViewModel.Factory<VM>,
) : AbstractSavedStateViewModelFactory(savedStateRegistryOwner, defaultBundle) {
    /**
     * Creates the [ViewModel] using [viewModelFactory]. Type-safety is guaranteed by the inline
     * extension methods below.
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle,
    ): T = viewModelFactory.create(handle) as T
}

/**
 * [ComponentActivity.viewModels] for a [BaseViewModel] [VM] given a [BaseViewModel.Factory].
 */
inline fun <reified VM : ViewModel> ComponentActivity.viewModels(
    viewModelFactory: BaseViewModel.Factory<VM>,
    savedStateRegistryOwner: SavedStateRegistryOwner = this,
    defaultBundle: Bundle? = intent.extras,
): Lazy<VM> = viewModels {
    GenericSavedStateViewModelFactory(
        savedStateRegistryOwner = savedStateRegistryOwner,
        defaultBundle = defaultBundle,
        viewModelFactory = viewModelFactory,
    )
}
