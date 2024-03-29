package com.dps_kaimur.view.login

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.dps_kaimur.R
import com.dps_kaimur.controller.sharedpreferences.LoginPrefences
import com.dps_kaimur.restservices.APIService
import com.dps_kaimur.restservices.ApiUtils
import com.dps_kaimur.utils.CustomProgressBar
import com.dps_kaimur.utils.Utils
import com.dps_kaimur.view.home.NavigationActivity
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject


class LoginActivity : AppCompatActivity() {

    //OTP
    companion object {
        private val permissionsRequired = arrayOf(Manifest.permission.READ_SMS)
       // private val permissionsRequired = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.READ_SMS)
        private val REQUEST_PERMISSION_SETTING = 101
        private val PERMISSION_CALLBACK_CONSTANT = 100
    }

    private var permissionStatus: SharedPreferences? = null
    private var sentToSettings = false

    private var APIService: APIService? = null
    private lateinit var pb: CustomProgressBar
    private var loginPreference: LoginPrefences? = null
    private val TAG = LoginActivity::class.java.getName()



    override fun onStart() {
        super.onStart()
        loginPreference = LoginPrefences.getInstance();
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        loginPreference = LoginPrefences.getInstance()
        APIService = ApiUtils.apiService

        if (!loginPreference!!.hasValue(loginPreference!!.getLoginPreferences(this@LoginActivity))) {
            startActivity(Intent(this@LoginActivity, NavigationActivity::class.java)
                    .putExtra("session", "1")
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                 finish()
        }

        setContentView(R.layout.activity_login)
        permissionStatus = getSharedPreferences("permissionStatus", Context.MODE_PRIVATE)
        ReadPermissions()


        Log.d("TAG_TEST", "CHECK: " + (!loginPreference!!.hasValue(loginPreference!!.getLoginPreferences(this@LoginActivity))).toString())

        //  val userPass = loginPreference!!.getPRE_Pass(LoginPrefences.getInstance().getLoginPreferences(this))
        // val userMOB = loginPreference!!.getPRE_Mobile(LoginPrefences.getInstance().getLoginPreferences(this))

        // Log.d("TAG_TEST", "DATA TEST: LOGIN: $userPass $userMOB")



        initViewSubmit()

        //Click Signup
        login_btn_signup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
        //Forgot Password
        btn_forgot_pass.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    private fun initViewSubmit() {
        btn_login.setOnClickListener { v ->
            val mob = login_et_mobile.text.toString()
            val pass = login_et_pass.text.toString()

            if (mob.isEmpty()) {
                Utils.showToast(this@LoginActivity, getString(R.string.login_validation_1), Color.WHITE)
                login_et_mobile.requestFocus()
                hideSoftKeyboad(v)
            }/* else if (mob.length < 5) {
                Utils.showToast(this@LoginActivity, getString(R.string.login_validation_2), Color.WHITE)
                login_et_mobile.requestFocus()
                hideSoftKeyboad(v)
            }*/ else if (pass.isEmpty()) {
                Utils.showToast(this@LoginActivity, getString(R.string.login_validation_3), Color.WHITE)
                login_et_pass.requestFocus()
            } else {
                hideSoftKeyboad(v)
                initJsonOperation(mob,pass)
            }
        }
    }
    private fun hideSoftKeyboad(v: View) {
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)

    }

    private fun initJsonOperation(mob:String,pass:String) {
        Utils.log(TAG!!, "Login data : $mob,$pass ")
        pb = CustomProgressBar(this)
        pb.setCancelable(false)
        pb.show()


            AndroidNetworking.post("http://dpsbhabua.co.in/DPS_BHABUA/login_api.php?apicall=login")
                    .addBodyParameter("admission_no",mob)
                    .addBodyParameter("password",pass)
                    .build()
                    .getAsJSONObject(object : JSONObjectRequestListener {
                        override fun onResponse(response: JSONObject) {
                            pb.dismiss()
                          Log.d(TAG,"Login Response : "+response)

                            val mess=response.getString("message")
                            Toast.makeText(this@LoginActivity,mess,Toast.LENGTH_SHORT).show()
                            if(response.getString("error") == "false") {

                                val res=response.getJSONObject("result")
                                val admn_no=res.getString("admn_no")
                                val roll_no=res.getString("roll_no")
                                val student_name=res.getString("student_name")
                                val avatar=res.getString("avatar")
                                val avatar_bg=res.getString("avatar_bg")


                                Log.d(TAG, "Login Data  : $admn_no $roll_no $student_name $avatar $avatar_bg")

                                val user=response.getJSONObject("user")




                            }





/*
                            val arr=response.getJSONArray("result")
                            for(i in 0 until arr.length()){
                                val obj=arr.getJSONObject(i)
                                Log.d("TAGS",obj.toString())
                                parseJson(obj)
                            }*/
                        }

                        override fun onError(error: ANError) {
                            pb.dismiss()
                            Log.d(TAG,"Login ANError : "+error.toString())
                        }
                    })
        }



/*

        Handler().postDelayed({pb.dismiss()

            LoginPrefences.getInstance().addData(this@LoginActivity, mob, pass,"","","")
            startActivity(Intent(this@LoginActivity, NavigationActivity::class.java)
                    .putExtra("session", "1")
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            finish()

        },2000)
*/




    //OTP ReadPermissions
    private fun ReadPermissions() {
       // if (ActivityCompat.checkSelfPermission(this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED) {
        if (ActivityCompat.checkSelfPermission(this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED ) {
          //  if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[0]) || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[1])) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[0])) {
                //Show Information about why you need the permission
                val builder = AlertDialog.Builder(this)
                builder.setCancelable(false)
                builder.setTitle("Need Permissions")
                builder.setMessage("This app needs SMS permissions.")
                builder.setPositiveButton("Grant") { dialog, which ->
                    dialog.cancel()
                    ActivityCompat.requestPermissions(this@LoginActivity, permissionsRequired, PERMISSION_CALLBACK_CONSTANT)
                }
                builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
                builder.show()
            } else if (permissionStatus!!.getBoolean(permissionsRequired[0], false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Need Permissions")
                builder.setMessage("This app needs SMS permissions.")
                builder.setPositiveButton("Grant") { dialog, which ->
                    dialog.cancel()
                    sentToSettings = true
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivityForResult(intent, REQUEST_PERMISSION_SETTING)
                    Toast.makeText(baseContext, "Go to Permissions to Grant  Camera and Location", Toast.LENGTH_LONG).show()
                }
                builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
                builder.show()
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT)
            }
            val editor = permissionStatus!!.edit()
            editor.putBoolean(permissionsRequired[0], true)
            editor.commit()
        } else {
            //You already have the permission, just go ahead.
            // proceedAfterPermission();
        }
    }
}
