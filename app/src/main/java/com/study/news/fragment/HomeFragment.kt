package com.study.news.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.study.news.helper.AppEvent
import com.study.news.R
import com.study.news.adapters.GenericAdapter
import com.study.news.api.apiManagerHelper.NetworkResult
import com.study.news.api.apiManagerHelper.ResponseMessageManager
import com.study.news.databinding.FragmentHomeBinding
import com.study.news.databinding.NewsCardItemBinding
import com.study.news.helper.Convertor
import com.study.news.helper.Convertor.toJson
import com.study.news.helper.getRandomColor
import com.study.news.helper.hideView
import com.study.news.helper.showView
import com.study.news.helper.timeAgo
import com.study.news.helper.toastMsg
import com.study.news.model.ArticleSaved
import com.study.news.model.TopTrendingResponce
import com.study.news.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BindingFragment<FragmentHomeBinding>() {

    private val viewModel by viewModels<HomeViewModel>()

    override val backPressedHandler: () -> Unit
        get() = {
            requireActivity().finishAffinity()
        }
    override val onDestroyViewHandler: () -> Unit
        get() = {}
    override val onCreateViewHandler: () -> Unit
        get() = {}
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentHomeBinding::inflate

    private var _pagerAdapter: GenericAdapter<ArticleSaved, NewsCardItemBinding>? = null

    private val pagerAdapter get() = _pagerAdapter!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.topTrendingNews(R.string.trending)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpUI()

        initializePagerAdapter()

        observables()

        initializeListener()

    }

    override fun onStart() {
        super.onStart()

    }

    private fun setUpUI() {
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        requireActivity().window.statusBarColor = Color.BLACK
        WindowInsetsControllerCompat(
            requireActivity().window,
            binding.root
        ).isAppearanceLightStatusBars = false
    }

    private fun initializePagerAdapter() {
        _pagerAdapter =
            GenericAdapter(
                NewsCardItemBinding::inflate,
                onBind = { itemData, itemBinding, position, listSize ->
                    itemBinding.apply {
                        val cardColor = getRandomColor()
                        container.setCardBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                cardColor
                            )
                        )
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
                            pagerAdapter.notifyItemChanged(position)
                        }
                        Glide.with(requireContext()).load(itemData.urlToImage).into(newsImg)
                        containOwner.text = itemData.title?.split("-")?.get(1)
                        title.text = itemData.title
                        auther.text = itemData.author?.replace(", ", ",\n")
                        publishDate.text = timeAgo(itemData.publishedAt ?: "")
                        description.text = itemData.description
                        container.setOnClickListener {
                            if (findNavController().currentDestination?.id == R.id.homeFragment) {
                                findNavController().navigate(
                                    HomeFragmentDirections.actionGlobalNewsDetailsFragment(
                                        newsData = toJson(itemData), newsBgColor = cardColor
                                    )
                                )
                            }
                        }
                    }
                })
        binding.newsRv.adapter = pagerAdapter
    }

    private fun observables() {
        viewModel.topTrendingResponce.observe(viewLifecycleOwner) {
            Log.e("responce", it.toString())
            when (it) {
                is NetworkResult.Error -> {
                    binding.progressBar.hideView()
                    toastMsg(getString(ResponseMessageManager.getMessage(it.error)))
                }

                is NetworkResult.Loading -> {
                    binding.progressBar.showView()
                }

                is NetworkResult.Success -> {
                    binding.progressBar.hideView()
                    val data = Convertor.jsonToObject<TopTrendingResponce>(toJson(it.data))
                    Log.e("data", data.toString())
                    viewModel.getAllNews().observe(viewLifecycleOwner) { savedNews ->
                        pagerAdapter.setData(ArrayList(data.articles?.filter { !it.author.isNullOrEmpty() }
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
                }
            }
        }

        viewModel.appEvent.observe(viewLifecycleOwner) {
            when (it) {
                is AppEvent.ToastEvent -> {
                    toastMsg(getString(it.message))
                }

                else -> {}
            }
            viewModel._appEvent.postValue(null)
        }
    }

    private fun initializeListener() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                Log.e("tab", tab?.text.toString())
                when (tab?.text) {
                    getString(R.string.trending) -> {
                        viewModel.topTrendingNews(R.string.trending)
                    }

                    getString(R.string.health) -> {
                        viewModel.topTrendingNews(R.string.health)
                    }

                    getString(R.string.sports) -> {
                        viewModel.topTrendingNews(R.string.sports)
                    }

                    getString(R.string.finance) -> {
                        viewModel.topTrendingNews(R.string.finance)
                    }
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

}