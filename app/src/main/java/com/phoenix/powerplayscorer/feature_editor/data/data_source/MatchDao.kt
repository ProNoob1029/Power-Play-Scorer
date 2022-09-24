package com.phoenix.powerplayscorer.feature_editor.data.data_source

import androidx.room.*
import com.phoenix.powerplayscorer.feature_editor.domain.model.Match
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {
    @Query("SELECT * FROM `match` WHERE `userId` = :userId")
    fun getMatches(userId: String): Flow<List<Match>>

    @Query("SELECT * FROM `match` WHERE `key` = :key")
    fun getMatchByKey(key: String): Flow<Match?>

    @Query("SELECT * FROM `match` WHERE `key` IN (:keyList)")
    suspend fun getMatchesByKeyList(keyList: List<String>): List<Match>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: Match)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatches(matchList: List<Match>)

    @Delete
    suspend fun deleteMatch(match: Match)

    @Query("DELETE FROM `match` WHERE `key` = :key")
    suspend fun deleteMatchByKey(key: String)

    @Query("DELETE FROM `match` WHERE `key`IN (:keyList)")
    suspend fun deleteMatchListByKeys(keyList: List<String>)

    @Query("SELECT MAX(uploadStamp) AS newestUpload FROM `match` WHERE `userId` = :userId")
    fun getLatestUploadStamp(userId: String): Flow<Long?>

    @Query("SELECT * FROM `match` WHERE uploadStamp = null AND `userId` = :userId")
    fun getMatchesNotUploaded(userId: String): Flow<List<Match>>

    @Query("SELECT `key` FROM `Match` WHERE uploadStamp != null AND userId = :userId")
    suspend fun getUploadedMatchesKeys(userId: String): List<String>

}