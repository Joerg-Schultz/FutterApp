package de.tierwohlteam.android.futterapp.dependencyInjection

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import de.tierwohlteam.android.futterapp.repositories.FutterAppDB
import org.junit.Test

import org.junit.Assert.*
import javax.inject.Named
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
object TestAppModule {
    @Provides
    @Singleton
    fun provideInMemoryPTDdb(@ApplicationContext app: Context) =
        Room.inMemoryDatabaseBuilder(app, FutterAppDB::class.java)
            .allowMainThreadQueries()
            .build()
}