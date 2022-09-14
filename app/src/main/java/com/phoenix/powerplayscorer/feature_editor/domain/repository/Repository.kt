package com.phoenix.powerplayscorer.feature_editor.domain.repository

import com.phoenix.powerplayscorer.feature_editor.domain.model.Match
import kotlinx.coroutines.flow.Flow

interface Repository {

    fun getMatches(): Flow<List<Match>>

    fun getMatchByKey(key: String): Flow<Match?>

    suspend fun getMatchesByKeyList(keyList: List<String>): List<Match>

    suspend fun insertMatches(matchList: List<Match>)

    suspend fun insertMatch(match: Match)

    suspend fun deleteMatch(match: Match)

    suspend fun deleteMatchByKey(key: String)

    suspend fun deleteMatchesByKeyList(keyList: List<String>)
}