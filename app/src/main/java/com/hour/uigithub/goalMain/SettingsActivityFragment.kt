package com.hour.uigithub.goalMain

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.hour.uigithub.R

class SettingsActivityFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }
}