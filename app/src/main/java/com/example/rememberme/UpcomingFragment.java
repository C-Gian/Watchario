package com.example.rememberme;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UpcomingFragment extends BaseFragment {

    private RecyclerView recycler_view_upcoming;
    private ImageButton back_arrow;
    private UpcomingsAdapter adapter;
    private ArrayList<MainElement> upcomings = new ArrayList<>();
    static View.OnClickListener myOnClickListener;
    private String URL = "https://rememberapp-13f74-default-rtdb.europe-west1.firebasedatabase.app/";
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance(URL).getReference();
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_upcoming, parent, false);

        //findviews
        if (true) {
            recycler_view_upcoming = view.findViewById(R.id.recycler_view_upcoming);
            back_arrow = view.findViewById(R.id.back_arrow);
            context = getContext();
        }

        getUpcomings(this);

        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentHolder,  new MainFragment())
                        .commit();
            }
        });

        return view;
    }

    private void getUpcomings(BaseFragment fragment) {
        upcomings.clear();
        //get data from firebase
        databaseReference = databaseReference.child("Main");
        databaseReference.keepSynced(true);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    //datasnapshot1 -> ANIME, FILM, SERIES, GAMES
                    for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                        //datasnapshot2 -> ToRelease, Released
                        if (dataSnapshot2.getKey().equals("ToRelease")) {
                            for (DataSnapshot dataSnapshot3 : dataSnapshot2.getChildren()) {
                                MainElement element = dataSnapshot3.getValue(MainElement.class);
                                element.setName(dataSnapshot3.getKey());
                                upcomings.add(element);
                            }
                        }
                    }
                }

                ArrayList<MainElement> ordered_list = getOrderedMap(upcomings);

                recycler_view_upcoming.setHasFixedSize(true);
                LinearLayoutManager layoutManager_upcoming = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                recycler_view_upcoming.setLayoutManager(layoutManager_upcoming);
                recycler_view_upcoming.setItemAnimator(new DefaultItemAnimator());

                adapter = new UpcomingsAdapter(ordered_list, getContext(), fragment);
                adapter.setDataSet(ordered_list);
                recycler_view_upcoming.setAdapter(adapter);
                recycler_view_upcoming.scheduleLayoutAnimation();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public ArrayList<MainElement> getOrderedMap(ArrayList<MainElement> upcomings) {
        Map<MainElement, Long> map_element_daysleft = new HashMap<>();
        List<MainElement> ordered_list = new ArrayList<>();
        for (MainElement element : upcomings) {
            long days_left;
            long actual_date_in_seconds = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                actual_date_in_seconds = Instant.now().getEpochSecond();
            }
            Date date = null;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(element.future_release_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long future_date_in_seconds = date.getTime()/1000;
            long days_left_in_seconds = future_date_in_seconds-actual_date_in_seconds;
            days_left = days_left_in_seconds/ (24*3600);

            map_element_daysleft.put(element, days_left);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ordered_list = map_element_daysleft.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        }
        return (ArrayList<MainElement>) ordered_list;
    }

    public void openShowElementFragment(int position, View view) {
        MainElement element = upcomings.get(position);
        ShowElementFragment showElementFragment = new ShowElementFragment();
        Bundle bundle = new Bundle();
        bundle.putString("transitionName", "transition" + position);
        bundle.putSerializable("element", element);
        bundle.putInt("position", position);
        bundle.putInt("from", 2);
        showElementFragment.setArguments(bundle);
        //((MainActivity) context).showFragmentWithTransition(this, showElementFragment, "showElementFragment", view, "transition" + position);
    }
}