package de.tierwohlteam.android.futterapp.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@SmallTest
@HiltAndroidTest
class FutterAppDBTest{
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("testDB")
    lateinit var db: FutterAppDB

    @Before
    internal fun setup() {
        hiltRule.inject()
    }
    /**
     * build the database in memory.
     * Tests only the PTDdb file,not the Database Builder
     */
    @Test
    internal fun getDatabaseTest(){
        assertThat(db).isNotNull()
    }
}
