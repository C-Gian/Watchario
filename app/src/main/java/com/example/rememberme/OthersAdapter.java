package com.example.rememberme;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class OthersAdapter extends RecyclerView.Adapter<OthersAdapter.MyViewHolder>{

    private ArrayList<OtherElement> dataSet;
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ChipGroup tags;
        TextView name;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.tags = (ChipGroup) itemView.findViewById(R.id.other_element_chipgroup);
            this.name = (TextView) itemView.findViewById(R.id.other_name);
        }
    }

    public void setDataSet(ArrayList<OtherElement> ds) {
        this.dataSet = ds;
    }

    public OthersAdapter(ArrayList<OtherElement> data, Context context) {
        this.dataSet = data;
        this.context=context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.other_element, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int listPosition) {
        OtherElement element = dataSet.get(holder.getAdapterPosition());
        holder.name.setText(capitalizer(element.getName()));
        String[] tags = element.getTags().split("\\.");
        holder.tags.removeAllViews();
        for (String s : tags) {
            final Chip chip = (Chip) LayoutInflater.from(context).inflate(R.layout.chip_element_others, null, false);
            chip.setTypeface(Typeface.create(ResourcesCompat.getFont(context,R.font.futura_book),Typeface.NORMAL));
            chip.setTextSize(12);
            chip.setText(s);
            chip.setClickable(false);
            chip.setCheckable(false);
            chip.setEnabled(false);
            ChipDrawable style = ChipDrawable.createFromAttributes(context, null, 0, R.style.show_tag_chip_Style);
            chip.setChipDrawable(style);
            holder.tags.addView(chip);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShowOthersElementFragment showOthersElementFragment = new ShowOthersElementFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("element", element);
                    showOthersElementFragment.setArguments(bundle);
                    ((MainActivity) context).getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentHolder,  showOthersElementFragment)
                            .commit();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    @Override
    public String toString() {
        return "OthersAdapter{" +
                "dataSet=" + dataSet +
                ", context=" + context +
                '}';
    }

    public String capitalizer(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

}