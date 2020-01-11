package com.myvocab.myvocab.ui.settings

import android.app.TimePickerDialog
import android.widget.CompoundButton
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.myvocab.myvocab.common.FastTranslationServiceManager
import com.myvocab.myvocab.common.ReminderScheduler
import com.myvocab.myvocab.common.moveTimeToNextDayIfNeeded
import com.myvocab.myvocab.data.source.WordRepository
import com.myvocab.myvocab.util.Event
import java.util.*
import javax.inject.Inject

class SettingsViewModel
@Inject
constructor(
        private val wordRepository: WordRepository,
        private val translationServiceManager: FastTranslationServiceManager,
        private val reminderScheduler: ReminderScheduler
) : ViewModel() {

    val translationEnabled: MutableLiveData<Boolean> = MutableLiveData()
    val reminderEnabled: MutableLiveData<Boolean> = MutableLiveData()
    val remindOnlyWordsToLearn: MutableLiveData<Boolean> = MutableLiveData()
    val remindingTime: MutableLiveData<Calendar> = MutableLiveData()

    val startTranslationServiceMessage: MutableLiveData<Event<Unit>> = MutableLiveData()

    val translationListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        if (isChecked) {
            startTranslationServiceMessage.value = Event(Unit)
        } else {
            stopTranslationService()
        }
    }

    val reminderListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        if (isChecked) {
            reminderScheduler.schedule()
        } else {
            reminderScheduler.cancel()
        }
        reminderEnabled.value = isChecked
    }

    val remindOnlyWordsToLearnListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        reminderScheduler.setRemindOnlyWordsToLearn(isChecked)
        remindOnlyWordsToLearn.value = isChecked
    }

    val remindingTimeListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
        val time = Calendar.getInstance()
        time.apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
            moveTimeToNextDayIfNeeded()
        }
        remindingTime.value = time
        reminderScheduler.scheduleIfEnabled(time.timeInMillis)
    }

    init {
        translationEnabled.value = translationServiceManager.isServiceRunning()
        reminderEnabled.value = reminderScheduler.isReminderEnabled()
        remindOnlyWordsToLearn.value = reminderScheduler.isRemindOnlyWordsToLearn()
        remindingTime.value = Calendar.getInstance().apply { time = Date(reminderScheduler.getRemindingTime()) }
    }

    fun startTranslationService() {
        translationServiceManager.start()
        translationEnabled.value = true
    }

    fun stopTranslationService() {
        translationServiceManager.cancel()
        translationEnabled.value = false
    }

    fun removeAllWords() {
        wordRepository.deleteAllWords().subscribe()
        wordRepository.deleteAllWordSets().subscribe()
    }

}