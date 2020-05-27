package com.example.damda.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ClipData
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.MediaStore
import android.system.Os
import android.text.format.Formatter
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.damda.Constants
import com.example.damda.R
import com.example.damda.SharedData
import kotlinx.android.synthetic.main.activity_add_photo.*
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.InputStream
import java.net.*
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {
    private var mPartialAp = arrayOf("10", "0", "2", "2")
    private val mTimeout = 180
    private var mIpAddress: String? = null
    private var mServerIP: String = ""
    private val mPCs = ArrayList<String>()
    private var mSyncDate: String? = null
    internal var mSharedData = SharedData.instance
    private var mDialog: AlertDialog? = null
    internal var mSearchDialogHandler: SearchDialogHandler? = null
    private var mSelectedModeIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        mIpAddress ?: setIp()
        mSharedData.isConnected = false
        mSearchDialogHandler = SearchDialogHandler()
        ProcessTask().start()
        mSearchDialogHandler?.sendEmptyMessage(0)

    }

    override fun onResume() {
        super.onResume()
        mIpAddress ?: setIp()
        Log.d("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh", "isConnected : ${mSharedData.isConnected}")
        if (!mSharedData.isConnected) {
            mPCs.remove(mServerIP)
        }
        setUi()
        SharedData.instance.allModeFileCount = 0
        SharedData.instance.allModeTotalFileCount = 0
        SharedData.instance.selectedModeFileCount = 0
        SharedData.instance.selectedModeTotalFileCount = 0
    }


    private inner class ProcessTask() : Thread() {

        override fun run() {
            var serverSocket: ServerSocket? = null
            try {
                var hostname: String? = null

                var connectSock: Socket? = null
                var serverDos: DataOutputStream? = null

                var receiveSocket: Socket? = null
                var socketAddress: SocketAddress

                if (!mSharedData.isConnected) {
                    try {
                        // Server IP 요청을 위한 Server 접속
                        hostname =
                            mPartialAp[0] + "." + mPartialAp[1] + "." + mPartialAp[2] + "." + mPartialAp[3]
                        socketAddress = InetSocketAddress(hostname, Constants.CONNECT_PORT)
                        Log.d(TAG, hostname + " : 서버 연결 시도...")
                        try {
                            connectSock = Socket()
                            connectSock.connect(socketAddress, mTimeout)

                            mSharedData.isConnected = true
                            Log.d(TAG, hostname + " : 서버 응답 확인!!!")

                            // Client IP 보내기
                            serverDos = DataOutputStream(connectSock.getOutputStream())
                            serverDos.writeUTF(mIpAddress!!)
                            serverDos.flush()

                            connectSock.close()

                            // 리스너 소켓 생성 후 대기
                            serverSocket = ServerSocket(Constants.FILE_SEND_PORT)
                            // 연결되면 수신용 소켓 생성
                            receiveSocket = serverSocket.accept()
                            Log.d(TAG, "서버 요청 확인")


                            mServerIP = hostname


                            if (!mPCs.contains(mServerIP)) {
                                mPCs.add(mServerIP)
                            }
                            Log.d(TAG, "서버 주소 설정 완료")

                            val dis: DataInputStream = DataInputStream(receiveSocket.getInputStream())
                            mSyncDate = dis.readUTF()
                            Log.d(TAG, "동기화 날짜 수신 완료")

                            dis.close()
                            receiveSocket?.close()
                            serverSocket.close()
                            mSearchDialogHandler?.sendEmptyMessage(1)
                            if (mSharedData.isConnected) {
                                connect_state.text = "PC 선택을 해주세요."
                            }
                        } catch (e: SocketTimeoutException) {
                            Log.d(TAG, "error: $e")
                        }


                    } catch (e: ConnectException) {
                        Log.d(TAG, "error: $e")
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
//            synchronized(this) {
//                mSharedData.threadCount = mSharedData.threadCount + 1
//
//                if (mSharedData.threadCount == 5) {
//                    mSearchDialogHandler?.sendEmptyMessage(1)
//                    mSharedData.threadCount = 0
//                }
//            }
        }
    }

    private fun setIp() {
        val wm = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val wifiInfo = wm.connectionInfo
        val ip = wifiInfo.ipAddress
        mIpAddress = Formatter.formatIpAddress(ip)
        val st = StringTokenizer(mIpAddress, ".")

//        for (i in mPartialAp.indices) {
//            mPartialAp[i] = st.nextToken()
//        }

        Log.d(TAG, mIpAddress) // phone ip
        Log.d(TAG, mPartialAp[0] + "." + mPartialAp[1] + "." + mPartialAp[2] + "." + mPartialAp[3]) // ap ip
    }

    private fun setUi() {
        connect_state.text = "PC와 연결이 필요합니다."
        select_pc.setOnClickListener { selectPc() }

        select_picture.setOnClickListener { selectMode() }
        select_picture.setTextColor(Color.GRAY)
        select_picture.isClickable = false

        search_pc.setOnClickListener {
            mSharedData.isConnected = false
            mSharedData.threadCount = 0
            ProcessTask().start()
            mSearchDialogHandler?.sendEmptyMessage(0)
        }

        @Suppress("DEPRECATION")
        mDialog = ProgressDialog(this@AddPhotoActivity, R.style.CustomDialog)
        mDialog?.setMessage("Server PC 검색중")
        mDialog?.setCancelable(false)
    }

    private fun selectPc() {
        val pcList = mPCs.toTypedArray<CharSequence>()
        val alt_bld = AlertDialog.Builder(this)
        alt_bld.setTitle("PC를 선택해주세요.")
        alt_bld.setSingleChoiceItems(
            pcList, -1
        ) { dialog, item ->
            connect_state.text = "연결 완료!!"
            select_picture.isClickable = true
            select_picture.setTextColor(Color.BLACK)
            mServerIP = pcList[item].toString()
            dialog.dismiss()
        }
        val alert = alt_bld.create()
        alert.show()
    }

    private fun selectMode() {
        val modes = arrayOf<CharSequence>("자동", "선택")
        val alt_bld = AlertDialog.Builder(this)
        alt_bld.setTitle("전송할 방법을 선택해주세요.")
        alt_bld.setSingleChoiceItems(
            modes, 0
        ) { dialog, item -> mSelectedModeIndex = item }.setPositiveButton(
            "확인"
        ) { dialog, which ->
            if (mSelectedModeIndex == 0) {
                val intent = Intent()
                intent.putExtra(Constants.IP, mServerIP)
                intent.putExtra(Constants.DATE, mSyncDate)
                intent.setClass(applicationContext, SendAutoItemActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent()
                intent.putExtra(Constants.IP, mServerIP)
                intent.setClass(applicationContext, SendSelectedItemActivity::class.java)
                startActivity(intent)
            }
        }
        val alert = alt_bld.create()
        alert.show()
    }


    internal inner class SearchDialogHandler : Handler() {
        override fun handleMessage(msg: Message) {
            if (msg.what == 0) {
                mDialog?.show()
            } else if (msg.what == 1) {
                mDialog?.let {
                    if (it.isShowing ?: true) {
                        it.dismiss()
                    }
                }
            }
        }
    }
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == PICK_IMAGE_FROM_ALBUM) {
//            if (resultCode == Activity.RESULT_OK) {
//                // This is path to the selected image
//                val images : ArrayList<Uri> = arrayListOf()
//                val clipdata : ClipData? = data?.clipData
//                if (clipdata != null) {
//                    for (i in 0 until clipdata.itemCount) {
//                        val imageUri = clipdata.getItemAt(i).uri
//                        images.add(imageUri)
//                        val image: String = imageUri.path!!
//                        val exif = ExifInterface(image)
//
//                        val attributes = arrayOf(
//                            ExifInterface.TAG_DATETIME,
//                            ExifInterface.TAG_DATETIME_DIGITIZED,
//                            ExifInterface.TAG_GPS_ALTITUDE,
//                            ExifInterface.TAG_GPS_ALTITUDE_REF,
//                            ExifInterface.TAG_GPS_DATESTAMP,
//                            ExifInterface.TAG_GPS_LATITUDE,
//                            ExifInterface.TAG_GPS_LATITUDE_REF,
//                            ExifInterface.TAG_GPS_LONGITUDE,
//                            ExifInterface.TAG_GPS_LONGITUDE_REF,
//                            ExifInterface.TAG_GPS_PROCESSING_METHOD,
//                            ExifInterface.TAG_GPS_TIMESTAMP,
//                            ExifInterface.TAG_MAKE,
//                            ExifInterface.TAG_MODEL,
//                            ExifInterface.TAG_ORIENTATION)
//
//                        for (i in attributes.indices) {
//                            val value = exif.getAttribute(attributes[i])
//                            if (value != null)
//                                Log.d("EXIF", "value: $value")
//                        }

//                        Log.d("images", "list: $images")
//                    }
//                } else {
//                    var imageUri : Uri? = data?.data
//                    if (imageUri != null) {
//                        Log.d("Image path", "URI: $imageUri")
//                        Log.d("Image path", "absolute: ${imageUri.encodedPath}")
//                        images.add(imageUri)

//                        val exif = ExifInterface(image)
//
//                        val attributes = arrayOf(
//                            ExifInterface.TAG_DATETIME,
//                            ExifInterface.TAG_DATETIME_DIGITIZED,
//                            ExifInterface.TAG_GPS_ALTITUDE,
//                            ExifInterface.TAG_GPS_ALTITUDE_REF,
//                            ExifInterface.TAG_GPS_DATESTAMP,
//                            ExifInterface.TAG_GPS_LATITUDE,
//                            ExifInterface.TAG_GPS_LATITUDE_REF,
//                            ExifInterface.TAG_GPS_LONGITUDE,
//                            ExifInterface.TAG_GPS_LONGITUDE_REF,
//                            ExifInterface.TAG_GPS_PROCESSING_METHOD,
//                            ExifInterface.TAG_GPS_TIMESTAMP,
//                            ExifInterface.TAG_MAKE,
//                            ExifInterface.TAG_MODEL,
//                            ExifInterface.TAG_ORIENTATION)
//
//                        for (i in attributes.indices) {
//                            val value = exif.getAttribute(attributes[i])
//                            if (value != null)
//                                Log.d("EXIF", "value: $value")
//                        }
//                    }
//                }
//
//                class ThreadClass : Thread() {
//                    override fun run() {
//                        for (image in images) {
//                            runOnUiThread { addphoto_image.setImageURI(image) }
//                        }
//
//                        try {
//                            sleep(3000)
//                        } catch (e: InterruptedException) {
//                            e.printStackTrace()
//                        }
//                    }
//                }
//
//                val tc = ThreadClass()
//                tc.start()
//            }else {
//                // Exit the addPhotoActivity if you leave the album without selecting it
//                finish()
//            }
//        }
//    }

//    private fun getAbsolutePath(uri: Uri) : String {
//        var path = filesDir.absolutePath
//        val projection = arrayOf(MediaStore.Images.Media.DATA)
//        val cursor = managedQuery(uri, projection, null, null, null)
//        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//        cursor.moveToFirst()
//        return cursor.getString(column_index)
//    }
//
//    fun contentUpload() {
//
//        // Make file name
//
//        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//        var imageFileName = "damda_" + timestamp + "_.jpg"

//        var storageRef = storage?.reference?.child("images")?.child(imageFileName)
//
//        // File upload
//        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
//            Toast.makeText(this, getString(R.string.upload_success), Toast.LENGTH_LONG).show()
//
//        }
//    }

    companion object {

        private const val TAG = "AddPhotoActivity"
    }
}
