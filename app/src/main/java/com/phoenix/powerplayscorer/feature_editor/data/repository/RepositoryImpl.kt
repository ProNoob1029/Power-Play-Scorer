package com.phoenix.powerplayscorer.feature_editor.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.phoenix.powerplayscorer.feature_editor.data.data_source.MatchDao
import com.phoenix.powerplayscorer.feature_editor.data.data_source.User
import com.phoenix.powerplayscorer.feature_editor.domain.model.Match
import com.phoenix.powerplayscorer.feature_editor.domain.repository.Repository
import com.phoenix.powerplayscorer.feature_editor.domain.use_case.auth.AuthUseCases
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RepositoryImpl (
    private val dao: MatchDao,
    private val authUseCases: AuthUseCases,
): Repository {
    private val db = Firebase.firestore
    private var newMatchListener: ListenerRegistration? = null
    private val scope = kotlinx.coroutines.MainScope()
    private var uploadJob: Job? = null

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
            Log.w(TAG, "Sync failed.", e)
        }
    }

    private fun documentListener(latestStamp: Long?, id: String): ListenerRegistration {
        val path = db.collection("users").document(id).collection("matches")
        return path.whereGreaterThanOrEqualTo(
            "uploadStamp",
            latestStamp ?: 0
        ).addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(TAG, "Listen failed.", error)
                return@addSnapshotListener
            }
            if (snapshot == null) return@addSnapshotListener
            val newMatches = mutableListOf<Match>()
            val deletedMatches = mutableListOf<Match>()
            for (doc in snapshot.documentChanges) {
                if (doc.document.metadata.hasPendingWrites().not()) {
                    when (doc.type) {
                        DocumentChange.Type.ADDED -> newMatches.add(doc.document.toObject())
                        DocumentChange.Type.MODIFIED -> newMatches.add(doc.document.toObject())
                        DocumentChange.Type.REMOVED -> deletedMatches.add(doc.document.toObject())
                    }
                }
            }
            scope.launch {
                dao.insertMatches(newMatches)
                dao.deleteMatches(deletedMatches)
            }
        }
    }

    private fun getUploadJob(id: String): Job {
        return dao.getMatchesNotUploaded(id).onEach { matches ->
            try {
                val uRef = db.collection("users").document(id)
                for (match in matches.take(250)) {
                    val mRef = uRef.collection("matches").document(match.key)
                    db.runBatch { batch ->
                        batch.set(mRef, match)
                        batch.update(uRef, "matchesIds", FieldValue.arrayUnion(match.key))
                        batch.commit()
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Upload failed.", e)
            }
        }.launchIn(scope)
    }

    init {
        scope.launch {
            authUseCases.getUserIdFlow().collect { _id ->
                newMatchListener?.remove()
                uploadJob?.cancel()
                _id?.let { id ->
                    syncDeletedMatches(id)
                    val latestStamp = dao.getLatestUploadStamp(id)
                    newMatchListener = documentListener(latestStamp, id)
                    uploadJob = getUploadJob(id)
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