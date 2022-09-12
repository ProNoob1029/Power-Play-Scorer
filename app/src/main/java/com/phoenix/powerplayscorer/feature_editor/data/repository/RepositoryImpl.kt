package com.phoenix.powerplayscorer.feature_editor.data.repository

import com.phoenix.powerplayscorer.feature_editor.data.data_source.MatchDao
import com.phoenix.powerplayscorer.feature_editor.domain.model.Match
import com.phoenix.powerplayscorer.feature_editor.domain.repository.Repository
import kotlinx.coroutines.flow.Flow

class RepositoryImpl (
    private val dao: MatchDao
): Repository {
    override fun getMatches(): Flow<List<Match>> {
        return dao.getMatches()
    }

    override fun getMatchByKey(key: String): Flow<Match?> {
        return dao.getMatchByKey(key)
    }

    override suspend fun insertMatch(match: Match) {
        return dao.insertMatch(match)
    }

    override suspend fun deleteMatch(match: Match) {
        return dao.deleteMatch(match)
    }
}