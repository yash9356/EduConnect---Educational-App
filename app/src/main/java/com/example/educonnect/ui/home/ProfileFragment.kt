package com.example.educonnect.ui.home

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.educonnect.ui.home.viewmodel.DashboardViewModel
import com.example.educonnect.ui.models.sealedclass.BaseState
import com.example.educonnect.ui.models.user.User
import com.example.educonnect.utils.AppConstants
import com.example.educonnect.utils.showLoadingState
import com.example.educonnect.utils.toast
import com.example.educonnect.utils.viewBinding
import com.example.educonnect_educationalapp.R
import com.example.educonnect_educationalapp.databinding.FragmentProfileBinding
import com.example.educonnect_educationalapp.databinding.ProfileInputDialogBinding

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private val binding: FragmentProfileBinding by viewBinding {
        FragmentProfileBinding.bind(it)
    }

    private val dashboardViewModel: DashboardViewModel by viewModels()
    private val openImageContract =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null) {
                val placeHolder =
                    AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.ic_user
                    )
                Glide.with(requireContext())
                    .load(it)
                    .placeholder(placeHolder)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(binding.userProfile)
                dashboardViewModel.updateUserProfilePic(it)
            } else {
                toast { getString(R.string.user_cancelled_the_operation) }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnProgressLayout.btnAction.text = getString(R.string.save)
        binding.btnProgressLayout.btnAction.setOnClickListener {
            val newUserName = binding.etUserName.text.toString()
            if (dashboardViewModel.userData?.name != newUserName && newUserName.isNotBlank()) {
                dashboardViewModel.updateUserName(newUserName)
            }
        }
        binding.btnImageSelection.setOnClickListener {
            openImageContract.launch(AppConstants.IMAGE_MIME_TYPE_INTENT)
        }
        binding.userProfile.setOnClickListener {
            openImageContract.launch(AppConstants.IMAGE_MIME_TYPE_INTENT)
        }
        dashboardViewModel.userLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is BaseState.Failed -> {
                    binding.errorLayout.root.isVisible = false
                }

                BaseState.Loading -> {
                    binding.errorLayout.showLoadingState()
                }

                is BaseState.Success -> {
                    binding.errorLayout.root.isVisible = false
                    val user = it.data
                    showUi(user)
                    if (user.name == null) {
                        val dialog = Dialog(requireContext())
                        val bindingProfileInfoInput =
                            ProfileInputDialogBinding.inflate(LayoutInflater.from(context))
                        dialog.setContentView(bindingProfileInfoInput.root)
                        dialog.window?.setLayout(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        dashboardViewModel.userData?.profilePic?.let {
                            val placeHolder =
                                AppCompatResources.getDrawable(
                                    bindingProfileInfoInput.root.context,
                                    R.drawable.ic_user
                                )
                            Glide.with(requireContext())
                                .load(it)
                                .placeholder(placeHolder)
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                .into(bindingProfileInfoInput.userProfile)
                        }
                        bindingProfileInfoInput.btnImageSelection.setOnClickListener {
                            openImageContract.launch(AppConstants.IMAGE_MIME_TYPE_INTENT)
                        }
                        bindingProfileInfoInput.userProfile.setOnClickListener {
                            openImageContract.launch(AppConstants.IMAGE_MIME_TYPE_INTENT)
                        }
                        bindingProfileInfoInput.btnProgressLayout.btnAction.text =
                            getString(R.string.save)
                        bindingProfileInfoInput.btnProgressLayout.btnAction.setOnClickListener {
                            val userName =
                                bindingProfileInfoInput.etUserName.text.toString()
                            if (userName.isNotBlank()) {
                                dashboardViewModel.updateUserName(userName)
                                dialog.dismiss()
                            } else {
                                toast { "Please enter your username" }
                            }
                        }
                        dialog.show()
                    }
                }
            }
        }
    }

    fun showUi(user: User) {
        val placeHolder =
            AppCompatResources.getDrawable(
                binding.root.context,
                R.drawable.ic_user
            )
        Glide.with(requireContext())
            .load(user.profilePic)
            .placeholder(placeHolder)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(binding.userProfile)
        binding.etUserName.setText(user.name)
        user.phoneNumber?.let {
            binding.userPhoneNumber.text = it
        }
        user.emailId?.let {
            binding.userEmailId.text = it
        }
    }
}