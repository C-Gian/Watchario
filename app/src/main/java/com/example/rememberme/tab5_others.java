package com.example.rememberme;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class tab5_others extends Fragment {
    private RecyclerView recycler_view_others;
    public static OthersAdapter others_adapter;
    private ArrayList<OtherElement> others_list = new ArrayList<>();

    public tab5_others(){ };

    public tab5_others(ArrayList<OtherElement> others_list) {
        this.others_list = others_list;
    }

    @Override
    public void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstancesState) {
        View view =  inflater.inflate(R.layout.fragment_tab5_others, container, false);
        recycler_view_others = view.findViewById(R.id.recycler_view_others);
        recycler_view_others.setHasFixedSize(true);
        LinearLayoutManager layoutManager_others = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recycler_view_others.setLayoutManager(layoutManager_others);
        recycler_view_others.setItemAnimator(new DefaultItemAnimator());
        others_adapter = new OthersAdapter(others_list, getContext());
        others_adapter.setDataSet(others_list);
        recycler_view_others.setAdapter(others_adapter);
        recycler_view_others.scheduleLayoutAnimation();
        others_adapter.notifyDataSetChanged();
        return view;
    }
}
