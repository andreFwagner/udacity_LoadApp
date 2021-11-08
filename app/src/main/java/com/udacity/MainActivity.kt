package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

/**
 * MainActivity to display Downloadoptions, initiate Download and handle Notification once finished
 */
class MainActivity : AppCompatActivity() {

    private val NOTIFICATION_ID = 0
    private val REQUEST_CODE = 0
    private val FLAGS = 0

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        notificationManager =
            ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager

        createChannel("load_app_channel", "loadApp")

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        custom_button.setOnClickListener {
            custom_button.buttonState = ButtonState.Loading
            download(
                when (radio_group.checkedRadioButtonId) {
                    retrofit_button.id -> RETROFIT_URL
                    app_button.id -> APP_URL
                    glide_button.id -> GLIDE_URL
                    else -> null
                }
            )
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            custom_button.buttonState = ButtonState.Completed

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val cursor = downloadManager.query(DownloadManager.Query().setFilterById(id!!))

            if (cursor.moveToFirst()) {
                val status = when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                    DownloadManager.STATUS_SUCCESSFUL -> true
                    else -> false
                }

                val fileName = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))
                val fileDescription = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION))
                val fileText = "$fileName - $fileDescription"

                notificationManager.sendNotification(status, fileText, applicationContext)
            }
        }
    }

    private fun NotificationManager.sendNotification(status: Boolean, fileText: String, applicationContext: Context) {
        val extras = Bundle()
        extras.putBoolean("status", status)
        extras.putString("fileText", fileText)
        val contentIntent = Intent(applicationContext, DetailActivity::class.java)
        contentIntent.putExtras(extras)
        pendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(applicationContext, "load_app_channel")
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentTitle(resources.getString(R.string.notification_title))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(R.drawable.ic_assistant_black_24dp, getString(R.string.notification_button), pendingIntent)

        if (status) builder.setContentText(resources.getString(R.string.notification_description))
        else builder.setContentText(resources.getString(R.string.notification_description_fail))

        notify(NOTIFICATION_ID, builder.build())
    }

    private fun createChannel(channelId: String, channelName: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.channel_description)

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun download(url: String?) {
        if (url != null) {
            val request =
                DownloadManager.Request(Uri.parse(url))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            when (url) {
                APP_URL -> {
                    request.setTitle(getString(R.string.app_name))
                    request.setDescription(getString(R.string.app_description))
                }
                GLIDE_URL -> {
                    request.setTitle(getString(R.string.glide_name))
                    request.setDescription(getString(R.string.glide_description))
                }
                RETROFIT_URL -> {
                    request.setTitle(getString(R.string.retrofit_name))
                    request.setDescription(getString(R.string.retrofit_description))
                }
            }

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request) // enqueue puts the download request in the queue.
        } else {
            custom_button.buttonState = ButtonState.Clicked
            val toast = Toast.makeText(applicationContext, resources.getText(R.string.please_select), Toast.LENGTH_SHORT)
            toast.show()
        }
    }

    companion object {
        private const val APP_URL = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/refs/heads/master.zip"
        private const val RETROFIT_URL = "https://github.com/square/retrofit/archive/refs/heads/master.zip"
        private const val GLIDE_URL = "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
    }
}
