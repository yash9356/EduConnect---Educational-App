package com.example.educonnect.repo

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class StorageRepo {
    suspend fun uploadFile(path: String, fileUri: Uri): String {
        val childReference = FirebaseStorage.getInstance().reference.child(path)
        childReference.putFile(fileUri).await()
        return childReference.downloadUrl.await().toString()
    }

    suspend fun deleteFolder(path: String) {
        val storageRef = FirebaseStorage.getInstance().reference.child(path)
        val fileList = storageRef.listAll().await()
        coroutineScope {
            fileList.items.map {
                async {
                    it.delete().await()
                }
            }.awaitAll()
        }
    }

}