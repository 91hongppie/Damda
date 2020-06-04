package com.example.damda

import android.content.Context
import android.content.SharedPreferences
import java.util.*

class MySharedPreferences(context: Context) {
    val PREFS_FILENAME = "prefs"
    val PREF_KEY_MY_EDITTEXT = "token"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)
    /* 파일 이름과 EditText를 저장할 Key 값을 만들고 prefs 인스턴스 초기화 */
    var token: String?
        get() = prefs.getString(PREF_KEY_MY_EDITTEXT, "")
        set(value) = prefs.edit().putString(PREF_KEY_MY_EDITTEXT, value).apply()

    /* get/set 함수 임의 설정. get 실행 시 저장된 값을 반환하며 default 값은 ""
     * set(value) 실행 시 value로 값을 대체한 후 저장 */
    var user_id: String?
        get() = prefs.getString("user_id", "")
        set(value) = prefs.edit().putString("user_id", value).apply()

    var family_id: String?
        get() = prefs.getString("family_id", "")
        set(value) = prefs.edit().putString("family_id", value).apply()

    var my_album: Boolean
        get() = prefs.getBoolean("my_album", false)
        set(value) = prefs.edit().putBoolean("my_album", value).apply()

    var gender: Int
        get() = prefs.getInt("gender", 0)
        set(value) = prefs.edit().putInt("gender", value).apply()

    var state: String?
        get() = prefs.getString("state", "")
        set(value) = prefs.edit().putString("state", value).apply()

    var autoStatus: Boolean
        get() = prefs.getBoolean("autoStatus", true)
        set(value) = prefs.edit().putBoolean("autoStatus", value).apply()

    var mobileAutoUpload: Boolean
        get() = prefs.getBoolean("mobileAutoUpload", false)
        set(value) = prefs.edit().putBoolean("mobileAutoUpload", value).apply()

    var autoId: String?
        get() = prefs.getString("autoId", null)
        set(value) = prefs.edit().putString("autoId", value).apply()

//    val damdaServer = "https://k02b2051.p.ssafy.io"
    val damdaServer = "http://10.0.2.2:8000"

    var device_token: String?
        get() = prefs.getString("device_token", "")
        set(value) = prefs.edit().putString("device_token", value).apply()

    var push_all: Boolean
        get() = prefs.getBoolean("push_all", true)
        set(value) = prefs.edit().putBoolean("push_all", value).apply()

    var push_rehi: Boolean
        get() = prefs.getBoolean("push_rehi", true)
        set(value) = prefs.edit().putBoolean("push_rehi", value).apply()

    var push_new: Boolean
        get() = prefs.getBoolean("push_new", true)
        set(value) = prefs.edit().putBoolean("push_new", value).apply()

    var push_congrat: Boolean
        get() = prefs.getBoolean("push_congrat", true)
        set(value) = prefs.edit().putBoolean("push_congrat", value).apply()

    var push_mission: Boolean
        get() = prefs.getBoolean("push_mission", true)
        set(value) = prefs.edit().putBoolean("push_mission", value).apply()
}