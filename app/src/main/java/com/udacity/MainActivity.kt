package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.udacity.util.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var selectedURL: String
    private lateinit var selectedName: String
    private lateinit var downloadManager: DownloadManager

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            selectedName = view.text.toString()
            when (view.getId()) {
                R.id.radioOptionGlide -> {
                    selectedURL = URL_GLIDE
                }
                R.id.radioOptionLoadApp -> {
                    selectedURL = URL_STARTER
                }
                R.id.radioOptionRetrofit -> {
                    selectedURL = URL_RETROFIT
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        createChannel()

        custom_button.setOnClickListener {
            if (this::selectedURL.isInitialized) {
                if (custom_button.getButtonState() != ButtonState.Loading) {
                    custom_button.setButtonState(ButtonState.Loading)
                    download()
                }
            } else {
                Toast.makeText(this, R.string.option_none, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            context?.let {
                val query = DownloadManager.Query().setFilterById(id!!)
                val cursor: Cursor = downloadManager.query(query)
                if (cursor.moveToFirst() && cursor.count > 0) {
                    val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    val status = when (cursor.getInt(columnIndex)) {
                        DownloadManager.STATUS_FAILED -> getString(R.string.status_failed)
                        DownloadManager.STATUS_PAUSED -> getString(R.string.status_paused)
                        DownloadManager.STATUS_PENDING -> getString(R.string.status_pending)
                        DownloadManager.STATUS_RUNNING -> getString(R.string.status_running)
                        DownloadManager.STATUS_SUCCESSFUL -> getString(R.string.status_successful)
                        else -> getString(R.string.status_none)
                    }
                    notificationManager = getSystemService(
                            NotificationManager::class.java
                    )
                    // send notification
                    notificationManager.sendNotification(
                            selectedName,
                            status,
                            CHANNEL_ID,
                            context,
                    )
                }
                // set button state to completed
                custom_button.setButtonState(ButtonState.Completed)
            }
        }
    }

    private fun download() {
        val request =
                DownloadManager.Request(Uri.parse(selectedURL))
                        .setTitle(getString(R.string.app_name))
                        .setDescription(getString(R.string.app_description))
                        .setRequiresCharging(false)
                        .setAllowedOverMetered(true)
                        .setAllowedOverRoaming(true)

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
                downloadManager.enqueue(request)// enqueue puts download request in the queue.
    }

    companion object {
        private const val URL_GLIDE =
                "https://github.com/bumptech/glide/archive/master.zip"
        private const val URL_RETROFIT =
                "https://github.com/square/retrofit/archive/master.zip"
        private const val URL_STARTER =
                "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
        private const val CHANNEL_NAME = "channelName"
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Timber.e("innitialized")
            val notificationChannel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH).apply {
                setShowBadge(false)
            }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)

            notificationManager = getSystemService(
                    NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }
}
