package com.dps_kaimur.view.generateReport

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.dps_kaimur.R
import kotlinx.android.synthetic.main.activity_generate_report.*

class GenerateReport : AppCompatActivity() {

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        setContentView(R.layout.activity_generate_report)

        report_btn_click_back.setOnClickListener {
            onBackPressed()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        submit_btn.setOnClickListener {
         //   onBackPressed()
        }

    }
}
