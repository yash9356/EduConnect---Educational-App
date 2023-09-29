package com.example.educonnect.ui.login

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.example.educonnect.ui.login.viewmodel.LoginViewModel
import com.example.educonnect.utils.hideProgressBar
import com.example.educonnect.utils.showProgressBar
import com.example.educonnect.utils.toE164Format
import com.example.educonnect.utils.toast
import com.example.educonnect.utils.unsafeLazy
import com.example.educonnect.utils.viewBinding
import com.example.educonnect_educationalapp.R
import com.example.educonnect_educationalapp.databinding.FragmentOtpVerificationBinding

class OtpVerificationFragment : Fragment(R.layout.fragment_otp_verification) {
    private val binding: FragmentOtpVerificationBinding by viewBinding {
        FragmentOtpVerificationBinding.bind(it)
    }
    private val mobileNum: String by unsafeLazy {
        requireArguments().getString(MOBILE_BUNDLE_KEY) ?: error("$MOBILE_BUNDLE_KEY key null")
    }
    private val authVerificationId: String by unsafeLazy {
        requireArguments().getString(VERIFICATION_ID_BUNDLE_KEY)
            ?: error("$VERIFICATION_ID_BUNDLE_KEY key null")
    }
    private val registrationViewModel: LoginViewModel by viewModels()
    private val editTexts by unsafeLazy {
        arrayOf(
            binding.inputCode1,
            binding.inputCode2,
            binding.inputCode3,
            binding.inputCode4,
            binding.inputCode5,
            binding.inputCode6
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarLayout.tvTitle.text = getString(R.string.verify_contact)
        binding.txtMobileNumber.text = mobileNum.toE164Format()
        binding.btnProgressLayout.btnAction.setOnClickListener {
            verifyData(authVerificationId)
        }
        binding.txtResendOtp.setOnClickListener {
            registrationViewModel.sendVerificationCode(mobileNum.toE164Format(), requireActivity())
        }
    }

    private fun verifyData(storedVerificationId: String?) {
        val otp = getOtp()
        if (otp.length == OTP_LENGTH) {
            showProgressBar()
            registrationViewModel.signInWithPhoneAuthCredential(
                storedVerificationId.toString(),
                otp
            )
        } else {
            toast {
                getString(R.string.enter_otp)
            }
        }
    }

    private fun hideProgressBar() {
        binding.toolbarLayout.btnBack.visibility = View.VISIBLE
        binding.btnProgressLayout.hideProgressBar()
    }

    private fun showProgressBar() {
        binding.btnProgressLayout.showProgressBar()
        binding.toolbarLayout.btnBack.visibility = View.INVISIBLE
    }

    private fun getOtp(): String {
        return editTexts.joinToString(separator = "") { it.text }
    }

    companion object {
        const val MOBILE_BUNDLE_KEY = "mobile_bundle_key"
        const val OTP_VERIFICATION_REQ: String = "otp_verification_req"
        const val VERIFICATION_ID_BUNDLE_KEY = "verification_id_bundle_key"
        private const val OTP_LENGTH = 6

        fun launch(
            verificationId: String?,
            mobileNum: String,
            navController: NavController
        ) {
            navController.navigate(
                R.id.otpVerificationFragment, bundleOf(
                    VERIFICATION_ID_BUNDLE_KEY to verificationId,
                    MOBILE_BUNDLE_KEY to mobileNum
                )
            )
        }


        private const val IS_SUCCESS_OTP_VALIDATION_BUNDLE_KEY =
            "is_success_otp_validation_bundle_key"
        fun isSuccessOtpValidation(bundle: Bundle): Boolean =
            bundle.getBoolean(IS_SUCCESS_OTP_VALIDATION_BUNDLE_KEY, false)
    }
}