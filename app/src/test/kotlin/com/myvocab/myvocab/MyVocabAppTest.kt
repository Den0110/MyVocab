package com.myvocab.myvocab

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.myvocab.core.util.PreferencesManager
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowAlarmManager
import kotlin.test.assertEquals
import kotlin.test.assertNull

@Config(sdk = [Build.VERSION_CODES.O_MR1])
@RunWith(RobolectricTestRunner::class)
class MyVocabAppTest {

    private lateinit var app: MyVocabApp
    private lateinit var shadowAlarmManager: ShadowAlarmManager
    private lateinit var alarmManager: AlarmManager
    private lateinit var prefManager: PreferencesManager

    @Before
    fun init() {
        app = ApplicationProvider.getApplicationContext()
        alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        shadowAlarmManager = Shadows.shadowOf(alarmManager)
        prefManager = PreferencesManager(app)
    }

    @Test
    fun `schedule reminder if enabled for time from memory`(){
        val nextAlarm = shadowAlarmManager.nextScheduledAlarm
        val isAlarmEnabled = prefManager.remindingState
        if(isAlarmEnabled) {
            assertEquals(nextAlarm?.triggerAtTime, prefManager.remindingTime)
        } else {
            assertNull(nextAlarm)
        }
    }

}