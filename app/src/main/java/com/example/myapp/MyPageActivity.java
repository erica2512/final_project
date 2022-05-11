package com.example.myapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MyPageActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    private ImageView animalprofile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        mFirebaseAuth = FirebaseAuth.getInstance();

        Button logout = findViewById(R.id.btn_Logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //로그아웃
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(MyPageActivity.this, LoginActivity.class);
                startActivity(intent);

                Toast.makeText(MyPageActivity.this, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        Button delete = findViewById(R.id.btn_Revoke);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //탈퇴
                mFirebaseAuth.getCurrentUser().delete();

                Intent intent = new Intent(MyPageActivity.this, LoginActivity.class);
                startActivity(intent);

                Toast.makeText(MyPageActivity.this, "탈퇴되었습니다", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        Button btn_Hos_Bm = findViewById(R.id.btn_Hos_Bm);
        btn_Hos_Bm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyPageActivity.this, BookMarkActivity.class);
                startActivity(intent);
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            String email = user.getEmail();//현재 로그인한 사용자의 이메일 정보 가져오기.
            Log.d("Email", email);
            TextView tv_email = findViewById(R.id.tv_email);
            tv_email.setText(email);
        } else {
            // No user is signed in
        }

        Button addprofile = findViewById(R.id.addprofile);
        addprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyPageActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        String idToken = user.getUid();
        Log.d("idToken", idToken);

        animalprofile = (ImageView) findViewById(R.id.aniprofile);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        StorageReference pathReference = storageReference.child(idToken);
        if (pathReference == null) {
            Toast.makeText(MyPageActivity.this, "저장소에 사진이 없습니다.", Toast.LENGTH_SHORT).show();
        } else {
            StorageReference submitProfile = storageReference.child(idToken);
            submitProfile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(MyPageActivity.this).load(uri).into(animalprofile);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }

    }
}