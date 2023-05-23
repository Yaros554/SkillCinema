package com.skyyaros.skillcinema.ui.detailactor

import com.skyyaros.skillcinema.entity.DetailActor

sealed class StateDetailActor {
    object Loading: StateDetailActor()
    data class Error(val message: String): StateDetailActor()
    data class Success(val data: DetailActor): StateDetailActor()
}
