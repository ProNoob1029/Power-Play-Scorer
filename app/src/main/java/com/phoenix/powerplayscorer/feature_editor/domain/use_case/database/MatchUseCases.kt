package com.phoenix.powerplayscorer.feature_editor.domain.use_case.database

data class MatchUseCases(
    val getMatches: GetMatches,
    val getMatch: GetMatch,
    val saveMatch: SaveMatch,
    val getMatchesByKeys: GetMatchesByKeys,
    val deleteMatchesByKeys: DeleteMatchesByKeys,
    val saveMatches: SaveMatches
)
