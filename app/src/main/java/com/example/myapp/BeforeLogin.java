package com.example.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class BeforeLogin extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beforlogin);


        if (SharedPreference.getUserName(BeforeLogin.this).length() == 0) {//로그인 고유데이터(현재는 이메일) 길이 0
            Intent intent = new Intent(BeforeLogin.this, LoginActivity.class);
            startActivity(intent);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);//액티비티 스택제거
            finish();
        } else {
            Intent intent = new Intent(BeforeLogin.this, MainActivity.class);
            intent.putExtra("STD_NUM", SharedPreference.getUserName(BeforeLogin.this).toString());
            Toast.makeText(getApplicationContext(), "자동 로그인 되었습니다", Toast.LENGTH_SHORT).show();
            startActivity(intent);
            finish();
        }
    }
}
