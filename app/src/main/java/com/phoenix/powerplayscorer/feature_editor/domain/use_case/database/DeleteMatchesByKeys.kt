package com.phoenix.powerplayscorer.feature_editor.domain.use_case.database

import com.phoenix.powerplayscorer.feature_editor.domain.model.Match
import com.phoenix.powerplayscorer.feature_editor.domain.repository.Repository

class DeleteMatchesByKeys(
    private val repository: Repository
) {
    suspend operator fun invoke(
        keyList: List<String>
    ) {
        val matches = repository.getMatchesByKeyList(keyList)
        val newMatches = mutableListOf<Match>()
        for (match in matches) {
            newMatches.add(match.copy(uploadStamp = -1))
        }
        repository.insertMatches(newMatches)
    }
}