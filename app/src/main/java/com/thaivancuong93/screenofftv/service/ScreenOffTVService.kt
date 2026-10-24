package com.thaivancuong93.screenofftv.service

import com.thaivancuong93.screenofftv.i18n.UiText
import com.thaivancuong93.screenofftv.R
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.thaivancuong93.screenofftv.shizuku.DisplayControlService
import com.thaivancuong93.screenofftv.shizuku.EventsProviderService
import com.thaivancuong93.screenofftv.shizuku.IDisplayControl
import com.thaivancuong93.screenofftv.shizuku.IEventsProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import com.thaivancuong93.screenofftv.BuildConfig
import com.thaivancuong93.screenofftv.ExceptionScenes
import com.thaivancuong93.screenofftv.ISolutionDependencyManager
import com.thaivancuong93.screenofftv.SolutionDependencyManager
import com.thaivancuong93.screenofftv.notifyException
import com.thaivancuong93.screenofftv.service.hosts.AwakeHost
import com.thaivancuong93.screenofftv.service.hosts.NotificationHost
import com.thaivancuong93.screenofftv.service.hosts.VolumeKeyEventShizukuHost
import com.thaivancuong93.screenofftv.settings.data.ISettingsRepository
import com.thaivancuong93.screenofftv.settings.data.SettingsTokens
import com.thaivancuong93.screenofftv.settings.data.SettingsTokens.SolutionValue.ShizukuPowerOffScreen
import com.thaivancuong93.screenofftv.settings.data.SettingsTokens.SolutionValue.ShizukuScreenBrightnessNeg1
import com.thaivancuong93.screenofftv.settings.data.SyncSettingsRepository
import com.thaivancuong93.screenofftv.settings.data.settingsDataStore
import rikka.shizuku.Shizuku

private const val TAG = "ScreenOffTVService"

class ScreenOffTVService : LifecycleService() {

    enum class State {
        Destroyed, Created, Prepared, Error
    }

    companion object {
        const val ACTION_LANGUAGE_CHANGED = "com.thaivancuong93.screenofftv.LANGUAGE_CHANGED"
        @JvmStatic
        val state = MutableStateFlow(State.Destroyed)
        val lastError = MutableStateFlow<UiText?>(null)

        @JvmStatic
        val screenState = MutableStateFlow(ScreenState.On)

        const val EXTRA_CHECK_CONNECTION = "check_connection"
        val connectionReport = MutableStateFlow<UiText?>(null)
        const val EXTRA_SCREEN = "screen"
        const val EXTRA_VALUE_SCREEN_ON = 0
        const val EXTRA_VALUE_SCREEN_OFF = 1
        const val EXTRA_VALUE_SCREEN_SWITCH = 2

        const val EXTRA_STOP = "stop"
        const val EXTRA_VALUE_STOP = 1

        const val EXTRA_VALUE_INVALID = -1
    }

    lateinit var solutionDependencyManager: ISolutionDependencyManager

    var awakeHost: AwakeHost? = null
    var volumeKeyEventShizukuHost: VolumeKeyEventShizukuHost? = null
    val notificationHost by lazy { NotificationHost(this) }

    private val updateHostStateMutex = Mutex()

    /**
     * should be invoked in main thread.
     * */
    suspend fun updateHostState(
        requestScreenOn: Boolean
    ) {
        updateHostStateMutex.withLock {
            if (requestScreenOn) awakeHost?.stopKeepAwake()
            else awakeHost?.startKeepAwake()

            volumeKeyEventShizukuHost?.wake()

            if (requestScreenOn) notificationHost.notify(requestScreenOn)
        }
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate: ")
        super.onCreate()
        solutionDependencyManager = SolutionDependencyManager(this).also {
            it.updateImmediately()
        }
        screenState.update { ScreenState.On }
        val notification = notificationHost.notify(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NotificationHost.NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(NotificationHost.NOTIFICATION_ID, notification)
        }
    }

