package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Record_DiaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_diary);

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String getDate = sdf.format(date);
//        TextView tv_date = findViewById(R.id.date);
//        tv_date.setText(getDate);

//        Intent intent = getIntent(); /*데이터 수신*/
//        String str = intent.getStringExtra("date");
//        Log.d("date1",str);


    }
}