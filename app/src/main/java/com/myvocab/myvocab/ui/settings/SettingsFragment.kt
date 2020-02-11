package com.myvocab.myvocab.ui.settings

import android.app.TimePickerDialog
import android.content.Context.POWER_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.myvocab.myvocab.R
import com.myvocab.myvocab.databinding.FragmentSettingsBinding
import com.myvocab.myvocab.ui.MainNavigationFragment
import com.myvocab.myvocab.util.PackageUtils
import com.myvocab.myvocab.util.canDrawOverlays
import kotlinx.android.synthetic.main.fragment_settings.*
import java.util.*
import javax.inject.Inject


class SettingsFragment : MainNavigationFragment() {

    companion object {
        const val REQUEST_CODE_DRAW_OVERLAYS = 1
        const val REQUEST_CODE_BATTERY = 2
    }

    private lateinit var binding: FragmentSettingsBinding

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
        if (canDrawOverlays(context)) {
            viewModel.startTranslationService()
        } else {
            AlertDialog.Builder(context!!)
                    .setMessage(R.string.dialog_permission_draw_overlay)
                    .setPositiveButton(R.string.dialog_permission_allow) { _, _ ->
                        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + context!!.packageName))
                        if (PackageUtils.isIntentCallable(context!!, intent))
                            startActivityForResult(intent, REQUEST_CODE_DRAW_OVERLAYS)
                    }
                    .setNegativeButton(R.string.dialog_permission_deny) { dialog, _ ->
                        viewModel.stopTranslationService()
                        dialog.dismiss()
                    }
                    .setOnDismissListener {
                        viewModel.stopTranslationService()
                    }
                    .create()
                    .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_DRAW_OVERLAYS) {
            if(canDrawOverlays(context)) {
                viewModel.startTranslationService()
                openBatteryDialog()
            } else {
                viewModel.stopTranslationService()
            }
        }
    }

    private fun openBatteryDialog(){
        if (!ignoresPowerOptimization()) {
            AlertDialog.Builder(context!!)
                    .setMessage(R.string.dialog_permission_battery_settings)
                    .setPositiveButton(R.string.dialog_permission_allow) { _, _ ->
                        openBatterySettings()
                    }
                    .setNegativeButton(R.string.dialog_permission_deny) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun openBatterySettings() {
        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        if (PackageUtils.isIntentCallable(context!!, intent)) {
            startActivityForResult(intent, REQUEST_CODE_BATTERY)
        }
    }

    private fun ignoresPowerOptimization() =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                    (context!!.getSystemService(POWER_SERVICE) as PowerManager)
                            .isIgnoringBatteryOptimizations(context!!.packageName)

}