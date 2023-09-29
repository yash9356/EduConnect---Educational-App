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
import com.google.firebase.ktx.Firebase
import java.util.UUID

class AppUtils {
}

public fun String.toE164Format(): String = "+91$this"

public fun <T> unsafeLazy(initializer: () -> T): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE, initializer)
}

inline fun FirebaseUser.toUser(
    emailId: String?,
    existingUser: User?,
    userType: String,
): User {
    val operationTimeInMillis = System.currentTimeMillis()
    return if (existingUser == null) {
        User(
            id = this.uid,
            creationDate = this.metadata?.creationTimestamp ?: operationTimeInMillis,
            lastUpdateDate = operationTimeInMillis,
            lastLoginDate = this.metadata?.lastSignInTimestamp ?: operationTimeInMillis,
            emailId = emailId ?: this.email,
            gender = null,
            name = null,
            phoneNumber = this.phoneNumber,
            profilePic = null,
            type = userType
        )
    } else {
        updateUser(existingUser)
    }
}

@PublishedApi
internal fun FirebaseUser.updateUser(existingUser: User): User {
    val operationTimeInMillis = System.currentTimeMillis()
    return existingUser.copy(
        lastLoginDate = this.metadata?.lastSignInTimestamp ?: operationTimeInMillis,
        lastUpdateDate = operationTimeInMillis,
    )
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