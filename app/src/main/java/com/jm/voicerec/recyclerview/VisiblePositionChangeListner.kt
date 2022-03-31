package com.jm.voicerec.recyclerview

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// 리사이클러뷰 현재 화면에 보이는 아이템 position 반환
// 출처 : https://myung6024.tistory.com/17

public open class VisiblePositionChangeListner(
    _linearLayoutManager : LinearLayoutManager,
    _listner :OnChangeListner
) : RecyclerView.OnScrollListener(){
    public interface OnChangeListner{
        fun onFirstVisiblePositionChanged(position : Int)
        fun onLastVisiblePositionChanged(position : Int)
    }
    private var listener = _listner
    private var firstVisiblePosition = RecyclerView.NO_POSITION
    private var lastVisiblePosition = RecyclerView.NO_POSITION
    private var layoutManager = _linearLayoutManager

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val firstPosition = layoutManager.findFirstVisibleItemPosition()
        val lastPosition = layoutManager.findLastVisibleItemPosition()

        if(firstVisiblePosition == RecyclerView.NO_POSITION || lastVisiblePosition == RecyclerView.NO_POSITION){
            firstVisiblePosition = firstPosition
            lastVisiblePosition = lastPosition
            return
        }

        if(firstPosition < firstVisiblePosition){
            if(firstVisiblePosition - firstPosition > 1){
                for (i in 1 until firstVisiblePosition - firstPosition + 1){
                    listener.onFirstVisiblePositionChanged(firstVisiblePosition - i)
                }
            }else{
                listener.onFirstVisiblePositionChanged(firstPosition)
            }
            firstVisiblePosition = firstPosition
        }
        else if(firstPosition > firstVisiblePosition) firstVisiblePosition = firstPosition

        if(lastPosition > lastVisiblePosition){
            if(lastPosition - lastVisiblePosition > 1){
                for(i in 1 until lastPosition - lastVisiblePosition +1){
                    listener.onLastVisiblePositionChanged(lastVisiblePosition + i)
                }
            }
            else{
                listener.onLastVisiblePositionChanged(lastPosition)
            }
            lastVisiblePosition = lastPosition
        }
        else if(lastPosition < lastVisiblePosition) lastVisiblePosition = lastPosition
    }
}