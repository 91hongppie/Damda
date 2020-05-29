package com.example.damda.navigation

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.damda.GlobalApplication
import com.example.damda.R
import com.example.damda.activity.AddVideoActivity
import com.example.damda.navigation.adapter.VideoAdapter
import com.example.damda.navigation.model.Video
import com.example.damda.retrofit.model.Videos
import com.example.damda.retrofit.service.VideoService
import kotlinx.android.synthetic.main.fragment_video.view.*
import life.knowledge4.videotrimmer.utils.FileUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class VideoFragment : Fragment() {
    private val REQUEST_VIDEO_TRIMMER = 0x01
    val EXTRA_VIDEO_PATH = "EXTRA_VIDEO_PATH"
    val PICK_VIDEO_REQUEST = 200
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_video, container, false)
        var videos: Videos? = null
        var videolist = emptyArray<Video>()
        var retrofit = Retrofit.Builder()
            .baseUrl(GlobalApplication.prefs.damdaServer)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val jwt = GlobalApplication.prefs.token
        val family_id = GlobalApplication.prefs.family_id.toString()
        var videoService: VideoService = retrofit.create(
            VideoService::class.java)
        videoService.requestVideo("JWT $jwt", family_id).enqueue(object: Callback<Videos> {
            override fun onFailure(call: Call<Videos>, t: Throwable) {
                var dialog = AlertDialog.Builder(context)
                dialog.setTitle("에러")
                dialog.setMessage("호출실패했습니다.")
                dialog.show()
            }

            override fun onResponse(call: Call<Videos>, response: Response<Videos>) {
                videos = response.body()
                videolist = videos!!.data
                view.video_list.adapter = VideoAdapter(videolist)
            }
        })
        view.video_list.layoutManager = LinearLayoutManager(context)
        view.video_upload.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "video/*"
            startActivityForResult(Intent.createChooser(intent, "동영상 선택"), PICK_VIDEO_REQUEST)
        }
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_VIDEO_REQUEST-> {
                    val uri: Uri? = data?.data
                    var intent = Intent(context, AddVideoActivity::class.java)
                    intent.putExtra("uri", uri)
                    startActivity(intent)
                }
            }
        }
//        TrimmerActivity 사용시
//        if (resultCode == RESULT_OK) {
//            if (requestCode == REQUEST_VIDEO_TRIMMER) {
//                val selectedUri: Uri? = data?.data
//                if (selectedUri != null) {
//                    startTrimActivity(selectedUri)
//                } else {
//                    Toast.makeText(
//                        context,
//                        R.string.toast_cannot_retrieve_selected_video,
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
    }

//    private fun pickFromGallery() {
//        if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getString(R.string.permission_read_storage_rationale), REQUEST_STORAGE_READ_ACCESS_PERMISSION)
//        } else {
//            val intent = Intent()
//            intent.setTypeAndNormalize("video/*")
//            intent.action = Intent.ACTION_PICK
////            intent.addCategory(Intent.CATEGORY_OPENABLE)
//            startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_video)), REQUEST_VIDEO_TRIMMER)
//        }
//    }
//
//    private fun startTrimActivity(uri: Uri) {
//        val intent = Intent(context, TrimmerActivity::class.java)
//        intent.putExtra(EXTRA_VIDEO_PATH, FileUtils.getPath(context, uri))
//        startActivity(intent)
//    }
//    private fun requestPermission(permission: String, rationale: String, requestCode: Int) {
//        val mainActivity = activity as MainActivity
//        if (ActivityCompat.shouldShowRequestPermissionRationale(mainActivity, permission)) {
//            val builder = AlertDialog.Builder(context)
//            builder.setTitle(getString(R.string.permission_title_rationale))
//            builder.setMessage(rationale)
//            builder.setPositiveButton(getString(R.string.label_ok)) { dialog, which -> activity?.let {
//                ActivityCompat.requestPermissions(
//                    it, arrayOf(permission), requestCode)
//            } }
//            builder.setNegativeButton(getString(R.string.label_cancel), null)
//            builder.show()
//        } else {
//            activity?.let { ActivityCompat.requestPermissions(it, arrayOf(permission), requestCode) }
//        }
//    }
//
//    /**
//     * Callback received when a permissions request has been completed.
//     */
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        when (requestCode) {
//            REQUEST_STORAGE_READ_ACCESS_PERMISSION -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                pickFromGallery()
//            }
//            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        }
//    }
//    companion object {
//        private const val REQUEST_VIDEO_TRIMMER = 0x01
//        private const val REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101
//        const val EXTRA_VIDEO_PATH = "EXTRA_VIDEO_PATH"
//    }

}

