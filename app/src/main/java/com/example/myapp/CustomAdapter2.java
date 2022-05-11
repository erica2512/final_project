package com.example.myapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

//recyclerview 사용 위한 CustomAdapter

public class CustomAdapter2 extends RecyclerView.Adapter<CustomAdapter2.CustomViewHolder> {

    private ArrayList<animal> arrayList;
    private Context context;

    public CustomAdapter2(ArrayList<animal> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        holder.tv_name.setText(arrayList.get(position).getName());
        holder.tv_number.setText(arrayList.get(position).getNumber());
        holder.tv_address.setText(arrayList.get(position).getAddress());
    }

    @Override
    public int getItemCount() {
        //삼항연산자
        return (arrayList !=null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name;
        TextView tv_number;
        TextView tv_address;


        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_name = itemView.findViewById(R.id.tv_name);
            this.tv_number = itemView.findViewById(R.id.tv_number);
            this.tv_address = itemView.findViewById(R.id.tv_address);
        }
    }



}
