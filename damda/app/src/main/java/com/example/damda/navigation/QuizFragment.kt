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
import androidx.fragment.app.Fragment
import com.example.damda.R
import com.example.damda.activity.MainActivity
import kotlinx.android.synthetic.main.list_item_quiz.view.*

class QuizFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val context = activity as MainActivity
        val view = inflater.inflate(R.layout.list_item_quiz, container, false)
        val editText = view.findViewById(R.id.et_answer) as EditText
        val button_quiz = view.findViewById<Button>(R.id.btn_quiz)
        button_quiz.setOnClickListener {
            Log.e("은비", "너걷ㅈㄱ서덕사ㅓ디ㅓㅅㅈ더사ㅓㅏ덧ㄱ더ㅏㅣ")
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