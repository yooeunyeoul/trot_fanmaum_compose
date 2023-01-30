package com.trotfan.trot.repository

import com.trotfan.trot.network.AuthService
import com.trotfan.trot.network.UserService
import com.trotfan.trot.network.response.CommonResponse
import java.io.File
import javax.inject.Inject

class MyPageRepository @Inject constructor(
    private val authService: AuthService,
    private val userService: UserService
) {
    suspend fun postLogout(token: String): CommonResponse<Unit> {
        return authService.postLogout(token)
    }

    suspend fun postUserProfile(token: String, userId: Long, file: File): CommonResponse<Unit> {
        return userService.userProfileUpload(token = token, userId = userId, image = file)
    }
}