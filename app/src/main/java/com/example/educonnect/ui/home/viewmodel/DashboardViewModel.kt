package com.example.educonnect.ui.home.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.educonnect.repo.EduResourcesRepo
import com.example.educonnect.repo.StorageRepo
import com.example.educonnect.repo.UserRepo
import com.example.educonnect.ui.models.resources.EducationalVideo
import com.example.educonnect.ui.models.sealedclass.ApiFailure
import com.example.educonnect.ui.models.sealedclass.BaseState
import com.example.educonnect.ui.models.user.User
import com.example.educonnect.utils.AppConstants
import com.example.educonnect.utils.runCoroutineCatching
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.io.File

class DashboardViewModel : ViewModel() {
    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val userRepo: UserRepo by lazy {
        UserRepo()
    }

    private val storageRepo: StorageRepo by lazy {
        StorageRepo()
    }

    private val eduResourcesRepo: EduResourcesRepo by lazy {
        EduResourcesRepo()
    }

    var userData: User? = null
    private val userMutableLiveData = MutableLiveData<BaseState<User, ApiFailure>>()
    val userLiveData: LiveData<BaseState<User, ApiFailure>>
        get() = userMutableLiveData

    private val educationVideosMutableLiveData =
        MutableLiveData<BaseState<List<EducationalVideo>, ApiFailure>>()
    val educationVideosLiveData: LiveData<BaseState<List<EducationalVideo>, ApiFailure>>
        get() = educationVideosMutableLiveData

    private val profilePhotoSuccessMutableLiveData =
        MutableLiveData<BaseState<Unit, ApiFailure>>()
    val profilePhotoSuccessLiveData: LiveData<BaseState<Unit, ApiFailure>>
        get() = profilePhotoSuccessMutableLiveData

    private val usernameSuccessMutableLiveData =
        MutableLiveData<BaseState<Unit, ApiFailure>>()
    val usernameSuccessLiveData: LiveData<BaseState<Unit, ApiFailure>>
        get() = usernameSuccessMutableLiveData

    init {
        loadUser()
        getVideoData()
    }

    private fun loadUser() {
        viewModelScope.launch {
            userMutableLiveData.value = runCoroutineCatching({
                val userId = getUserId() ?: run {
                    return@launch
                }
                val user = userRepo.getUser(userId)
                if (user != null) {
                    userData = user
                    BaseState.Success(user)
                } else {
                    BaseState.Failed(ApiFailure.Unauthorised)
                }
            }) {
                BaseState.Failed(ApiFailure.Unknown(it))
            }
        }
    }

    private fun getVideoData() {
        viewModelScope.launch {
            educationVideosMutableLiveData.value = BaseState.Loading
            educationVideosMutableLiveData.value = runCoroutineCatching({
                val list = eduResourcesRepo.loadVideoResources()
                BaseState.Success(list)
            }) {
                BaseState.Failed(ApiFailure.Unknown(it))
            }
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun getUserId(): String? {
        return auth.currentUser?.uid
    }

    fun updateUserProfilePic(uri: Uri){
        viewModelScope.launch {
            profilePhotoSuccessMutableLiveData.value = BaseState.Loading
            profilePhotoSuccessMutableLiveData.value = runCoroutineCatching({
                val userId = getUserId() ?: run {
                    return@launch
                }
                val url = storageRepo.uploadFile(
                    AppConstants.USERS + File.separator + userId + File.separator,
                    uri
                )
                userRepo.updateUserProfile(
                    url,
                    userId
                )
                BaseState.Success(Unit)
            }){
                BaseState.Failed(ApiFailure.Unknown(it))
            }
        }
    }
    fun updateUserName(userName: String) {
        viewModelScope.launch {
            usernameSuccessMutableLiveData.value = BaseState.Loading
            usernameSuccessMutableLiveData.value = runCoroutineCatching({
                val userId = getUserId() ?: run {
                    return@launch
                }
                userRepo.updateUserName(userId, userName)
                BaseState.Success(Unit)
            }){
                BaseState.Failed(ApiFailure.Unknown(it))
            }
        }
    }
}