package com.landvibe.commalk

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.landvibe.commalk.promise.Promise
import kotlinx.android.synthetic.main.list_promise.view.*
import java.util.*

class PromiseRecyclerViewAdapter (
    var items: MutableList<Promise> = mutableListOf()
) : RecyclerView.Adapter<PromiseRecyclerViewAdapter.PromiseRecyclerViewHolder>(){

    class PromiseRecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromiseRecyclerViewHolder {
        return PromiseRecyclerViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_promise,parent, false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }


    override fun onBindViewHolder(holder: PromiseRecyclerViewHolder, position: Int) {
        val item = items[position]
        with(holder.itemView){
            list_promise_text_date.text = item.date

            list_promise_text.text = item.promise

            val todayCal : Calendar = Calendar.getInstance()
            val today : Long = todayCal.timeInMillis/86400000

            val count = today - item.time + 1

            val pDay = count.toString() + "日"
            list_promise_date_gap.text = pDay
        }
    }

}