package com.example.educonnect.utils

import android.view.View
import androidx.annotation.StringRes
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.educonnect_educationalapp.databinding.LayoutEmptyOrErrorStateBinding
import com.example.educonnect_educationalapp.databinding.LayoutProgressButtonBinding
import com.example.educonnect_educationalapp.databinding.LayoutTopBarBinding


fun LayoutTopBarBinding.setTitleToolbar(@StringRes titleRes: Int) {
    this.tvTitle.setText(titleRes)
    this.btnBack.isVisible = false
    this.btnInfo.isVisible = false
}

fun LayoutProgressButtonBinding.hideProgressBar() {
    this.buttonProgressBar.visibility = View.INVISIBLE
    this.btnAction.visibility = View.VISIBLE
}

fun LayoutProgressButtonBinding.showProgressBar() {
    this.buttonProgressBar.visibility = View.VISIBLE
    this.btnAction.visibility = View.INVISIBLE
}

fun LayoutEmptyOrErrorStateBinding.showLoadingState() {
    root.visibility = View.VISIBLE
    errorLayoutProgressBar.isVisible = true
    btnTryAgain.isGone = true
    tvErrorEmpty.isGone = true
}

fun LayoutEmptyOrErrorStateBinding.showErrorState(message: String, onCLick: (View) -> Unit) {
    root.isVisible = true
    tvErrorEmpty.text = message
    btnTryAgain.isVisible = true
    tvErrorEmpty.isVisible = true
    errorLayoutProgressBar.isGone = true
    btnTryAgain.setOnClickListener(onCLick)
}
