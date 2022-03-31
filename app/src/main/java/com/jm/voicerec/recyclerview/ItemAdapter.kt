package com.jm.voicerec.recyclerview

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.animation.OvershootInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.jm.voicerec.MainActivity
import com.jm.voicerec.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_list.view.*

class ItemAdapter(val itemClick: (position:Int,item: Item) -> Unit) : RecyclerView.Adapter<ItemViewHolder>() {
    private var items: List<Item> = listOf()
    private var flag = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder =
        ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false))

    override fun onViewDetachedFromWindow(holder: ItemViewHolder) {
        super.onViewDetachedFromWindow(holder)
        flag = false
    }

    override fun onViewAttachedToWindow(holder: ItemViewHolder) {
        super.onViewAttachedToWindow(holder)
        flag = false
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])


//        Log.e("Adapter>>","${holder.adapterPosition} / ${holder.itemId}/ ${holder.layoutPosition}")

        holder.itemView.setOnClickListener {
//        Log.e("Adapter>>","$p0 / $p1")
//            val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X,1f, 1.33f)
//            val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y,1f, 1.33f)
            flag = !flag
            val scaleX : PropertyValuesHolder
            val scaleY : PropertyValuesHolder
            if (flag){
                scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X,1.33f, 1.33f)
                scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y,1.33f, 1f)
            }
            else{
                scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X,1.33f, 1.33f)
                scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y,1f, 1.33f)
            }

            ObjectAnimator.ofPropertyValuesHolder(holder.itemView, scaleX,scaleY).apply {
                duration = 500
                interpolator = OvershootInterpolator()
            }.start()

            itemClick(position,items[position])
        }
    }
    override fun getItemCount() = items.size

    fun setItems(newItems: List<Item>) {
        items = newItems
        notifyDataSetChanged()
    }
}

class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    fun bind(item: Item) {
        view.content_text.text = item.content
        view.list_item_icon.setImageResource(item.icon)
    }
}