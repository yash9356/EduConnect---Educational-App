package com.example.educonnect.ui.models.user

data class User(
    val id: String,
    val creationDate: Long,
    val lastLoginDate: Long,
    val lastUpdateDate: Long,
    val emailId: String?,
    val gender: String?,
    val name: String?,
    val phoneNumber: String?,
    val profilePic: String?,
    val type: String,
)
