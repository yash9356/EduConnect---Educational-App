package com.example.educonnect.utils

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import kotlinx.coroutines.CancellationException
import androidx.annotation.StringRes
import com.example.educonnect.EduConnectApplication
import com.example.educonnect.ui.models.sealedclass.ApiFailure
import com.example.educonnect.ui.models.user.User
import com.example.educonnect_educationalapp.R
import com.google.firebase.auth.FirebaseUser
import java.util.UUID

class AppUtils {
    fun getNewStudentUser(id: String?, fireBaseUser: FirebaseUser?): User {
        return User(
            id = id ?: UUID.randomUUID().toString(),
            creationDate = System.currentTimeMillis(),
            lastLoginDate = System.currentTimeMillis(),
            lastUpdateDate = System.currentTimeMillis(),
            gender = null,
            emailId = fireBaseUser?.email,
            name = null,
            phoneNumber = fireBaseUser?.phoneNumber,
            profilePic = null,
            type = AppConstants.FlagConstants.STUDENT
        )
    }
}

inline fun <M> runCoroutineCatching(block: () -> M, errorBlock: (Throwable) -> M): M {
    return try {
        block()
    } catch (cancellationException: CancellationException) {
        throw cancellationException
    } catch (t: Throwable) {
        errorBlock(t)
    }
}

@StringRes
inline fun ApiFailure.getStringIdForApiFailure(signOutEventHandler: () -> Unit): Int = when (this) {
    ApiFailure.NetworkFailure -> {
        R.string.network_error
    }

    is ApiFailure.Unknown -> {
        R.string.something_went_wrong
    }

    is ApiFailure.VerificationError -> {
        signOutEventHandler()
        this.msgId
    }

    ApiFailure.Unauthorised -> {
        signOutEventHandler()
        R.string.user_does_not_have_permission
    }
}

inline fun toast(crossinline msgProvider: () -> String) {
    val block = {
        Toast.makeText(EduConnectApplication.appContext, msgProvider(), Toast.LENGTH_SHORT).show()
    }
    postBlockInMainLooper(block)
}

fun postBlockInMainLooper(block: () -> Unit) {
    if (Looper.getMainLooper() == Looper.myLooper()) {
        block()
    } else {
        Handler(Looper.getMainLooper()).post(block)
    }
}