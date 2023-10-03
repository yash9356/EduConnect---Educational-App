package com.example.educonnect.ui.models.user

import com.example.educonnect.utils.PropertyNameConstants
import java.util.UUID

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
){
    @Suppress("unused")
    internal constructor() : this(
        id = UUID.randomUUID().toString(),
        creationDate = System.currentTimeMillis(),
        lastLoginDate = System.currentTimeMillis(),
        lastUpdateDate = System.currentTimeMillis(),
        emailId = null,
        gender = null,
        name = "",
        phoneNumber = "",
        profilePic = null,
        type = PropertyNameConstants.FlagConstants.STUDENT,
    )
}
