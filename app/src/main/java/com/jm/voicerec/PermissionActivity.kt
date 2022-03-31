package com.jm.voicerec

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class PermissionActivity : AppCompatActivity() {
    val MULTIPLE_PERMISSIONS = 77
    var permissionListener = MutableLiveData<Boolean>()
    lateinit var button : TextView
    internal lateinit var intent : Intent

    private lateinit var permissions : Array<String>

//    private val permissions = arrayOf(
//        Manifest.permission.READ_EXTERNAL_STORAGE,
//        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//        Manifest.permission.RECORD_AUDIO
//    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)

        permissions = if(android.os.Build.VERSION.SDK_INT>=29){
            arrayOf(Manifest.permission.RECORD_AUDIO)
        }else{
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
            )
        }

        button=findViewById(R.id.permissionGuideButton)
        intent = Intent(this,MainActivity::class.java)

        // 권한이 이미 있으면 바로 mainActivity로 이동
        var result = false

        permissions.forEach { pm ->
            result = (ContextCompat.checkSelfPermission(this, pm) == PackageManager.PERMISSION_GRANTED)
        }

        if (result) {
            startActivity(intent)
            this.finish()
        }

        button.setOnClickListener {
            permissionListener.value=checkPermissions()
            permissionListener.observe(this, Observer {
                if(it==true){
                    startActivity(intent)
                    this.finish()
                }
            })
        }
    }

    private fun checkPermissions(): Boolean {
        var result: Int
        val permissionList: MutableList<String> = ArrayList()
        for (pm in permissions) {
            result = ContextCompat.checkSelfPermission(this, pm)
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(pm)
            }
        }
        if (permissionList.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionList.toTypedArray(),
                MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }

    /**
     * checkPermission 메소드를 실행한 후 콜백 메소드
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MULTIPLE_PERMISSIONS -> {
                var isDeny = false
                if (grantResults.isNotEmpty()) {
                    var i = 0
                    while (i < permissions.size) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            //permission denyed
                            isDeny = true
                        }
                        i++
                    }
                }
                if (isDeny) {
                    showNoPermissionToastAndFinish()
                }else{
                    permissionListener.postValue(true)
                }
            }
        }
    }

    /**
     * 권한 거부 시 실행 메소드
     */
    private fun showNoPermissionToastAndFinish() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("서비스 이용 알림")
        builder.setMessage("필수 권한을 허용해야\n서비스 정상 이용이 가능합니다.\n권한 요청 시 반드시 허용해주세요.")
        builder.setPositiveButton(
            "확인"
        ) { dialog, which ->
            moveTaskToBack(true) // 태스크를 백그라운드로 이동
            finishAndRemoveTask() // 액티비티 종료 + 태스크 리스트에서 지우기
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }
}