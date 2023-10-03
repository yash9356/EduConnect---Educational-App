package com.example.educonnect.ui.models.resources

import java.util.UUID

data class EducationalVideo(
    val id: String,
    val thumbnailUrl: String,
    val title: String,
    val description: String,
    val rating: Float,
    val creatorName: String,
    val creationDate: Long
){
    @Suppress("unused")
    internal constructor() : this(
        id = UUID.randomUUID().toString(),
        thumbnailUrl = "",
        title = "",
        rating = 0.0f,
        description = "",
        creationDate = System.currentTimeMillis(),
        creatorName = ""
    )
}
