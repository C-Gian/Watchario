package com.example.rememberme;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rememberme.utils.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class tab1_anime extends BaseFragment {
    private RecyclerView recycler_view_anime;
    public static AFSGAdapter anime_adapter;
    private ArrayList<MainElement> anime_list = new ArrayList<>();
    private Context context;

    public tab1_anime(){ };

    public tab1_anime(Map<String, ArrayList<MainElement>> anime_complete_list) {
        anime_list.addAll(anime_complete_list.get("Released"));
        anime_list.addAll(anime_complete_list.get("ToRelease"));
    }

    @Override
    public void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstancesState) {
        View view = inflater.inflate(R.layout.fragment_tab1_anime, container, false);
        recycler_view_anime = view.findViewById(R.id.recycler_view_anime);
        recycler_view_anime.setHasFixedSize(true);
        context = getActivity();
        LinearLayoutManager layoutManager_anime_crucial = new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false);
        recycler_view_anime.setLayoutManager(layoutManager_anime_crucial);
        recycler_view_anime.setItemAnimator(new DefaultItemAnimator());
        int spanCount = 3; // 3 columns
        int spacing = 10; // 10 for my phone, 35 for the emulator
        boolean includeEdge = true;
        recycler_view_anime.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        anime_adapter = new AFSGAdapter(anime_list, getContext(), this);
        anime_adapter.setDataSet(anime_list);
        recycler_view_anime.setAdapter(anime_adapter);
        recycler_view_anime.scheduleLayoutAnimation();
        anime_adapter.notifyDataSetChanged();
        return view;
    }

    /*public void openShowElementFragment(int position, View view) {
        MainElement element = anime_list.get(position);
        ShowElementFragment showElementFragment = new ShowElementFragment();
        Bundle bundle = new Bundle();
        bundle.putString("transitionName", "transition" + position);
        bundle.putSerializable("element", element);
        bundle.putInt("position", position);
        bundle.putInt("from", 0);
        showElementFragment.setArguments(bundle);
        ((MainActivity) context).showFragmentWithTransition(this, showElementFragment, "showElementFragment", view, "transition" + position);
    }*/
}
