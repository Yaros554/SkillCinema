package com.skyyaros.skillcinema.entity

enum class VideoSource {
    ANY, YOUTUBE, KINOPOISK
}

enum class AppTheme {
    AUTO, DAY, NIGHT
}

data class AppSettings (
    val videoSource: VideoSource,
    val theme: AppTheme,
    val animActive: Boolean
)