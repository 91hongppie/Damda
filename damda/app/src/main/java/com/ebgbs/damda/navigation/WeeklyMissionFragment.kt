package com.ebgbs.damda.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.ebgbs.damda.GlobalApplication
import com.ebgbs.damda.R
import com.ebgbs.damda.activity.MainActivity
import com.ebgbs.damda.navigation.adapter.MissionAdapter
import com.ebgbs.damda.navigation.model.Mission
import com.ebgbs.damda.navigation.model.Missions
import com.ebgbs.damda.retrofit.service.MissionService
import kotlinx.android.synthetic.main.fragment_mission_list.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeeklyMissionFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val context = activity as MainActivity
        val view = inflater.inflate(R.layout.fragment_mission_list, container, false)
        var missionList = emptyArray<Mission>()
        var retrofit = Retrofit.Builder()
            .baseUrl(GlobalApplication.prefs.damdaServer)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val jwt = GlobalApplication.prefs.token
        val user_id = GlobalApplication.prefs.user_id.toString()
        var missionService: MissionService = retrofit.create(
            MissionService::class.java)
        view.rv_mission.adapter = MissionAdapter(missionList, context,this@WeeklyMissionFragment)
        missionService.requestMission("JWT $jwt", user_id, 1).enqueue(object: Callback<Missions> {
            override fun onFailure(call: Call<Missions>, t: Throwable) {
                var dialog = AlertDialog.Builder(context)
                dialog.setTitle("에러")
                dialog.setMessage("호출실패했습니다.")
                dialog.show()
            }
            override fun onResponse(call: Call<Missions>, response: Response<Missions>) {
                val missions = response.body()
                missionList = missions!!.data
                view.rv_mission.adapter = MissionAdapter(missionList, context,this@WeeklyMissionFragment)
            }
        })
        view.rv_mission?.layoutManager = GridLayoutManager(activity, 1)

        return view
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 1) {
            this.fragmentManager?.beginTransaction()?.detach(this)?.attach(this)?.commit()
        }
    }
}