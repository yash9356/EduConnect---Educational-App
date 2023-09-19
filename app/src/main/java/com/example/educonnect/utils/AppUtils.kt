package com.example.educonnect.utils

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.example.educonnect.EduConnectApplication

class AppUtils {
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