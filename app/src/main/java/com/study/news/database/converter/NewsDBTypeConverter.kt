package com.study.news.database.converter

import androidx.room.TypeConverter
import com.study.news.helper.Convertor
import com.study.news.model.Source
import java.sql.Timestamp

class NewsDBTypeConverter {

    @TypeConverter
    fun fromSourceToString(source: Source): String {
        return Convertor.toJson(source)
    }

    @TypeConverter
    fun fromStringToSource(source: String): Source {
        return Convertor.jsonToObject<Source>(source)
    }

    @TypeConverter
    fun fromTimestampToString(timestamp: Timestamp): String {
        return Convertor.toJson(timestamp)
    }

    @TypeConverter
    fun fromStringToTimestamp(timestamp: String): Timestamp {
        return Convertor.jsonToObject<Timestamp>(timestamp)
    }
}