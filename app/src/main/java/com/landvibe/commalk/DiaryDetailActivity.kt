package com.landvibe.commalk

import android.R.id.home
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.landvibe.commalk.common.AppDatabase
import com.landvibe.commalk.diary.Diary
import com.landvibe.commalk.promise.Promise
import kotlinx.android.synthetic.main.activity_diary_detail.*
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class DiaryDetailActivity : AppCompatActivity(), GestureDetector.OnDoubleTapListener {
    @RequiresApi(Build.VERSION_CODES.O)
    //val now: LocalDate = LocalDate.now()
    val now: Long = System.currentTimeMillis()
    private var dayMode: Boolean = false

    @RequiresApi(Build.VERSION_CODES.O)
    val time = Date(now)
    private val mFormat = SimpleDateFormat("yyyy年 MM月 dd日 HH:mm")
    private val date: String = mFormat.format(time)

    private var mode: Boolean = true

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_detail)
        setupToolbarWrite()
        setupListener()
        //createAlertDialog()

        loadDate()
        loadDiary()

        //loadPromise()
    }

    override fun onDoubleTap(event: MotionEvent?): Boolean {
        Log.d("Gestures", "onDoubleTap: $event")
        Toast.makeText(this, "더블탭", Toast.LENGTH_SHORT).show()
        return true
    }

    override fun onDoubleTapEvent(event: MotionEvent?): Boolean {
        Log.d("Gestures", "onDoubleTapEvent: $event")
        Toast.makeText(this, "더블탭", Toast.LENGTH_SHORT).show()
        return true
    }

    override fun onSingleTapConfirmed(p0: MotionEvent?): Boolean {
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadDate() {
        text_date.text = date
    }

    private fun loadDiary() {
        if (intent.getIntExtra("id", 0) == 0) {
            return
        }
        val id = intent.getIntExtra("id", 0)
        val diary = AppDatabase.instance.diaryDao().get(id)

        edit_title.setText(diary.title)
        edit_body.setText(diary.contents)
        edit_promise.setText(diary.promise)
        val time = Date(diary.date)
        val mFormat = SimpleDateFormat("yyyy年 MM月 dd日 HH:mm")
        val date: String = mFormat.format(time)
        text_date.text = date
    }

    //저장
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupListener() {
        bt_promise.setOnClickListener {
            //alertDialog.show()
            if (mode) {
                diary_detail_promise_layout.visibility = View.GONE
                bt_promise.foreground = resources.getDrawable(R.drawable.heart)
                mode = false
            } else {
                diary_detail_promise_layout.visibility = View.VISIBLE
                bt_promise.foreground = resources.getDrawable(R.drawable.heart2)
                mode = true
            }
        }

        bt_store.setOnClickListener {
            if((edit_body.text.toString().isNotEmpty()&&edit_title.text.toString().isNotEmpty())||edit_promise.text.toString().isNotEmpty()) {
                Toast.makeText(this, "저장되었습니다", Toast.LENGTH_SHORT).show()
                val id = intent.getIntExtra("id", 0)
                val title = edit_title.text.toString()
                val contents = edit_body.text.toString()
                val promise = edit_promise.text.toString()

                if (intent.getIntExtra("id", 0) == 0) {
                    val diary = Diary(id, now, title, contents, promise)
                    if (promise.isNotEmpty()) {
                        val todayCal : Calendar = Calendar.getInstance()
                        val today : Long = todayCal.timeInMillis/86400000

                        val mFormat = SimpleDateFormat("yyyy年 MM月 dd日")
                        val date: String = mFormat.format(todayCal.time)

                        val promiseDb = Promise(id, today, date, promise)
                        AppDatabase.instance.promiseDao().insert(promiseDb)
                    }
                    AppDatabase.instance.diaryDao().insert(diary)
                } else {
                    val diaryDemo = AppDatabase.instance.diaryDao().get(id)
                    val promiseDemo = AppDatabase.instance.promiseDao().get(id)
                    val nowDate = diaryDemo.date

                    val diary: Diary = Diary(id, nowDate, title, contents, promise)

                    if (promise != promiseDemo?.promise) {//수정
                        val todayCal : Calendar = Calendar.getInstance()
                        val today : Long = todayCal.timeInMillis/86400000

                        val mFormat = SimpleDateFormat("yyyy年 MM月 dd日")
                        val date: String = mFormat.format(todayCal.time)

                        val promiseDb = Promise(id, today, date, promise)
                        AppDatabase.instance.promiseDao().insert(promiseDb)
                        if(promise.isNullOrEmpty())
                            AppDatabase.instance.promiseDao().delete(promiseDb)
                    }

                    AppDatabase.instance.diaryDao().insert(diary)
                }

                finish()
            }
            else
                Toast.makeText(this,"일기 또는 다짐을 작성해주세요", Toast.LENGTH_SHORT).show()
        }
    }

    //툴바
    private fun setupToolbarWrite() {
        setSupportActionBar(main_write_toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //supportActionBar?.setHomeAsUpIndicator(R.drawable.back)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_write_top, menu)
        return true
        //return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_write_delete -> {
                if (intent.getIntExtra("id", 0) == 0) {
                    Toast.makeText(this, "글을 작성한 후 삭제해주세요", Toast.LENGTH_SHORT).show()
                }
                else {
                    val id = intent.getIntExtra("id", 0)
                    val diary = AppDatabase.instance.diaryDao().get(id)
                    AppDatabase.instance.diaryDao().delete(diary)
                    finish()
                }
                true
            }
            home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}