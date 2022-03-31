package com.jm.voicerec

import android.app.Activity
import android.widget.Toast


class BackPressHandler(_activity: Activity?) {
    private var backKeyPressedTime: Long = 0
    private var activity: Activity? = null
    private var toast: Toast? = null

    init {
        this.activity = _activity
    }

    fun onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis()
            showGuide()
            return
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity?.finish()
            toast?.cancel()
        }
    }

    fun onBackPressed(msg: String) {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis()
            showGuide(msg)
            return
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity?.finish()
            toast?.cancel()
        }
    }

    fun onBackPressed(time: Double) {
        if (System.currentTimeMillis() > backKeyPressedTime + time * 1000) {
            backKeyPressedTime = System.currentTimeMillis()
            showGuide()
            return
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity?.finish()
            toast?.cancel()
        }
    }

    fun onBackPressed(msg: String, time: Double) {
        if (System.currentTimeMillis() > backKeyPressedTime + time * 1000) {
            backKeyPressedTime = System.currentTimeMillis()
            showGuide(msg)
            return
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity?.finish()
            toast?.cancel()
        }
    }

    private fun showGuide() {
        toast = Toast.makeText(activity, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다", Toast.LENGTH_SHORT)
        toast?.show()
    }

    private fun showGuide(msg: String) {
        toast = Toast.makeText(activity, msg, Toast.LENGTH_SHORT)
        toast?.show()
    }

}