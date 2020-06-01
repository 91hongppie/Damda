package com.example.damda.navigation

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.damda.GlobalApplication
import com.example.damda.R
import com.example.damda.activity.MainActivity
import com.example.damda.navigation.adapter.MissionPagerAdapter
import com.example.damda.navigation.model.Score
import com.example.damda.retrofit.service.ScoreService
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_mission.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MissionFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val context = activity as MainActivity
        val view = LayoutInflater.from(activity).inflate(R.layout.fragment_mission, container, false)
        val tl = view.findViewById<TabLayout>(R.id.tl_mission)
        val viewPager = view.vp_mission
        var retrofit = Retrofit.Builder()
            .baseUrl(GlobalApplication.prefs.damdaServer)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val jwt = GlobalApplication.prefs.token
        val user_id = GlobalApplication.prefs.user_id.toString()
        var scoreService: ScoreService = retrofit.create(
            ScoreService::class.java)
        scoreService.requestScore("JWT $jwt", user_id).enqueue(object:
            Callback<Score> {
            override fun onFailure(call: Call<Score>, t: Throwable) {
                var dialog = AlertDialog.Builder(context)
                dialog.setTitle("에러")
                dialog.setMessage("호출실패했습니다.")
                dialog.show()
            }
            override fun onResponse(call: Call<Score>, response: Response<Score>) {
                var score:Score = response.body()!!
                view.tv_score.text = score.score.toString()
                my_score = score.score
                if (my_score < 50) {
                    view.tv_thename.text = "병아리 효자"
                } else if (my_score in 50..199) {
                    view.tv_thename.text = "효자"
                } else if (my_score in 200..599) {
                    view.tv_thename.text = "프로 효자"
                } else if (my_score in 600..999) {
                    view.tv_thename.text = "대장 효자"
                } else if (my_score in 1000..9999999) {
                    view.tv_thename.text = "전설의 효자"
                } else {
                    view.tv_thename.text = "집에서 놀고먹어도 인정하는 효자"
                }
                view.tv_name.text = score.name
            }
        })
        mission_fragment = this
        tl!!.addTab(tl.newTab().setText("일일 미션"))
        tl.addTab(tl.newTab().setText("주간 미션"))
        tl.addTab(tl.newTab().setText("월간 미션"))
        tl.tabGravity = TabLayout.GRAVITY_FILL
        tl.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {
            }
        })
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tl))
        viewPager.adapter = MissionPagerAdapter(context, childFragmentManager!!, tl.tabCount)
        return view
    }
    fun refreshMissionFragment(fragment: Fragment) {
        fragment.fragmentManager?.beginTransaction()?.detach(fragment)?.attach(fragment)?.commit()
    }
    companion object {
        var my_score = 0
        var mission_fragment = Fragment()
    }
}