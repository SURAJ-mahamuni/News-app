package com.study.news.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class TopTrendingResponce(
    val status: String? = null,
    val totalResults: Long? = null,
    val articles: List<Article>? = null,
) : Parcelable

@Parcelize
data class Article(
    val source: Source? = null,
    val author: String? = null,
    val title: String? = null,
    val description: String? = null,
    val url: String? = null,
    val urlToImage: String? = null,
    val publishedAt: String? = null,
    val content: String? = null,
) : Parcelable

@Parcelize
@Entity("saved_news")
data class ArticleSaved(
    @PrimaryKey(autoGenerate = true) val id : Int? = null,
    val source: Source? = null,
    val author: String? = null,
    val title: String? = null,
    val description: String? = null,
    val url: String? = null,
    val urlToImage: String? = null,
    val publishedAt: String? = null,
    val content: String? = null,
    var isSaved : Boolean = false
) : Parcelable

@Parcelize
data class Source(
    val id: String? = null,
    val name: String? = null,
) : Parcelable
