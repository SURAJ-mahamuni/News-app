package com.study.news.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.study.news.model.SearchHistory

@Dao
interface SearchHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSearchHistory(history : SearchHistory)

    @Query("Select * from search_history")
    fun getAllSearchHistory(): List<SearchHistory>

    @Query("SELECT * FROM search_history ORDER BY date DESC LIMIT 1")
    fun getLastRecord() : List<SearchHistory>

    @Query("DELETE FROM search_history WHERE searchQuery = :searchQuery")
    suspend fun deleteBySearchQuery(searchQuery: String)


}