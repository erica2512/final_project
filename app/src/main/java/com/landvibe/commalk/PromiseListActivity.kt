package com.landvibe.commalk

import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.landvibe.commalk.common.AppDatabase
import kotlinx.android.synthetic.main.activity_promise_list.*

class PromiseListActivity : AppCompatActivity() {
    private lateinit var adapter: PromiseRecyclerViewAdapter
    private val p: Paint = Paint()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promise_list)

        setupRecyclerView()
        initSwipe()
    }
    //앱이 화면에 다시 보여질때(홈버튼을 눌렀다가 다시 보여지거나 다른 화면 갔다 온 경우)
    override fun onResume() {
        super.onResume()
        loadListAndApplyToRecyclerView()
    }

    //상태 불러오기
    private fun loadListAndApplyToRecyclerView() {
        val promiseList = AppDatabase.instance.promiseDao().getAll()
        adapter.items.clear()
        adapter.items.addAll(promiseList)
        adapter.notifyDataSetChanged()
    }

    private fun setupRecyclerView() {
        adapter = PromiseRecyclerViewAdapter()
        promise_recycler.adapter = adapter
        promise_recycler.layoutManager = LinearLayoutManager(this)
    }

    private fun initSwipe() {
        val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if (direction == ItemTouchHelper.LEFT) {
                    Thread {
                        val promiseDb = adapter.items[position]
                        AppDatabase.instance.promiseDao().delete(promiseDb)
                    }.start()
                    loadListAndApplyToRecyclerView()
                } else {
                    //오른쪽으로 밀었을때.
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )

                var icon: Bitmap
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val itemView: View = viewHolder.itemView
                    val height = itemView.bottom.toFloat() - itemView.top.toFloat()
                    val width = height / 3

                    if (dX > 0) {
                        //오른쪽으로 밀었을 때
                    } else {
                        p.color = Color.parseColor("#F5F5F5") // alicblue
                        val background = RectF(
                            itemView.right.toFloat() + dX, itemView.top.toFloat(),
                            itemView.right.toFloat(), itemView.bottom.toFloat()
                        )
                        c.drawRect(background, p)
                        icon = BitmapFactory.decodeResource(resources, R.drawable.close)
                        val iconDest = RectF( itemView.right - 2 * width,  itemView.top + width,  itemView.right - width, itemView.bottom - width);
                        c.drawBitmap(icon, null, iconDest, p);

                    }
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(promise_recycler)
    }


}
