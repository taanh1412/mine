package com.map.tatuongvietanh.minesweeper4


import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Switch
import android.widget.Spinner

class SettingsActivity : AppCompatActivity() {

    private lateinit var soundSwitch: Switch
    private lateinit var difficultySpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        soundSwitch = findViewById(R.id.soundSwitch)
        difficultySpinner = findViewById(R.id.difficultySpinner)

        // Load current settings
        loadSettings()

        // Set listeners for changes
        soundSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveSoundSetting(isChecked)
        }

        difficultySpinner.onItemSelectedListener = DifficultyChangeListener()
    }

    private fun loadSettings() {
        // Load settings (mocked here, you may implement shared preferences)
        soundSwitch.isChecked = true // Example setting
    }

    private fun saveSoundSetting(isChecked: Boolean) {
        // Save setting (e.g., using SharedPreferences)
    }

    private inner class DifficultyChangeListener : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            // Save difficulty level
        }

        override fun onNothingSelected(parent: AdapterView<*>) {}
    }
}
