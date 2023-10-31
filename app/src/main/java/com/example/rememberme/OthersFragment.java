package com.example.rememberme;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class OthersFragment extends BaseFragment {

    private TextView heading_elements_Sections_text;
    private ImageButton back_arrow, filter_button, add_others_button;
    private RecyclerView recycler_view_others;
    static View.OnClickListener myOnClickListener;
    private String URL = "https://rememberapp-13f74-default-rtdb.europe-west1.firebasedatabase.app/";
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance(URL).getReference();
    private ArrayList<OtherElement> complete_list = new ArrayList<>();
    private Context context;
    private OthersAdapter adapter;
    private BottomSheetDialog filters_others_sheet_dialog;
    private View bottomSheetView;
    private ChipGroup filters_others_mostused_chipgroup, filters_others_alltags_chipgroup;
    private SearchView filters_others_searchview;
    private ArrayList<String> checkedOthersTag = new ArrayList<>();


    public OthersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_others, container, false);

        //findviews
        if (true) {
            heading_elements_Sections_text = view.findViewById(R.id.heading_elements_Sections_text);
            back_arrow = view.findViewById(R.id.back_arrow);
            add_others_button = view.findViewById(R.id.add_others_button);
            filter_button = view.findViewById(R.id.filter_button);
            recycler_view_others = view.findViewById(R.id.recycler_view_others);

            filters_others_sheet_dialog = new BottomSheetDialog(
                    getContext(), R.style.BottomSheetDialogTheme);
            bottomSheetView = LayoutInflater.from(getContext()).
                    inflate(
                            R.layout.filters_others_bottomsheet_layout, null
                    );
            filters_others_sheet_dialog.setContentView(bottomSheetView);
            filters_others_mostused_chipgroup = filters_others_sheet_dialog.findViewById(R.id.filters_others_mostused_chipgroup);
            filters_others_alltags_chipgroup = filters_others_sheet_dialog.findViewById(R.id.filters_others_alltags_chipgroup);
            filters_others_searchview = filters_others_sheet_dialog.findViewById(R.id.filters_others_searchview);
        }

        retrieveData(this);

        filter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filters_others_sheet_dialog.show();

                if (filters_others_searchview != null) {
                    filters_others_searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            search(newText);
                            return true;
                        }
                    });
                }
                ImageButton filters_back_arrow, filters_save_button;
                TextView filters_reset_button;

                filters_back_arrow = filters_others_sheet_dialog.findViewById(R.id.filters_back_arrow);
                filters_reset_button = filters_others_sheet_dialog.findViewById(R.id.filters_reset_button);
                filters_save_button = filters_others_sheet_dialog.findViewById(R.id.filters_save_button);

                filters_back_arrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        filters_others_sheet_dialog.dismiss();
                    }
                });

                filters_reset_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        filters_others_mostused_chipgroup.clearCheck();
                        filters_others_alltags_chipgroup.clearCheck();
                    }
                });

                filters_save_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if ( (((filters_others_mostused_chipgroup.getCheckedChipIds()).size())==0) && (((filters_others_alltags_chipgroup.getCheckedChipIds()).size())==0) )  {
                            //no tag selected, return the normal view no filtered
                            adapter.setDataSet(complete_list);
                            adapter.notifyDataSetChanged();
                        } else {
                            //at least one tag selected so we need to filter
                            ArrayList<String> tags_to_filter = new ArrayList<>();
                            tags_to_filter.addAll(getTagsCheckedChips(filters_others_mostused_chipgroup));
                            tags_to_filter.addAll(getTagsCheckedChips(filters_others_alltags_chipgroup));
                            ArrayList<OtherElement> filtered_others_list = othersElementListFiltered(tags_to_filter);
                            adapter.setDataSet(filtered_others_list);
                            adapter.notifyDataSetChanged();
                        }
                        filters_others_sheet_dialog.dismiss();
                    }
                });
            }
        });

        add_others_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentHolder,  new AddOthersElement())
                        .commit();
            }
        });

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
        databaseReference = databaseReference.child("Others");
        databaseReference.keepSynced(true);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //code to retrieve elements from db
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    //datasnapshot1 -> others elements
                    OtherElement element = dataSnapshot1.getValue(OtherElement.class);
                    element.setName(dataSnapshot1.getKey());
                    complete_list.add(element);
                }

                recycler_view_others.setHasFixedSize(true);
                LinearLayoutManager layoutManager_others = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                recycler_view_others.setLayoutManager(layoutManager_others);
                recycler_view_others.setItemAnimator(new DefaultItemAnimator());
                adapter = new OthersAdapter(complete_list, getContext());
                recycler_view_others.setAdapter(adapter);
                recycler_view_others.scheduleLayoutAnimation();
                adapter.notifyDataSetChanged();

                setOthersDialogElements();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //set code to show an error
            }
        });
    }

    @NonNull
    private Map<String, Integer> retrieveAllTags(@NonNull ArrayList<OtherElement> others_list) {
        Map<String, Integer> tag_with_count = new HashMap<>();
        for (OtherElement element : others_list) {
            String[] elemet_tags = element.tags.split("\\.");
            for (String tag : elemet_tags) {
                String tag_lower = tag.toLowerCase().trim();
                if (tag_with_count.containsKey(tag_lower)) {
                    //tag already exist in tags, then ++ the map count
                    int tag_count = tag_with_count.get(tag_lower)+1;
                    tag_with_count.put(tag_lower, tag_count);
                } else {
                    //tag never found before then it's a new key value pair
                    tag_with_count.put(tag_lower, 1);
                }
            }
        }
        return tag_with_count;
    }

    public void createChip(String s, String type, boolean toDisable) {
        final Chip chip = (Chip) this.getLayoutInflater().inflate(R.layout.chip_element_others, null, false);
        //Chip chip = new Chip(MainActivity.this);
        chip.setText(s);
        chip.setTypeface(Typeface.create(ResourcesCompat.getFont(getContext(),R.font.futura_book),Typeface.NORMAL));
        chip.setTextSize(14);

        if (type.equals("most_used")) {
            ChipDrawable chipDrawable = ChipDrawable.createFromAttributes(getContext(),
                    null,
                    0,
                    R.style.main_others_filter);
            chipDrawable.setChipBackgroundColorResource(R.color.chip_color);
            chip.setChipDrawable(chipDrawable);
            filters_others_mostused_chipgroup.addView(chip);
        } else {
            if (toDisable) {
                chip.setEnabled(false);
                ChipDrawable chipDrawable = ChipDrawable.createFromAttributes(getContext(),
                        null,
                        0,
                        R.style.disable_chips_Style);
                chip.setChipDrawable(chipDrawable);
                filters_others_alltags_chipgroup.addView(chip);
            } else {
                ChipDrawable chipDrawable = ChipDrawable.createFromAttributes(getContext(),
                        null,
                        0,
                        R.style.main_others_filter);
                chipDrawable.setChipBackgroundColorResource(R.color.chip_color);
                chip.setChipDrawable(chipDrawable);
                if (checkedOthersTag.contains(chip.getText().toString())) {
                    chip.setChecked(true);
                }
                filters_others_alltags_chipgroup.addView(chip);
            }
        }
    }

    private ArrayList<String> getTagsCheckedChips(ChipGroup chipGroup) {
        checkedOthersTag.clear();
        Set<String> tag_to_filter = new HashSet<>();
        int i=0;
        while (i < chipGroup.getChildCount()) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            if (chip.isChecked()) {
                tag_to_filter.add(chip.getText().toString());
                checkedOthersTag.add(chip.getText().toString());
            }
            i++;
        }
        return new ArrayList<String>(tag_to_filter);
    }

    private ArrayList<OtherElement> othersElementListFiltered(ArrayList<String> tags_to_filter) {
        ArrayList<OtherElement> filtered_elements = new ArrayList<>();
        System.out.println(tags_to_filter + "\n");
        for (OtherElement element : complete_list) {
            System.out.println(element.getName() + "\n");
            ArrayList<String> element_tags = new ArrayList<>(Arrays.asList(element.tags.split("\\.")));
            System.out.println(element_tags + " ------------------- \n");
            if (element_tags.containsAll(tags_to_filter)) {
                System.out.println("Entered" + "\n \n");
                filtered_elements.add(element);
            }
        }
        return filtered_elements;
    }

    private void setOthersDialogElements() {
        filters_others_mostused_chipgroup.removeAllViews();
        filters_others_alltags_chipgroup.removeAllViews();
        Map<String, Integer> tag_with_count = retrieveAllTags(complete_list);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            ArrayList<String> all_tags = new ArrayList<>(tag_with_count.keySet());
            Collections.sort(all_tags);
            List<String> most_used = tag_with_count.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .map(e -> e.getKey())
                    .limit(5)
                    .collect(Collectors.toList()); // or any other terminal method

            for (String tag : all_tags) {
                if (most_used.contains(tag)) {
                    createChip(tag, "all_tags", true);
                } else {
                    createChip(tag, "all_tags", false);
                }
            }

            for (String tag : most_used) {
                createChip(tag, "most_used", false);
            }
        }
    }

    private void search(String str) {
        ArrayList<OtherElement> other_list = new ArrayList<>();
        for (OtherElement element : complete_list) {
            if (element.getName().toLowerCase().contains(str.toLowerCase())) {
                other_list.add(element);
            }
        }
        tab5_others.others_adapter.setDataSet(other_list);
        tab5_others.others_adapter.notifyDataSetChanged();
    }
}