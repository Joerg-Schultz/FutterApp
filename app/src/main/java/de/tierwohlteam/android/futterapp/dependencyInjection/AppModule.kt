package de.tierwohlteam.android.futterapp.dependencyInjection

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.tierwohlteam.android.futterapp.others.Constants.FUTTERAPP_DB_NAME
import de.tierwohlteam.android.futterapp.repositories.FutterAppDB
import de.tierwohlteam.android.futterapp.repositories.FutterAppRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFutterAppDB(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app.applicationContext,
        FutterAppDB::class.java,
        FUTTERAPP_DB_NAME
    ) //.allowMainThreadQueries() //devdebug only!!!!
        //.fallbackToDestructiveMigration() // comment out in production
        .build()

    @Singleton
    @Provides
    fun provideRepository(db: FutterAppDB) = FutterAppRepository(db)

    @Singleton
    @Provides
    fun provideRatingDao(db: FutterAppDB) = db.ratingDao()

    @Singleton
    @Provides
    fun provideFoodDao(db: FutterAppDB) = db.foodDao()

    @Singleton
    @Provides
    fun provideMealDao(db: FutterAppDB) = db.mealDao()

}