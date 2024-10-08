package com.study.news.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import androidx.viewpager.widget.PagerAdapter

class GenericPagerAdapter<T, VB : ViewBinding>(
    private val bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> VB,
    private val onBind: (VB, T) -> Unit
) : PagerAdapter() {

    private var data = ArrayList<T>()

    override fun getCount() = data.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding =
            bindingInflater(LayoutInflater.from(container.context), container, false)
        onBind(binding, data[position])
        container.addView(binding.root)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }

    fun removeAllPages() {
        data.clear()
        notifyDataSetChanged()
    }


    fun setPagerData(newData: ArrayList<T>) {
        data = newData
        notifyDataSetChanged()
    }

}