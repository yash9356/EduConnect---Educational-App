package com.example.educonnect.repo

import com.example.educonnect.ui.models.resources.EducationalVideo
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class EduResourcesRepo {
    suspend fun loadVideoResources(): List<EducationalVideo> {
        return FirebaseFirestore.getInstance().collection("resources").get().await()
            .toObjects(EducationalVideo::class.java)
    }
}