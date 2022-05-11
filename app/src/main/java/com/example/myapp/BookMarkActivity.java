package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

//즐겨찾기 데이터 가져오는 부분

public class BookMarkActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<animal> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_mark);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();


        database = FirebaseDatabase.getInstance();

        databaseReference = database.getReference();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            String id = user.getUid();
            Log.d("IdToken",id);

            //db에서 데이터 가져오기
            databaseReference.child("Commal").child("BookMark").child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                    arrayList.clear();

                    for(DataSnapshot snapshot : datasnapshot.getChildren()){
                        animal animal = snapshot.getValue(com.example.myapp.animal.class);
                        arrayList.add(animal);//arrayList에 데이터 넣기
                    }

                    adapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("Database", String.valueOf(databaseError.toException()));
                }
            });

        } else {
            // No user is signed in
        }




        adapter = new CustomAdapter2(arrayList, this);
        recyclerView.setAdapter(adapter);

    }

}