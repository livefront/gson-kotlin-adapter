package com.livefront.gsonkotlinadapter.sample.main

import android.os.Bundle
import android.view.LayoutInflater
import com.livefront.gsonkotlinadapter.sample.common.BaseActivity
import com.livefront.gsonkotlinadapter.sample.common.viewModels
import com.livefront.gsonkotlinadapter.sample.main.MainState.RequestButton
import com.livefront.gsonkotlinadapter.sample.util.mapClicks
import com.livefront.sample.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<MainAction, MainState, MainEvent, MainViewModel, ActivityMainBinding>(
    viewModelDelegateProducer = {
        check(this is MainActivity)
        viewModels(viewModelFactory = viewModelFactory)
    },
) {
    @Inject
    lateinit var viewModelFactory: MainViewModel.ViewModelFactory

    override fun onBind(inflater: LayoutInflater): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding
            .networkRequest
            .mapClicks { MainAction.RequestClicked }
            .filterIsResumed()
            .onEach(::sendAction)
            .repeatOnLifecycleCreated()
    }

    override fun handleEvent(event: MainEvent) = Unit

    override fun handleState(state: MainState) {
        binding.networkResponse.text = state.responseLabel
        binding.networkRequest.isEnabled = state.requestButton == RequestButton.READY
    }
}
