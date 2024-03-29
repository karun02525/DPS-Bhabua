package com.dps_kaimur.view.login

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.dps_kaimur.R
import com.dps_kaimur.controller.sharedpreferences.LoginPrefences
import com.dps_kaimur.model.ResponseModel
import com.dps_kaimur.restservices.APIService
import com.dps_kaimur.restservices.ApiUtils
import com.dps_kaimur.utils.CustomProgressBar
import com.dps_kaimur.utils.Utils
import com.dps_kaimur.view.home.NavigationActivity
import kotlinx.android.synthetic.main.activity_signup.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback


class SignupActivity : AppCompatActivity() {

    private var countryCode: String? = null


    private var APIService: APIService? = null
    private lateinit var pb: CustomProgressBar
    private var loginPreference: LoginPrefences? = null
    private val TAG = SignupActivity::class.java!!.getName()


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        setContentView(R.layout.activity_signup)

        countryCode = "+1"
        loginPreference = LoginPrefences.getInstance()
        APIService = ApiUtils.apiService


        //skip condition
        btn_signup_skip.setOnClickListener {
            startActivity(Intent(this, NavigationActivity::class.java)
                    .putExtra("session", "0"))
        }


        initViewSubmit()

        //Term condition
        val s = getString(R.string.term_conditionss)
        term_condition.text = Html.fromHtml(s)//,Html.FROM_HTML_MODE_LEGACY)
        term_condition.setOnClickListener {
        }

        //again login
        signup_btn_login.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        //click country_code
        country_code.setOnClickListener { }
    }



    private fun hideSoftKeyboad(v: View) {
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)

    }

    private fun initViewSubmit() {

        signup_btn.setOnClickListener { v ->

            val mobile = txt_signup_mob.text.toString()
            val email = txt_signup_email.text.toString()
            val pass = txt_signup_pass.text.toString()
            val conf_pass = txt_signup_conf_pass.text.toString()

           /* if (mobile.isEmpty()) {
                Utils.showToast(this@SignupActivity, getString(R.string.login_validation_1), Color.WHITE)
                txt_signup_mob.requestFocus()
                hideSoftKeyboad(v)
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Utils.showToast(this@SignupActivity, getString(R.string.signup_validation_2), Color.WHITE)
                txt_signup_email.requestFocus()
                hideSoftKeyboad(v)
            } else if (pass.isEmpty()) {
                Utils.showToast(this@SignupActivity, getString(R.string.login_validation_3), Color.WHITE)
                txt_signup_pass.requestFocus()
                hideSoftKeyboad(v)
            } else if (conf_pass.isEmpty()) {
                Utils.showToast(this@SignupActivity, getString(R.string.signup_validation_4), Color.WHITE)
                txt_signup_conf_pass.requestFocus()
                hideSoftKeyboad(v)
            } else if (pass != conf_pass) {
                Utils.showToast(this@SignupActivity, getString(R.string.signup_validation_5), Color.WHITE)
                txt_signup_conf_pass.requestFocus()
                hideSoftKeyboad(v)
            } else if (!txt_signup_check.isChecked) {
                Utils.showToast(this@SignupActivity, getString(R.string.signup_validation_6), Color.WHITE)
            } else {*/

             initJsonOperation(countryCode!!, mobile, email, conf_pass)
             //  initJsonOperation("+91","8860961980","yuuu@g.c","12345678")


               /* startActivity(Intent(this@SignupActivity,OTPActivity::class.java)
                        .putExtra("session_otp","0")
                        .putExtra("userId","21")
                )*/

          //  }
        }

    }

    private fun initJsonOperation(code: String, mob: String, email: String, pass: String) {
        //  Utils.log(TAG!!, "signup data : $code,$mob,$email,$pass ")
        pb = CustomProgressBar(this)
        pb.setCancelable(false)
        pb.show()

        val requestBody = HashMap<String, String>()
        requestBody.put("email", email)
        requestBody.put("contact", mob)
        requestBody.put("countryCode",code)
        requestBody.put("password", pass)
        APIService!!.getUser(requestBody).enqueue(object : Callback, retrofit2.Callback<ResponseModel> {
            override fun onResponse(call: Call<ResponseModel>?, response: Response<ResponseModel>?) {
                pb.dismiss()
                Utils.log(TAG!!, "signup onResponse  code: ${response!!.raw()}")
                val status = response!!.code()

                if (status != 200) {
                    when (status) {
                        201 -> {
                            val mess = response!!.body().message.toString()
                            val userId = response!!.body().result.userId
                            Utils.log(TAG!!, "signup onResponse  body: $mess   $userId")
                            Utils.showToast(this@SignupActivity, mess, Color.YELLOW)

                            startActivity(Intent(this@SignupActivity,OTPActivity::class.java)
                                     .putExtra("session_otp","0")
                                     .putExtra("user_id",userId.toString())
                             )
                        }
                        204 -> Utils.showToast(this@SignupActivity,errorHandler(response), Color.YELLOW)
                        409 -> Utils.showToast(this@SignupActivity,errorHandler(response), Color.YELLOW)
                        400 -> Utils.showToast(this@SignupActivity,errorHandler(response), Color.YELLOW)
                        401 -> Utils.showToast(this@SignupActivity,errorHandler(response), Color.YELLOW)
                        403 -> Utils.showToast(this@SignupActivity,errorHandler(response), Color.YELLOW)
                        404 -> Utils.showToast(this@SignupActivity,errorHandler(response), Color.YELLOW)
                        500 -> Utils.showToast(this@SignupActivity,resources.getString(R.string.error_status_1), Color.YELLOW)
                           else -> Utils.showToast(this@SignupActivity,resources.getString(R.string.error_status_1), Color.WHITE)
                    }
                }
            }
            override fun onFailure(call: Call<ResponseModel>?, t: Throwable?) {
                pb.dismiss()
                Utils.log(TAG!!, "signup Throwable : $t")
                Utils.showToast(this@SignupActivity, "Sorry!No internet available", Color.WHITE)
            }
        })
    }


    private fun errorHandler(response: Response<ResponseModel>?):String{
        return try {
            val jObjError = JSONObject(response!!.errorBody().string())
            jObjError.getString("message")
        } catch (e: Exception) {
            e.message!!
        }
    }
}