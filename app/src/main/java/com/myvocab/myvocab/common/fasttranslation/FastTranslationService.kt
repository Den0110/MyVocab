package com.myvocab.myvocab.common.fasttranslation

import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.SystemClock
import android.webkit.URLUtil
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.myvocab.myvocab.R
import com.myvocab.myvocab.ui.MainActivity
import com.myvocab.myvocab.ui.fast_translation.FastTranslationWidget
import com.myvocab.myvocab.util.*
import dagger.android.DaggerService
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class FastTranslationService : DaggerService() {

    companion object {
        private const val REQUEST_CODE = 121
        private const val SERVICE_STARTER_PERIOD = 5*60*1000L

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

    override fun onCreate() {
        super.onCreate()
        Timber.d("Service created")

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
                .doOnNext { Timber.d("Copied: $it") }
                // filter empty, non-English or url strings
                .filter { it.isNotEmpty() && it[0].toInt() in 65..122 && !URLUtil.isValidUrl(it) }
                .subscribe ({
                    translate(it!!)
                }, {
                    Timber.e(it)
                })

        pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE,
                Intent(this, FastTranslationServiceStarter::class.java), 0)

        translationWidget.lifecycle.addObserver(LifecycleEventObserver { owner, event ->
            if(event == Lifecycle.Event.ON_DESTROY){
                NotificationManagerCompat.from(this)
                        .notify(Constants.NotificationId.FOREGROUND_SERVICE, getDefaultNotification())
            }
        })

    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (Constants.START_FOREGROUND_ACTION == intent.action) {
            Timber.d("Starting foreground service")
            startForeground(Constants.NotificationId.FOREGROUND_SERVICE, getDefaultNotification())
            startServiceStarter()
        } else if (Constants.STOP_FOREGROUND_ACTION == intent.action) {
            Timber.d("Stopping foreground service")
            stopServiceStarter()
            stopForeground(true)
            stopSelf()
        }
        return START_STICKY
    }

    private fun translate(translatable: String){
        translationWidget.start(translatable)
    }

    private fun getDefaultNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.action = Constants.MAIN_ACTION
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        return NotificationCompat.Builder(this, FAST_TRANSLATION_CHANNEL_ID)
                .setContentTitle("Fast Translation")
                .setTicker("Fast Translation")
                .setLargeIcon(ContextCompat.getDrawable(applicationContext, R.mipmap.ic_launcher)?.toBitmap())
                .setSmallIcon(getNotificationIconId())
                .setContentText("Select and copy a word to translate")
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .build()
    }

    private fun startServiceStarter() {
        if (serviceStarterAlarmManager == null) {
            serviceStarterAlarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            serviceStarterAlarmManager?.setRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + 5000,
                    SERVICE_STARTER_PERIOD, pendingIntent)
        }
    }

    private fun stopServiceStarter() {
        serviceStarterAlarmManager?.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("Service destroyed")
        clipboardDisposable.dispose()
        translationWidget.finish()
    }

}