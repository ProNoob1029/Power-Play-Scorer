package com.phoenix.powerplayscorer.feature_editor.data.data_source

import androidx.room.*
import com.phoenix.powerplayscorer.feature_editor.domain.model.Match
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {
    @Query("SELECT * FROM `match`")
    fun getMatches(): Flow<List<Match>>

    @Query("SELECT * FROM `match` WHERE `key` = :key")
    fun getMatchByKey(key: String): Flow<Match?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: Match)

    @Delete
    suspend fun deleteMatch(match: Match)
}