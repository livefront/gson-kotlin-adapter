package com.livefront.gsonkotlinadapter.sample.common

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

/**
 * A base [ViewModel]. The logic contained in here should be minimal, as all logic should be handled by the actor
 * created from the passed in actor factory.
 */
abstract class BaseViewModel<A, S, E>(
    defaultState: S,
    protected val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val internalActionChannel: Channel<A> = Channel(capacity = Channel.RENDEZVOUS)
    private val internalEventChannel: Channel<E> = Channel(capacity = Channel.UNLIMITED)
    protected val mutableStateFlow: MutableStateFlow<S> = MutableStateFlow(defaultState)

    /**
     * A [SendChannel] for sending actions into the view model.
     */
    val actionChannel: SendChannel<A> = internalActionChannel

    /**
     * The [Flow] of events emitted by the view model.
     */
    val eventFlow: Flow<E> = internalEventChannel.receiveAsFlow()

    /**
     * The [StateFlow] of states emitted by the view model.
     */
    val stateFlow: StateFlow<S> = mutableStateFlow.asStateFlow()

    init {
        internalActionChannel
            .receiveAsFlow()
            .onEach(::handleAction)
            .launchIn(viewModelScope)
    }

    /**
     * Returns a delegate provider for a [MutableStateFlow] of type [T] with the given [defaultValue].
     *
     * The key used to store values is taken to be the property name that is using this delegate.
     */
    protected fun <T : Any> SavedStateHandle.mutableStateFlow(
        defaultValue: T,
    ): PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, MutableStateFlow<T>>> =
        mutableStateFlow(
            scope = viewModelScope,
            defaultValue = defaultValue,
        )

    /**
     * Helper method for sending an internal action.
     */
    protected suspend fun sendAction(action: A) {
        actionChannel.send(action)
    }

    /**
     * Sends an internal action with the provided [CoroutineScope].
     */
    protected fun sendActionWithScope(
        coroutineScope: CoroutineScope = viewModelScope,
        action: suspend () -> A,
    ): Job = coroutineScope.launch { sendAction(action()) }

    /**
     * Sends an event.
     */
    protected fun sendEvent(event: E) {
        viewModelScope.launch { internalEventChannel.send(event) }
    }

    /**
     * Handles the [action] in a non-suspending manner.
     */
    protected abstract fun handleAction(action: A)

    /**
     * A factory for creating a [ViewModel] subclass of type [VM].
     */
    interface Factory<VM : ViewModel> {

        /**
         * Creates a [VM] with the given [SavedStateHandle].
         */
        fun create(handle: SavedStateHandle): VM
    }
}
