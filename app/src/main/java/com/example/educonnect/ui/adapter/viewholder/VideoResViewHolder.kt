package com.example.educonnect.ui.adapter.viewholder

import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.educonnect.ui.models.resources.EducationalVideo
import com.example.educonnect_educationalapp.R
import com.example.educonnect_educationalapp.databinding.VideoResItemBinding

class VideoResViewHolder(
    private val binding: VideoResItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: EducationalVideo) {
        binding.data = item
        val context = binding.root.context
        val placeHolder =
            AppCompatResources.getDrawable(binding.root.context, R.drawable.ic_image_placeholder)
        placeHolder?.let { drawable ->
            Glide.with(context)
                .load(item.thumbnailUrl)
                .placeholder(drawable)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(binding.poster)
        }
    }
}