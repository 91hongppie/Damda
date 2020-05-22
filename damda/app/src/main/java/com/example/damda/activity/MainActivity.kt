package com.example.damda.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.damda.navigation.AlarmFragment
import com.example.damda.navigation.PhotoListFragment
import com.example.damda.navigation.AlbumListFragment
import com.example.damda.navigation.UserFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import androidx.fragment.app.Fragment
import com.example.damda.MyFirebaseInstanceIdService
import com.example.damda.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottom_navigation.setOnNavigationItemSelectedListener(this)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token
                sendRegistrationToServer(token!!)
                // Log and toast
                val msg = getString(R.string.msg_token_fmt, token)
                Log.d(TAG, msg)

                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            })

        bottom_navigation.selectedItemId = R.id.action_search
    }

    fun sendRegistrationToServer(token: String) {
        var thread = NetworkThread()
        thread.start()
    }

    inner class NetworkThread: Thread() {
        override fun run() {

            var token : String? = null

            @SuppressLint("HandlerLeak")
            val handler: Handler = object : Handler() {
                override fun handleMessage(message: Message) {
                    token = message.obj.toString()
                }
            }

            var client = OkHttpClient()

            var builder = Request.Builder()
            var url = builder.url("http://10.0.2.2:8000/api/accounts/addtoken/")
            var formBody = FormBody.Builder()

            var body = formBody
                .add("token", token!!)
                .build()

            var request = url
                .post(body)
                .build()

            var callback = Callback1()

            client.newCall(request).enqueue(callback)

        }

        inner class Callback1: Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {

            }

            override fun onResponse(call: okhttp3.Call, response: Response) {

                var result = response?.body()?.string()

                Log.d("Server response", "result: $result")
            }
        }
    }

    fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.main_content,fragment).addToBackStack(null).commit()
    }
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when (p0.itemId) {
            R.id.action_album_list -> {
                var gridFragment = AlbumListFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, gridFragment)
                    .commit()
                return true
            }
            R.id.action_photo_list -> {
                var gridFragment = AlbumListFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, gridFragment)
                    .commit()
                return true
            }
            R.id.action_add_photo -> {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    startActivity(Intent(this, AddPhotoActivity::class.java))
                }
                return true
            }
            R.id.action_favorite_alarm -> {
                var alarmFragment = AlarmFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, alarmFragment)
                    .commit()
                return true
            }
            R.id.action_account -> {
                var userFragment = UserFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, userFragment)
                    .commit()
                return true
            }
        }
        return false
    }
    interface RetrofitNetwork { @GET("/network") fun listUser() : Call<Array<String>> }

    val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8000")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = retrofit.create(RetrofitNetwork::class.java)

    companion object {

        private const val TAG = "MainActivity"
    }

}
