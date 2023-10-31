package com.example.rememberme;

import androidx.recyclerview.widget.RecyclerView;

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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class AFSGAdapter extends RecyclerView.Adapter<AFSGAdapter.MyViewHolder>{

    private ArrayList<MainElement> dataSet;
    Context context;
    private BaseFragment fragment;

    public void setDataSet(ArrayList<MainElement> ds) {
        this.dataSet = ds;
    }

    public AFSGAdapter(ArrayList<MainElement> data, Context context, BaseFragment fragment) {
        this.dataSet = data;
        this.context=context;
        this.fragment = fragment;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_element, parent, false);
        view.setOnClickListener(MainFragment.myOnClickListener);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int listPosition) {
        MainElement elemento = dataSet.get(holder.getAdapterPosition());
        if ( elemento.getImage_url().toString().contains("Empty Element") ) {
            holder.image.setImageResource(R.drawable.afsg_empty_element);
        } else {
            Glide.with(context)
                    .load(elemento.getImage_url())
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .skipMemoryCache(false)
                    .into(holder.image); //holder.itemView.getContext()
        }
        holder.name.setText(capitalizer(elemento.getName()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onClick(View v) {
                if (!(elemento.getImage_url().toString().contains("Empty Element"))) {
                    //holder.image.setTransitionName("transition" + holder.getAdapterPosition());
                    ShowElementFragment showElementFragment = new ShowElementFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("element", elemento);
                    showElementFragment.setArguments(bundle);
                    ((MainActivity) context).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentHolder, showElementFragment)
                            .commit();
                    /*if (fragment instanceof tab1_anime) {
                        ((tab1_anime) fragment).openShowElementFragment(holder.getAdapterPosition(), v.findViewById(R.id.main_image));
                    } else if (fragment instanceof tab2_film) {
                        System.out.println(holder.getAdapterPosition());
                        ((tab2_film) fragment).openShowElementFragment(holder.getAdapterPosition(), v.findViewById(R.id.main_image));
                    } else if (fragment instanceof tab3_series) {
                        ((tab3_series) fragment).openShowElementFragment(holder.getAdapterPosition(), v.findViewById(R.id.main_image));
                    } else if (fragment instanceof tab4_games) {
                        ((tab4_games) fragment).openShowElementFragment(holder.getAdapterPosition(), v.findViewById(R.id.main_image));
                    } else {

                    }*/
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

        TextView name;
        ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.image = (ImageView) itemView.findViewById(R.id.main_image);
            this.name = (TextView) itemView.findViewById(R.id.main_name);
        }
    }

    public void clear() {
        this.dataSet.clear();
    }
}