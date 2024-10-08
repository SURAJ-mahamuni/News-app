package com.study.news.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.study.news.model.ArticleSaved
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedNewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSavedNews(news: ArticleSaved)

    @Query("Select * from saved_news")
    fun getAllSavedNews(): Flow<List<ArticleSaved>>

    @Query("Delete from saved_news where title = :title ")
    fun deleteNews(title : String)
}