package com.myvocab.myvocab.ui.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.paolorotolo.appintro.ISlidePolicy
import com.myvocab.myvocab.R
import dagger.android.support.DaggerFragment

class IntroEnableFastTranslationFragment : DaggerFragment(), ISlidePolicy {

//    @Inject
//    lateinit var translationServiceManager: FastTranslationServiceManager

//    private var triedToRequestPermission = false

//    private val dialog by lazy {
//        AlertDialog.Builder(context!!)
//                .setMessage(R.string.dialog_permission_draw_overlay)
//                .setPositiveButton(R.string.dialog_permission_allow) { _, _ ->
//                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                            Uri.parse("package:" + context!!.packageName))
//                    if (PackageUtils.isIntentCallable(context!!, intent))
//                        startActivityForResult(intent, REQUEST_CODE_DRAW_OVERLAYS)
//                }
//                .setNegativeButton(R.string.dialog_permission_deny) { dialog, _ ->
//                    triedToRequestPermission = true
//                    (context as IntroActivity).exit()
//                    dialog.dismiss()
//                }
//                .create()
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_intro_enable_fast_translate, container, false)
    }

    override fun onUserIllegallyRequestedNextPage() {

    }

    override fun isPolicyRespected(): Boolean = true
//            when {
//                canDrawOverlays(context) -> {
//                    translationServiceManager.start()
//                    true
//                }
//                triedToRequestPermission -> {
//                    (context as IntroActivity).exit()
//                    false
//                }
//                else -> {
//                    dialog.show()
//                    false
//                }
//            }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQUEST_CODE_DRAW_OVERLAYS) {
//            if (canDrawOverlays(context)) {
//                startTranslationService()
//                openBatteryDialog()
//            }
//        }
//    }
//
//    private fun openBatteryDialog() {
//        if (!ignoresPowerOptimization(context)) {
//            AlertDialog.Builder(context!!)
//                    .setMessage(R.string.dialog_permission_battery_settings)
//                    .setPositiveButton(R.string.dialog_permission_allow) { _, _ ->
//                        triedToRequestPermission = true
//                        openBatterySettings()
//                    }
//                    .setNegativeButton(R.string.dialog_permission_deny) { dialog, _ ->
//                        dialog.dismiss()
//                    }
//                    .create()
//                    .show()
//        }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.M)
//    private fun openBatterySettings() {
//        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
//        if (PackageUtils.isIntentCallable(context!!, intent)) {
//            startActivityForResult(intent, REQUEST_CODE_BATTERY)
//        }
//    }
//
//    private fun startTranslationService() {
//        translationServiceManager.start()
//    }

}