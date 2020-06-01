package com.example.damda.navigation.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.damda.navigation.DailyMissionFragment
import com.example.damda.navigation.MonthlyMissionFragment
import com.example.damda.navigation.WeeklyMissionFragment

class MissionPagerAdapter(private val myContext: Context, fm: FragmentManager, private var totalTabs: Int) : FragmentStatePagerAdapter(fm) {
    // this is for fragment tabs
    override fun getItem(position: Int): Fragment {
        println("여기는 몇번째입니까? ${position}")
        when (position) {
            0 -> {
                return DailyMissionFragment()
            }
            1 -> {
                return WeeklyMissionFragment()
            }
            else -> {
                return MonthlyMissionFragment()
            }
        }
    }

    // this counts total number of tabs
    override fun getCount(): Int {
        return totalTabs
    }
}