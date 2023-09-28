package com.example.educonnect.ui.models.sealedclass

import androidx.annotation.StringRes

sealed class ApiFailure {
    object NetworkFailure : ApiFailure()
    data class Unknown(val error: Throwable) : ApiFailure()

    /**
     * null [error] means verification error has already been handled
     */
    data class VerificationError(val error: Throwable?, @StringRes val msgId: Int) : ApiFailure()
    object Unauthorised : ApiFailure()
}