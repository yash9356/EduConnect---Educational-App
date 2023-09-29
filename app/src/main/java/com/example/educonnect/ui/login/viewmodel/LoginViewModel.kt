package com.example.educonnect.ui.login.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.educonnect.EduConnectApplication
import com.example.educonnect.repo.SharedPrefRepo
import com.example.educonnect.repo.UserRepo
import com.example.educonnect.ui.models.sealedclass.ApiFailure
import com.example.educonnect.ui.models.sealedclass.BaseState
import com.example.educonnect.ui.models.sealedclass.SignUpLoadStateSuccess
import com.example.educonnect.utils.AppConstants
import com.example.educonnect.utils.AppConstants.OTP_TIMEOUT_VALUE_IN_MILLIS
import com.example.educonnect.utils.AppUtils
import com.example.educonnect.utils.PropertyNameConstants
import com.example.educonnect.utils.runCoroutineCatching
import com.example.educonnect.utils.toE164Format
import com.example.educonnect.utils.toUser
import com.example.educonnect.utils.toast
import com.example.educonnect_educationalapp.R
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class LoginViewModel : ViewModel() {

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val userRepo: UserRepo by lazy {
        UserRepo()
    }

    private val sharedPrefRepo: SharedPrefRepo by lazy {
        SharedPrefRepo(EduConnectApplication.sharedPreferences, EduConnectApplication.dataStore)
    }

    lateinit var storedVerificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private val signUpSuccessMutableLiveData =
        MutableLiveData<BaseState<SignUpLoadStateSuccess, ApiFailure>?>()
    val signUpSuccessLiveData: LiveData<BaseState<SignUpLoadStateSuccess, ApiFailure>?>
        get() = signUpSuccessMutableLiveData

    fun resetSignUpState() {
        signUpSuccessMutableLiveData.value = null
    }

    fun loginWithGoogle(acct: GoogleSignInAccount?) {
        viewModelScope.launch {
            signUpSuccessMutableLiveData.value = BaseState.Loading
            signUpSuccessMutableLiveData.value = runCoroutineCatching({
                val credential = GoogleAuthProvider.getCredential(acct?.idToken, null)
                val authResult = auth.signInWithCredential(credential).await()
                authResult.user?.let { fireBaseUser ->
                    val existingUser = if (authResult.additionalUserInfo?.isNewUser == true) {
                        sharedPrefRepo.updateNewUser(true)
                        null
                    } else {
                        userRepo.getUser(fireBaseUser.uid)
                    }
                    userRepo.addUser(
                        fireBaseUser.toUser(
                            null,
                            existingUser,
                            PropertyNameConstants.FlagConstants.STUDENT
                        )
                    )
                    BaseState.Success(SignUpLoadStateSuccess.VerificationSuccessFul)
                } ?: run {
                    // handle when authResult.user is null
                    BaseState.Failed(
                        ApiFailure.Unknown(
                            IllegalStateException("authResult.user returned null")
                        )
                    )
                }
            }) { throwable ->
                BaseState.Failed(
                    ApiFailure.VerificationError(throwable, R.string.verification_error)
                )
            }
        }
    }

    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            signUpSuccessMutableLiveData.value = BaseState.Loading
            signUpSuccessMutableLiveData.value = runCoroutineCatching({
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                authResult.user?.let { fireBaseUser ->
                    val existingUser = if (authResult.additionalUserInfo?.isNewUser == true) {
                        sharedPrefRepo.updateNewUser(true)
                        null
                    } else {
                        userRepo.getUser(fireBaseUser.uid)
                    }
                    userRepo.addUser(
                        fireBaseUser.toUser(
                            email,
                            existingUser,
                            PropertyNameConstants.FlagConstants.STUDENT
                        )
                    )
                    BaseState.Success(SignUpLoadStateSuccess.VerificationSuccessFul)
                } ?: run {
                    // handle when authResult.user is null
                    BaseState.Failed(
                        ApiFailure.Unknown(
                            IllegalStateException("authResult.user returned null")
                        )
                    )
                }
            }) { throwable ->
                BaseState.Failed(
                    ApiFailure.VerificationError(throwable, R.string.verification_error)
                )
            }
        }
    }

    fun performSignIn(phoneText: String, activity: Activity) {
        sendVerificationCode(phoneText.toE164Format(), activity)
    }

    fun signInWithPhoneAuthCredential(authVerificationId: String, otp: String) {
        viewModelScope.launch {
            signUpSuccessMutableLiveData.value = BaseState.Loading
            signUpSuccessMutableLiveData.value = runCoroutineCatching({
                val credential: PhoneAuthCredential =
                    PhoneAuthProvider.getCredential(authVerificationId, otp)
                val authResult = auth.signInWithCredential(credential).await()
                authResult.user?.let { fireBaseUser ->
                    val existingUser = if (authResult.additionalUserInfo?.isNewUser == true) {
                        sharedPrefRepo.updateNewUser(true)
                        null
                    } else {
                        userRepo.getUser(fireBaseUser.uid)
                    }
                    userRepo.addUser(
                        fireBaseUser.toUser(
                            null,
                            existingUser,
                            PropertyNameConstants.FlagConstants.STUDENT
                        )
                    )
                    BaseState.Success(SignUpLoadStateSuccess.VerificationSuccessFul)
                } ?: run {
                    // handle when authResult.user is null
                    BaseState.Failed(
                        ApiFailure.Unknown(
                            IllegalStateException("authResult.user returned null")
                        )
                    )
                }
            }) { throwable ->
                BaseState.Failed(
                    ApiFailure.VerificationError(throwable, R.string.verification_error)
                )
            }
        }
    }

    fun sendVerificationCode(phoneNumber: String, activity: Activity) {
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signUpSuccessMutableLiveData.value =
                    BaseState.Success(SignUpLoadStateSuccess.VerificationSuccessFul)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                signUpSuccessMutableLiveData.value = BaseState.Failed(
                    ApiFailure.VerificationError(error = null, R.string.verification_error)
                )
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                storedVerificationId = verificationId
                resendToken = token
                signUpSuccessMutableLiveData.value =
                    BaseState.Success(SignUpLoadStateSuccess.VerificationCodeSent)
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth).apply {
            setPhoneNumber(phoneNumber) // Phone number to verify
            setTimeout(OTP_TIMEOUT_VALUE_IN_MILLIS, TimeUnit.MILLISECONDS) // Timeout and unit
            setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            setActivity(activity)
        }.build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}