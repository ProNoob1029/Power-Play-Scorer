package com.phoenix.powerplayscorer.feature_editor.domain.use_case

import com.phoenix.powerplayscorer.feature_editor.domain.model.Match
import com.phoenix.powerplayscorer.feature_editor.domain.repository.Repository

class GetMatchesByKeys(
    private val repository: Repository
) {
    suspend operator fun invoke(
        keyList: List<String>
    ): List<Match> {
        return repository.getMatchesByKeyList(keyList)
    }
}