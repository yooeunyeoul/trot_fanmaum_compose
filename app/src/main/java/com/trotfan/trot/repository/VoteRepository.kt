package com.trotfan.trot.repository

import com.trotfan.trot.model.*
import com.trotfan.trot.network.VoteService
import com.trotfan.trot.network.response.CommonResponse
import javax.inject.Inject

class VoteRepository @Inject constructor(
    private val voteService: VoteService
) {
    suspend fun getVote(): CommonResponse<Top3Benefit>? =
        voteService.vote()

}