package com.example.educonnect.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.RadioButton
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.educonnect.ui.HomeActivity
import com.example.educonnect.ui.login.viewmodel.LoginViewModel
import com.example.educonnect.ui.models.sealedclass.BaseState
import com.example.educonnect.ui.models.sealedclass.SignUpLoadStateSuccess
import com.example.educonnect.utils.getStringIdForApiFailure
import com.example.educonnect.utils.showLoadingState
import com.example.educonnect.utils.viewBinding
import com.example.educonnect.utils.toast
import com.example.educonnect_educationalapp.R
import com.example.educonnect_educationalapp.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class LoginFragment : Fragment(R.layout.fragment_login) {
    private val binding: FragmentLoginBinding by viewBinding {
        FragmentLoginBinding.bind(it)
    }
    private var isPhoneLoginSelected = true
    private lateinit var googleSignInClient: GoogleSignInClient
    private val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.let {
            isPhoneLoginSelected = it.getBoolean(IS_PHONE_LOGIN_SELECTED)
        }
        setUpTabUi()
        setUpGoogleSignIn()
        observeSignUpLoadState()
        if (!isPhoneLoginSelected) {
            binding.inputEmail.isGone = false
            binding.inputPassword.isGone = false
            binding.inputNumber.isVisible = false
        }
        binding.loginBtn.setOnClickListener {
            if (isPhoneLoginSelected) {
                val phoneNumber = binding.etNumber.text.toString()
                viewModel.performSignIn(phoneNumber, requireActivity())
            } else {
                val email = binding.etEmail.text.toString()
                val password = binding.etPassword.text.toString()
                viewModel.loginWithEmail(email, password)
            }
        }
    }

    private fun observeSignUpLoadState() {
        viewModel.signUpSuccessLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is BaseState.Failed -> {
                    binding.errorLayout.root.visibility = View.INVISIBLE
                    val messageId = it.error.getStringIdForApiFailure {
                        /* no-op */
                    }
                    toast { getString(messageId) }
                }

                BaseState.Loading -> {
                    binding.errorLayout.showLoadingState()
                }

                is BaseState.Success -> {
                    binding.errorLayout.root.visibility = View.INVISIBLE

                    when(it.data){
                        SignUpLoadStateSuccess.UserExist -> {}
                        SignUpLoadStateSuccess.VerificationCodeSent -> {
                            OtpVerificationFragment.launch(
                                verificationId = viewModel.storedVerificationId,
                                mobileNum = binding.etNumber.text.toString(),
                                findNavController()
                            )
                            setFragmentResultListener(
                                OtpVerificationFragment.OTP_VERIFICATION_REQ
                            ) { requestKey, bundle ->
                                if (requestKey == OtpVerificationFragment.OTP_VERIFICATION_REQ &&
                                    OtpVerificationFragment.isSuccessOtpValidation(bundle)
                                ) {
                                    startActivity(Intent(requireActivity(), HomeActivity::class.java))
                                    requireActivity().finish()
                                }
                            }

                        }
                        SignUpLoadStateSuccess.VerificationSuccessFul -> {
                            // this is the case when we don't need otp for user verification
                            // we need to still check all the required field
                            startActivity(Intent(requireActivity(), HomeActivity::class.java))
                            requireActivity().finish()
                        }
                    }
                    viewModel.resetSignUpState()
                }

                null -> {
                    /* no-op */
                }
            }
        }
    }

    private fun setUpGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        binding.googleLogin.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun setUpTabUi() {
        binding.phoneLoginBtn.setOnClickListener {
            displayPhoneLogin()
            binding.phoneLoginRadioBtn.performClick()
        }
        binding.emailLoginBtn.setOnClickListener {
            displayEmailLogin()
            binding.emailLoginRadioBtn.performClick()
        }
        binding.phoneLoginRadioBtn.setOnClickListener {
            displayPhoneLogin()
            bookServiceOptionSelector(it.findViewById(R.id.phone_login_radio_btn))
        }
        binding.emailLoginRadioBtn.setOnClickListener {
            displayEmailLogin()
            bookServiceOptionSelector(it.findViewById(R.id.email_login_radio_btn))
        }
    }

    private fun displayPhoneLogin() {
        isPhoneLoginSelected = true
        binding.emailLoginBtn.isVisible = true
        binding.phoneLoginBtn.isVisible = false
    }

    private fun displayEmailLogin() {
        isPhoneLoginSelected = false
        binding.emailLoginBtn.isVisible = false
        binding.phoneLoginBtn.isVisible = true
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                viewModel.loginWithGoogle(account)
            } catch (e: Exception) {
                toast { e.toString() }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        isPhoneLoginSelected.let { outState.putBoolean(IS_PHONE_LOGIN_SELECTED, it) }
    }

    companion object {
        private const val IS_PHONE_LOGIN_SELECTED = "is_phone_login_selected"
        private const val RC_SIGN_IN = 9001
    }

    private fun bookServiceOptionSelector(
        view: RadioButton
    ) {
        val isSelected = view.isChecked
        when (view.id) {
            R.id.phone_login_radio_btn -> {
                binding.inputPassword.isGone = isSelected
                binding.inputEmail.isGone = isSelected
                binding.inputNumber.isVisible = isSelected
            }

            R.id.email_login_radio_btn -> {
                binding.inputPassword.isGone = !isSelected
                binding.inputEmail.isGone = !isSelected
                binding.inputNumber.isVisible = !isSelected
            }
        }
    }
}