package com.example.educonnect.ui.home

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import com.example.educonnect.ui.home.viewmodel.DashboardViewModel
import com.example.educonnect.ui.models.sealedclass.BaseState
import com.example.educonnect.utils.showLoadingState
import com.example.educonnect.utils.viewBinding
import com.example.educonnect_educationalapp.R
import com.example.educonnect_educationalapp.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {
    private val binding: FragmentDashboardBinding by viewBinding {
        FragmentDashboardBinding.bind(it)
    }

    private val dashboardViewModel: DashboardViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeEducationalResourcesData()
        dashboardViewModel.userLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is BaseState.Failed -> {
                    //TODO Signout
                }

                BaseState.Loading -> {
                }

                is BaseState.Success -> {
                    val user = it.data
                    if (user.name == null) {
                        val dialog = Dialog(requireContext())

                        dialog.show()
                    }
                }
            }
        }
    }

    private fun observeEducationalResourcesData() {
        dashboardViewModel.educationVideosLiveData.observe(viewLifecycleOwner) {
            when(it){
                is BaseState.Failed -> {
                    /* no-op */
                }
                BaseState.Loading -> {
                    binding.errorLayout.showLoadingState()
                }
                is BaseState.Success -> {

                }
            }
        }
    }
}