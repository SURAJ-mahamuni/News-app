package com.study.news.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.study.news.helper.AppEvent
import com.study.news.R
import com.study.news.api.APIManager
import com.study.news.api.EndPointItems
import com.study.news.api.apiManagerHelper.NetworkResult
import com.study.news.database.dao.SavedNewsDao
import com.study.news.database.dao.SearchHistoryDao
import com.study.news.model.ArticleSaved
import com.study.news.model.SearchHistory
import com.study.news.model.TopTrendingResponce
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val api: APIManager,
    private val searchHistoryDao: SearchHistoryDao,
    private val newsDao: SavedNewsDao
) : ViewModel() {

    var _appEvent: MutableLiveData<AppEvent> = MutableLiveData()

    val appEvent: LiveData<AppEvent> get() = _appEvent

    var searchQuery = MutableLiveData<String>("")

    var searchesponce = MutableLiveData<NetworkResult<TopTrendingResponce>>()

    var uiVisibilityState : Int? = null


    fun search() {
        if (!searchQuery.value?.trim().isNullOrEmpty()) {
            searchesponce.postValue(NetworkResult.Loading())
            viewModelScope.launch(Dispatchers.IO) {
                val result =
                    api.request<TopTrendingResponce>(EndPointItems.Search(searchQuery.value ?: ""))
                searchesponce.postValue(result)
            }
        }
    }

    fun getAllSearchHistory(): List<SearchHistory> {
        return searchHistoryDao.getAllSearchHistory()
    }


    fun addSearchHistory(history: SearchHistory) {
        viewModelScope.launch(Dispatchers.IO) {
            searchHistoryDao.deleteBySearchQuery(history.searchQuery)
            searchHistoryDao.addSearchHistory(history)
        }
    }

    fun savedNews(news: ArticleSaved) {
        viewModelScope.launch(Dispatchers.IO) {
            newsDao.addSavedNews(news)
            _appEvent.postValue(AppEvent.ToastEvent(R.string.news_saved))
        }
    }

    fun deleteNews(news: ArticleSaved) {
        viewModelScope.launch(Dispatchers.IO) {
            newsDao.deleteNews(news.title ?: "")
            _appEvent.postValue(AppEvent.ToastEvent(R.string.news_removed))
        }

    }

    fun getAllNews(): LiveData<List<ArticleSaved>> {
        return newsDao.getAllSavedNews().asLiveData()
    }

}