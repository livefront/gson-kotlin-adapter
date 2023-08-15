package com.livefront.gsonkotlinadapter.sample.common

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * The base [ComponentActivity] implementation.
 */
abstract class BaseActivity<A, S, E, VM : BaseViewModel<A, S, E>, VB : ViewBinding>(
    viewModelDelegateProducer: ComponentActivity.() -> Lazy<VM>,
) : AppCompatActivity() {
    /**
     * The [VM] that contains all of the logic for this screen.
     */
    private val viewModel: VM by lazy { viewModelDelegateProducer.invoke(this).value }

    /**
     * The [VB] that this activity has bound.
     */
    protected lateinit var binding: VB

    /**
     * Creates the [VB] for this screen.
     */
    abstract fun onBind(inflater: LayoutInflater): VB

    /**
     * The function to handle all events.
     */
    abstract fun handleEvent(event: E)

    /**
     * The suspend function to handle all values in the [state] object.
     */
    abstract fun handleState(state: S)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = onBind(layoutInflater)
        setContentView(binding.root)
        viewModel.stateFlow.onEach(::handleState).repeatOnLifecycleCreated()
        viewModel.eventFlow.onEach(::handleEvent).repeatOnLifecycleResumed()
    }

    /**
     * Sends an action.
     */
    protected suspend fun sendAction(action: A) {
        viewModel.actionChannel.send(action)
    }

    /**
     * Sends an action with the provided [CoroutineScope].
     */
    protected fun sendActionWithScope(action: A, coroutineScope: CoroutineScope = lifecycleScope) {
        coroutineScope.launch { sendAction(action) }
    }

    /**
     * A terminal flow operator that launches the collection with the Fragments lifecycle scope with
     * [Lifecycle.repeatOnLifecycle] for [State.CREATED].
     */
    protected fun <T> Flow<T>.repeatOnLifecycleCreated() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(State.CREATED) { collect() }
        }
    }

    /**
     * A terminal flow operator that launches the collection with the Fragments lifecycle scope with
     * [Lifecycle.repeatOnLifecycle] for [State.RESUMED].
     */
    protected fun <T> Flow<T>.repeatOnLifecycleResumed() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(State.RESUMED) { collect() }
        }
    }

    /**
     * Filters the given [Flow] if the lifecycle is at least [State.RESUMED].
     *
     * This is mainly used to filter click events.
     */
    protected fun <T> Flow<T>.filterIsResumed(): Flow<T> = filter {
        lifecycle.currentState >= State.RESUMED
    }
}
