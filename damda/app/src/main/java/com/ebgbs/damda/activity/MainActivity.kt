package com.ebgbs.damda.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import androidx.fragment.app.Fragment
import com.ebgbs.damda.GlobalApplication
import com.ebgbs.damda.GlobalApplication.Companion.prefs
import com.ebgbs.damda.ImageUpload
import com.ebgbs.damda.R
import com.ebgbs.damda.navigation.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.net.URL

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    var mBackWait: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseMessaging.getInstance().isAutoInitEnabled = true

        bottom_navigation.setOnNavigationItemSelectedListener(this)

        if (!prefs.my_album) {
            checkMyAlbum()
        }

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        if (prefs.autoStatus && intent.getStringExtra("before") != "AddPhoto") {
            if ((activeNetwork?.type == ConnectivityManager.TYPE_MOBILE && prefs.mobileAutoUpload) || (activeNetwork?.type == ConnectivityManager.TYPE_WIFI)) {
                checkMidea()
            }
        }

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
            })
        if (intent.getBooleanExtra("사진업로드", false)) {
            bottom_navigation.selectedItemId = R.id.action_photo_list
        } else {
            bottom_navigation.selectedItemId = R.id.action_album_list
        }
    }

    private fun sendRegistrationToServer(token: String) {
        prefs.device_token = token
        Log.d("SharedPreference", "${prefs.device_token}")

        val url = URL(prefs.damdaServer + "/api/accounts/device/")
        val jwt = GlobalApplication.prefs.token
        val formBody = FormBody.Builder()
            .add("token", token)
            .add("user_id", "${prefs.user_id}")
            .build()
        val request = Request.Builder().url(url).addHeader("Authorization", "JWT $jwt")
            .method("POST", formBody)
            .build()
        val client = OkHttpClient()

        val callback = Callback1()

        client.newCall(request).enqueue(callback)

        Log.d("SEND TOKEN", "MY PHONE: $token")
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.main_content, fragment)
            .addToBackStack(null).commit()
    }

    fun replaceNavbar() {
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
                supportFragmentManager.popBackStack(null, POP_BACK_STACK_INCLUSIVE)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, albumListFragment)
                    .commit()
                photoStatus = 0
                return true
            }
            R.id.action_photo_list -> {
                var photoListFragment = PhotoListFragment()
                supportFragmentManager.popBackStack(null, POP_BACK_STACK_INCLUSIVE)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, photoListFragment)
                    .commit()
                photoStatus = 0
                return true
            }
            R.id.action_add_photo -> {
                supportFragmentManager.popBackStack(null, POP_BACK_STACK_INCLUSIVE)
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    intent = Intent(this, ImagePickerActivity::class.java)
                    intent.putExtra("before", "addPhoto")
                    startActivity(intent)
                }
                return true
            }
            R.id.action_favorite_alarm -> {
                var missionFragment = MissionFragment()
                supportFragmentManager.popBackStack(null, POP_BACK_STACK_INCLUSIVE)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, missionFragment)
                    .commit()
                return true
            }
            R.id.action_account -> {
                var userFragment = UserFragment()
                supportFragmentManager.popBackStack(null, POP_BACK_STACK_INCLUSIVE)
                supportFragmentManager.beginTransaction().replace(R.id.main_content, userFragment)
                    .commit()
                return true
            }
        }
        return false
    }

    interface RetrofitNetwork {
        @GET("/network")
        fun listUser(): Call<Array<String>>
    }

    val retrofit = Retrofit.Builder()
        .baseUrl(prefs.damdaServer)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = retrofit.create(RetrofitNetwork::class.java)

    companion object {
        var navStatus = 0
        var photoStatus = 0
        var currentPosition = 0
        private const val TAG = "MainActivity"
    }

    override fun onBackPressed() {
        // 뒤로가기 버튼 클릭
        if (supportFragmentManager.backStackEntryCount == 0) {
            if (System.currentTimeMillis() - mBackWait >= 2000) {
                mBackWait = System.currentTimeMillis()
                Toast.makeText(this, "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
            } else {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }

    @SuppressLint("Recycle")
    fun checkMidea() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val imageProjection =
                arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA)
            val lastId = prefs.autoId
            val selection = "${MediaStore.Images.Media.DATA} NOT LIKE ?"
            val selectionArgs = arrayOf(
                "%damda%"
            )
            val imageCursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                imageProjection,
                selection,
                selectionArgs,
                MediaStore.Images.Media._ID + " desc "
            )
            val images = arrayListOf<File>()
            val paths = arrayListOf<String>()
            val ids = arrayListOf<String>()
            val imageIdIndex = imageCursor?.getColumnIndex(MediaStore.Images.Media._ID)
            val imageDataIndex = imageCursor?.getColumnIndex(MediaStore.Images.Media.DATA)
            if (imageCursor != null && imageCursor.count > 0) {
                imageCursor.moveToFirst()
                prefs.autoId = imageCursor.getString(imageIdIndex!!)
                while (true) {
                    val imageId = imageCursor.getString(imageIdIndex)
                    val imageData = imageCursor.getString(imageDataIndex!!)
                    // 최종 동기화 아이디보다 이전 아이디일 경우 중지
                    if (!isValidDate(lastId, imageId)) {
                        break
                    }
                    paths.add(imageData)
                    ids.add(imageId)

                    val image = File(imageData)
                    try {
                        images.add(image)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }

                    if (!imageCursor.moveToNext()) {
                        break
                    }
                }

                if (images.size > 0) {
                    var dialogBuilder = android.app.AlertDialog.Builder(this)
                    dialogBuilder.setTitle("사진 업로드")
                    dialogBuilder.setMessage("새로운 사진이 있습니다.\n업로드 하시겠습니까?")
                    var dialogListener = object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            when (which) {
                                DialogInterface.BUTTON_NEUTRAL -> {
                                    val uploadIntent =
                                        Intent(this@MainActivity, ImageUpload::class.java)
                                    uploadIntent.putExtra("paths", paths)
                                    uploadIntent.putExtra("before", "main")
                                    uploadIntent.putExtra("ids", ids)
//                                    startService(uploadIntent)
                                    ImageUpload().enqueueWork(this@MainActivity, uploadIntent)
                                }
                                DialogInterface.BUTTON_POSITIVE -> {
                                    prefs.autoId = lastId
                                }
                            }
                        }
                    }
                    dialogBuilder.setPositiveButton("나중에 하기", dialogListener)
                    dialogBuilder.setNegativeButton("안하기", dialogListener)
                    dialogBuilder.setNeutralButton("지금 업로드", dialogListener)
                    dialogBuilder.setCancelable(false)
                    dialogBuilder.show()
                }
            }
        }
        return
    }

    inner class Callback1 : Callback {
        override fun onFailure(call: okhttp3.Call, e: IOException) {
            Log.d("Sever response", "error: $e")

        }

        override fun onResponse(call: okhttp3.Call, response: Response) {
            val result = response.body()?.string()

            Log.d("Sever response", "result: $result")
        }
    }

    private fun isValidDate(lastId: String?, Id: String): Boolean {
        if (lastId != null) {
            return lastId.compareTo(Id) < 0
        }
        return true
    }

    fun checkMyAlbum() {
        var dialog = AlertDialog.Builder(this)
        dialog.setTitle("내 앨범 만들기")
        dialog.setMessage("내 앨범을 만들어 주세요.")
        var dialogListener = object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        moveActivity()
                    }
                }
            }
        }
        dialog.setPositiveButton("만들러 가기", dialogListener)
        dialog.setCancelable(false)
        dialog.show()
    }

    fun moveActivity() {
        var intent = Intent(this, ImagePickerOneActivity::class.java)
        intent.putExtra("before", "addFamily")
        startActivity(intent)
        finish()
    }
}
