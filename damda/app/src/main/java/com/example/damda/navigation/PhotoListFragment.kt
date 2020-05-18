package com.example.damda.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.damda.GlobalApplication
import com.example.damda.PhotoAdapter
import com.example.damda.navigation.model.Photos
import com.example.damda.R
import com.example.damda.navigation.model.PhotoList
import com.example.damda.navigation.model.Album
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_photo_list.*
import kotlinx.android.synthetic.main.fragment_photo_list.view.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.URL

class PhotoListFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_photo_list, container, false)
        fetchJson()
//        var photoList = arrayListOf<Photos>(
//            Photos(R.drawable.avatar)
//        )
//        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_photo_list, container, false)
//
//        view.rv_photo.adapter = PhotoAdapter(photoList)
//        view.rv_photo.layoutManager = GridLayoutManager(activity, 3)


        return view
    }
    fun fetchJson() {
        val url = URL("http://10.0.2.2:8000/api/albums/photo/")
        val jwt = GlobalApplication.prefs.myEditText
        val request = Request.Builder().url(url).addHeader("Authorization", "JWT $jwt")
            .build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException){
                println("Failed to execute request!")
            }
            override fun onResponse(call: Call, response: Response) {
                val body = response?.body()?.string()
                val gson = GsonBuilder().create()
                val list = gson.fromJson(body, PhotoList::class.java)
                activity?.runOnUiThread {

                    val album = arguments?.getParcelable<Album>("album")
                    view?.albumTitle?.text = album?.title
                    view?.rv_photo?.adapter = PhotoAdapter(list)
                    view?.rv_photo?.layoutManager = GridLayoutManager(activity, 3)
                }

            }
        }
        )
    }

}