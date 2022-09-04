package com.phoenix.energizescorer.di

import android.app.Application
import androidx.room.Room
import com.phoenix.energizescorer.feature_editor.data.data_source.MatchDatabase
import com.phoenix.energizescorer.feature_editor.data.repository.RepositoryImpl
import com.phoenix.energizescorer.feature_editor.domain.repository.Repository
import com.phoenix.energizescorer.feature_editor.domain.use_case.GetMatches
import com.phoenix.energizescorer.feature_editor.domain.use_case.MatchUseCases
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
            getMatches = GetMatches(repository)
        )
    }
}