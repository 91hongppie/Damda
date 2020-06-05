package com.ebgbs.damda.navigation.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.ebgbs.damda.navigation.DailyMissionFragment
import com.ebgbs.damda.navigation.MonthlyMissionFragment
import com.ebgbs.damda.navigation.QuizFragment
import com.ebgbs.damda.navigation.WeeklyMissionFragment

class MissionPagerAdapter(private val myContext: Context, fm: FragmentManager, private var totalTabs: Int) : FragmentStatePagerAdapter(fm) {
    // this is for fragment tabs
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                DailyMissionFragment()
            }
            1 -> {
                WeeklyMissionFragment()
            }
            2 -> {
                MonthlyMissionFragment()
            }
            else -> {
                QuizFragment()
            }
        }
    }

    // this counts total number of tabs
    override fun getCount(): Int {
        return totalTabs
    }
}