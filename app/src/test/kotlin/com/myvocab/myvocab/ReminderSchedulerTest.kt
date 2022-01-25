package com.myvocab.myvocab

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.myvocab.core.util.PreferencesManager
import com.myvocab.fasttranslation.ReminderSchedulerImpl
import com.myvocab.fasttranslation.nextRemindTime
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowAlarmManager
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull

@Config(sdk = [Build.VERSION_CODES.O_MR1])
@RunWith(RobolectricTestRunner::class)
class ReminderSchedulerTest {

    private lateinit var context: Context
    private lateinit var shadowAlarmManager: ShadowAlarmManager
    private lateinit var alarmManager: AlarmManager
    private lateinit var reminderScheduler: ReminderSchedulerImpl
    private lateinit var prefManager: PreferencesManager

    @Before
    fun init() {
        context = getApplicationContext<Context>()
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        shadowAlarmManager = Shadows.shadowOf(alarmManager)
        prefManager = PreferencesManager(context)
        reminderScheduler = ReminderSchedulerImpl(context, prefManager)

        cancel() // cancel any alarms
    }

    //
    // schedule
    //

    @Test
    fun `schedule for time from memory`() {
        // write time to the memory
        // like previous run of the app
        val time = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
        }

        prefManager.remindingTime = time.timeInMillis

        // check will it restore it for current run
        reminderScheduler.schedule()
        val nextAlarm = getNextReminder()
        assertEquals(nextAlarm?.triggerAtTime, nextRemindTime(time.timeInMillis))
    }

    @Test
    fun `schedule for specified time`() {
        val time = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
        }

        // schedule for specified time
        reminderScheduler.schedule(time.timeInMillis)
        val nextAlarm = getNextReminder()
        // check if it set and wrote time to the memory correctly
        assertEquals(nextAlarm?.triggerAtTime, nextRemindTime(time.timeInMillis))
        assertEquals(prefManager.remindingTime, nextRemindTime(time.timeInMillis))
    }

    //
    // scheduleIfEnabled
    //

    @Test
    fun `schedule for time from memory when reminder enabled`(){
        // write time to the memory
        // like previous run of the app
        val time = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 15)
        }
        prefManager.remindingTime = time.timeInMillis

        // enable reminder
        prefManager.remindingState = true

        // check will it schedule and restore time for current run
        `schedule for time from memory if reminder enabled`()
    }

    @Test
    fun `schedule for time from memory when reminder disabled`(){
        // disable reminder
        prefManager.remindingState = false

        // check won't it schedule
        `schedule for time from memory if reminder enabled`()
    }

    private fun `schedule for time from memory if reminder enabled`(){
        // save init state of reminder
        val isAlarmEnabled = prefManager.remindingState

        reminderScheduler.scheduleIfEnabled()
        val alarm = getNextReminder()

        if(isAlarmEnabled){
            // check if it scheduled for time from the memory
            assertEquals(alarm?.triggerAtTime, nextRemindTime(prefManager.remindingTime))
        } else {
            // check if it didn't schedule
            assertNull(alarm)
        }
    }

    @Test
    fun `schedule for specified time when reminder enabled`(){
        // enable reminder
        prefManager.remindingState = true
        // check will it schedule it
        `schedule for specified time if reminder enabled`()
    }

    @Test
    fun `schedule for specified time when reminder disabled`(){
        // disable reminder
        prefManager.remindingState = false
        // check won't it schedule it
        `schedule for specified time if reminder enabled`()
    }

    private fun `schedule for specified time if reminder enabled`(){
        // save init state of reminder
        val isAlarmEnabled = prefManager.remindingState

        val time = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 13)
        }

        reminderScheduler.scheduleIfEnabled(time.timeInMillis)
        val alarm = getNextReminder()

        if(isAlarmEnabled){
            // check if it scheduled, and set and wrote specified time to the memory correctly
            assertEquals(alarm?.triggerAtTime, nextRemindTime(time.timeInMillis))
            assertEquals(prefManager.remindingTime, nextRemindTime(time.timeInMillis))
        } else {
            // check if it didn't schedule
            assertNull(alarm)
        }
    }

    @Test
    fun cancel(){
        reminderScheduler.cancel()
        assertNoAlarms()
    }

    @After
    fun finish(){
        cancel()
    }

    private fun assertNoAlarms(){
        assertNull(getNextReminder())
    }

    private fun getNextReminder() = if(getReminders().isNotEmpty()) getReminders()[0] else null

    private fun getReminders() = shadowAlarmManager.scheduledAlarms

}