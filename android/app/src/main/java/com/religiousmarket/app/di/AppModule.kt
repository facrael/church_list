package com.religiousmarket.app.di

import android.content.Context
import com.religiousmarket.app.data.repository.ChurchRepository
import com.religiousmarket.app.data.repository.PrayerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideChurchRepository(): ChurchRepository {
        return ChurchRepository()
    }

    @Provides
    @Singleton
    fun providePrayerRepository(): PrayerRepository {
        return PrayerRepository()
    }
}
