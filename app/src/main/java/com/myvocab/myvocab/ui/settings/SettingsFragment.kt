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
import androidx.lifecycle.ViewModelProvider
import com.myvocab.myvocab.R
import com.myvocab.myvocab.databinding.FragmentSettingsBinding
import com.myvocab.myvocab.ui.MainNavigationFragment
import com.myvocab.myvocab.util.PackageUtils
import com.myvocab.myvocab.util.PreferencesManager
import com.myvocab.myvocab.util.REQUEST_CODE_BATTERY
import com.myvocab.myvocab.util.ignoresPowerOptimization
import java.util.*
import javax.inject.Inject

class SettingsFragment : MainNavigationFragment() {

    private lateinit var binding: FragmentSettingsBinding

    @Inject
    lateinit var preferencesManager: PreferencesManager

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
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

        binding.serviceSwitch.setOnCheckedChangeListener(viewModel.translationStateListener)
        binding.serviceSwitch.setOnClickListener(viewModel.translationClickListener)
        binding.reminderSwitch.setOnCheckedChangeListener(viewModel.reminderListener)

        if(!preferencesManager.fastTranslationGuideShowed) {
            binding.allowWorkInBackgroundContainer.setBackgroundResource(R.drawable.word_suggestion_bg)
            preferencesManager.fastTranslationGuideShowed = true
        }

        viewModel.reminderEnabled.observe(viewLifecycleOwner, {
            if (it) {
                enableReminderTimeSwitch()
                enableReminderModeSwitch()
            } else {
                disableReminderTimeSwitch()
                disableReminderModeSwitch()
            }
        })

        viewModel.remindingTime.observe(viewLifecycleOwner, {
            binding.remindingTimeValue.text = DateUtils.formatDateTime(context,
                    viewModel.remindingTime.value?.timeInMillis!!, DateUtils.FORMAT_SHOW_TIME)
        })

        viewModel.startTranslationServiceMessage.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let {
                startTranslationService()
            }
        })

    }

    override fun onResume() {
        super.onResume()
        
        if (!ignoresPowerOptimization(context)) {
            binding.allowWorkInBackgroundContainer.visibility = View.VISIBLE
            binding.allowWorkInBgBtn.setOnClickListener {
                binding.allowWorkInBackgroundContainer.background = null
                openBatterySettings()
            }
        } else {
            binding.allowWorkInBackgroundContainer.visibility = View.GONE
        }
    }

    private fun enableReminderTimeSwitch(){
        binding.remindingTime.alpha = 1f
        binding.remindingTime.setOnClickListener {
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
        binding.remindingTime.alpha = 0.5f
        binding.remindingTime.setOnClickListener(null)
    }

    private fun enableReminderModeSwitch(){
        binding.reminderModeSwitch.alpha = 1f
        binding.reminderModeSwitch.isClickable = true
        binding.reminderModeSwitch.setOnCheckedChangeListener(viewModel.remindOnlyWordsToLearnListener)
    }

    private fun disableReminderModeSwitch(){
        binding.reminderModeSwitch.alpha = 0.5f
        binding.reminderModeSwitch.isClickable = false
        binding.reminderModeSwitch.setOnCheckedChangeListener(null)
    }

    private fun startTranslationService() {
        viewModel.startTranslationService()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun openBatterySettings() {
        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        if (PackageUtils.isIntentCallable(requireContext(), intent)) {
            startActivityForResult(intent, REQUEST_CODE_BATTERY)
        }
    }

}