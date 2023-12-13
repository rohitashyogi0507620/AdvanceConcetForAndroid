package com.Yogify.birthdayreminder.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.Yogify.birthdayreminder.R
import com.Yogify.birthdayreminder.backup.DriveHelper
import com.Yogify.birthdayreminder.db.DataBaseConstant.REMINDER_DATABASE
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.annotation.Nullable
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Provides
    fun provideChannelDao(appDatabase: AppDatabase): DataDAO {
        return appDatabase.dataDao()
    }

    @Provides
    @Nullable
    fun providegoogleAccount(@ApplicationContext context: Context): GoogleSignInAccount? {
        GoogleSignIn.getLastSignedInAccount(context)?.let { lastAccount ->
            return lastAccount
        }
        return null
    }


    @Provides
    fun provideGoogleSignOption(): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_FILE)).build()
    }


    @Provides
    fun provideSignInClient(
        @ApplicationContext context: Context,
        googleSignInOptions: GoogleSignInOptions
    ): GoogleSignInClient {
        return GoogleSignIn.getClient(context, googleSignInOptions)
    }

    @Provides
    @Nullable
    fun provideDrive(
        @ApplicationContext context: Context,
        @Nullable googleAccount: GoogleSignInAccount?
    ): Drive? {
        if (googleAccount != null) {
            val credential =
                GoogleAccountCredential.usingOAuth2(context, listOf(DriveScopes.DRIVE_FILE))
            credential.selectedAccount = googleAccount.account!!
            return Drive.Builder(NetHttpTransport(), GsonFactory(), credential)
                .setApplicationName(context.getString(R.string.app_name)).build()
        } else {
            return null
        }

    }

    @Provides
    @Nullable
    fun provideDriveHelper(@Nullable drive: Drive?): DriveHelper? {
        if (drive != null) return DriveHelper(drive) else return null
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