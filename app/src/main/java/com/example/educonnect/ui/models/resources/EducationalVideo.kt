package com.example.educonnect.ui.models.resources

data class EducationalVideo(
    val id: String,
    val thumbnailUrl: String,
    val title: String,
    val description: String,
    val rating: Int,
    val creatorName: String,
    val creationDate: Long
)
