package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Animal_record extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_record);

        Button weightbtn = findViewById(R.id.weightbtn);
        weightbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Animal_record.this, WeightActivity.class);
                startActivity(intent);
            }
        });

        Button feedbtn = findViewById(R.id.feedbtn);
        feedbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Animal_record.this, FeedActicity.class);
                startActivity(intent);
            }
        });

        Button waterbtn = findViewById(R.id.waterbtn);
        waterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Animal_record.this, WatherActicity.class);
                startActivity(intent);
            }
        });

        Button btn_diary = findViewById(R.id.btn_diary);
        btn_diary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Animal_record.this, Record_DiaryActivity.class);
                startActivity(intent);

            }
        });


    }
}