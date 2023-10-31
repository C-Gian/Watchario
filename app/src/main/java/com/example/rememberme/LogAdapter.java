package com.example.rememberme;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.MyViewHolder>{

    private ArrayList<LogElement> dataSet;
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView actor, action1, action2, action3, action4, action5, date;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.actor = (TextView) itemView.findViewById(R.id.log_element_actor);
            this.action1 = (TextView) itemView.findViewById(R.id.log_edit_element1);
            this.action2 = (TextView) itemView.findViewById(R.id.log_edit_element2);
            this.action3 = (TextView) itemView.findViewById(R.id.log_edit_element3);
            this.action4 = (TextView) itemView.findViewById(R.id.log_edit_element4);
            this.action5 = (TextView) itemView.findViewById(R.id.log_edit_element5);
            this.date = (TextView) itemView.findViewById(R.id.logs_date);
        }
    }

    public void setDataSet(ArrayList<LogElement> ds) {
        this.dataSet = ds;
    }

    public LogAdapter(ArrayList<LogElement> data, Context context) {
        this.dataSet = data;
        this.context=context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_element, parent, false);
        view.setOnClickListener(SearchingFragment.myOnClickListener);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int listPosition) {
        LogElement element = dataSet.get(holder.getAdapterPosition());
        holder.actor.setText(element.getActor());
        int action_number = element.getActions().split("\\|").length;
        String[] splitted_actions = element.getActions().split("\\|");
        switch (action_number) {
            case 1:
                holder.action1.setText(splitted_actions[0]);
                break;
            case 2:
                holder.action1.setText(splitted_actions[0]);
                holder.action2.setText(splitted_actions[1]);
                holder.action2.setVisibility(View.VISIBLE);
                break;
            case 3:
                holder.action1.setText(splitted_actions[0]);
                holder.action2.setText(splitted_actions[1]);
                holder.action3.setText(splitted_actions[2]);
                holder.action2.setVisibility(View.VISIBLE);
                holder.action3.setVisibility(View.VISIBLE);
                break;
            case 4:
                holder.action1.setText(splitted_actions[0]);
                holder.action2.setText(splitted_actions[1]);
                holder.action3.setText(splitted_actions[2]);
                holder.action4.setText(splitted_actions[3]);
                holder.action2.setVisibility(View.VISIBLE);
                holder.action3.setVisibility(View.VISIBLE);
                holder.action4.setVisibility(View.VISIBLE);
                break;
            case 5:
                holder.action1.setText(splitted_actions[0]);
                holder.action2.setText(splitted_actions[1]);
                holder.action3.setText(splitted_actions[2]);
                holder.action4.setText(splitted_actions[3]);
                holder.action5.setText(splitted_actions[4]);
                holder.action2.setVisibility(View.VISIBLE);
                holder.action3.setVisibility(View.VISIBLE);
                holder.action4.setVisibility(View.VISIBLE);
                holder.action5.setVisibility(View.VISIBLE);
                break;
        }
        String date_formatted = new SimpleDateFormat("dd MM yyyy hh:mm:ss", Locale.ITALIAN).format(element.getDate()*1000);
        holder.date.setText(date_formatted);
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