package com.phoenix.powerplayscorer.feature_editor.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.phoenix.powerplayscorer.feature_editor.data.data_source.MatchDao
import com.phoenix.powerplayscorer.feature_editor.domain.model.Match
import com.phoenix.powerplayscorer.feature_editor.domain.repository.Repository
import com.phoenix.powerplayscorer.feature_editor.domain.use_case.auth.AuthUseCases
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RepositoryImpl (
    private val dao: MatchDao,
    private val authUseCases: AuthUseCases,
): Repository {
    private val db = Firebase.firestore
    private var newMatchListener: ListenerRegistration? = null

    init {
        val userIdFlow = authUseCases.getUserIdFlow()
        kotlinx.coroutines.MainScope().launch {
            userIdFlow.collectLatest { _userId ->
                _userId?.let { userId ->
                    dao.getLatestUploadStamp(userId).collectLatest { uploadStamp ->
                        Log.d(TAG, "new uploadStamp: $uploadStamp")
                        val query = db.collection("users").document(userId).collection("matches")
                            .whereGreaterThanOrEqualTo("uploadStamp", uploadStamp ?: 0)
                        newMatchListener?.remove()
                        newMatchListener = query.addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, e ->
                            if (e != null) {
                                Log.w(TAG, "listen:error", e)
                                return@addSnapshotListener
                            }
                            for (doc in snapshot!!) {
                                if (doc.metadata.hasPendingWrites().not()) {
                                    this.launch {
                                        dao.insertMatch(doc.toObject())
                                    }
                                }
                            }
                        }
                    }
                    dao.getMatchesNotUploaded(userId).collect {

                    }
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