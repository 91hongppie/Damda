package com.example.damda.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.damda.GlobalApplication
import com.example.damda.R
import com.example.damda.navigation.AlarmFragment
import com.example.damda.navigation.AlbumListFragment
import com.example.damda.navigation.PhotoListFragment
import com.example.damda.navigation.PhotoListFragment.Companion.photoArray
import com.example.damda.navigation.UserFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.iid.FirebaseInstanceId
import com.jakewharton.rxbinding2.view.layoutChanges
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.jetbrains.anko.above
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
<<<<<<< HEAD
import androidx.fragment.app.Fragment
import com.example.damda.GlobalApplication
import com.example.damda.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import okhttp3.*
=======
>>>>>>> c77e416b129a31685514ddfff7e08966f17ce7d1
import java.io.IOException
import java.net.URL

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottom_navigation.setOnNavigationItemSelectedListener(this)
        if (navStatus == 1) {
            bottom_navigation.layoutParams.height = 0
        } else {
            bottom_navigation.layoutParams.height = WRAP_CONTENT
        }
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

        bottom_navigation.selectedItemId = R.id.action_album_list
    }

    private fun sendRegistrationToServer(token: String) {
        val url = URL(getString(R.string.damda_server)+"/api/accounts/addtoken/")
        val jwt = GlobalApplication.prefs.token
        val formBody = FormBody.Builder()
            .add("token", token)
            .build()
        val request = Request.Builder().url(url).addHeader("Authorization", "JWT $jwt").method("POST", formBody)
            .build()
        val client = OkHttpClient()

        val callback = Callback1()

        client.newCall(request).enqueue(callback)
    }

    inner class Callback1: Callback {
        override fun onFailure(call: okhttp3.Call, e: IOException) {

        }

        override fun onResponse(call: okhttp3.Call, response: Response) {

            val result = response.body()?.string()

            Log.d("Server response", "result: $result")
        }
    }

    fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.main_content,fragment).addToBackStack(null).commit()
    }
    fun replaceNavbar(){
        if (navStatus == 1) {
            bottom_navigation.layoutParams.height = 0
        } else {
            bottom_navigation.layoutParams.height = WRAP_CONTENT
        }
    }
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when (p0.itemId) {
            R.id.action_album_list -> {
                var albumListFragment = AlbumListFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, albumListFragment)
                    .commit()
                photoStatus = 0
                return true
            }
            R.id.action_photo_list -> {
                var photoListFragment = PhotoListFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, photoListFragment)
                    .commit()
                photoStatus = 0
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
        var navStatus = 0
        var photoStatus = 0
        private const val TAG = "MainActivity"
    }

}
