package com.study.news.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.study.news.R
import com.study.news.adapters.GenericAdapter
import com.study.news.api.apiManagerHelper.NetworkResult
import com.study.news.api.apiManagerHelper.ResponseMessageManager
import com.study.news.databinding.FragmentSearchBinding
import com.study.news.databinding.NewsCardItemBinding
import com.study.news.databinding.SearchHistoryBinding
import com.study.news.helper.Convertor
import com.study.news.helper.getRandomColor
import com.study.news.helper.hideView
import com.study.news.helper.setupUI
import com.study.news.helper.showView
import com.study.news.helper.timeAgo
import com.study.news.helper.toastMsg
import com.study.news.model.ArticleSaved
import com.study.news.model.SearchHistory
import com.study.news.model.TopTrendingResponce
import com.study.news.viewModel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.util.ArrayList

@AndroidEntryPoint
class SearchFragment : BindingFragment<FragmentSearchBinding>() {

    private val viewModel by viewModels<SearchViewModel>()

    override val backPressedHandler: () -> Unit
        get() = { requireActivity().finishAffinity() }
    override val onDestroyViewHandler: () -> Unit
        get() = {}
    override val onCreateViewHandler: () -> Unit
        get() = {
            binding.viewModel = viewModel
            binding.lifecycleOwner = this
        }
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentSearchBinding::inflate

    var searchHistoryList = ArrayList<SearchHistory>()

    private var _searchAdapter: GenericAdapter<ArticleSaved, NewsCardItemBinding>? = null

    private val searchAdapter get() = _searchAdapter!!

    private var _searchHistoryAdapter: GenericAdapter<SearchHistory, SearchHistoryBinding>? = null

    private val searchHistoryAdapter get() = _searchHistoryAdapter!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpStatusBar(Color.BLACK)

        setupUI(view) {}

        observables()

