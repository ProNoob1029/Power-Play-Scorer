package com.phoenix.energizescorer.feature_editor.domain.use_case

import com.phoenix.energizescorer.feature_editor.domain.model.Match
import com.phoenix.energizescorer.feature_editor.domain.repository.Repository
import kotlinx.coroutines.flow.Flow

class GetMatch(
    private val repository: Repository
) {
    operator fun invoke(
        key: String
    ): Flow<Match?> {
        return repository.getMatchByKey(key)
    }
}