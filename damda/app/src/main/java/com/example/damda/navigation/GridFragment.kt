package com.example.damda.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.damda.navigation.model.Album
import com.example.damda.AlbumAdapter
import com.example.damda.R
import kotlinx.android.synthetic.main.fragment_grid.view.*


class GridFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_grid, container, false)
        var albumList = arrayListOf<Album>(
            Album("첫번째 앨범"),
            Album("두번째 앨범"),
            Album("2번째 앨범"),
            Album("3번째 앨범"),
            Album("첫4번째 앨범"),
            Album("두번5째 앨범"),
            Album("세번째6 앨범")
        )
        view.rv_album.adapter = AlbumAdapter(albumList)
        view.rv_album.layoutManager = GridLayoutManager(activity, 3)
        return view
    }
}