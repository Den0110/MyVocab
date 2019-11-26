package com.myvocab.myvocab.common.fasttranslation

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.myvocab.myvocab.R
import com.myvocab.myvocab.ui.MainActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.myvocab.myvocab.ui.fast_translation.FastTranslationWidget
import com.myvocab.myvocab.ui.fast_translation.FastTranslationWidgetViewModel
import com.myvocab.myvocab.util.*
import dagger.android.DaggerService
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class FastTranslationService : DaggerService() {

    companion object {
        private const val TAG = "FastTranslationService"
        private const val BUBBLE_VIEW_GRAVITY = Gravity.TOP or Gravity.END
        private const val BUBBLE_SHOW_TIME = 3000L
        private const val REQUEST_CODE = 1

        fun start(context: Context) {
            val startIntent = Intent(context.applicationContext, FastTranslationService::class.java)
            startIntent.action = Constants.START_FOREGROUND_ACTION
            ContextCompat.startForegroundService(context, startIntent)
        }

        fun stop(context: Context) {
            val stopIntent = Intent(context.applicationContext, FastTranslationService::class.java)
            stopIntent.action = Constants.STOP_FOREGROUND_ACTION
            context.startService(stopIntent)
        }
    }

    private lateinit var clipboardManager: ClipboardManager
    private lateinit var clipboard: Observable<String?>
    private lateinit var clipboardDisposable: Disposable

    @Inject
    lateinit var translationWidget: FastTranslationWidget

    private var serviceStarterAlarmManager: AlarmManager? = null
    private lateinit var pendingIntent: PendingIntent

    @Inject
    lateinit var windowManager: WindowManager
    @Inject
    lateinit var inflater: LayoutInflater

    private var bubbleRootView: View? = null
    private var translateBtn: View? = null

    private var bubbleShowAnim: Animation? = null
    private var bubbleHideAnim: Animation? = null

    private val bubbleHandler = Handler()
    private val hideBubbleRunnable = Runnable { hideBubble() }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")

        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard = Observable.create {
            val onPrimaryClipChangedListener = ClipboardManager.OnPrimaryClipChangedListener {
                val clip = clipboardManager.primaryClip
                if (clip?.itemCount!! > 0 && clip.getItemAt(0)?.text != null) {
                    it.onNext(clip.getItemAt(0).text.toString())
                }
            }
            clipboardManager.addPrimaryClipChangedListener(onPrimaryClipChangedListener)
            it.setCancellable {
                clipboardManager.removePrimaryClipChangedListener(onPrimaryClipChangedListener)
            }
        }

        clipboardDisposable = clipboard
                .throttleFirst(300, TimeUnit.MILLISECONDS)
                .doOnNext { Log.d(FastTranslationWidgetViewModel.TAG, "Copied: $it") }
                .filter { it.isNotEmpty() && it[0].toInt() in 65..122 }
                .subscribe ({
                    showBubble(it!!)
                }, {
                    Log.e(TAG, it.toString())
                })

        pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE,
                Intent(this, FastTranslationServiceStarter::class.java), 0)

        bubbleRootView = inflater.inflate(R.layout.fast_translation_bubble_view, null)

        val bubbleParams = getDefaultWindowParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT)
        bubbleParams.gravity = BUBBLE_VIEW_GRAVITY
        bubbleParams.y = dpToPixels(64)
        windowManager.addView(bubbleRootView, bubbleParams)

        translateBtn = bubbleRootView!!.findViewById(R.id.translate_btn)

        bubbleShowAnim = AnimationUtils.loadAnimation(applicationContext, R.anim.bubble_show)
        bubbleHideAnim = AnimationUtils.loadAnimation(applicationContext, R.anim.bubble_hide)
        bubbleHideAnim!!.setAnimationListener(object : AnimationListenerAdapter() {
            override fun onAnimationEnd(animation: Animation) {
                translateBtn!!.visibility = View.GONE
            }
        })

    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (Constants.START_FOREGROUND_ACTION == intent.action) {
            val notificationIntent = Intent(this, MainActivity::class.java)
            notificationIntent.action = Constants.MAIN_ACTION
            notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

            val notification = NotificationCompat.Builder(this, FAST_TRANSLATION_CHANNEL_ID)
                    .setContentTitle("Translator")
                    .setTicker("Translator")
                    .setLargeIcon(ContextCompat.getDrawable(applicationContext, R.mipmap.ic_launcher)?.toBitmap())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentText("Fast translation enabled")
                    .setContentIntent(pendingIntent)
                    .build()

            Log.d(TAG, "Starting foreground service")
            startForeground(Constants.NotificationId.FOREGROUND_SERVICE, notification)
            startServiceStarter()

        } else if (Constants.STOP_FOREGROUND_ACTION == intent.action) {
            Log.d(TAG, "Stopping foreground service")
            stopServiceStarter()
            stopForeground(true)
            stopSelf()
        }
        return START_STICKY
    }

    private fun showBubble(translatable: String) {
        translateBtn!!.visibility = View.VISIBLE
        translateBtn!!.startAnimation(bubbleShowAnim)
        bubbleRootView!!.setOnClickListener {
            hideBubble()
            translationWidget.start(translatable)
        }
        bubbleHandler.postDelayed(hideBubbleRunnable, BUBBLE_SHOW_TIME)
    }

    private fun hideBubble() {
        translateBtn!!.startAnimation(bubbleHideAnim)
        bubbleRootView!!.setOnClickListener(null)
        bubbleHandler.removeCallbacks(hideBubbleRunnable)
    }

    private fun startServiceStarter() {
        if (serviceStarterAlarmManager == null) {
            serviceStarterAlarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            serviceStarterAlarmManager?.setRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + 5000,
                    60000, pendingIntent)
        }
    }

    private fun stopServiceStarter() {
        serviceStarterAlarmManager?.cancel(pendingIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
        if (bubbleRootView != null) {
            windowManager.removeView(bubbleRootView)
            bubbleRootView = null
        }
        clipboardDisposable.dispose()
        translationWidget.finish()
    }

}