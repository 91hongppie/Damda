package com.example.damda.navigation

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.example.damda.GlobalApplication
import com.example.damda.R
import com.example.damda.activity.MainActivity
import com.example.damda.navigation.adapter.MissionPagerAdapter
import com.example.damda.navigation.model.Quiz
import com.example.damda.navigation.model.Quizs
import com.example.damda.navigation.model.Score
import com.example.damda.retrofit.service.QuizService
import com.example.damda.retrofit.service.ScoreService
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_mission.view.*
import kotlinx.android.synthetic.main.fragment_mission.view.tv_name
import kotlinx.android.synthetic.main.fragment_quiz.view.*
import kotlinx.android.synthetic.main.image_fullscreen.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MissionFragment: Fragment() {
    private var quizs = emptyArray<Quiz>()
    lateinit var quizPagerAdapter: MissionFragment.QuizPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val context = activity as MainActivity
        var retrofit = Retrofit.Builder()
            .baseUrl(GlobalApplication.prefs.damdaServer)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val jwt = GlobalApplication.prefs.token
        val user_id = GlobalApplication.prefs.user_id.toString()
        val family_id = GlobalApplication.prefs.family_id.toString()
        val state = GlobalApplication.prefs.state.toString()
        if (state == "4") {
            quizPagerAdapter = QuizPagerAdapter()
            val view = LayoutInflater.from(activity).inflate(R.layout.fragment_quiz, container, false)
            val viewPager = view.vp_quiz
            var name = view.findViewById<TextView>(R.id.tv_name)
            viewPager.adapter = QuizPagerAdapter()
            var quizService: QuizService = retrofit.create(
                QuizService::class.java)
            quizService.requestQuiz("JWT $jwt", family_id, user_id).enqueue(object: Callback<Quizs> {
                override fun onFailure(call: Call<Quizs>, t: Throwable) {

                }
                override fun onResponse(call: Call<Quizs>, response: Response<Quizs>) {
                    quizs = response.body()!!.data
                    var user_name = response.body()!!.name
                    name.text = response.body()!!.name
                    viewPager.adapter = quizPagerAdapter
                }
            })

            return view




            return view
        } else {
            val view =
                LayoutInflater.from(activity).inflate(R.layout.fragment_mission, container, false)
            val tl = view.findViewById<TabLayout>(R.id.tl_mission)
            val viewPager = view.vp_mission
            var scoreService: ScoreService = retrofit.create(
                ScoreService::class.java
            )
            scoreService.requestScore("JWT $jwt", user_id).enqueue(object :
                Callback<Score> {
                override fun onFailure(call: Call<Score>, t: Throwable) {
                    var dialog = AlertDialog.Builder(context)
                    dialog.setTitle("에러")
                    dialog.setMessage("호출실패했습니다.")
                    dialog.show()
                }

                override fun onResponse(call: Call<Score>, response: Response<Score>) {
                    var score: Score = response.body()!!
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
            tl.addTab(tl.newTab().setText("부모님 퀴즈"))
            tl.tabGravity = TabLayout.GRAVITY_FILL
            viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tl))
            viewPager.adapter = MissionPagerAdapter(context, childFragmentManager, tl.tabCount)
            tl.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    viewPager.currentItem = tab.position
                }

                override fun onTabReselected(p0: TabLayout.Tab?) {
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {

                }
            })
        return view
        }
    }
    fun refreshMissionFragment(fragment: Fragment) {
        fragment.fragmentManager?.beginTransaction()?.detach(fragment)?.attach(fragment)?.commit()
    }
    inner class QuizPagerAdapter : PagerAdapter() {

        @SuppressLint("SetTextI18n")
        override fun instantiateItem(container: ViewGroup, position: Int): Any {

            val layoutInflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = layoutInflater.inflate(R.layout.list_item_quiz, container, false)
            val quiz = quizs.get(position)
            // load image
            var question = view.findViewById<TextView>(R.id.tv_quiz)
            var answer = view.findViewById<EditText>(R.id.et_answer)
            val button = view.findViewById<Button>(R.id.btn_quiz)
            var answercheck = view.findViewById<TextView>(R.id.tv_answercheck)
            val quiz_id = quiz.id
            var quiz_answer: String? = null
            question.text = quiz.quiz
            if (quiz.answer!!.contains("no answer").not()) {
                button.visibility = View.INVISIBLE
                answer.visibility = View.INVISIBLE
                answercheck.visibility = View.VISIBLE
            } else {
                button.visibility = View.VISIBLE
                answer.visibility = View.VISIBLE
                answercheck.visibility = View.INVISIBLE
            }
            button.setOnClickListener {
                quiz_answer = answer.text.toString()
                if (quiz_answer?.length == 0) {
                    Toast.makeText(context, "답변을 적어주세요", Toast.LENGTH_SHORT).show()
                } else {
                    var retrofit = Retrofit.Builder()
                        .baseUrl(GlobalApplication.prefs.damdaServer)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                    val jwt = GlobalApplication.prefs.token
                    val family_id = GlobalApplication.prefs.family_id.toString()
                    val user_id = GlobalApplication.prefs.user_id.toString()
                    var quizService: QuizService = retrofit.create(
                        QuizService::class.java)
                    quizService.makeQuiz("JWT $jwt", family_id, user_id, quiz_id, quiz_answer!!).enqueue(object: Callback<Int> {
                        override fun onFailure(call: Call<Int>, t: Throwable) {

                        }
                        override fun onResponse(call: Call<Int>, response: Response<Int>) {
                            val data = response.body()!!
                            if (data==1) {
                                button.visibility = View.INVISIBLE
                                answer.visibility = View.INVISIBLE
                                answercheck.visibility = View.VISIBLE
                            }
                        }
                    })
                }
            }

            container.addView(view)



            return view
        }

        override fun getCount(): Int {
            return quizs.size
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj as View
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            container.removeView(obj as View)
        }
    }
    companion object {
        var my_score = 0
        var mission_fragment = Fragment()
    }
}