package com.phoenix.powerplayscorer.feature_editor.domain.use_case

import com.phoenix.powerplayscorer.feature_editor.domain.repository.Repository

class DeleteMatchesByKeys(
    private val repository: Repository
) {
    suspend operator fun invoke(
        keyList: List<String>
    ) {
        repository.deleteMatchesByKeyList(keyList)
    }
}