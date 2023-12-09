package com.Yogify.birthdayreminder.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.Yogify.birthdayreminder.db.DataBaseConstant.REMINDER_DATABASE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Provides
    fun provideChannelDao(appDatabase: AppDatabase): DataDAO {
        return appDatabase.dataDao()
    }


    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java, REMINDER_DATABASE
        ).setJournalMode(RoomDatabase.JournalMode.TRUNCATE).allowMainThreadQueries().build()
    }
}