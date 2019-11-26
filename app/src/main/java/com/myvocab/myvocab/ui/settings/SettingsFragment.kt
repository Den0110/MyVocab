package com.myvocab.myvocab.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.myvocab.myvocab.BuildConfig
import com.myvocab.myvocab.R
import com.myvocab.myvocab.common.fasttranslation.FastTranslationService
import com.myvocab.myvocab.databinding.FragmentSettingsBinding
import com.myvocab.myvocab.util.PackageUtils
import com.myvocab.myvocab.util.getFastTranslationState
import com.myvocab.myvocab.util.isServiceRunning
import com.myvocab.myvocab.util.setFastTranslationState
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_settings.*
import javax.inject.Inject

class SettingsFragment : DaggerFragment() {

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

        if(BuildConfig.DEBUG){
            remove_all_words.visibility = View.VISIBLE
            remove_all_words.setOnClickListener { viewModel.removeAllWords() }
        }

        service_switch.setOnCheckedChangeListener { _, isChecked ->
            val isRunning = isServiceRunning(context!!, FastTranslationService::class.java)
            if (isChecked) {
                if (!isRunning){
                    startTranslationService()
                }
            } else {
                if (isRunning){
                    stopTranslationService()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val state = getFastTranslationState(context!!)
        val isRunning = isServiceRunning(context!!, FastTranslationService::class.java)
        service_switch.isChecked = state
        if(state && !isRunning){
            startTranslationService()
        } else if (!state && isRunning){
            stopTranslationService()
        }
    }

    private fun startTranslationService(){
        if (Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(context)) {
            FastTranslationService.start(context!!)
            setFastTranslationState(context!!, true)
        } else {
            AlertDialog.Builder(context!!)
                    .setMessage(R.string.dialog_permission_draw_overlay)
                    .setPositiveButton(R.string.dialog_permission_allow) { _, _ ->
                        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + context!!.packageName))
                        if(PackageUtils.isIntentCallable(context!!, intent))
                            startActivityForResult(intent, REQUEST_CODE_DRAW_OVERLAYS)
                    }
                    .setNegativeButton(R.string.dialog_permission_deny) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
        }
    }

    private fun stopTranslationService(){
        FastTranslationService.stop(context!!)
        setFastTranslationState(context!!, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_DRAW_OVERLAYS) {
            FastTranslationService.start(context!!)
            setFastTranslationState(context!!, true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun openBatterySettings(){
        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        if (PackageUtils.isIntentCallable(context!!, intent)) {
            startActivityForResult(intent, REQUEST_CODE_BATTERY)
        }
    }

}