package com.example.educonnect.ui.home

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.example.educonnect.ui.adapter.VideoResAdapter
import com.example.educonnect.ui.home.viewmodel.DashboardViewModel
import com.example.educonnect.ui.models.sealedclass.BaseState
import com.example.educonnect.utils.AppConstants.IMAGE_MIME_TYPE_INTENT
import com.example.educonnect.utils.showLoadingState
import com.example.educonnect.utils.toast
import com.example.educonnect.utils.viewBinding
import com.example.educonnect_educationalapp.R
import com.example.educonnect_educationalapp.databinding.FragmentDashboardBinding
import com.example.educonnect_educationalapp.databinding.ProfileInputDialogBinding

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {
    private val binding: FragmentDashboardBinding by viewBinding {
        FragmentDashboardBinding.bind(it)
    }

    private lateinit var videoResAdapter: VideoResAdapter
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private val openImageContract =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null) {
                dashboardViewModel.updateUserProfilePic(it)
            } else {
                toast { getString(R.string.user_cancelled_the_operation) }
            }
        }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeEducationalResourcesData()
        videoResAdapter = VideoResAdapter()
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
                    if (user.name == null) {
                        val dialog = Dialog(requireContext())
                        dialog.window?.setLayout(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        val binding =
                            ProfileInputDialogBinding.inflate(LayoutInflater.from(context))
                        dialog.setContentView(binding.root)
                        binding.btnImageSelection.isVisible = false
                        binding.btnImageSelection.setOnClickListener {
                            openImageContract.launch(IMAGE_MIME_TYPE_INTENT)
                        }
                        binding.userProfile.setOnClickListener {
                            openImageContract.launch(IMAGE_MIME_TYPE_INTENT)
                        }
                        binding.btnProgressLayout.btnAction.text = getString(R.string.save)
                        binding.btnProgressLayout.btnAction.setOnClickListener {
                            val userName = binding.etUserName.text.toString()
                            if (userName.isNotBlank()){
                                dashboardViewModel.updateUserName(userName)
                                dialog.dismiss()
                            } else{
                                toast { "Please enter your username" }
                            }
                        }
                        dialog.show()
                    } else{
                        binding.txtHiUser.text = getString(R.string.hi_s, user.name)
                    }
                }
            }
        }
    }

    private fun observeEducationalResourcesData() {
        dashboardViewModel.educationVideosLiveData.observe(viewLifecycleOwner) {
            when(it){
                is BaseState.Failed -> {
                    binding.errorLayout.root.isVisible = false
                }
                BaseState.Loading -> {
                    binding.errorLayout.showLoadingState()
                }
                is BaseState.Success -> {
                    binding.errorLayout.root.isVisible = false
                    videoResAdapter.submitList(it.data)
                    binding.videoRecyclerview.adapter = videoResAdapter
                }
            }
        }
    }
}