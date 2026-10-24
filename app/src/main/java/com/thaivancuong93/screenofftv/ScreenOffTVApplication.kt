package com.thaivancuong93.screenofftv

import android.app.Application

class ScreenOffTVApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        createNotificationChannels()
    }
}
