package com.dps_kaimur.utils

import android.app.Activity
import android.content.Context
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.TextView
import com.dps_kaimur.R


object Utils {

    private val SHOW_LOG = true

    //Custom Toast
    fun showToast(context: Context, message: String,color: Int): Snackbar {
        val sb = Snackbar.make((context as Activity).findViewById<View>(android.R.id.content), message, Snackbar.LENGTH_LONG)
        sb.view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
        val textView = sb.view.findViewById<TextView>(android.support.design.R.id.snackbar_text)
        textView.setTextColor(color)
        sb.show()
        return sb
    }

    fun log(tag: String, message: String) {
        if (SHOW_LOG)
            Log.d(tag, message)
    }

}