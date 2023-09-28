package com.example.educonnect.ui.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.educonnect.utils.viewBinding
import com.example.educonnect_educationalapp.R
import com.example.educonnect_educationalapp.databinding.FragmentOtpVerificationBinding

class OtpVerificationFragment : Fragment(R.layout.fragment_otp_verification) {
    private val binding: FragmentOtpVerificationBinding by viewBinding {
        FragmentOtpVerificationBinding.bind(it)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarLayout.tvTitle.text = getString(R.string.verify_contact)

    }
}