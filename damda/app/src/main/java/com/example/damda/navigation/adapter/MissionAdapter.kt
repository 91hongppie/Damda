package com.example.damda.navigation.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.damda.GlobalApplication
import com.example.damda.R
import com.example.damda.activity.MainActivity
import com.example.damda.activity.MissionAddPhotoActivity
import com.example.damda.navigation.MissionFragment
import com.example.damda.navigation.MissionFragment.Companion.mission_fragment
import com.example.damda.navigation.MissionFragment.Companion.my_score
import com.example.damda.navigation.model.Mission
import com.example.damda.navigation.model.Score
import com.example.damda.retrofit.service.MissionService
import com.example.damda.retrofit.service.ScoreService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MissionAdapter (val missionList: Array<Mission>, val activity: MainActivity, val fragment: Fragment) : RecyclerView.Adapter<MissionAdapter.CustomViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_mission, parent, false)
        val url = GlobalApplication.prefs.damdaServer
        return CustomViewHolder(view, url)
    }

    override fun getItemCount(): Int {
        return missionList.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(missionList[position])
    }

    inner class CustomViewHolder(val view: View, val url: String) : RecyclerView.ViewHolder(view) {
        val title = view.findViewById<TextView>(R.id.tv_title)
        val point = view.findViewById<TextView>(R.id.tv_point)
        val proceed = view.findViewById<TextView>(R.id.tv_progress)
        val progressbar = view.findViewById<ProgressBar>(R.id.pb_mission)
        val mission_btn = view.findViewById<Button>(R.id.btn_mission)
        val check = view.findViewById<ImageView>(R.id.iv_check)
        val prize = view.findViewById<TextView>(R.id.tv_prize)
        val mission_check = view.findViewById<TextView>(R.id.btn_checkmission)
        val cl_mission = view.findViewById<ConstraintLayout>(R.id.cl_mission)
        @SuppressLint("SetTextI18n")
        fun bind(mission: Mission) {
            title.text = mission.title
            point.text = mission.point.toString() + " Point"
            if (mission.status == 1) {
                if (mission.prize == 0) {
                    proceed.text = "1 / 1"
                    progressbar.progress = 100
                    check.visibility = View.INVISIBLE
                    prize.visibility = View.INVISIBLE
                    mission_check.visibility = View.INVISIBLE
                    cl_mission.visibility = View.VISIBLE
                    mission_btn.visibility = View.VISIBLE
                    point.visibility = View.VISIBLE
                    mission_btn.setOnClickListener {
                        var retrofit = Retrofit.Builder()
                            .baseUrl(GlobalApplication.prefs.damdaServer)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()
                        val jwt = GlobalApplication.prefs.token
                        val user_id = GlobalApplication.prefs.user_id.toString()
                        var scoreService: ScoreService = retrofit.create(
                            ScoreService::class.java)
                        my_score += mission.point
                        scoreService.changeScore("JWT $jwt", user_id, my_score, mission.id).enqueue(object:
                            Callback<Score> {
                            override fun onFailure(call: Call<Score>, t: Throwable) {
                                var dialog = AlertDialog.Builder(MainActivity())
                                dialog.setTitle("에러")
                                dialog.setMessage("호출실패했습니다.")
                                dialog.show()
                            }
                            override fun onResponse(call: Call<Score>, response: Response<Score>) {
                                var score: Score = response.body()!!
                                println(score)
                                my_score = score.score
                                cl_mission.visibility = View.VISIBLE
                                check.visibility = View.VISIBLE
                                prize.visibility = View.VISIBLE
                                mission_check.visibility = View.INVISIBLE
                                mission_btn.visibility = View.INVISIBLE
                                point.visibility = View.INVISIBLE

                            }
                        })
                    }
                } else {
                    cl_mission.visibility = View.VISIBLE
                    check.visibility = View.VISIBLE
                    prize.visibility = View.VISIBLE
                    mission_check.visibility = View.INVISIBLE
                    mission_btn.visibility = View.INVISIBLE
                    point.visibility = View.INVISIBLE
                    proceed.text = "1 / 1"
                    progressbar.progress = 100
                }
            } else {
                proceed.text = "0 / 1"
                progressbar.progress = 0
                cl_mission.visibility = View.VISIBLE
                mission_check.visibility = View.VISIBLE
                check.visibility = View.INVISIBLE
                prize.visibility = View.INVISIBLE
                mission_btn.visibility = View.INVISIBLE
                point.visibility = View.VISIBLE
            }
            mission_check.setOnClickListener {
                var intent = Intent(activity, MissionAddPhotoActivity::class.java)
                intent.putExtra("mission_id", mission.id)
                intent.putExtra("mission_title", mission.title)
                intent.putExtra("period", mission.period)
                fragment.startActivityForResult(intent, 1)
            }
        }
    }

    companion object {
        var photos = 0
    }
}