        initializeAdapter()

    }

    private fun setUpStatusBar(color: Int) {
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        requireActivity().window.statusBarColor = color
        WindowInsetsControllerCompat(
            requireActivity().window,
            binding.root
        ).isAppearanceLightStatusBars = false
    }


    private fun initializeAdapter() {
        _searchAdapter = GenericAdapter(
            bindingInflater = NewsCardItemBinding::inflate,
            onBind = { itemData, itemBinding, position, listSize ->
                itemBinding.apply {
                    val cardColor = getRandomColor()
                    if (itemData.isSaved) {
                        bookMark.setImageResource(R.drawable.fill_tag)
                    } else {
                        bookMark.setImageResource(R.drawable.unfill_tag)
                    }
                    bookMark.setOnClickListener {
                        if (itemData.isSaved) {
                            itemData.isSaved = false
                            viewModel.deleteNews(itemData)
                        } else {
                            itemData.isSaved = true
                            viewModel.savedNews(itemData)
                        }
                        searchAdapter.notifyItemChanged(position)
                    }
                    Glide.with(requireContext()).load(itemData.urlToImage).into(newsImg)
                    containOwner.text = itemData.source?.name
                    title.text = itemData.title
                    auther.text = itemData.author?.replace(", ", ",\n")
                    publishDate.text = timeAgo(itemData.publishedAt ?: "")
                    description.text = itemData.description
                    container.setOnClickListener {
                        if (findNavController().currentDestination?.id == R.id.searchFragment) {
                            findNavController().navigate(
                                SearchFragmentDirections.actionGlobalNewsDetailsFragment3(
                                    newsData = Convertor.toJson(itemData), newsBgColor = cardColor
                                )
                            )
                        }
                    }
                }
            })
        binding.newsRv.adapter = searchAdapter
        _searchHistoryAdapter = GenericAdapter(
            bindingInflater = SearchHistoryBinding::inflate,
            onBind = { itemData, itemBinding, position, listSize ->
                itemBinding.apply {
                    query.text = itemData.searchQuery
                    query.setOnClickListener {
                        viewModel.searchQuery.postValue(itemData.searchQuery)
                    }
                }
            })
        binding.searchHistoryRv.adapter = searchHistoryAdapter
    }

    private fun observables() {
        viewModel.searchQuery.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                Log.e("searchQuery", it)
                if (!searchHistoryList.isEmpty()){
                    setVisibilityShow(R.id.search_history_rv)
                }else{
                    setVisibilityShow(R.id.no_search_found)
                }
            } else {
                searchAdapter.setData(ArrayList())
                setVisibilityShow(R.id.search_history_rv)
                viewModel.uiVisibilityState?.let { state ->
                    setVisibilityShow(state)
                }
            }
        }
        viewModel.searchesponce.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error -> {
                    setVisibilityShow(R.id.news_rv, false)
                    binding.progressBar2.hideView()
                    toastMsg(getString(ResponseMessageManager.getMessage(it.error)))
                }

                is NetworkResult.Loading -> {
                    setVisibilityShow(R.id.news_rv, false)
                    binding.progressBar2.showView()
                }

                is NetworkResult.Success -> {
                    binding.progressBar2.hideView()
                    Log.e("search", it.data.toString())
                    val data =
                        Convertor.jsonToObject<TopTrendingResponce>(Convertor.toJson(it.data))
                    setVisibilityShow(R.id.news_rv)
                    if (data.articles.isNullOrEmpty()) {
                        setVisibilityShow(R.id.no_search_result_found)
                    }
                    viewModel.getAllNews().observe(viewLifecycleOwner) { savedNews ->
                        searchAdapter.setData(ArrayList(data.articles?.filter { !it.author.isNullOrEmpty() }
                            ?.map { artical ->
                                ArticleSaved(
                                    source = artical.source,
                                    author = artical.author,
                                    title = artical.title,
                                    description = artical.description,
                                    url = artical.url,
                                    urlToImage = artical.urlToImage,
                                    publishedAt = artical.publishedAt,
                                    content = artical.content,
                                    isSaved = savedNews.toString().contains(artical.title ?: "")
                                )
                            }))
                    }
                    viewModel.addSearchHistory(
                        SearchHistory(
                            searchQuery = viewModel.searchQuery.value ?: "",
                            date = Timestamp(System.currentTimeMillis())
                        )
                    )
                }
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getAllSearchHistory().let {
                lifecycleScope.launch(Dispatchers.Main) {
                    Log.e("list", it.toString())
                    if (it.isNotEmpty()) {
                        searchHistoryList = ArrayList(it.reversed())
                        searchHistoryAdapter.setData(ArrayList(it.reversed()))
                        setVisibilityShow(R.id.search_history_rv)
                        viewModel.uiVisibilityState?.let { state ->
                            setVisibilityShow(state)
                        }
                    } else {
                        setVisibilityShow(R.id.no_search_found)
                    }
                }
            }
        }
    }

    private fun setVisibilityShow(viewName: Int?, toShow: Boolean = true) {
        when (viewName) {
            R.id.no_search_result_found -> {
                binding.noSearchFound.hideView()
                binding.noSearchResultFound.showView()
                binding.searchHistoryRv.hideView()
                binding.newsRv.hideView()
                viewModel.uiVisibilityState = viewName
            }

            R.id.no_search_found -> {
                binding.noSearchFound.showView()
                binding.noSearchResultFound.hideView()
                binding.searchHistoryRv.hideView()
                binding.newsRv.hideView()
                viewModel.uiVisibilityState = viewName
            }

            R.id.search_history_rv -> {
                binding.noSearchFound.hideView()
                binding.noSearchResultFound.hideView()
                binding.searchHistoryRv.showView()
                binding.newsRv.hideView()
                viewModel.uiVisibilityState = viewName
            }

            R.id.news_rv -> {
                binding.noSearchFound.hideView()
                binding.noSearchResultFound.hideView()
                binding.searchHistoryRv.hideView()
                binding.newsRv.showView()
                viewModel.uiVisibilityState = viewName
            }

            else -> {}
        }
        if (!toShow) {
            binding.noSearchFound.hideView()
            binding.noSearchResultFound.hideView()
            binding.searchHistoryRv.hideView()
            binding.newsRv.hideView()
        }
    }

}