package com.skyyaros.skillcinema.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "filmActorTable", primaryKeys = ["category", "kinopoiskId"])
data class FilmActorTable(
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "kinopoiskId") val kinopoiskId: Long,
    @ColumnInfo(name = "isActor") val isActor: Boolean?,
    @ColumnInfo(name = "posterUrlPreview") val posterUrlPreview: String?,
    @ColumnInfo(name = "nameRu") val nameRu: String?,
    @ColumnInfo(name = "nameEn") val nameEn: String?,
    @ColumnInfo(name = "nameOriginal") val nameOriginal: String?,
    @ColumnInfo(name = "genres") val genres: List<Genre>?,
    @ColumnInfo(name = "rating") val rating: String?,
    @ColumnInfo(name = "dateCreate") val dateCreate: Long = System.currentTimeMillis()
): Parcelable