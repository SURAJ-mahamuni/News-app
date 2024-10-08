package com.study.news.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.study.news.helper.AppEvent
import com.study.news.model.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NewsDetailsViewModel @Inject constructor() : ViewModel() {

    val OPEN_URL = "open_url"

    var _appEvent: MutableLiveData<AppEvent> = MutableLiveData()

    val appEvent: LiveData<AppEvent> get() = _appEvent

    val newsData = MutableLiveData<Article>(Article())


    fun openUrl(){
        _appEvent.postValue(AppEvent.Other(OPEN_URL))
    }

}