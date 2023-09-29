package com.example.educonnect.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.doOnLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import com.example.educonnect_educationalapp.R
import com.example.educonnect_educationalapp.databinding.LayoutEmptyOrErrorStateBinding
import com.example.educonnect_educationalapp.databinding.LayoutProgressButtonBinding
import com.example.educonnect_educationalapp.databinding.LayoutTopBarBinding

private const val IMAGE_HASH = "hash"
private const val HASH = "hash"
private const val PLACE_HOLDER = "placeHolder"
private const val CIRCULAR_IMAGE_URL = "circularImageUrl"
private const val IMAGE_URL = "imageUrl"


// fun LayoutTopBarBinding.updateToolbar(
//     @StringRes titleRes: Int,
//     @StringRes toolTipText: Int?,
//     isTransparent: Boolean = false,
//     onCLick: (View) -> Unit,
// ) {
//     this.tvTitle.setText(titleRes)
//     this.btnBack.isClickable = true
//     this.btnBack.setOnClickListener(onCLick)
//     this.root.background = if (isTransparent) {
//         ContextCompat.getDrawable(this.root.context, R.drawable.screen_background)
//     } else {
//         null
//     }
//     if (toolTipText != null) {
//         this.btnInfo.isVisible = true
//         this.btnInfo.isClickable = true
//         this.btnInfo.applyTooltipAndContentDesc(this.root.context.getString(R.string.info))
//     } else {
//         this.btnInfo.isGone = true
//     }
// }

// @BindingAdapter(value = ["applyTooltipAndContentDesc"])
// fun View.applyTooltipAndContentDesc(contentDescription: String) {
//     this.contentDescription = contentDescription
//     ViewCompat.setTooltipText(this, contentDescription)
// }

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
