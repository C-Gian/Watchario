package com.example.rememberme;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
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

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder>{

    private ArrayList<MainElement> dataSet;
    Context context;
    private BaseFragment fragment;

    public void setDataSet(ArrayList<MainElement> ds) {
        this.dataSet = ds;
    }

    public SearchAdapter(ArrayList<MainElement> data, Context context, BaseFragment fragment) {
        this.dataSet = data;
        this.context=context;
        this.fragment = fragment;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_element, parent, false);
        view.setOnClickListener(UpcomingFragment.myOnClickListener);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int listPosition) {
        MainElement elemento = dataSet.get(holder.getAdapterPosition());
        Fade fade = new Fade();
        //fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        Glide.with(context)
                .load(elemento.getImage_url())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false)
                .into(holder.image);
        holder.name.setText(capitalizer(elemento.getName()));
        holder.type.setText(capitalizer(elemento.getType()));
        if (elemento.getTime().equals("Future")) {
            holder.release_date.setVisibility(View.VISIBLE);
            holder.release_date.setText(elemento.getFuture_release_date());
        } else {
            holder.release_date.setVisibility(View.GONE);
        }
        switch (elemento.getTime()) {
            case "Crucial":
                holder.color.setBackgroundColor(context.getResources().getColor(R.color.released));
                break;
            case "General":
                holder.color.setBackgroundColor(context.getResources().getColor(R.color.to_released));
                break;
            case "Future":
                holder.color.setBackgroundColor(context.getResources().getColor(R.color.black));
                break;
            case "Seen":
                holder.color.setBackgroundColor(context.getResources().getColor(R.color.white));
                break;
        }
        holder.description.setText(capitalizer(elemento.getDescription()));
        holder.description.post(new Runnable() {
            @Override
            public void run() {
                if (holder.description.getLineCount() < 5) {
                    holder.description.setEllipsize(null);
                } else {
                    holder.description.setEllipsize(TextUtils.TruncateAt.END);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onClick(View v) {
                if (!(elemento.getImage_url().toString().contains("Empty Element"))) {
                    holder.image.setTransitionName("transition" + holder.getAdapterPosition());
                    //((SearchingFragment) fragment).openShowElementFragment(holder.getAdapterPosition(), v.findViewById(R.id.searchelement_image));
                    ShowElementFragment showElementFragment = new ShowElementFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("element", elemento);
                    bundle.putString("from", "search");
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
            this.image = (ImageView) itemView.findViewById(R.id.searchelement_image);
            this.name = (TextView) itemView.findViewById(R.id.searchelement_title);
            this.description = (TextView) itemView.findViewById(R.id.searchelement_desc);
            this.type = (TextView) itemView.findViewById(R.id.searchelement_type);
            this.release_date = (TextView) itemView.findViewById(R.id.searchelement_release_date);
            this.color = (View) itemView.findViewById(R.id.showelement_circle_color);
        }
    }

    public void clear() {
        this.dataSet.clear();
    }
}