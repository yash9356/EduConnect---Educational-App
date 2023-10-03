package com.example.educonnect.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.educonnect.ui.adapter.viewholder.VideoResViewHolder
import com.example.educonnect.ui.models.resources.EducationalVideo
import com.example.educonnect_educationalapp.databinding.VideoResItemBinding

class VideoResAdapter: ListAdapter<EducationalVideo, VideoResViewHolder>(VideoResDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoResViewHolder {
        val binding =
            VideoResItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoResViewHolder(
            binding = binding,
        )
    }

    override fun onBindViewHolder(holder: VideoResViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }


    class VideoResDiffCallback : DiffUtil.ItemCallback<EducationalVideo>() {
        override fun areItemsTheSame(oldItem: EducationalVideo, newItem: EducationalVideo) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: EducationalVideo, newItem: EducationalVideo) =
            oldItem == newItem
    }
}