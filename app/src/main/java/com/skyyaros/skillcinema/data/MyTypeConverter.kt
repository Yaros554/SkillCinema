package com.skyyaros.skillcinema.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.skyyaros.skillcinema.entity.Genre


class MyTypeConverter {
    @TypeConverter
    fun toGenreList(value: String?): List<Genre>? {
        val listType = object : TypeToken<List<Genre>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromGenreList(list: List<Genre>?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }
}