package com.example.rememberme;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

public class UpcomingsAdapter extends RecyclerView.Adapter<UpcomingsAdapter.MyViewHolder>{

    private ArrayList<MainElement> dataSet;
    Context context;
    private BaseFragment fragment;

    public void setDataSet(ArrayList<MainElement> ds) {
        this.dataSet = ds;
    }

    public UpcomingsAdapter(ArrayList<MainElement> data, Context context, BaseFragment fragment) {
        this.dataSet = data;
        this.context=context;
        this.fragment = fragment;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.upcoming_element, parent, false);
        view.setOnClickListener(SearchingFragment.myOnClickListener);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int listPosition) {
        MainElement elemento = dataSet.get(holder.getAdapterPosition());
        Glide.with(context)
                .load(elemento.getImage_url())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false)
                .into(holder.image); //holder.itemView.getContext()
        holder.name.setText(capitalizer(elemento.getName()));
        holder.release_date.setText(capitalizer(elemento.getFuture_release_date()));
        long days_left = 0;
        long actual_date_in_seconds = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            actual_date_in_seconds = Instant.now().getEpochSecond();
        }
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(elemento.future_release_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long future_date_in_seconds = date.getTime()/1000;
        long days_left_in_seconds = future_date_in_seconds-actual_date_in_seconds;
        days_left = days_left_in_seconds/ (24*3600);
        holder.days_left.setText(String.valueOf(days_left));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onClick(View v) {
                if (!(elemento.getImage_url().toString().contains("Empty Element"))) {
                    //holder.image.setTransitionName("transition" + holder.getAdapterPosition());
                    //((UpcomingFragment) fragment).openShowElementFragment(holder.getAdapterPosition(), v.findViewById(R.id.upcoming_image));
                    ShowElementFragment showElementFragment = new ShowElementFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("element", elemento);
                    bundle.putString("from", "upcom");
                    showElementFragment.setArguments(bundle);
                    ((FragmentActivity)context).getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.fragmentHolder, showElementFragment)
                            .addToBackStack(null)
                            .commit();
                } else {
                    Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
                    holder.image.startAnimation(shake);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    @Override
    public String toString() {
        return "AFSGAdapter{" + dataSet.get(0).getType() + "  |  " + dataSet.get(0).getTime() + "}";
    }

    public String capitalizer(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, description, type, release_date, days_left;
        ImageView image;
        View color;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.image = (ImageView) itemView.findViewById(R.id.upcoming_image);
            this.name = (TextView) itemView.findViewById(R.id.upcoming_name);
            this.release_date = (TextView) itemView.findViewById(R.id.upcoming_date_value);
            this.days_left = (TextView) itemView.findViewById(R.id.upcoming_days_count);
        }
    }

    public void clear() {
        this.dataSet.clear();
    }
}