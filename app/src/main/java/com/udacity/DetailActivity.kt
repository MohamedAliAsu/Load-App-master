package com.udacity

import android.app.DownloadManager
import android.app.DownloadManager.*
import android.app.NotificationManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        (getSystemService(NotificationManager::class.java) as NotificationManager).cancelAll()
        val downloadedFile =
            (getSystemService(DOWNLOAD_SERVICE) as DownloadManager).query(Query()
                .setFilterById(intent.getLongExtra("DOWNLOAD ID", 0)))
        Log.i("as0", downloadedFile.getColumnIndex(COLUMN_URI).toString())
        if (downloadedFile.moveToFirst()) {
            Status.text =
                when (downloadedFile.getInt(downloadedFile.getColumnIndex(COLUMN_STATUS))) {
                    STATUS_SUCCESSFUL -> "SUCCESS"
                    STATUS_FAILED -> "FAILED"
                    STATUS_PAUSED -> "PAUSED"
                    STATUS_PENDING -> "PENDING"
                    STATUS_RUNNING -> "RUNNING"
                    else -> "UNKNOWN ERROR"
                }
            fileName.text = downloadedFile.getString(downloadedFile.getColumnIndex(
                COLUMN_TITLE))


        }

        okButton.setOnClickListener{
            constraintLayout.transitionToStart()
            finish()
        }

    }

}
