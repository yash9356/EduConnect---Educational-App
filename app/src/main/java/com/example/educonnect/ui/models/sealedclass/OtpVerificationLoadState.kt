package com.example.educonnect.ui.models.sealedclass

import androidx.annotation.StringRes

sealed class OtpVerificationLoadStateSuccess {
    object OtpVerificationSuccess : OtpVerificationLoadStateSuccess()
}

sealed class OtpVerificationLoadStateFailure {
    data class Unknown(val throwable: Throwable, @StringRes val msgId: Int? = null) : OtpVerificationLoadStateFailure()
    object PermissionDenied : OtpVerificationLoadStateFailure()
    object InvalidOtp : OtpVerificationLoadStateFailure()
    object NetworkError : OtpVerificationLoadStateFailure()
    data class OtpVerificationError(val error: Throwable) : OtpVerificationLoadStateFailure()
}