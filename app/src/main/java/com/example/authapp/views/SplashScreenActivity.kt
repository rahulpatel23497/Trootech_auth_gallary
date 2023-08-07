package com.example.authapp.views

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.authapp.R
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val auth: FirebaseAuth
        var isLogin = false;

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            isLogin = true
            Handler(Looper.getMainLooper()).postDelayed({
                if (isLogin){
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else{
                    val intent = Intent(this, AuthenticationActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }, 2000) // 3000 is the delayed time in milliseconds.
        } else {
            isLogin = false
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, AuthenticationActivity::class.java)
                startActivity(intent)
                finish()
            }, 2000)
        }
    }
}