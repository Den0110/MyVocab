package com.myvocab.myvocab.ui.settings

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.myvocab.myvocab.R
import com.myvocab.myvocab.databinding.FragmentSettingsBinding
import com.myvocab.myvocab.ui.MainNavigationFragment
import com.myvocab.myvocab.util.*
import kotlinx.android.synthetic.main.fragment_settings.*
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import java.util.*
import javax.inject.Inject

class SettingsFragment : MainNavigationFragment() {

    private lateinit var binding: FragmentSettingsBinding

    @Inject
    lateinit var preferencesManager: PreferencesManager

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory).get(SettingsViewModel::class.java)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewmodel = viewModel
        }

        service_switch.setOnCheckedChangeListener(viewModel.translationStateListener)
        service_switch.setOnClickListener(viewModel.translationClickListener)
        reminder_switch.setOnCheckedChangeListener(viewModel.reminderListener)

        if(!preferencesManager.fastTranslationGuideShowed) {
            allow_work_in_background_container.setBackgroundResource(R.drawable.word_suggestion_bg)
            preferencesManager.fastTranslationGuideShowed = true
        }

        viewModel.reminderEnabled.observe(viewLifecycleOwner, Observer {
            if (it) {
                enableReminderTimeSwitch()
                enableReminderModeSwitch()
            } else {
                disableReminderTimeSwitch()
                disableReminderModeSwitch()
            }
        })

        viewModel.remindingTime.observe(viewLifecycleOwner, Observer {
            reminding_time_value.text = DateUtils.formatDateTime(context,
                    viewModel.remindingTime.value?.timeInMillis!!, DateUtils.FORMAT_SHOW_TIME)
        })

        viewModel.startTranslationServiceMessage.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                startTranslationService()
            }
        })

    }

    override fun onResume() {
        super.onResume()
        
        if (!ignoresPowerOptimization(context)) {
            allow_work_in_background_container.visibility = View.VISIBLE
            allow_work_in_bg_btn.setOnClickListener {
                allow_work_in_background_container.background = null
                openBatterySettings()
            }
        } else {
            allow_work_in_background_container.visibility = View.GONE
        }
    }

    private fun enableReminderTimeSwitch(){
        reminding_time.alpha = 1f
        reminding_time.setOnClickListener {
            TimePickerDialog(
                    context,
                    viewModel.remindingTimeListener,
                    viewModel.remindingTime.value?.get(Calendar.HOUR_OF_DAY)!!,
                    viewModel.remindingTime.value?.get(Calendar.MINUTE)!!,
                    true
            ).show()
        }
    }

    private fun disableReminderTimeSwitch(){
        reminding_time.alpha = 0.5f
        reminding_time.setOnClickListener(null)
    }

    private fun enableReminderModeSwitch(){
        reminder_mode_switch.alpha = 1f
        reminder_mode_switch.isClickable = true
        reminder_mode_switch.setOnCheckedChangeListener(viewModel.remindOnlyWordsToLearnListener)
    }

    private fun disableReminderModeSwitch(){
        reminder_mode_switch.alpha = 0.5f
        reminder_mode_switch.isClickable = false
        reminder_mode_switch.setOnCheckedChangeListener(null)
    }

    private fun startTranslationService() {
        viewModel.startTranslationService()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun openBatterySettings() {
        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        if (PackageUtils.isIntentCallable(context!!, intent)) {
            startActivityForResult(intent, REQUEST_CODE_BATTERY)
        }
    }

}