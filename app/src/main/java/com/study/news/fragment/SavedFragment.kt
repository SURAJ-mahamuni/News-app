package com.study.news.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.study.news.R
import com.study.news.adapters.GenericAdapter
import com.study.news.databinding.FragmentSavedBinding
import com.study.news.databinding.NewsCardItemBinding
import com.study.news.helper.Convertor
import com.study.news.helper.getRandomColor
import com.study.news.helper.hideView
import com.study.news.helper.showView
import com.study.news.helper.timeAgo
import com.study.news.model.ArticleSaved
import com.study.news.viewModel.SavedViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList

@AndroidEntryPoint
class SavedFragment : BindingFragment<FragmentSavedBinding>() {

    private val viewModel by viewModels<SavedViewModel>()

    override val backPressedHandler: () -> Unit
        get() = {requireActivity().finishAffinity()}
    override val onDestroyViewHandler: () -> Unit
        get() = {}
    override val onCreateViewHandler: () -> Unit
        get() = {}
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentSavedBinding::inflate

    private var _savedNewsAdapter: GenericAdapter<ArticleSaved, NewsCardItemBinding>? = null

    private val savedNewsAdapter get() = _savedNewsAdapter!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpStatusBar(Color.BLACK)

        initializeAdapter()

        observables()

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
        _savedNewsAdapter = GenericAdapter(
            bindingInflater = NewsCardItemBinding::inflate,
            onBind = { itemData, itemBinding, position, listSize ->
                itemBinding.apply {
                    val cardColor = getRandomColor()
                    container.setCardBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            cardColor
                        )
                    )
                    Glide.with(requireContext()).load(itemData.urlToImage).into(newsImg)
                    bookMark.hideView()
                    containOwner.text = itemData.title?.split("-")?.get(1)
                    title.text = itemData.title
                    auther.text = itemData.author?.replace(", ", ",\n")
                    publishDate.text = timeAgo(itemData.publishedAt ?: "")
                    description.text = itemData.description
                    container.setOnClickListener {
                        if (findNavController().currentDestination?.id == R.id.savedFragment) {
                            findNavController().navigate(
                                SavedFragmentDirections.actionGlobalNewsDetailsFragment2(
                                    newsData = Convertor.toJson(itemData), newsBgColor = cardColor
                                )
                            )
                        }
                    }
                }
            })
        binding.savedNewsRv.adapter = savedNewsAdapter
    }

    private fun observables() {
        viewModel.getAllSavedNews().observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                savedNewsAdapter.setData(ArrayList(it))
                binding.emptyState.hideView()
            } else {
                binding.emptyState.showView()
            }
        }
    }

}