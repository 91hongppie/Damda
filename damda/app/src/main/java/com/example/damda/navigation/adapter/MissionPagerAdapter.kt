package com.example.damda.navigation.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.damda.activity.CropperActivity
import com.example.damda.navigation.MissionListFragment

class MissionPagerAdapter(private val myContext: Context, fm: FragmentManager, private var totalTabs: Int) : FragmentStatePagerAdapter(fm) {
    // this is for fragment tabs
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                println("여기는 0번 탭입니다. $position")
                mission_period = 0
                return MissionListFragment()
            }
            1 -> {
                println("여기는 1번 탭입니다. $position")
                mission_period = 1
                return MissionListFragment()
            }
            else -> {
                println("여기는 2번 탭입니다. $position")
                mission_period = 2
                return MissionListFragment()
            }
        }
    }

    // this counts total number of tabs
    override fun getCount(): Int {
        return totalTabs
    }
    companion object {
        var mission_period = 0
    }
}