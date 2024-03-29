package com.dps_kaimur.view.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.dps_kaimur.R
import com.dps_kaimur.utils.CustomProgressBar
import com.dps_kaimur.view.login.LoginActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var pb: CustomProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({

            startActivity(Intent(this, LoginActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                     finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }, 1000)

    }
}
