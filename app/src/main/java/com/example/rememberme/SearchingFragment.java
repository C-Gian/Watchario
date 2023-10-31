package com.example.rememberme;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchingFragment extends BaseFragment {

    private RecyclerView recycler_view_searching;
    private SearchAdapter adapter;
    private SearchView search_bar_anime;
    static View.OnClickListener myOnClickListener;
    private ImageButton back_arrow;
    private String URL = "https://rememberapp-13f74-default-rtdb.europe-west1.firebasedatabase.app/";
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance(URL).getReference();
    private ArrayList<MainElement> complete_list = new ArrayList<>();
    private Context context;
    private ConstraintLayout add_element_container;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_searching, parent, false);

        //find views
        if (true) {
            recycler_view_searching = view.findViewById(R.id.recycler_view_searching);
            search_bar_anime = view.findViewById(R.id.search_bar_anime);
            add_element_container = view.findViewById(R.id.add_element_container);
            back_arrow = view.findViewById(R.id.back_arrow);
            context = getActivity();
        }

        retrieveData(this);

        if (search_bar_anime != null) {
            search_bar_anime.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    search(s);
                    return true;
                }
            });
        }

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

    private void retrieveData(BaseFragment fragment) {
        databaseReference = databaseReference.child("Main");
        databaseReference.keepSynced(true);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //code to retrieve elements from db
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    //datasnapshot1 -> FILM, SERIES, GAMES, OTHERS
                    if (!(dataSnapshot1.getKey().equals("Others"))) {
                        for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                            //datasnapshot2 -> CRUCIAL, GENERAL, FUTURE, SEEN
                            for (DataSnapshot dataSnapshot3 : dataSnapshot2.getChildren()) {
                                //datasnapshot3 -> elements
                                MainElement element = dataSnapshot3.getValue(MainElement.class);
                                element.setName(dataSnapshot3.getKey());
                                complete_list.add(element);
                            }
                        }
                    }
                }

                recycler_view_searching.setHasFixedSize(true);
                LinearLayoutManager layoutManager_searching = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                recycler_view_searching.setLayoutManager(layoutManager_searching);
                recycler_view_searching.setItemAnimator(new DefaultItemAnimator());
                adapter = new SearchAdapter(complete_list, getContext(), fragment);
                adapter.setDataSet(complete_list);
                recycler_view_searching.setAdapter(adapter);
                recycler_view_searching.scheduleLayoutAnimation();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //set code to show an error
            }
        });
    }

    /*private void search(String str) {
        //old search without margin error
        ArrayList<AddElement> start_list = new ArrayList<>();
        for (AddElement element : complete_list) {
            if ( element.getName().toLowerCase().contains(str.toLowerCase()) ) {
                start_list.add(element);
            }
        }
        adapter.setDataSet(start_list);
        adapter.notifyDataSetChanged();
    }*/

    private static int minimum(int a, int b, int c) {
        return Math.min(Math.min(a,b), c);
    }

    public static int computeLevenshtinDistance(CharSequence lhs, CharSequence rhs) {
        int[][] distance = new int[lhs.length()+1][rhs.length()+1];

        for (int i=0; i<=lhs.length(); i++) {
            distance[i][0] = i;
        }

        for (int j=1; j<=rhs.length(); j++) {
            distance[0][j] = j;
        }

        for (int i=1; i<=lhs.length(); i++) {
            for (int j=1; j <= rhs.length(); j++) {
                distance[i][j] = minimum(
                        distance[i-1][j]+1,
                        distance[i][j-1]+1,
                        distance[i-1][j-1] + ((lhs.charAt(i-1) == rhs.charAt(j-1)) ? 0 : 1));
            }
        }
        int result;
        if (lhs.length() > rhs.length()) {
            result = distance[lhs.length()][rhs.length()];
        } else {
            result = distance[lhs.length()][lhs.length()];
        }
        return result;
    }

    public void search(String str) {
        int distance = 0;
        if (str.length() < 4) {
            distance = 1;
        } else if (str.length() >= 4 && str.length() <= 10) {
            distance = 2;
        } else {
            distance = 5;
        }
        ArrayList<MainElement> start_list = new ArrayList<>();
        for (MainElement element : complete_list) {
            String element_name = element.getName();
            if (computeLevenshtinDistance(str.toLowerCase(), element_name.toLowerCase()) <= distance) {
                start_list.add(element);
                add_element_container.setVisibility(View.GONE);
            }
        }

        if (start_list.isEmpty()) {
            add_element_container.setVisibility(View.VISIBLE);
            add_element_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, AddActivity.class);
                    intent.putExtra("name", str);
                    startActivity(intent);
                }
            });
        }

        adapter.setDataSet(start_list);
        adapter.notifyDataSetChanged();
    }
}