package com.landvibe.commalk

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.landvibe.commalk.diary.Diary
import kotlinx.android.synthetic.main.list_diary.view.*
import java.text.SimpleDateFormat
import java.util.*

class DiaryRecyclerViewAdapter (
    var items: MutableList<Diary> = mutableListOf()
) : RecyclerView.Adapter<DiaryRecyclerViewAdapter.DiaryRecyclerViewHolder>() {

    class DiaryRecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryRecyclerViewHolder {
        return DiaryRecyclerViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_diary,parent, false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    //레이아웃 값 할당, 클릭할 시 어떤 일 할 지
    override fun onBindViewHolder(holder: DiaryRecyclerViewHolder, position: Int) {
        val item = items[position]
        with(holder.itemView){
            val time = Date(item.date)
            val mFormat = SimpleDateFormat("yyyy年 MM月 dd日")
            val date: String = mFormat.format(time)

            list_diary_text_title.text = item.title
            list_diary_text_date.text = date
            if(item.promise.isNotEmpty())
                list_diary_image.setImageResource(R.drawable.square2)
            else
                list_diary_image.setImageResource(R.drawable.square)

            holder.itemView.setOnClickListener {
                context.startActivity(Intent(context, DiaryDetailActivity::class.java).putExtra("id", item.id))
            }
        }
    }



}