    /**
     * Control ScreenOffTV Service with Intent
     *
     * ## PendingIntent Internal
     *
     * ## Shell Command External
     *
     * ```
     * adb shell am startservice -n com.thaivancuong93.screenofftv/.service.ScreenOffTVService --ei [key] [int_value]
     * ```
     * */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: ")
        if (intent?.action == ACTION_LANGUAGE_CHANGED) {
            notificationHost.notify(isScreenOn)
            return super.onStartCommand(intent, flags, startId)
        }

        intent?.getIntExtra(EXTRA_STOP, EXTRA_VALUE_INVALID)?.let {
            if (it == EXTRA_VALUE_STOP) {
                stopSelf()
                return super.onStartCommand(intent, flags, startId)
            }
        }

        when (state.value) {
            State.Destroyed -> initialize()
            State.Error -> initialize()
            else -> Unit
        }

        if (intent?.getBooleanExtra(EXTRA_CHECK_CONNECTION, false) == true) {
            lifecycleScope.launch {
                val displayAlive = displayControlService?.asBinder()?.isBinderAlive == true
                val inputAlive = withContext(Dispatchers.IO) {
                    runCatching { eventsProviderService?.isRunning == true }.getOrDefault(false)
                }
                connectionReport.value = when {
                    displayAlive && inputAlive -> UiText(R.string.connection_good)
                    state.value == State.Created -> UiText(R.string.connection_pending)
                    else -> UiText(R.string.connection_not_ready)
                }
            }
        }
        processArgs(intent)
        return super.onStartCommand(intent, flags, startId)
    }

    var processArgsJob: Job? = null

    private fun processArgs(intent: Intent?) {
        if (processArgsJob?.isActive == true) return
        processArgsJob = lifecycleScope.launch {
            initializeJob?.join()
            if (state.value != State.Prepared) return@launch

            intent?.getIntExtra(EXTRA_STOP, EXTRA_VALUE_INVALID)?.let {
                if (it == EXTRA_VALUE_STOP) Log.d(TAG, "request stop service")
            }
            intent?.getIntExtra(EXTRA_SCREEN, EXTRA_VALUE_INVALID)?.let {
                Log.d(TAG, "EXTRA_SCREEN: $it")
                when (it) {
                    EXTRA_VALUE_SCREEN_ON -> lifecycleScope.launch(Dispatchers.Main) {
                        if (!isScreenOn) {
                            updateHostState(true)
                            turnScreenOn()
                        }
                    }

                    EXTRA_VALUE_SCREEN_OFF -> lifecycleScope.launch(Dispatchers.Main) {
                        if (isScreenOn) {
                            updateHostState(false)
                            turnScreenOff()
                        }
                    }

                    EXTRA_VALUE_SCREEN_SWITCH -> lifecycleScope.launch(Dispatchers.Main) {
                        if (isScreenOn) {
                            updateHostState(false)
                            turnScreenOff()
                        } else {
                            updateHostState(true)
                            turnScreenOn()
                        }
                    }

                    else -> Unit
                }
            }

        }
    }

    var initializeJob: Job? = null

    private fun initialize() {
        if (initializeJob?.isActive == true) return
        initializeJob = lifecycleScope.launch(Dispatchers.Main) {
            try {
            lastError.value = null
            state.update { State.Created }

            if (Shizuku.pingBinder() && checkShizukuPermission()) {
                bindEventsProviderService()
                bindDisplayControlService()
            }
            val settingRepository = SyncSettingsRepository(settingsDataStore)
            settingRepository.update()
            loadFeatureFromSettings(settingRepository)

            withTimeout(30000) {
                while (!Shizuku.pingBinder()) delay(25)
            }
            solutionDependencyManager.updateImmediately()
            if (!solutionDependencyManager.state.value.isShizukuPermissionGranted) {
                lastError.value = UiText(R.string.shizuku_unavailable)
                state.update { State.Error }
                stopSelf()
                return@launch
            }

            awakeHost = AwakeHost(this@ScreenOffTVService)

            bindEventsProviderService()

            bindDisplayControlService()

            withTimeout(30000) {
                try {
                    while (
                        displayControlService == null ||
                        eventsProviderService == null
                    ) {
                        delay(10)
                    }
                } finally {
                    if (isActive) {
                        awakeHost?.create()

                        eventsProviderService?.let { service ->
                            volumeKeyEventShizukuHost = VolumeKeyEventShizukuHost(
                                this@ScreenOffTVService, service
                            ) {
                                lifecycleScope.launch(Dispatchers.Main) {
                                    if (isScreenOn) {
                                        updateHostState(false)
                                        turnScreenOff()
                                    } else {
                                        updateHostState(true)
                                        turnScreenOn()
                                    }
                                }
                            }.also { it.register() }

                            service.launch(null)
                            withContext(Dispatchers.IO) {
                                displayControlService?.prepare()?.withResult { name, detail ->
                                    Log.w(TAG, "Display warm-up: $name: $detail")
                                }
                            }
                        }

                        registerSystemLockReceiver()

                        notificationHost.notify(isScreenOn).also {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                startForeground(NotificationHost.NOTIFICATION_ID, it, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
                            } else {
                                startForeground(NotificationHost.NOTIFICATION_ID, it)
                            }
                        }

                        state.update { State.Prepared }
                    } else {
                        notifyException(
                            ExceptionScenes.ExceptionWhenAccessShizukuRemote,
                            "binding shizuku remote service timeout"
                        )
                        state.update { State.Error }
                    }
                }
            }
            } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
                lastError.value = UiText(R.string.connection_timeout)
                stopSelf()
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Cannot initialize TV service", e)
                lastError.value = UiText(R.string.connection_failed)
                stopSelf()
            }
        }
    }

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): ScreenOffTVService = this@ScreenOffTVService
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.d(TAG, "onBind: ")
        super.onBind(intent)
        if (BuildConfig.DEBUG) return binder
        return null
    }

    override fun onRebind(intent: Intent?) {
        Log.d(TAG, "onRebind: ")
        super.onRebind(intent)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: ")
        initializeJob?.cancel()
        processArgsJob?.cancel()
        runBlocking {
            if (state.value == State.Prepared && screenState.value == ScreenState.Off) {
                turnScreenOn()
            }
            unregisterSystemLockReceiver()
            awakeHost?.destroy()
            runCatching { volumeKeyEventShizukuHost?.unregister() }

            eventsProviderService?.let {
                runCatching { it.stop() }
                unbindEventsProviderService()
            }

            displayControlService?.let {
                unbindDisplayControlService()
            }

            solutionDependencyManager.destroy()
            super.onDestroy()
            state.update { State.Destroyed }
        }
    }

    //== Feature

    var feature = Feature(
        solution = SettingsTokens.SolutionValue.valueOf(SettingsTokens.Solution.default),
        brightnessManualWhenScreenOff = SettingsTokens.Compatibility.BrightnessManualWhenScreenOff.default,
    )

    data class Feature(
        val solution: SettingsTokens.SolutionValue,
        val brightnessManualWhenScreenOff: Boolean,
    )

    private fun loadFeatureFromSettings(repository: ISettingsRepository) {
        feature = Feature(repository.solution, repository.compatibility.brightnessManualWhenScreenOff)
    }

    //== Bind with shizuku

    fun checkShizukuPermission() =
        Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED

    var displayControlService: IDisplayControl? = null

    private val displayControlServiceArgs = Shizuku.UserServiceArgs(
        ComponentName(
            BuildConfig.APPLICATION_ID,
            DisplayControlService::class.java.name
        )
    )
        .processNameSuffix(DisplayControlService.TAG)
        .tag(DisplayControlService.TAG)
        .debuggable(BuildConfig.DEBUG)
        .version(BuildConfig.VERSION_CODE)
        .daemon(false)

    private val displayControlServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service != null && Shizuku.pingBinder()) {
                displayControlService = IDisplayControl.Stub.asInterface(service)
            } else {
                notifyException(
                    ExceptionScenes.ExceptionWhenAccessShizukuRemote,
                    "get a null DisplayControlService connect or binder dead"
                )
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            displayControlService = null
            lastError.value = UiText(R.string.shizuku_disconnected)
            stopSelf()
        }

    }

    private var displayBinding = false
    fun bindDisplayControlService() {
        if (displayBinding) return
        displayBinding = true
        try {
            Shizuku.bindUserService(
                displayControlServiceArgs,
                displayControlServiceConnection
            )
        } catch (e: Exception) {
            notifyException(ExceptionScenes.ExceptionWhenAccessShizukuRemote, e)
        }
    }

    fun unbindDisplayControlService() {
        try {
            Shizuku.unbindUserService(
                displayControlServiceArgs,
                displayControlServiceConnection,
                true
            )
        } catch (e: Exception) {
            notifyException(ExceptionScenes.ExceptionWhenAccessShizukuRemote, e)
        }
    }

    var eventsProviderService: IEventsProvider? = null

    private val eventsProviderServiceArgs = Shizuku.UserServiceArgs(
        ComponentName(
            BuildConfig.APPLICATION_ID,
            EventsProviderService::class.java.name
        )
    )
        .processNameSuffix(EventsProviderService.TAG)
        .tag(EventsProviderService.TAG)
        .debuggable(BuildConfig.DEBUG)
        .version(BuildConfig.VERSION_CODE)
        .daemon(false)

    private val eventsProviderServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service != null && Shizuku.pingBinder()) {
                eventsProviderService = IEventsProvider.Stub.asInterface(service)
            } else {
                notifyException(
                    ExceptionScenes.ExceptionWhenAccessShizukuRemote,
                    "get a null DisplayControlService connect or binder dead"
                )
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            eventsProviderService = null
            lastError.value = UiText(R.string.remote_disconnected)
            stopSelf()
        }

    }

    private var eventsBinding = false
    fun bindEventsProviderService() {
        if (eventsBinding) return
        eventsBinding = true
        try {
            Shizuku.bindUserService(
                eventsProviderServiceArgs,
                eventsProviderServiceConnection
            )
        } catch (e: Exception) {
            notifyException(ExceptionScenes.ExceptionWhenAccessShizukuRemote, e)
        }
    }

    fun unbindEventsProviderService() {
        try {
            Shizuku.unbindUserService(
                eventsProviderServiceArgs,
                eventsProviderServiceConnection,
                true
            )
        } catch (e: Exception) {
            notifyException(ExceptionScenes.ExceptionWhenAccessShizukuRemote, e)
        }
    }

    //== Screen state
    private val screenControlMutex = Mutex()
    val isScreenOn get() = screenState.value == ScreenState.On
    var recordBrightness = 255
    var recordBrightnessMode = Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC

    enum class ScreenState {
        On, Off
    }

    suspend fun turnScreenOn() {
        screenControlMutex.withLock {
            screenState.update { ScreenState.On }
            try {
                when (feature.solution) {
                    ShizukuPowerOffScreen -> {
                        checkNotNull(displayControlService).setPowerModeToSurfaceControl(
                            DisplayControlService.POWER_MODE_NORMAL
                        )?.withResult { name, detail ->
                            throw IllegalStateException("$name: $detail")
                        }
                    }

                    ShizukuScreenBrightnessNeg1 -> {
                        checkNotNull(displayControlService).setBrightnessToSurfaceControl(
                            // Release the backlight-off value, then restore system brightness below.
                            0f
                        )?.withResult { name, detail ->
                            throw IllegalStateException("$name: $detail")
                        }
                    }
                }
            } catch (e: Exception) {
                screenState.value = ScreenState.Off
                updateHostState(false)
                lastError.value = UiText(R.string.screen_on_failed, e.localizedMessage.orEmpty())
                notifyException(ExceptionScenes.ExceptionWhenAccessShizukuRemote, e)
                return@withLock
            }
            if (feature.solution == ShizukuScreenBrightnessNeg1) {
            try {
                val firstBrightness = if (recordBrightness < 8) recordBrightness + 5
                else recordBrightness - 5
                displayControlService?.setBrightnessToSetting(firstBrightness)
                delay(25)
                displayControlService?.setBrightnessToSetting(recordBrightness)

            } catch (e: Exception) {
                notifyException(ExceptionScenes.ExceptionWhenAccessShizukuRemote, e)
            }
            try {
                if (feature.brightnessManualWhenScreenOff && feature.solution == ShizukuScreenBrightnessNeg1) {
                    displayControlService?.setBrightnessModeToSetting(recordBrightnessMode)
                }
            } catch (e: Exception) {
                notifyException(ExceptionScenes.ExceptionWhenAccessShizukuRemote, e)
            }            }

        }
    }

    suspend fun turnScreenOff() {
        screenControlMutex.withLock {
            screenState.update { ScreenState.Off }
            if (feature.solution == ShizukuScreenBrightnessNeg1) {
            recordBrightness = try {
                Settings.System.getInt(
                    contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS
                )
            } catch (e: Exception) {
                notifyException(ExceptionScenes.SystemUnsupportedBrightnessSettings, e)
                128
            }
            if (feature.brightnessManualWhenScreenOff && feature.solution == ShizukuScreenBrightnessNeg1) {
                recordBrightnessMode = try {
                    Settings.System.getInt(
                        contentResolver,
                        Settings.System.SCREEN_BRIGHTNESS_MODE
                    )
                } catch (e: Exception) {
                    notifyException(ExceptionScenes.SystemUnsupportedBrightnessSettings, e)
                    Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
                }
                try {
                    displayControlService?.setBrightnessModeToSetting(Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL)
                } catch (e: Exception) {
                    notifyException(ExceptionScenes.ExceptionWhenAccessShizukuRemote, e)
                }
            }
            }
            try {
                when (feature.solution) {
                    ShizukuPowerOffScreen -> {
                        checkNotNull(displayControlService).setPowerModeToSurfaceControl(
                            DisplayControlService.POWER_MODE_OFF
                        )?.withResult { name, detail ->
                            throw IllegalStateException("$name: $detail")
                        }
                    }

                    ShizukuScreenBrightnessNeg1 -> {
                        checkNotNull(displayControlService).setBrightnessToSurfaceControl(
                            // SurfaceControl uses -1 to turn the backlight off; 0 is only minimum brightness.
                            -1f
                        )?.withResult { name, detail ->
                            throw IllegalStateException("$name: $detail")
                        }
                    }
                }
                notificationHost.notify(false)
            } catch (e: Exception) {
                screenState.value = ScreenState.On
                updateHostState(true)
                lastError.value = UiText(R.string.screen_off_failed, e.localizedMessage.orEmpty())
                notifyException(ExceptionScenes.ExceptionWhenAccessShizukuRemote, e)
            }
        }
    }

    /**
     * Listen system lock. Update host when locking.
     * */
    private var systemLockReceiver: BroadcastReceiver? = null

    private fun registerSystemLockReceiver() {
        systemLockReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                lifecycleScope.launch(Dispatchers.Main) {
                    screenControlMutex.withLock {
                        if (feature.solution == ShizukuScreenBrightnessNeg1) {
                            displayControlService?.setBrightnessToSetting(recordBrightness)
                        }
                    }
                    updateHostState(true)
                }
            }
        }
        systemLockReceiver?.let {
            registerReceiver(
                it,
                IntentFilter().apply {
                    addAction(Intent.ACTION_SCREEN_OFF)
                }
            )
        }
    }

    private fun unregisterSystemLockReceiver() {
        systemLockReceiver?.let {
            unregisterReceiver(it)
            systemLockReceiver = null
        }
    }

}
