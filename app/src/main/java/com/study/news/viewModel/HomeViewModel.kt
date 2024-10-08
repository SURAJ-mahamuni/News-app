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
import com.study.news.model.ArticleSaved
import com.study.news.model.TopTrendingResponce
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: APIManager,
    private val newsDao: SavedNewsDao,
) : ViewModel() {

    var _appEvent: MutableLiveData<AppEvent> = MutableLiveData()

    val appEvent: LiveData<AppEvent> get() = _appEvent

    var topTrendingResponce = MutableLiveData<NetworkResult<TopTrendingResponce>>()

    var selectedTab = R.string.trending


    fun topTrendingNews(newsType: Int) {
        topTrendingResponce.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            when (newsType) {
                R.string.trending -> {
                    val result = api.request<TopTrendingResponce>(EndPointItems.TopHeadLines)
                    topTrendingResponce.postValue(result)
                }

                R.string.health -> {
                    val result = api.request<TopTrendingResponce>(EndPointItems.Health)
                    topTrendingResponce.postValue(result)
                }

                R.string.sports -> {
                    val result = api.request<TopTrendingResponce>(EndPointItems.Sports)
                    topTrendingResponce.postValue(result)
                }

                R.string.finance -> {
                    val result = api.request<TopTrendingResponce>(EndPointItems.Finance)
                    topTrendingResponce.postValue(result)
                }
            }
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