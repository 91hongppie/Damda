package com.example.damda.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.damda.GlobalApplication
import com.example.damda.R
import com.example.damda.activity.*
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.fragment_user.view.*

class UserFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_user, container, false)


        view.logout.setOnClickListener {
            GlobalApplication.prefs.token = ""
            GlobalApplication.prefs.user_id = ""
            GlobalApplication.prefs.family_id = ""
            GlobalApplication.prefs.state = ""
            var intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        view.list_request.setOnClickListener {
            var intent = Intent(context, RequestListActivity::class.java)
            startActivity(intent)
        }
        view.family_btn.setOnClickListener {
            var intent = Intent(context, AddMemberActivity::class.java)
            startActivity(intent)
        }
        return view
    }
}