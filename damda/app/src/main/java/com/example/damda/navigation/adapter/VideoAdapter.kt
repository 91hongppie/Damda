package com.example.damda.navigation.adapter

import android.content.Intent
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.VideoView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.damda.GlobalApplication.Companion.prefs
import com.example.damda.R
import com.example.damda.activity.MainActivity
import com.example.damda.navigation.model.Video
import kotlinx.android.synthetic.main.list_item_video.view.*


class VideoAdapter(val videolist: Array<Video>) : RecyclerView.Adapter<VideoAdapter.CustomViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_video, parent, false)
        return CustomViewHolder(view, parent)
    }

    override fun getItemCount(): Int {
        return videolist.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(videolist[position])
    }

    inner class CustomViewHolder(itemView: View, parent: ViewGroup) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.video_title)
        val context = parent.context
        fun bind (video: Video) {
            name?.text = video.title
            if (video.file != null) {
                itemView.video_item_layout.setOnClickListener {
                    val it = Intent(Intent.ACTION_VIEW)
                    val uri = Uri.parse(prefs.damdaServer + "${video.file}")
                    it.setDataAndType(uri, "video/mp4")
                    context.startActivity(it)
                }
            }
        }

    }

}