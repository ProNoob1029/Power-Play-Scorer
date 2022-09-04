package com.phoenix.energizescorer

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ScorerApp @Inject constructor(): Application()