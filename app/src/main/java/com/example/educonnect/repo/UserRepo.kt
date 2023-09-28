package com.example.educonnect.repo

import com.example.educonnect.ui.models.user.User
import com.example.educonnect.utils.AppConstants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepo {
    suspend fun addUser(user: User) {
        FirebaseFirestore.getInstance().collection(AppConstants.USERS).document(user.id).set(user)
            .await()
    }

    suspend fun getUser(userId: String): User? {
        return FirebaseFirestore.getInstance().collection(AppConstants.USERS).document(userId).get()
            .await().toObject(User::class.java)
    }
}