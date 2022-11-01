package com.udacity

import android.app.DownloadManager
import android.app.DownloadManager.*
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_main.view.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        notificationManager =
            getSystemService(NotificationManager::class.java) as NotificationManager
        registerReceiver(receiver, IntentFilter(ACTION_DOWNLOAD_COMPLETE))
        createNotificationChannel()
        include.custom_button.setOnClickListener {
            notificationManager.cancelAll()
            download()

        }

    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(EXTRA_DOWNLOAD_ID, -1)
            val query = Query()
            query.setFilterById(id!!)
            val downloadedFile =
                (getSystemService(DOWNLOAD_SERVICE) as DownloadManager).query(query)
            var fileName = ""
            if (downloadedFile.moveToFirst()) {
                fileName = downloadedFile.getString((downloadedFile.getColumnIndex(COLUMN_TITLE)))

            }
            notificationManager.sendNotification(applicationContext, "$fileName download completed",id)
        }
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            (getSystemService(NotificationManager::class.java) as NotificationManager).createNotificationChannel(
                NotificationChannel(CHANNEL_ID,
                    "Download Channel",
                    NotificationManager.IMPORTANCE_HIGH))
    }

    private fun download() {
        if (radioGroup.checkedRadioButtonId == -1) {
            runOnUiThread {
                Toast.makeText(this, "please check one option to download ", Toast.LENGTH_LONG)
                    .show()
            }


        } else {
            val selectedLink =
                findViewById<RadioButton>(radioGroup.checkedRadioButtonId).tag.toString()
            Log.i("qw211",selectedLink)
            val request =
                Request(Uri.parse(selectedLink))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)
                    .setTitle(when (radioGroup.checkedRadioButtonId) {
                        glide.id -> "glide library"
                        retrofit.id -> "retrofit"
                        else -> "project Repository"
                    })
                    .setMimeType("zip")


            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.

        }
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"

    }

}

fun NotificationManager.sendNotification(context: Context, msg: String,downloadId :Long) {
    val notificationbuilt = NotificationCompat.Builder(context, "channelId").apply {
        setContentText(msg)
        setSmallIcon(R.drawable.ic_assistant_black_24dp)
        setContentTitle("Udacity - Android Kotlin Nanodegree")
        priority = NotificationCompat.PRIORITY_HIGH
        addAction(R.drawable.abc_vector_test,
            "check download status",
            PendingIntent.getActivity(context,
                notificationId,
                Intent(context, DetailActivity::class.java).putExtra("DOWNLOAD ID",downloadId),
                PendingIntent.FLAG_ONE_SHOT))




    }
    notify(notificationId, notificationbuilt.build())
}

private const val notificationId = 0