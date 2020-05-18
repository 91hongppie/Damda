package com.example.damda.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.damda.navigation.model.Album
import com.example.damda.AlbumAdapter
import com.example.damda.R
import com.example.damda.replaceFragment
import kotlinx.android.synthetic.main.fragment_grid.view.*


class GridFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        val context = activity as AppCompatActivity
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
        val albumAdapter = AlbumAdapter(albumList) { album ->
            var bundle = Bundle()
            bundle.putParcelable("album",album)
            var fragment = PhotoListFragment()
            fragment.arguments = bundle
            context.replaceFragment(fragment)
        }
        view.rv_album.adapter = albumAdapter
        view.rv_album.layoutManager = GridLayoutManager(activity, 3)
        return view
    }
}