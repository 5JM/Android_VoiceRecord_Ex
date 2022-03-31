package com.jm.voicerec

import android.graphics.drawable.AnimatedVectorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import kotlinx.android.synthetic.main.activity_play.*
import java.io.File
import java.io.IOException


class PlayActivity : AnimateBaseActivity(TransitionMode.HORIZON) {
    private var mediaPlayer : MediaPlayer? = null
    lateinit var fileName : String
    lateinit var filePath : String
    private var isPlaying = MutableLiveData(false)
    private var handler = PlayerHandler()
    private val context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        fileName = intent.getStringExtra("fileName").toString()
        filePath = intent.getStringExtra("filePath").toString()

        val file = File(filePath)

        playTitle.text = fileName

        isPlaying.observe(this
        ) {
            if (it){
                playButton.setImageResource(R.drawable.anim_vector_play_to_pause)
                (playButton.drawable as AnimatedVectorDrawable).start()
            }
            else{
                playButton.setImageResource(R.drawable.anim_vector_pause_to_play)
                (playButton.drawable as AnimatedVectorDrawable).start()
            }
        }

        playButton.setOnClickListener {
            if(isPlaying.value == true) {
                stopAudio()
            }
            if(isPlaying.value == false){
                playAudio(file)
            }
        }

//        playButton.setOnClickListener {
//
//            if(isPlaying.value == true) stopAudio()
//            else {
//                playAudio(file)
//            }
//        }

        deleteButton.setOnClickListener {
            Log.e("Thread>>","alive : ${PlayerThread().isAlive}")

            if (file.delete()) Toast.makeText(this, "삭제 완료", Toast.LENGTH_SHORT).show()
            else Toast.makeText(this, "삭제 실패.\n파일이 존재하지 않음.", Toast.LENGTH_SHORT).show()
        }
    }

    // 녹음 파일 재생
    private fun playAudio(file: File) {

        mediaPlayer = MediaPlayer()
        try {
            mediaPlayer!!.setDataSource(file.getAbsolutePath())
            mediaPlayer!!.prepare()

            progressBar.max = mediaPlayer!!.duration

            Log.e("Each>>","len : ${mediaPlayer!!.duration}")

            mediaPlayer!!.start()
//            var curTime = System.currentTimeMillis()
//            Log.e("Each>>","start : ${curTime}")

            isPlaying.postValue(true)

            PlayerThread().start()
            mediaPlayer!!.setOnCompletionListener {
//                var endTime = System.currentTimeMillis()
//                Log.e("Each>>","end : ${endTime - curTime}")
                stopAudio()
            }
        } catch (e: IOException) {
            Toast.makeText(this, "재생 파일이 존재하지 않음.", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    // 녹음 파일 중지
    private fun stopAudio() {
        isPlaying.postValue(false)
        if(mediaPlayer!=null)
            mediaPlayer!!.stop()
    }

    inner class PlayerThread : Thread(){
        override fun run() {
            if(mediaPlayer!=null){
                while (true){
                    if(mediaPlayer!!.currentPosition >= mediaPlayer!!.duration){
                        break
                    }
//                    Log.e("Thread>>","start ${mediaPlayer==null} / ${isPlaying.value}")
                    Log.e("Thread>>","${mediaPlayer!!.currentPosition} / ${mediaPlayer!!.duration}")
                    if(isPlaying.value==true){
                        val msg = handler.obtainMessage()
                        val bundle = Bundle()
                        bundle.putInt("value",mediaPlayer!!.currentPosition)
                        msg.data = bundle
                        handler.sendMessage(msg)
                    }
//                    progressBar.progress = mediaPlayer!!.currentPosition
                }
            }

        }
    }
    inner class PlayerHandler : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            var bundle = msg.data
            val value = bundle.getInt("value")
            progressBar.progress = value
        }
    }
}
