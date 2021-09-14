package de.tierwohlteam.android.futterapp.dependencyInjection

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.tierwohlteam.android.futterapp.repositories.FutterAppDB
import org.junit.Test

import org.junit.Assert.*
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {
    @Provides
    @Named("testDB")
    fun provideInMemoryPTDdb(@ApplicationContext app: Context) =
        Room.inMemoryDatabaseBuilder(app, FutterAppDB::class.java)
            .allowMainThreadQueries()
            .build()

    @Provides
    @Named("testRatingDao")
    fun provideRatingDao(db: FutterAppDB) = db.ratingDao()

    @Provides
    @Named("testFoodDao")
    fun provideFoodDao(db: FutterAppDB) = db.foodDao()
}