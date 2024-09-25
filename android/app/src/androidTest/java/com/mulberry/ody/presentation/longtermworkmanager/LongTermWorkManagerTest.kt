package com.mulberry.ody.presentation.longtermworkmanager

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.hilt.work.HiltWorkerFactory
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.mulberry.ody.data.local.service.LongTermJobUUIDStore
import com.mulberry.ody.data.local.service.LongTermWorkManager
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime
import javax.inject.Inject

@HiltAndroidTest
class LongTermWorkManagerTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var testDataStore: DataStore<Preferences>

    val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setup() {
        hiltRule.inject()
        val config =
            Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .setMaxSchedulerLimit(50)
                .setMinimumLoggingLevel(Log.DEBUG)
                .setExecutor(SynchronousExecutor())
                .build()

        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
    }

    @Test
    fun a() =
        runBlocking {
            // when
            val workManager = WorkManager.getInstance(context)
            val dataStore = LongTermJobUUIDStore(testDataStore)

            val longTermWorkManager = LongTermWorkManager<String>(workManager, dataStore)
            val startTime = LocalDateTime.now()
            val endTime = LocalDateTime.now().plusSeconds(40)
            longTermWorkManager.enqueueLongTermJob(1, startTime, endTime)
            assertEquals(longTermWorkManager.longTermJobUUIDStore, dataStore)
            val uuid = dataStore.getJobUUID(1).first()
            /*
            val flow = longTermWorkManager.getWorkProgressFlow(1)
            val actual = flow.first()

            // then
            assertEquals("test", actual)
             */
        }
}
