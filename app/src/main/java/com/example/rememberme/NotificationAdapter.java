package com.example.rememberme;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder>{

    private ArrayList<NotificationElement> dataSet;
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name, desc;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.image = (ImageView) itemView.findViewById(R.id.notification_new_release_image);
            this.name = (TextView) itemView.findViewById(R.id.notification_new_release_name);
            this.desc = (TextView) itemView.findViewById(R.id.notification_new_release_desc);
        }
    }

    public void setDataSet(ArrayList<NotificationElement> ds) {
        this.dataSet = ds;
    }

    public NotificationAdapter(ArrayList<NotificationElement> data, Context context) {
        this.dataSet = data;
        this.context=context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_element, parent, false);
        view.setOnClickListener(SearchingFragment.myOnClickListener);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int listPosition) {
        NotificationElement element = dataSet.get(holder.getAdapterPosition());
        holder.name.setText(capitalizer(element.getName()));
        holder.desc.setText(capitalizer(element.getDesc()));
        Glide.with(context)
                .load(element.getImage_url())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.image); //holder.itemView.getContext()
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    @Override
    public String toString() {
        return "NotificationAdapter{" +
                "dataSet=" + dataSet +
                ", context=" + context +
                '}';
    }

    public String capitalizer(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }
}