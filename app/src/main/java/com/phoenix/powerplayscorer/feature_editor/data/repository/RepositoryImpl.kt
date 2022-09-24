package com.phoenix.powerplayscorer.feature_editor.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.phoenix.powerplayscorer.feature_editor.data.data_source.MatchDao
import com.phoenix.powerplayscorer.feature_editor.data.data_source.User
import com.phoenix.powerplayscorer.feature_editor.domain.model.Match
import com.phoenix.powerplayscorer.feature_editor.domain.repository.Repository
import com.phoenix.powerplayscorer.feature_editor.domain.use_case.auth.AuthUseCases
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RepositoryImpl (
    private val dao: MatchDao,
    private val authUseCases: AuthUseCases,
): Repository {
    private val db = Firebase.firestore
    private var newMatchListener: ListenerRegistration? = null

    private suspend fun syncDeletedMatches(id: String) {
        val matches = dao.getUploadedMatchesKeys(id)
        val docRef = db.collection("users").document(id)
        try {
            val result = docRef.get().await()
            result.toObject<User>()?.let { user ->
                val onlineMatches = user.matchesArray
                val deletedMatches: MutableList<String> = mutableListOf()
                for (match in matches) {
                    if (onlineMatches.contains(match).not()) {
                        deletedMatches.add(match)
                    }
                }
                if (deletedMatches.isEmpty().not())
                    dao.deleteMatchListByKeys(deletedMatches.toList())
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message ?: "unknown exception", e)
        }
    }

    init {
        val scope = kotlinx.coroutines.MainScope()
        scope.launch {
            authUseCases.getUserIdFlow().collect { _id ->
                _id?.let { id ->
                    syncDeletedMatches(id)
                }
            }
        }
    }

    override fun getMatches(): Flow<List<Match>> {
        return dao.getMatches(authUseCases.getUserId())
    }

    override fun getMatchByKey(key: String): Flow<Match?> {
        return dao.getMatchByKey(key)
    }

    override suspend fun insertMatch(match: Match) {
        return dao.insertMatch(match)
    }

    override suspend fun insertMatches(matchList: List<Match>) {
        return dao.insertMatches(matchList)
    }

    override suspend fun deleteMatch(match: Match) {
        return dao.deleteMatch(match)
    }

    override suspend fun deleteMatchByKey(key: String) {
        return dao.deleteMatchByKey(key)
    }

    override suspend fun deleteMatchesByKeyList(keyList: List<String>) {
        return dao.deleteMatchListByKeys(keyList)
    }

    override suspend fun getMatchesByKeyList(keyList: List<String>): List<Match> {
        return dao.getMatchesByKeyList(keyList)
    }
}