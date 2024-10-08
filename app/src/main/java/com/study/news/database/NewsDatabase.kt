package com.study.news.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.study.news.database.converter.NewsDBTypeConverter
import com.study.news.database.dao.SavedNewsDao
import com.study.news.database.dao.SearchHistoryDao
import com.study.news.model.ArticleSaved
import com.study.news.model.SearchHistory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@TypeConverters(NewsDBTypeConverter::class)
@Database(entities = [ArticleSaved::class, SearchHistory::class], version = 1)
abstract class NewsDatabase : RoomDatabase() {

    abstract fun getSavedNewsDao(): SavedNewsDao

    abstract fun getSearchHistoryDao(): SearchHistoryDao
}

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Provides
    fun getSavedNewsDao(newsDatabase: NewsDatabase): SavedNewsDao {
        return newsDatabase.getSavedNewsDao()
    }

    @Provides
    fun getSavedHistoryDao(newsDatabase: NewsDatabase): SearchHistoryDao {
        return newsDatabase.getSearchHistoryDao()
    }

    @Provides
    @Singleton
    fun getNewsDatabase(@ApplicationContext appContext: Context): NewsDatabase {
        return Room.databaseBuilder(
            appContext,
            NewsDatabase::class.java,
            "News-database"
        ).build()
    }

}