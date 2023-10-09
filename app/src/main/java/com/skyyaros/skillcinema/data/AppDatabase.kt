package com.skyyaros.skillcinema.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.skyyaros.skillcinema.entity.FilmActorTable

@Database(entities = [FilmActorTable::class], version = 1)
@TypeConverters(MyTypeConverter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun filmActorDao(): FilmActorDao

    companion object RoomDbProvider {
        private const val DATABASE_NAME = "FilmActorDb"

        fun provide(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()
        }
    }
}