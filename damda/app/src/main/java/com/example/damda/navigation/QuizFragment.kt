package com.example.damda.navigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.damda.GlobalApplication
import com.example.damda.R
import com.example.damda.activity.MainActivity
import com.example.damda.navigation.adapter.MissionAdapter
import com.example.damda.navigation.model.Missions
import com.example.damda.navigation.model.Quiz
import com.example.damda.retrofit.service.MissionService
import com.example.damda.retrofit.service.QuizService
import kotlinx.android.synthetic.main.fragment_mission_list.view.*
import kotlinx.android.synthetic.main.list_item_quiz.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class QuizFragment: Fragment() {
    private var quiz: Quiz? = null
    private var num = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val context = activity as MainActivity
        val view = inflater.inflate(R.layout.list_item_child_quiz, container, false)
        val editText = view.findViewById<EditText>(R.id.et_answer)
        val question = view.findViewById<TextView>(R.id.tv_quiz)
        val button_quiz = view.findViewById<Button>(R.id.btn_quiz)
        var button_point = view.findViewById<Button>(R.id.btn_point)
        var check = view.findViewById<TextView>(R.id.tv_check)
        var congrat = view.findViewById<TextView>(R.id.tv_congrat)
        var no_question = view.findViewById<TextView>(R.id.tv_empty)
        editText.visibility = View.VISIBLE
        question.visibility = View.VISIBLE
        button_quiz.visibility = View.VISIBLE
        button_point.visibility = View.INVISIBLE
        check.visibility = View.INVISIBLE
        congrat.visibility = View.INVISIBLE
        no_question.visibility = View.INVISIBLE
        var retrofit = Retrofit.Builder()
            .baseUrl(GlobalApplication.prefs.damdaServer)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val jwt = GlobalApplication.prefs.token
        val user_id = GlobalApplication.prefs.user_id.toString()
        var quizService: QuizService = retrofit.create(
            QuizService::class.java)
        quizService.getQuiz("JWT $jwt", user_id).enqueue(object: Callback<Quiz> {
            override fun onFailure(call: Call<Quiz>, t: Throwable) {
                editText.visibility = View.INVISIBLE
                question.visibility = View.INVISIBLE
                button_quiz.visibility = View.INVISIBLE
                button_point.visibility = View.INVISIBLE
                check.visibility = View.INVISIBLE
                congrat.visibility = View.INVISIBLE
                no_question.visibility = View.VISIBLE
            }
            override fun onResponse(call: Call<Quiz>, response: Response<Quiz>) {
                quiz = response.body()
                question.text = quiz!!.quiz
            }
        })
        button_quiz.setOnClickListener {
            println("1111111111111111111111111111111")
            println("${editText.text}")
            println("${quiz!!.answer}")
            println("1111111111111111111111111111111")
            if ("${editText.text}" != quiz!!.answer) {
                Toast.makeText(context, "정답이 아닙니다.", Toast.LENGTH_SHORT).show()
            } else {
                button_quiz.visibility = View.INVISIBLE
                editText.visibility = View.INVISIBLE
                check.visibility = View.VISIBLE
                congrat.visibility = View.VISIBLE
                button_point.visibility = View.VISIBLE
                val quiz_id = quiz!!.id.toString()
                quizService.postQuiz("JWT $jwt", user_id, quiz_id).enqueue(object: Callback<Int> {
                    override fun onFailure(call: Call<Int>, t: Throwable) {
                        var dialog = AlertDialog.Builder(context)
                        dialog.setTitle("에러")
                        dialog.setMessage("호출실패했습니다.")
                        dialog.show()
                    }
                    override fun onResponse(call: Call<Int>, response: Response<Int>) {
                        num = response.body()!!
                        print("!111111111111111111111 ${num}")

                    }
                })
                button_point.setOnClickListener {
                    if (num == 1) {
                        this.fragmentManager?.beginTransaction()?.detach(this)?.attach(this)?.commit()
                    }
                }


            }
        }
        return view
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 1) {
            this.fragmentManager?.beginTransaction()?.detach(this)?.attach(this)?.commit()
        }
    }
}