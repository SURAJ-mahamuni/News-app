package com.study.news.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.study.news.helper.AppEvent
import com.study.news.databinding.FragmentNewsDetailsBinding
import com.study.news.helper.Convertor
import com.study.news.helper.timeAgo
import com.study.news.model.Article
import com.study.news.viewModel.NewsDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NewsDetailsFragment : BindingFragment<FragmentNewsDetailsBinding>() {

    private val args by navArgs<NewsDetailsFragmentArgs>()

    private val viewModel by viewModels<NewsDetailsViewModel>()

    var newsData = Article()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("data", args.newsData ?: "")
        newsData = Convertor.jsonToObject<Article>(args.newsData ?: "")
        viewModel.newsData.postValue(newsData)
    }

    override val backPressedHandler: () -> Unit
        get() = {
            findNavController().navigateUp()
        }
    override val onDestroyViewHandler: () -> Unit
        get() = {}
    override val onCreateViewHandler: () -> Unit
        get() = {
            binding.viewModel = viewModel
            binding.lifecycleOwner = this
        }
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentNewsDetailsBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpUi()

        initializeListener()

        observables()

    }

    private fun observables() {
        viewModel.appEvent.observe(viewLifecycleOwner) {
            when (it) {
                is AppEvent.Other -> {
                    if (viewModel.OPEN_URL == it.message) {
                        openUrl()
                    }
                }

                else -> {}
            }
            viewModel._appEvent.postValue(null)
        }
    }

    private fun setUpStatusBar(color: Int) {
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        requireActivity().window.statusBarColor = color
        WindowInsetsControllerCompat(
            requireActivity().window,
            binding.root
        ).isAppearanceLightStatusBars = true
    }

    private fun initializeListener() {
        binding.topBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setUpUi() {
        val color = ContextCompat.getColor(
            requireContext(),
            args.newsBgColor
        )

        setUpStatusBar(color)
        binding.container.setBackgroundColor(color)
        binding.topBarLayout.setBackgroundColor(color)

        Glide.with(requireContext()).load(newsData.urlToImage)
            .into(binding.newsPoster)

        binding.publishDate.text = timeAgo(newsData.publishedAt ?: "")


    }

    private fun openUrl() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(newsData.url))
        startActivity(browserIntent)
    }

}