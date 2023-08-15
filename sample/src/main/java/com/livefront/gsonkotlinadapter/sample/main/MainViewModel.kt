package com.livefront.gsonkotlinadapter.sample.main

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.livefront.gsonkotlinadapter.sample.common.BaseViewModel
import com.livefront.gsonkotlinadapter.sample.main.MainState.RequestButton
import com.livefront.gsonkotlinadapter.sample.network.internal.model.NetworkResult.Failure
import com.livefront.gsonkotlinadapter.sample.network.internal.model.NetworkResult.Success
import com.livefront.gsonkotlinadapter.sample.network.repo.RandomUserRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

class MainViewModel private constructor(
    savedStateHandle: SavedStateHandle,
    private val randomUserRepo: RandomUserRepo,
) : BaseViewModel<MainAction, MainState, MainEvent>(
    defaultState = MainState.DEFAULT,
    savedStateHandle = savedStateHandle,
) {
    private val mutableInternalState: MutableStateFlow<InternalState> by savedStateHandle.mutableStateFlow(
        InternalState(
            isLoading = false,
            value = null,
        )
    )

    init {
        mutableInternalState
            .map {
                // A transformer should be used here to turn the internal raw state into the
                // total UI state.
                MainState(
                    requestButton = if (it.isLoading) {
                        RequestButton.LOADING
                    } else {
                        RequestButton.READY
                    },
                    responseLabel = it.value,
                )
            }
            .onEach { mutableStateFlow.value = it }
            .launchIn(viewModelScope)
    }

    override fun handleAction(action: MainAction) {
        when (action) {
            MainAction.RequestClicked -> handleRequestClicked()
            is MainAction.RequestCompleted -> handleRequestComplete(action)
        }
    }

    /**
     * Make the request.
     */
    private fun handleRequestClicked() {
        mutableInternalState.value = mutableInternalState.value.copy(isLoading = true)
        viewModelScope.launch {
            val action = when (val user = randomUserRepo.getRandomUsers()) {
                is Failure -> MainAction.RequestCompleted(user.throwable.message ?: "Error")
                is Success -> MainAction.RequestCompleted(user.value.toString())
            }
            sendAction(action)
        }
    }

    /**
     * Updates the internal state based on the response.
     */
    private fun handleRequestComplete(action: MainAction.RequestCompleted) {
        mutableInternalState.value = mutableInternalState.value.copy(
            isLoading = false,
            value = action.responseValue
        )
    }

    @Parcelize
    data class InternalState(
        val isLoading: Boolean,
        val value: String?,
    ) : Parcelable

    /**
     * An factory for creating [MainViewModel] instances.
     */
    class ViewModelFactory @Inject constructor(
        private val randomUserRepo: RandomUserRepo,
    ) : Factory<MainViewModel> {
        override fun create(
            handle: SavedStateHandle,
        ): MainViewModel = MainViewModel(
            savedStateHandle = handle,
            randomUserRepo = randomUserRepo,
        )
    }
}

sealed class MainAction {
    object RequestClicked : MainAction()

    data class RequestCompleted(
        val responseValue: String,
    ) : MainAction()
}

data class MainState(
    val requestButton: RequestButton,
    val responseLabel: String?,
) {
    companion object {
        val DEFAULT: MainState = MainState(
            requestButton = RequestButton.READY,
            responseLabel = null,
        )
    }

    enum class RequestButton {
        LOADING,
        READY,
    }
}

sealed class MainEvent
