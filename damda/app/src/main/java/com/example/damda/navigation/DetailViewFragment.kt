package com.example.damda.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.damda.PhotoAdapter
import com.example.damda.Photos
import com.example.damda.R
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_detail.view.*

class DetailViewFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_detail, container, false)
        var photoList = arrayListOf<Photos>(
            Photos(R.drawable.photo),
            Photos(R.drawable.photo),
            Photos(R.drawable.photo),
            Photos(R.drawable.photo),
            Photos(R.drawable.photo),
            Photos(R.drawable.photo),
            Photos(R.drawable.photo),
            Photos(R.drawable.photo),
            Photos(R.drawable.photo),
            Photos(R.drawable.photo)
        )

        view.rv_photo.adapter = PhotoAdapter(photoList)
        view.rv_photo.layoutManager = GridLayoutManager(activity, 6)


        return view
    }
}