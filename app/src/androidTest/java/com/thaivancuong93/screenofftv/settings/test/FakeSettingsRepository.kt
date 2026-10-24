package com.thaivancuong93.screenofftv.settings.test

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.thaivancuong93.screenofftv.settings.data.ISettingsRepository
import com.thaivancuong93.screenofftv.settings.data.SettingsTokens

class FakeSettingsRepository : ISettingsRepository {
    override var solution by mutableStateOf(SettingsTokens.SolutionValue.ShizukuPowerOffScreen)

    override val compatibility = object : ISettingsRepository.Compatibility {
        override var brightnessManualWhenScreenOff: Boolean by mutableStateOf(false)
    }

}