package com.example.damda.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import androidx.core.view.isVisible
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
        if (GlobalApplication.prefs.push_all) {
            switch_each.visibility = View.VISIBLE
        } else {
            switch_each.visibility = View.INVISIBLE
        }
        switch_all.isChecked = GlobalApplication.prefs.push_all
        Log.d("PUSH TAG", "${GlobalApplication.prefs.push_all}, ${GlobalApplication.prefs.push_rehi}, ${GlobalApplication.prefs.push_new}, ${GlobalApplication.prefs.push_congrat}, ${GlobalApplication.prefs.push_mission}")
        rehi_alarm.isChecked = GlobalApplication.prefs.push_rehi
        new_alarm.isChecked = GlobalApplication.prefs.push_new
        congratulations_alarm.isChecked = GlobalApplication.prefs.push_congrat
        mission_alarm.isChecked = GlobalApplication.prefs.push_mission

        switch_all.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                switch_each.visibility = View.VISIBLE
            } else {
                switch_each.visibility = View.INVISIBLE
            }
            GlobalApplication.prefs.push_all = isChecked
        }

        rehi_alarm.setOnCheckedChangeListener { buttonView, isChecked ->
            GlobalApplication.prefs.push_rehi = isChecked
        }

        new_alarm.setOnCheckedChangeListener { buttonView, isChecked ->
            GlobalApplication.prefs.push_new = isChecked
        }

        congratulations_alarm.setOnCheckedChangeListener { buttonView, isChecked ->
            GlobalApplication.prefs.push_congrat = isChecked
        }

        mission_alarm.setOnCheckedChangeListener { buttonView, isChecked ->
            GlobalApplication.prefs.push_mission = isChecked
        }
    }
}