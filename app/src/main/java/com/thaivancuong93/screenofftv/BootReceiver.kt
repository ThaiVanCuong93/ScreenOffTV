package com.thaivancuong93.screenofftv

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.thaivancuong93.screenofftv.service.ScreenOffTVService
import com.thaivancuong93.screenofftv.tv.TvPreferences

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        if (!TvPreferences(context).autoStart) return
        // The service waits for Shizuku with a bounded timeout, after entering foreground.
        runCatching {
            ContextCompat.startForegroundService(context, Intent(context, ScreenOffTVService::class.java))
        }.onFailure { Log.e("BootReceiver", "Cannot start TV service after boot", it) }
    }
}
