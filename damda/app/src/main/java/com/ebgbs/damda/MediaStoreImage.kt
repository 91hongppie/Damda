package com.ebgbs.damda

import android.net.Uri
import androidx.recyclerview.widget.DiffUtil
import java.util.Date

/**
 * Simple data class to hold information about an image included in the device's MediaStore.
 */
data class MediaStoreImage(
    val id: Long,
    val displayName: String,
    val dateTaken: Date,
    val contentUri: Uri,
    val contentPath: String
) {
    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<MediaStoreImage>() {
            override fun areItemsTheSame(oldItem: MediaStoreImage, newItem: MediaStoreImage) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: MediaStoreImage, newItem: MediaStoreImage) =
                oldItem == newItem
        }
    }
}
data class MediaStoreAlbum(
    val id: Long,
    val displayName: String,
    val contentUri: Uri,
    val contentPath: String
) {
    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<MediaStoreAlbum>() {
            override fun areItemsTheSame(oldItem: MediaStoreAlbum, newItem: MediaStoreAlbum) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: MediaStoreAlbum, newItem: MediaStoreAlbum) =
                oldItem == newItem
        }
    }
}