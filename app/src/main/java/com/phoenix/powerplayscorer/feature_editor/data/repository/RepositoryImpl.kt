package com.phoenix.powerplayscorer.feature_editor.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.phoenix.powerplayscorer.feature_editor.data.data_source.MatchDao
import com.phoenix.powerplayscorer.feature_editor.data.data_source.User
import com.phoenix.powerplayscorer.feature_editor.domain.model.*
import com.phoenix.powerplayscorer.feature_editor.domain.repository.Repository
import com.phoenix.powerplayscorer.feature_editor.domain.use_case.auth.AuthUseCases
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class RepositoryImpl (
    private val dao: MatchDao,
    private val authUseCases: AuthUseCases,
): Repository {
    private val db = Firebase.firestore
    private var newMatchListener: ListenerRegistration? = null
    private val handler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "Firebase sync failed.", throwable)
    }
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default + handler)
    private var syncDeletedMatchesJob: Job? = null
    private var uploadJob: Job? = null
    private var deleteJob: Job? = null

    init {
        scope.launch {
            supervisorScope {
                authUseCases.getUserIdFlow().collect { _uid ->
                    Log.e(TAG, "New uid: $_uid")
                    newMatchListener?.remove()
                    syncDeletedMatchesJob?.cancel()
                    uploadJob?.cancel()
                    deleteJob?.cancel()
                    _uid?.let { uid ->
                        syncDeletedMatchesJob = launch {
                            syncDeletedMatches(uid)
                        }
                        uploadJob = launch {
                            upload(uid)
                        }
                        deleteJob = launch {
                            delete(uid)
                        }
                        val latestStamp = async {
                            dao.getLatestUploadStamp(uid)
                        }
                        newMatchListener = documentListener(
                            latestStamp = latestStamp.await(),
                            id = uid,
                            onSnapshotFinished = { newMatches, deletedMatches ->
                                launch {
                                    dao.insertMatches(newMatches)
                                    val uploadedKeys = dao.getUploadedMatchesKeys(uid)
                                    val willBeDeleted = mutableListOf<Match>()
                                    for (match in deletedMatches) {
                                        if (uploadedKeys.contains(match.key))
                                            willBeDeleted.add(match)
                                    }
                                    dao.deleteMatches(willBeDeleted)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    private suspend fun syncDeletedMatches(id: String) {
        val matches = dao.getUploadedMatchesKeys(id)
        val docRef = db.collection("users").document(id)
        val source = Source.SERVER
        val result = docRef.get(source).await()
        Log.e(TAG, "Synced deleted messages")
        result.toObject<User>()?.let { user ->
            val onlineMatches = user.matchesIds
            val deletedMatches: MutableList<String> = mutableListOf()
            for (match in matches) {
                if (onlineMatches.contains(match).not()) {
                    deletedMatches.add(match)
                }
            }
            if (deletedMatches.isEmpty().not())
                dao.deleteMatchListByKeys(deletedMatches.toList())
        }
    }

    private fun documentListener(latestStamp: Long?, id: String, onSnapshotFinished: (newMatches: List<Match>, deletedMatches: List<Match>) -> Unit): ListenerRegistration {
        val path = db.collection("users").document(id).collection("matches")
        Log.e(TAG, "Latest stamp: $latestStamp")
        return path.whereGreaterThanOrEqualTo(
            "uploadStamp",
            (latestStamp ?: 0).toTimestamp()
        ).addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Listen failed.", error)
                return@addSnapshotListener
            }
            if (snapshot == null) return@addSnapshotListener
            val newMatches = mutableListOf<Match>()
            val deletedMatches = mutableListOf<Match>()
            Log.e(TAG, "Got new snapshot")
            for (doc in snapshot.documentChanges) {
                if (doc.document.metadata.hasPendingWrites().not()) {
                    when (doc.type) {
                        DocumentChange.Type.ADDED -> newMatches.add(doc.document.toObject<FirebaseMatch>().toMatch(doc.document.id, id))
                        DocumentChange.Type.MODIFIED -> newMatches.add(doc.document.toObject<FirebaseMatch>().toMatch(doc.document.id, id))
                        DocumentChange.Type.REMOVED -> deletedMatches.add(doc.document.toObject<FirebaseMatch>().toMatch(doc.document.id, id))
                    }
                }
            }
            onSnapshotFinished(newMatches, deletedMatches)
        }
    }

    private suspend fun upload(id: String) {
        dao.getMatchesNotUploaded(id).collect { matches ->
            if (matches.isEmpty()) return@collect
            val uRef = db.collection("users").document(id)
            val batch = db.batch()
            for (match in matches.take(250)) {
                val mRef = uRef.collection("matches").document(match.key)
                batch.set(mRef, match.toFirebaseMatch())
                batch.update(uRef, "matchesIds", FieldValue.arrayUnion(match.key))
            }
            batch.commit().await()
            Log.e(TAG, "Uploaded a new batch")
        }
    }

    private suspend fun delete(id: String) {
        dao.getDeletedMatchesFlow(id).collect { matches ->
            if (matches.isEmpty()) return@collect
            val uRef = db.collection("users").document(id)
            val batch = db.batch()
            for (match in matches.take(250)) {
                val mRef = uRef.collection("matches").document(match.key)
                batch.delete(mRef)
                batch.update(uRef, "matchesIds", FieldValue.arrayRemove(match.key))
            }
            batch.commit().await()
            Log.e(TAG, "Deleted a batch")
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