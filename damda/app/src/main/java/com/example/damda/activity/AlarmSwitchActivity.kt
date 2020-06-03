package com.example.damda.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import com.example.damda.GlobalApplication
import com.example.damda.R
import com.jakewharton.rxbinding2.widget.checked
import com.jakewharton.rxbinding2.widget.checkedChanges
import kotlinx.android.synthetic.main.activity_alarm_switch.*

class AlarmSwitchActivity : AppCompatActivity() {
    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_switch)

        switch_all.isChecked = GlobalApplication.prefs.push_all

        rehi_alarm.isChecked = GlobalApplication.prefs.push_rehi
        new_alarm.isChecked = GlobalApplication.prefs.push_new
        congratulations_alarm.isChecked = GlobalApplication.prefs.push_congrat
        mission_alarm.isChecked = GlobalApplication.prefs.push_mission

        switch_all.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                rehi_alarm.checkedChanges()
                rehi_alarm.isChecked = GlobalApplication.prefs.push_rehi
                new_alarm.isChecked = GlobalApplication.prefs.push_new
                congratulations_alarm.isChecked = GlobalApplication.prefs.push_congrat
                mission_alarm.isChecked = GlobalApplication.prefs.push_mission
            } else {
                rehi_alarm.isChecked = false
                new_alarm.isChecked = false
                congratulations_alarm.isChecked = false
                mission_alarm.isChecked = false
            }
            GlobalApplication.prefs.push_all = isChecked
        }
    }
}