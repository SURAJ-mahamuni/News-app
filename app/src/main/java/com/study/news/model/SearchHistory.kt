package com.study.news.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity("search_history")
data class SearchHistory(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    var searchQuery: String = "",
    var date: Timestamp = Timestamp(System.currentTimeMillis()),
)
