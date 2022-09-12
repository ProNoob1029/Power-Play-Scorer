package com.phoenix.powerplayscorer.feature_editor.domain.model

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.phoenix.powerplayscorer.feature_editor.domain.util.autoId

@Keep
@Entity
data class Match(
    @PrimaryKey val key: String = autoId(),

    val title: String = "",
    val createStamp: Long = 0,
    val editStamp: Long = 0,
    val uploadStamp: Long? = null,
    val totalPoints: Int = 0,
    val twoTeams: Boolean = false,
    val alliance: Boolean? = null,

    val autoDuck: Boolean = false,
    val autoStorage: Int = 0,
    val autoHub1: Int = 0,
    val autoHub2: Int = 0,
    val autoHub3: Int = 0,
    val autoFreightBonus1: Boolean? = null,
    val autoFreightBonus2: Boolean? = null,
    val autoParked1: Boolean? = null,
    val autoParked2: Boolean? = null,
    val autoFullyParked1: Boolean = false,
    val autoFullyParked2: Boolean = false,
    val driverStorage: Int = 0,
    val driverHub1: Int = 0,
    val driverHub2: Int = 0,
    val driverHub3: Int = 0,
    val driverShared: Int = 0,
    val endBalanced: Boolean = false,
    val endLeaning: Boolean = false,
    val endDucks: Int = 0,
    val endCapping: Boolean? = null,
    val endParked1: Boolean? = null,
    val endParked2: Boolean? = null
)
