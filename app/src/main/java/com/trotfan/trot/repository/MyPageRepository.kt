package com.trotfan.trot.repository

import com.trotfan.trot.model.Alarm
import com.trotfan.trot.network.AuthService
import com.trotfan.trot.network.SettingService
import com.trotfan.trot.network.response.CommonResponse
import com.trotfan.trot.ui.home.mypage.setting.AlarmType
import javax.inject.Inject

class MyPageRepository @Inject constructor(
    private val authService: AuthService,
    private val settingService: SettingService
) {
    suspend fun postLogout(token: String): CommonResponse<Unit> {
        return authService.postLogout(token)
    }

    suspend fun getPushSetting(userToken: String, userId: Long): CommonResponse<Alarm> {
        return settingService.getPushSetting(userToken, userId)
    }

    suspend fun setPushSetting(
        userToken: String,
        userId: Long,
        alarmType: AlarmType
    ): CommonResponse<Unit> {
        return settingService.setPushSetting(userToken, userId, alarmType)
    }
}