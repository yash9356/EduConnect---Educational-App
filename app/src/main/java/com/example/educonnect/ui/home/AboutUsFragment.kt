package com.example.educonnect.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.educonnect.utils.viewBinding
import com.example.educonnect_educationalapp.R
import com.example.educonnect_educationalapp.databinding.FragmentAboutUsBinding

class AboutUsFragment : Fragment(R.layout.fragment_about_us) {
    private val binding: FragmentAboutUsBinding by viewBinding {
        FragmentAboutUsBinding.bind(it)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}