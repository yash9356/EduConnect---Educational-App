package com.example.educonnect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.educonnect.ui.HomeActivity
import com.example.educonnect_educationalapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser!=null){
            startActivity(Intent(this, HomeActivity::class.java))
            this.finish()
        }else{
            setContentView(R.layout.activity_login)
        }
    }
}