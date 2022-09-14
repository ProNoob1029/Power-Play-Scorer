package com.phoenix.powerplayscorer.di

import android.app.Application
import androidx.room.Room
import com.phoenix.powerplayscorer.feature_editor.data.data_source.MatchDatabase
import com.phoenix.powerplayscorer.feature_editor.data.repository.RepositoryImpl
import com.phoenix.powerplayscorer.feature_editor.domain.repository.Repository
import com.phoenix.powerplayscorer.feature_editor.domain.use_case.GetMatch
import com.phoenix.powerplayscorer.feature_editor.domain.use_case.GetMatches
import com.phoenix.powerplayscorer.feature_editor.domain.use_case.MatchUseCases
import com.phoenix.powerplayscorer.feature_editor.domain.use_case.SaveMatch
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRoomDatabase(app: Application): MatchDatabase {
        return Room.databaseBuilder(
            app,
            MatchDatabase::class.java,
            MatchDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideRepository(db: MatchDatabase): Repository {
        return RepositoryImpl(db.matchDao)
    }

    @Provides
    @Singleton
    fun provideMatchUseCases(repository: Repository): MatchUseCases {
        return MatchUseCases(
            getMatches = GetMatches(repository),
            getMatch = GetMatch(repository),
            saveMatch = SaveMatch(repository)
        )
    }
}