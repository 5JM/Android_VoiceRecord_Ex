package com.jm.voicerec

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.PagerSnapHelper
import com.jm.voicerec.recyclerview.Item
import com.jm.voicerec.recyclerview.ItemAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_list.*
import java.io.IOException

class MainActivity : AnimateBaseActivity(TransitionMode.HORIZON), SelectItemInterface {
    private var selectItem : String? = ""
    private var selected = MutableLiveData<Boolean>(false)
    private lateinit var audioUri : Uri

    private val itemAdapter by lazy{
        ItemAdapter{ position: Int, item: Item ->
//            Toast.makeText(this@MainActivity, "Pos ${position}", Toast.LENGTH_SHORT).show()
            selected.postValue(!selected.value!!)
            selectItem = position.toString()
            carouselRecyclerView.smoothScrollToPosition(position)
        }
    }
    private val possibleItems = listOf(
        Item("안녕하세요 1", R.drawable.ic_baseline_touch),
        Item("안녕하세요 2", R.drawable.ic_baseline_touch),
        Item("안녕하세요 3", R.drawable.ic_baseline_touch),
        Item("안녕하세요 4", R.drawable.ic_baseline_touch),
        Item("안녕하세요 5", R.drawable.ic_baseline_touch)
    )

    private val backPressHandler = BackPressHandler(this)

    private var audioFilePath : String? = null
    private var audioFileName : String? = null
    private var mediaRecorder: MediaRecorder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        selected.observe(this) {
//            Log.e("Adapter>>","${it}")
//        }

        selected.observe(this) {
            if(it) recordButton.visibility = View.VISIBLE
            else recordButton.visibility = View.GONE
        }

        nextButton.setOnClickListener {
            if(!audioFileName.isNullOrEmpty() && !recordButton.isChecked){
                intent = Intent(this, PlayActivity::class.java)
                intent.putExtra("filePath",audioFilePath)
                intent.putExtra("fileName",audioFileName)
//                Log.e("Record>>","fileName : ${audioFileName}")
                startActivity(intent)
            }
            else Toast.makeText(this, "녹음된 파일이 없습니다.",Toast.LENGTH_SHORT).show()
        }

        recordButton.setOnCheckedChangeListener { compoundButton, checked ->
            // animation 효과 여러개 적용시
            val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X,0.8f, 1f)
            val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y,0.8f, 1f)
            val rotate = PropertyValuesHolder.ofFloat(View.ROTATION, 15f, 0f)

            ObjectAnimator.ofPropertyValuesHolder(recordButton, scaleX,scaleY,rotate).apply {
                duration = 500
                interpolator = OvershootInterpolator()
            }.start()

//            // todo record
//            if(checked) selectItem?.let { startRecord(it) }
//            else stopRecording()

            Toast.makeText(this, "${selectItem}", Toast.LENGTH_SHORT).show()

            // animation 효과 하나 적용시
//            val animator = ObjectAnimator.ofFloat(recordButton, View.ROTATION, 10f, 0f)
//            rotate.duration = 300
//            rotate.interpolator = LinearInterpolator()
//            rotate.start()
        }

        carouselRecyclerView.initialize(itemAdapter)
        carouselRecyclerView.setViewsToChangeColor(listOf(R.id.list_item_background))
//        carouselRecyclerView.setViewsToChangeColor(listOf(R.id.list_item_background, R.id.list_item_text))

        // recyclerView item 한칸씩 이동
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(carouselRecyclerView)

        itemAdapter.setItems(possibleItems)
    }

    private fun getLargeListOfItems(): List<Item> {
        val items = mutableListOf<Item>()
        (0..20).map { items.add(possibleItems.random()) }
//        (0..40).map { items.add(possibleItems.size) }
        return items
    }

    override fun stateSelected(state : Boolean) {
        if (state)
            selected.postValue(false)

//        if(recordButton.isChecked) recordButton.isChecked= false

//        if (state == 0) recordButton.visibility = View.VISIBLE
////        else recordButton.visibility = View.INVISIBLE
//        else recordButton.visibility = View.GONE

//        selected.observe(this) {
//            recordButton.isEnabled = it

//            if(it) recordButton.visibility = View.VISIBLE
//            else recordButton.visibility = View.GONE

//            if(state == 0 && it) recordButton.visibility = View.VISIBLE
//            else recordButton.visibility = View.GONE
//        }
    }

//    override fun currentItem(position: Int) {
//        selectItem = position.toString()
//    }

    override fun onBackPressed() {
        backPressHandler.onBackPressed("\'뒤로\' 버튼을 두 번 누르면 종료됩니다.")
    }

    private fun startRecord(itemNum : String){
        val recordPath = getExternalFilesDir("/")?.absolutePath
        audioFileName = itemNum + "_" + "audio.mp4"
        audioFilePath = "$recordPath/$audioFileName"
        Log.e("Record>>", audioFileName!!)
        //Media Recorder 생성 및 설정
        mediaRecorder = MediaRecorder()
        if (mediaRecorder != null) {
            mediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            mediaRecorder!!.setOutputFile(audioFilePath)
            mediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                mediaRecorder!!.prepare()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            //녹음 시작
            Log.e("Record>>","Recording...")
            mediaRecorder!!.start()
        }
        else{
            Log.e("RecordErr>>","MediaRecorder is null!")
        }
    }

    // 녹음 종료
    private fun stopRecording() {
        if(mediaRecorder!=null){
            // 녹음 종료 종료
            mediaRecorder!!.stop()
            mediaRecorder!!.release()
            mediaRecorder = null

            // 파일 경로(String) 값을 Uri로 변환해서 저장
            //      - Why? : 리사이클러뷰에 들어가는 ArrayList가 Uri를 가지기 때문
            //      - File Path를 알면 File을  인스턴스를 만들어 사용할 수 있기 때문
//            audioUri = Uri.parse(audioFilePath)
//            // 데이터 ArrayList에 담기
//            audioList.add(audioUri).........

            // 데이터 갱신
//            audioAdapter.notifyDataSetChanged()
        }

    }
}