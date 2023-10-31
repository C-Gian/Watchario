package com.example.rememberme;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class tab2_film extends BaseFragment {
    private RecyclerView recycler_view_film;
    public static AFSGAdapter film_adapter;
    private ArrayList<MainElement> film_list = new ArrayList<>();
    private Context context;

    public tab2_film(){ };

    public tab2_film(Map<String, ArrayList<MainElement>> film_complete_list) {
        film_list.addAll(film_complete_list.get("Released"));
        film_list.addAll(film_complete_list.get("ToRelease"));
    }

    @Override
    public void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstancesState) {
        View view =  inflater.inflate(R.layout.fragment_tab2_film, container, false);
        context = getActivity();
        recycler_view_film = view.findViewById(R.id.recycler_view_film);
        recycler_view_film.setHasFixedSize(true);
        LinearLayoutManager layoutManager_anime_crucial = new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false);
        recycler_view_film.setLayoutManager(layoutManager_anime_crucial);
        recycler_view_film.setItemAnimator(new DefaultItemAnimator());
        recycler_view_film.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view); // item position
                int spanCount = 3;
                int spacing = 10;//spacing between views in grid

                if (position >= 0) {
                    int column = position % spanCount; // item column

                    outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                    outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                    if (position < spanCount) { // top edge
                        outRect.top = spacing;
                    }
                    outRect.bottom = spacing; // item bottom
                } else {
                    outRect.left = 0;
                    outRect.right = 0;
                    outRect.top = 0;
                    outRect.bottom = 0;
                }
            }
        });
        film_adapter = new AFSGAdapter(film_list, getContext(), this);
        film_adapter.setDataSet(film_list);
        recycler_view_film.setAdapter(film_adapter);
        recycler_view_film.scheduleLayoutAnimation();
        film_adapter.notifyDataSetChanged();
        return view;
    }

    /*public void openShowElementFragment(int position, View view) {
        MainElement element = film_list.get(position);
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
