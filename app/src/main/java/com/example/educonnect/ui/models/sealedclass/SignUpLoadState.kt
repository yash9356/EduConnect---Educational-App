package com.example.educonnect.ui.models.sealedclass

sealed class SignUpLoadStateSuccess {
    object UserExist : SignUpLoadStateSuccess()
    object VerificationSuccessFul : SignUpLoadStateSuccess()
    object VerificationCodeSent : SignUpLoadStateSuccess()
}

class UnAuthException : RuntimeException()