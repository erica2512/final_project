package com.landvibe.commalk

import android.content.Intent
import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.landvibe.commalk.common.AppDatabase
import kotlinx.android.synthetic.main.activity_main.*
import com.landvibe.commalk.DiaryRecyclerViewAdapter as DiaryRecyclerViewAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: DiaryRecyclerViewAdapter
    private val p: Paint = Paint()
    private var backBtnTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupToolbar()
        setupRecyclerView()
        insertPlusDiary()
        initSwipe()

        weightbtn.setOnClickListener({
            val intent = Intent(this, WeightActivity::class.java)
            startActivity(intent)
        })

        feedbtn.setOnClickListener({
            val intent = Intent(this, FeedActivity::class.java)
            startActivity(intent)
        })

        waterbtn.setOnClickListener({
            val intent = Intent(this, WaterActivity::class.java)
            startActivity(intent)
        })


    }


    //앱이 화면에 다시 보여질때(홈버튼을 눌렀다가 다시 보여지거나 다른 화면 갔다 온 경우)
    override fun onResume() {
        super.onResume()
        loadListAndApplyToRecyclerView()
    }

    //화면이 아예 사라질 때(finish)
    override fun onDestroy() {
        super.onDestroy()
    }

    //화면이 홈버튼 같은거로 백그라운드로 될때
    override fun onPause() {
        super.onPause()
    }

    override fun onBackPressed() {
        val curTime = System.currentTimeMillis()
        val gapTime = curTime - backBtnTime
        if(gapTime in 0..2000)
            super.onBackPressed()
        else{
            backBtnTime = curTime
            Toast.makeText(this, "앱을 종료하려면 한번 더 누르세요", Toast.LENGTH_SHORT).show()

        }
    }


    //툴바
    private fun setupToolbar(){
        setSupportActionBar(main_toolbar)
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_top, menu)
        return true
        //return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item?.itemId){
            R.id.menu_promise_list ->{
                val intent = Intent(this, PromiseListActivity::class.java)
                intent.putExtra("id", 0)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    //추가버튼
    private fun insertPlusDiary() {
        main_button_add.setOnClickListener {
            val intent = Intent(this, DiaryDetailActivity::class.java)
            intent.putExtra("id", 0)
            startActivity(intent)
        }
    }


    //상태 불러오기
    private fun loadListAndApplyToRecyclerView() {
        val diaryList = AppDatabase.instance.diaryDao().getAll()
        adapter.items.clear()
        adapter.items.addAll(diaryList)
        adapter.notifyDataSetChanged()

        if(adapter.items.size == 0) {
            empty_item.visibility=View.VISIBLE
            main_recycler.visibility=View.GONE
        }
        else{
            empty_item.visibility=View.GONE
            main_recycler.visibility=View.VISIBLE
        }
    }

    private fun setupRecyclerView() {
        adapter = DiaryRecyclerViewAdapter()
        main_recycler.adapter = adapter
        main_recycler.layoutManager = LinearLayoutManager(this)
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
                        val diaryDb = adapter.items[position]
                        AppDatabase.instance.diaryDao().delete(diaryDb)
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
                        p.color = Color.parseColor("#F5F5F5") // plum
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
        itemTouchHelper.attachToRecyclerView(main_recycler)
    }
}