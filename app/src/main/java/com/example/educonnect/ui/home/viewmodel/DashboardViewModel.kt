package com.example.educonnect.ui.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.educonnect.repo.EduResourcesRepo
import com.example.educonnect.repo.UserRepo
import com.example.educonnect.ui.models.resources.EducationalVideo
import com.example.educonnect.ui.models.sealedclass.ApiFailure
import com.example.educonnect.ui.models.sealedclass.BaseState
import com.example.educonnect.ui.models.user.User
import com.example.educonnect.utils.runCoroutineCatching
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val userRepo: UserRepo by lazy {
        UserRepo()
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

    init {
        loadUser()
        getVideoData()
    }

    private fun loadUser() {
        viewModelScope.launch {
            userMutableLiveData.value = runCoroutineCatching({
                val userId = getUserId() ?: run {
                    // sendSignOutEvent()
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

    fun getUserId(): String? {
        return auth.currentUser?.uid
    }
}