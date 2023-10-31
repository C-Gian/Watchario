package com.example.rememberme;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.Map;

public class ViewPagerFragmentSateAdapter extends FragmentStateAdapter {

    private Map<String, Map<String, ArrayList<MainElement>>> complete_list;
    private Context c;

    public ViewPagerFragmentSateAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public ViewPagerFragmentSateAdapter(@NonNull FragmentActivity fragmentActivity, Map<String, Map<String, ArrayList<MainElement>>> complete_list) {
        super(fragmentActivity);
        this.complete_list=complete_list;
        this.c=fragmentActivity;
    }

    public ViewPagerFragmentSateAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public ViewPagerFragmentSateAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new tab1_anime(complete_list.get("Anime"));
            case 1:
                return new tab2_film(complete_list.get("Film"));
            case 2:
                return new tab3_series(complete_list.get("Series"));
            case 3:
                return new tab4_games(complete_list.get("Games"));
        }
        return new tab1_anime(complete_list.get("Anime"));
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
