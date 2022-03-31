package com.jm.voicerec

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class AnimateBaseActivity(private val transitionMode: TransitionMode = TransitionMode.NONE) : AppCompatActivity(){
    enum class TransitionMode {
        NONE,
        HORIZON,
        VERTICAL
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when (transitionMode) {
            TransitionMode.HORIZON -> overridePendingTransition(R.anim.horizion_enter, R.anim.non_anim)
//            TransitionMode.VERTICAL -> overridePendingTransition(R.anim.vertical_enter, R.anim.none)
            else -> Unit
        }
    }

    override fun finish() {
        super.finish()

        when (transitionMode) {
            TransitionMode.HORIZON -> overridePendingTransition(R.anim.non_anim, R.anim.horizion_exit)
//            TransitionMode.VERTICAL -> overridePendingTransition(R.anim.none, R.anim.vertical_exit)
            else -> Unit
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (isFinishing) {
            when (transitionMode) {
                TransitionMode.HORIZON -> overridePendingTransition(R.anim.non_anim, R.anim.horizion_exit)
//                TransitionMode.VERTICAL -> overridePendingTransition(R.anim.none, R.anim.vertical_exit)
                else -> Unit
            }
        }
    }
}
