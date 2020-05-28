package com.example.damda.navigation

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.damda.R
import com.example.damda.activity.TrimmerActivity
import com.example.damda.navigation.model.Video
import com.example.damda.retrofit.model.Videos
import kotlinx.android.synthetic.main.fragment_video.view.*
import life.knowledge4.videotrimmer.utils.FileUtils


class VideoFragment : Fragment() {
    private val REQUEST_VIDEO_TRIMMER = 0x01
    val EXTRA_VIDEO_PATH = "EXTRA_VIDEO_PATH"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_video, container, false)
        var video: Videos? = null
        var videolist = emptyArray<Video>()
//        view.video_list.adapter =
//            AlbumAdapter(videolist, context, this) { album ->
//                var bundle = Bundle()
//                bundle.putParcelable("album", album)
//                var fragment = PhotoListFragment()
//                fragment.arguments = bundle
//                context.replaceFragment(fragment)
//            }
//        var retrofit = Retrofit.Builder()
//            .baseUrl(getString(R.string.damda_server))
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//        val jwt = GlobalApplication.prefs.token
//        val family_id = GlobalApplication.prefs.family_id.toString()
//        var albumsService: AlbumsService = retrofit.create(
//            AlbumsService::class.java)
//        albumsService.requestAlbums("JWT $jwt", family_id).enqueue(object: Callback<Albums> {
//            override fun onFailure(call: Call<Albums>, t: Throwable) {
//                var dialog = AlertDialog.Builder(context)
//                dialog.setTitle("에러")
//                dialog.setMessage("호출실패했습니다.")
//                dialog.show()
//            }
//
//            override fun onResponse(call: Call<Albums>, response: Response<Albums>) {
//                val albums = response.body()
//                albumList = albums!!.data
//                val albumAdapter =
//                    AlbumAdapter(albumList, context,this@AlbumListFragment) { album ->
//                        var bundle = Bundle()
//                        bundle.putParcelable("album", album)
//                        var fragment = PhotoListFragment()
//                        fragment.arguments = bundle
//                        context.replaceFragment(fragment)
//                    }
//                view.rv_album.adapter = albumAdapter
//            }
//        })
//        view.rv_album.layoutManager = LinearLayoutManager(context)
        view.video_upload.setOnClickListener {
            pickFromGallery()
        }
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_VIDEO_TRIMMER) {
                val selectedUri: Uri? = data?.data
                if (selectedUri != null) {
                    startTrimActivity(selectedUri)
                } else {
                    Toast.makeText(
                        context,
                        R.string.toast_cannot_retrieve_selected_video,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun pickFromGallery() {
            val intent = Intent()
            intent.setTypeAndNormalize("video/*")
            intent.action = Intent.ACTION_PICK
            startActivityForResult(
                Intent.createChooser(
                    intent,
                    getString(R.string.label_select_video)
                ), REQUEST_VIDEO_TRIMMER
            )
    }

    private fun startTrimActivity(uri: Uri) {
        val intent = Intent(context, TrimmerActivity::class.java)
        intent.putExtra(EXTRA_VIDEO_PATH, FileUtils.getPath(context, uri))
        startActivity(intent)
    }
}

