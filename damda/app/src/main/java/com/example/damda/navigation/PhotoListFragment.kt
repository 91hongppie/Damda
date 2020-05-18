package com.example.damda.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.damda.GlobalApplication
import com.example.damda.MainActivity
import com.example.damda.PhotoAdapter
import com.example.damda.navigation.model.Photos
import com.example.damda.R
import com.example.damda.navigation.model.Album
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_photo_list.view.*
import okhttp3.*
import java.io.IOException
import java.net.URL

class PhotoListFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_photo_list, container, false)
        val album = arguments?.getParcelable<Album>("album")
        val url = URL("http://10.0.2.2:8000/api/albums/photo/${album?.id}/")
        val jwt = GlobalApplication.prefs.myEditText
        val request = Request.Builder().url(url).addHeader("Authorization", "JWT $jwt")
            .build()
        val client = OkHttpClient()
        var result = emptyArray<Photos>()
        view.rv_photo?.adapter = PhotoAdapter(result)
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException){
                println("Failed to execute request!")
            }
            override fun onResponse(call: Call, response: Response) {
                val body = response.body()?.string()
                val gson = GsonBuilder().create()
                var photoList = emptyArray<Photos>()
                val list = gson.fromJson(body, photoList::class.java)
                activity?.runOnUiThread(Runnable {
                    view.rv_photo?.adapter = PhotoAdapter(list)
                })
            }
        })
        view.albumTitle?.text = album?.title
        view.rv_photo?.layoutManager = GridLayoutManager(activity, 3)

        return view
    }
}