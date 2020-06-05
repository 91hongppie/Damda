package com.ebgbs.damda.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ebgbs.damda.GlobalApplication
import com.ebgbs.damda.R
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