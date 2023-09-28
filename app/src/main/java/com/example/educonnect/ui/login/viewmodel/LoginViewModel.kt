package com.example.educonnect.ui.login.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.educonnect.repo.UserRepo
import com.example.educonnect.ui.models.sealedclass.ApiFailure
import com.example.educonnect.ui.models.sealedclass.BaseState
import com.example.educonnect.ui.models.sealedclass.SignUpLoadStateSuccess
import com.example.educonnect.utils.AppConstants
import com.example.educonnect.utils.AppUtils
import com.example.educonnect.utils.runCoroutineCatching
import com.example.educonnect.utils.toast
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val userRepo: UserRepo by lazy {
        UserRepo()
    }

    private val signUpSuccessMutableLiveData =
        MutableLiveData<BaseState<SignUpLoadStateSuccess, ApiFailure>?>()
    val signUpSuccessLiveData: LiveData<BaseState<SignUpLoadStateSuccess, ApiFailure>?>
        get() = signUpSuccessMutableLiveData

    fun loginWithGoogle(acct: GoogleSignInAccount?, activity: Activity) {
        viewModelScope.launch {
            val credential = GoogleAuthProvider.getCredential(acct?.idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener(activity) { task ->
                    signUpSuccessMutableLiveData.value = if (task.isSuccessful) {
                        // Login successful, handle the user's session here
                        val isNewUser = task.result?.additionalUserInfo?.isNewUser
                        if (isNewUser == true) {
                            val user = AppUtils().getNewStudentUser(null, auth.currentUser)
                            // userRepo.addUser(user)
                            BaseState.Success(SignUpLoadStateSuccess.UserCreated)
                        } else {
                            BaseState.Success(SignUpLoadStateSuccess.UserExist)
                        }
                    } else {
                        // Login failed, display an error message
                        BaseState.Failed(
                            ApiFailure.Unknown(
                                task.exception ?: error("Email signup failed")
                            )
                        )
                    }
                }
        }
    }

    fun loginWithEmail(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            signUpSuccessMutableLiveData.value = if (it.isSuccessful) {
                // Login successful, handle the user's session here
                val isNewUser = it.result?.additionalUserInfo?.isNewUser
                if (isNewUser == true) {
                    BaseState.Success(SignUpLoadStateSuccess.VerificationSuccessFul)
                } else {
                    BaseState.Success(SignUpLoadStateSuccess.UserExist)
                }
            } else {
                // Login failed, display an error message
                BaseState.Failed(ApiFailure.Unknown(it.exception ?: error("Email signup failed")))
            }
        }
    }
}