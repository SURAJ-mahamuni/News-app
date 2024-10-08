package com.study.news.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.study.news.helper.AppEvent
import com.study.news.database.dao.SavedNewsDao
import com.study.news.model.ArticleSaved
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SavedViewModel @Inject constructor(private val newsDao: SavedNewsDao) : ViewModel() {

    var _appEvent: MutableLiveData<AppEvent> = MutableLiveData()

    val appEvent: LiveData<AppEvent> get() = _appEvent

    fun getAllSavedNews(): LiveData<List<ArticleSaved>> {
        return newsDao.getAllSavedNews().asLiveData()
    }

}