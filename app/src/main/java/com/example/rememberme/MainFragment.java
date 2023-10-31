package com.example.rememberme;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.viewpager2.widget.ViewPager2;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.rememberme.utils.ReminderWorker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MainFragment extends BaseFragment {

    private Map<String, Map<String, ArrayList<MainElement>>> complete_list = new HashMap<>();
    private ImageButton search_icon, upcomings_icon, notifications_icon, filter_button;
    private TabLayout tabLayout;
    private TextView main_appname_textheading, heading_elements_Sections_text;
    public static ViewPager2 viewPager;
    private String[] titles = new String[]{"Anime", "Film", "Series", "Games"};
    static View.OnClickListener myOnClickListener;
    private String URL_storage = "gs://rememberapp-13f74.appspot.com";
    private String URL = "https://rememberapp-13f74-default-rtdb.europe-west1.firebasedatabase.app/";
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance(URL).getReference();
    private StorageReference storageReference = FirebaseStorage.getInstance(URL_storage).getReference();
    private long actual_date_in_seconds;
    public static int notifications_number = 1;
    private int tab_to_go;
    private long today_date = 0;
    private Chip[] filters_times_chips = new Chip[3];
    private Chip[] filter_urgency_chips = new Chip[2];
    private Chip[] filters_sort_chips = new Chip[4];
    private BottomSheetDialog filters_main_sheet_dialog;
    private View bottomSheetView;
    private int current_tab = 999;
    private long test_notifications_seconds;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        Bundle b = getArguments();
        if (b != null) {
            tab_to_go = b.getInt("tab", 0);
        }

        updateData();

        //findviews
        if (true) {
            filter_button = view.findViewById(R.id.filter_button);
            main_appname_textheading = view.findViewById(R.id.main_appname_textheading);
            search_icon = view.findViewById(R.id.search_icon);
            upcomings_icon = view.findViewById(R.id.upcomings_icon);
            notifications_icon = view.findViewById(R.id.notifications_icon);
            tabLayout = view.findViewById(R.id.tab_layout);
            viewPager = view.findViewById(R.id.view_pager);
            heading_elements_Sections_text = view.findViewById(R.id.heading_elements_Sections_text);
            filters_main_sheet_dialog = new BottomSheetDialog(
                    getContext(), R.style.BottomSheetDialogTheme);
            bottomSheetView = LayoutInflater.from(getContext()).
                    inflate(
                            R.layout.filters_main_bottomsheet_layout, null
                    );
            filters_main_sheet_dialog.setContentView(bottomSheetView);

            Chip chip_seen_filter, chip_noturgent_filter, chip_released_filter, chip_toRelease_filter, chip_urgency_filter, filters_alphabet_AZ_chip, filters_alphabet_ZA_chip, filters_date_newerolder_chip, filters_date_oldernewer_chip;

            chip_released_filter = filters_main_sheet_dialog.findViewById(R.id.chip_released_filter);
            chip_toRelease_filter = filters_main_sheet_dialog.findViewById(R.id.chip_toRelease_filter);
            chip_seen_filter = filters_main_sheet_dialog.findViewById(R.id.chip_seen_filter);

            chip_urgency_filter = filters_main_sheet_dialog.findViewById(R.id.chip_urgent_filter);
            chip_noturgent_filter = filters_main_sheet_dialog.findViewById(R.id.chip_noturgent_filter);

            filters_alphabet_AZ_chip = filters_main_sheet_dialog.findViewById(R.id.filters_alphabet_AZ_chip);
            filters_alphabet_ZA_chip = filters_main_sheet_dialog.findViewById(R.id.filters_alphabet_ZA_chip);
            filters_date_newerolder_chip = filters_main_sheet_dialog.findViewById(R.id.filters_date_newerolder_chip);
            filters_date_oldernewer_chip = filters_main_sheet_dialog.findViewById(R.id.filters_date_oldernewer_chip);

            filters_times_chips[0] = chip_released_filter;
            filters_times_chips[1] = chip_toRelease_filter;
            filters_times_chips[2] = chip_seen_filter;

            filter_urgency_chips[0] = chip_urgency_filter;
            filter_urgency_chips[1] = chip_noturgent_filter;

            filters_sort_chips[0] = filters_alphabet_AZ_chip;
            filters_sort_chips[1] = filters_alphabet_ZA_chip;
            filters_sort_chips[2] = filters_date_newerolder_chip;
            filters_sort_chips[3] = filters_date_oldernewer_chip;
        }

        filter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filters_main_sheet_dialog.show();

                ImageButton filters_back_arrow, filters_save_button;
                TextView filters_reset_button;

                filters_back_arrow = filters_main_sheet_dialog.findViewById(R.id.filters_back_arrow);
                filters_reset_button = filters_main_sheet_dialog.findViewById(R.id.filters_reset_button);
                filters_save_button = filters_main_sheet_dialog.findViewById(R.id.filters_save_button);

                filters_back_arrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        filters_main_sheet_dialog.dismiss();
                    }
                });

                //setting chips background
                if (true) {
                    ((ChipDrawable)filters_times_chips[0].getChipDrawable()).setChipBackgroundColorResource(R.color.chip_color);
                    ((ChipDrawable)filters_times_chips[1].getChipDrawable()).setChipBackgroundColorResource(R.color.chip_color);
                    ((ChipDrawable)filters_times_chips[2].getChipDrawable()).setChipBackgroundColorResource(R.color.chip_color);

                    ((ChipDrawable)filter_urgency_chips[0].getChipDrawable()).setChipBackgroundColorResource(R.color.chip_color);
                    ((ChipDrawable)filter_urgency_chips[1].getChipDrawable()).setChipBackgroundColorResource(R.color.chip_color);

                    ((ChipDrawable)filters_sort_chips[0].getChipDrawable()).setChipBackgroundColorResource(R.color.chip_color);
                    ((ChipDrawable)filters_sort_chips[1].getChipDrawable()).setChipBackgroundColorResource(R.color.chip_color);
                    ((ChipDrawable)filters_sort_chips[2].getChipDrawable()).setChipBackgroundColorResource(R.color.chip_color);
                    ((ChipDrawable)filters_sort_chips[3].getChipDrawable()).setChipBackgroundColorResource(R.color.chip_color);
                }

                filters_save_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int tab_selected = viewPager.getCurrentItem();
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            ArrayList<Chip> filters_times_chips_checked = (ArrayList<Chip>) Arrays.asList(filters_times_chips).stream().filter(chip -> chip.isChecked()).collect(Collectors.toList());
                            ArrayList<Chip> filter_urgency_chip_checked = (ArrayList<Chip>) Arrays.asList(filter_urgency_chips).stream().filter(chip -> chip.isChecked()).collect(Collectors.toList());
                            ArrayList<Chip> filters_sort_chips_checked = (ArrayList<Chip>) Arrays.asList(filters_sort_chips).stream().filter(chip -> chip.isChecked()).collect(Collectors.toList());
                            if (filters_times_chips_checked.size() <= 0 &&
                                    filter_urgency_chip_checked.size() <= 0 &&
                                    filters_sort_chips_checked.size() <= 0) {
                                //clean empty filter applied so return elements to normality
                                ArrayList<MainElement> non_filtered_list = new ArrayList<>();
                                switch (tab_selected) {
                                    case 0:
                                        non_filtered_list.addAll(complete_list.get("Anime").get("Released"));
                                        non_filtered_list.addAll(complete_list.get("Anime").get("ToRelease"));
                                        heading_elements_Sections_text.setText("All Anime");
                                        tab1_anime.anime_adapter.setDataSet(non_filtered_list);
                                        tab1_anime.anime_adapter.notifyDataSetChanged();
                                        filters_main_sheet_dialog.dismiss();
                                        break;
                                    case 1:
                                        non_filtered_list.addAll(complete_list.get("Film").get("Released"));
                                        non_filtered_list.addAll(complete_list.get("Film").get("ToRelease"));
                                        heading_elements_Sections_text.setText("All Films");
                                        tab2_film.film_adapter.setDataSet(non_filtered_list);
                                        tab2_film.film_adapter.notifyDataSetChanged();
                                        filters_main_sheet_dialog.dismiss();
                                        break;
                                    case 2:
                                        non_filtered_list.addAll(complete_list.get("Series").get("Released"));
                                        non_filtered_list.addAll(complete_list.get("Series").get("ToRelease"));
                                        heading_elements_Sections_text.setText("All Series");
                                        tab3_series.series_adapter.setDataSet(non_filtered_list);
                                        tab3_series.series_adapter.notifyDataSetChanged();
                                        filters_main_sheet_dialog.dismiss();
                                        break;
                                    case 3:
                                        non_filtered_list.addAll(complete_list.get("Games").get("Released"));
                                        non_filtered_list.addAll(complete_list.get("Games").get("ToRelease"));
                                        heading_elements_Sections_text.setText("All Games");
                                        tab4_games.games_adapter.setDataSet(non_filtered_list);
                                        tab4_games.games_adapter.notifyDataSetChanged();
                                        filters_main_sheet_dialog.dismiss();
                                        break;
                                }
                            } else {
                                String sort_selected = "";
                                for (Chip chip : filters_sort_chips_checked) {
                                    if (chip.isChecked()) {
                                        sort_selected = chip.getText().toString();
                                    }
                                }
                                ArrayList<MainElement> filtered_elements = new ArrayList<>();
                                StringBuilder filters_text = new StringBuilder();
                                switch (tab_selected) {
                                    case 0:
                                        if (filters_times_chips_checked.size()==0 || filters_times_chips_checked.size() == 3) {
                                            filtered_elements.addAll(complete_list.get("Anime").get("Released"));
                                            filtered_elements.addAll(complete_list.get("Anime").get("ToRelease"));
                                            filters_text.append( "All Anime");
                                        } else {
                                            for (int i = 0; i < filters_times_chips_checked.size(); i++) {
                                                Chip filter_chip = filters_times_chips_checked.get(i);
                                                filtered_elements.addAll(complete_list.get("Anime").get(filter_chip.getText().toString()));
                                                if (!(i == filters_times_chips_checked.size() - 1)) {
                                                    filters_text.append(filter_chip.getText().toString() + " - ");
                                                } else {
                                                    filters_text.append(filter_chip.getText().toString());
                                                }
                                            }
                                        }
                                        if (!(filter_urgency_chip_checked.size() == 0)) {
                                            filters_text.append(" - ");
                                            if (filter_urgency_chip_checked.get(0).getText().toString().equals("Urgent")) {
                                                filtered_elements.removeIf(x -> x.isImportant_to_watch()==false);
                                                filters_text.append( "Urgency");
                                            } else {
                                                filtered_elements.removeIf(x -> x.isImportant_to_watch()==true);
                                                filters_text.append( "NotUrgency");
                                            }
                                        }
                                        if (!(sort_selected.isEmpty())) {
                                            filters_text.append(" - ");
                                            filters_text.append(sort_selected);
                                            switch (sort_selected) {
                                                case "A-Z":
                                                    Collections.sort(filtered_elements);
                                                    break;
                                                case "Z-A":
                                                    Collections.sort(filtered_elements, reverseOrder());
                                                    break;
                                                case "Newer-Older":
                                                    Collections.sort(filtered_elements, comparing(MainElement::getElement_date).reversed());
                                                    break;
                                                case "Older-Newer":
                                                    Collections.sort(filtered_elements, comparing(MainElement::getElement_date));
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                        heading_elements_Sections_text.setText(filters_text.toString());
                                        tab1_anime.anime_adapter.setDataSet(filtered_elements);
                                        tab1_anime.anime_adapter.notifyDataSetChanged();
                                        filters_main_sheet_dialog.dismiss();
                                        break;
                                    case 1:
                                        if (filters_times_chips_checked.size()==0 || filters_times_chips_checked.size() == 3) {
                                            filtered_elements.addAll(complete_list.get("Film").get("Released"));
                                            filtered_elements.addAll(complete_list.get("Film").get("ToRelease"));
                                            filters_text.append( "All Film");
                                        } else {
                                            for (int i = 0; i < filters_times_chips_checked.size(); i++) {
                                                Chip filter_chip = filters_times_chips_checked.get(i);
                                                filtered_elements.addAll(complete_list.get("Film").get(filter_chip.getText().toString()));
                                                if (!(i == filters_times_chips_checked.size() - 1)) {
                                                    filters_text.append(filter_chip.getText().toString() + " - ");
                                                } else {
                                                    filters_text.append(filter_chip.getText().toString());
                                                }
                                            }
                                        }
                                        if (!(filter_urgency_chip_checked.size() == 0)) {
                                            filters_text.append(" - ");
                                            if (filter_urgency_chip_checked.get(0).getText().toString().equals("Urgent")) {
                                                filtered_elements.removeIf(x -> x.isImportant_to_watch()==false);
                                                filters_text.append( "Urgency");
                                            } else {
                                                filtered_elements.removeIf(x -> x.isImportant_to_watch()==true);
                                                filters_text.append( "NotUrgency");
                                            }
                                        }
                                        if (!(sort_selected.isEmpty())) {
                                            filters_text.append(" - ");
                                            filters_text.append(sort_selected);
                                            switch (sort_selected) {
                                                case "A-Z":
                                                    Collections.sort(filtered_elements);
                                                    break;
                                                case "Z-A":
                                                    Collections.sort(filtered_elements, reverseOrder());
                                                    break;
                                                case "Newer-Older":
                                                    Collections.sort(filtered_elements, comparing(MainElement::getElement_date).reversed());
                                                    break;
                                                case "Older-Newer":
                                                    Collections.sort(filtered_elements, comparing(MainElement::getElement_date));
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                        heading_elements_Sections_text.setText(filters_text.toString());
                                        tab2_film.film_adapter.setDataSet(filtered_elements);
                                        tab2_film.film_adapter.notifyDataSetChanged();
                                        filters_main_sheet_dialog.dismiss();
                                        break;
                                    case 2:
                                        if (filters_times_chips_checked.size()==0 || filters_times_chips_checked.size() == 3) {
                                            filtered_elements.addAll(complete_list.get("Series").get("Released"));
                                            filtered_elements.addAll(complete_list.get("Series").get("ToRelease"));
                                            filters_text.append( "All Series");
                                        } else {
                                            for (int i = 0; i < filters_times_chips_checked.size(); i++) {
                                                Chip filter_chip = filters_times_chips_checked.get(i);
                                                filtered_elements.addAll(complete_list.get("Series").get(filter_chip.getText().toString()));
                                                if (!(i == filters_times_chips_checked.size() - 1)) {
                                                    filters_text.append(filter_chip.getText().toString() + " - ");
                                                } else {
                                                    filters_text.append(filter_chip.getText().toString());
                                                }
                                            }
                                        }
                                        if (!(filter_urgency_chip_checked.size() == 0)) {
                                            filters_text.append(" - ");
                                            if (filter_urgency_chip_checked.get(0).getText().toString().equals("Urgent")) {
                                                filtered_elements.removeIf(x -> x.isImportant_to_watch()==false);
                                                filters_text.append( "Urgency");
                                            } else {
                                                filtered_elements.removeIf(x -> x.isImportant_to_watch()==true);
                                                filters_text.append( "NotUrgency");
                                            }
                                        }
                                        if (!(sort_selected.isEmpty())) {
                                            filters_text.append(" - ");
                                            filters_text.append(sort_selected);
                                            switch (sort_selected) {
                                                case "A-Z":
                                                    Collections.sort(filtered_elements);
                                                    break;
                                                case "Z-A":
                                                    Collections.sort(filtered_elements, reverseOrder());
                                                    break;
                                                case "Newer-Older":
                                                    Collections.sort(filtered_elements, comparing(MainElement::getElement_date).reversed());
                                                    break;
                                                case "Older-Newer":
                                                    Collections.sort(filtered_elements, comparing(MainElement::getElement_date));
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                        heading_elements_Sections_text.setText(filters_text.toString());
                                        tab3_series.series_adapter.setDataSet(filtered_elements);
                                        tab3_series.series_adapter.notifyDataSetChanged();
                                        filters_main_sheet_dialog.dismiss();
                                        break;
                                    case 3:
                                        if (filters_times_chips_checked.size()==0 || filters_times_chips_checked.size() == 3) {
                                            filtered_elements.addAll(complete_list.get("Games").get("Released"));
                                            filtered_elements.addAll(complete_list.get("Games").get("ToRelease"));
                                            filters_text.append( "All Games");
                                        } else {
                                            for (int i = 0; i < filters_times_chips_checked.size(); i++) {
                                                Chip filter_chip = filters_times_chips_checked.get(i);
                                                filtered_elements.addAll(complete_list.get("Games").get(filter_chip.getText().toString()));
                                                if (!(i == filters_times_chips_checked.size() - 1)) {
                                                    filters_text.append(filter_chip.getText().toString() + " - ");
                                                } else {
                                                    filters_text.append(filter_chip.getText().toString());
                                                }
                                            }
                                        }
                                        if (!(filter_urgency_chip_checked.size() == 0)) {
                                            filters_text.append(" - ");
                                            if (filter_urgency_chip_checked.get(0).getText().toString().equals("Urgent")) {
                                                filtered_elements.removeIf(x -> x.isImportant_to_watch()==false);
                                                filters_text.append( "Urgency");
                                            } else {
                                                filtered_elements.removeIf(x -> x.isImportant_to_watch()==true);
                                                filters_text.append( "NotUrgency");
                                            }
                                        }
                                        if (!(sort_selected.isEmpty())) {
                                            filters_text.append(" - ");
                                            filters_text.append(sort_selected);
                                            switch (sort_selected) {
                                                case "A-Z":
                                                    Collections.sort(filtered_elements);
                                                    break;
                                                case "Z-A":
                                                    Collections.sort(filtered_elements, reverseOrder());
                                                    break;
                                                case "Newer-Older":
                                                    Collections.sort(filtered_elements, comparing(MainElement::getElement_date).reversed());
                                                    break;
                                                case "Older-Newer":
                                                    Collections.sort(filtered_elements, comparing(MainElement::getElement_date));
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                        heading_elements_Sections_text.setText(filters_text.toString());
                                        tab4_games.games_adapter.setDataSet(filtered_elements);
                                        tab4_games.games_adapter.notifyDataSetChanged();
                                        filters_main_sheet_dialog.dismiss();
                                        break;
                                }
                            }
                        }
                    }
                });

                filters_reset_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for (Chip chip : filters_times_chips) {
                            chip.setChecked(false);
                        }
                        for (Chip chip : filter_urgency_chips) {
                            chip.setChecked(false);
                        }
                        for (Chip chip : filters_sort_chips) {
                            chip.setChecked(false);
                        }
                    }
                });
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //initialize tab when tab changing, removing filters
                if (!(current_tab == 999)) {
                    for (Chip chip : filters_times_chips) {
                        chip.setChecked(false);
                    }
                    for (Chip chip : filters_sort_chips) {
                        chip.setChecked(false);
                    }
                    ArrayList<MainElement> non_filtered_list = new ArrayList<>();
                    switch (current_tab) {
                        case 0:
                            non_filtered_list.addAll(complete_list.get("Anime").get("Released"));
                            non_filtered_list.addAll(complete_list.get("Anime").get("ToRelease"));
                            tab1_anime.anime_adapter.setDataSet(non_filtered_list);
                            tab1_anime.anime_adapter.notifyDataSetChanged();
                            break;
                        case 1:
                            non_filtered_list.addAll(complete_list.get("Film").get("Released"));
                            non_filtered_list.addAll(complete_list.get("Film").get("ToRelease"));
                            tab2_film.film_adapter.setDataSet(non_filtered_list);
                            tab2_film.film_adapter.notifyDataSetChanged();
                            break;
                        case 2:
                            non_filtered_list.addAll(complete_list.get("Series").get("Released"));
                            non_filtered_list.addAll(complete_list.get("Series").get("ToRelease"));
                            tab3_series.series_adapter.setDataSet(non_filtered_list);
                            tab3_series.series_adapter.notifyDataSetChanged();
                            break;
                        case 3:
                            non_filtered_list.addAll(complete_list.get("Games").get("Released"));
                            non_filtered_list.addAll(complete_list.get("Games").get("ToRelease"));
                            tab4_games.games_adapter.setDataSet(non_filtered_list);
                            tab4_games.games_adapter.notifyDataSetChanged();
                            break;
                    }
                }

                //handling the switch tab labels text
                switch (tab.getPosition()) {
                    case 0:
                        heading_elements_Sections_text.setText("All Anime");
                        current_tab = 0;
                        break;
                    case 1:
                        heading_elements_Sections_text.setText("All Film");
                        current_tab = 1;
                        break;
                    case 2:
                        heading_elements_Sections_text.setText("All Series");
                        current_tab = 1;
                        break;
                    case 3:
                        heading_elements_Sections_text.setText("All Games");
                        current_tab = 2;
                        break;
                    case 4:
                        heading_elements_Sections_text.setText("All Others");
                        current_tab = 3;
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        search_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentHolder,  new SearchingFragment())
                        .commit();
            }
        });

        upcomings_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentHolder,  new UpcomingFragment())
                        .commit();
            }
        });

        notifications_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentHolder,  new NotificationFragment())
                        .commit();
            }
        });

        main_appname_textheading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentHolder,  new OthersFragment())
                        .commit();
            }
        });

        return view;
    }

    public void initializeList() {
        complete_list.clear();
        ArrayList<MainElement> released_anime = new ArrayList<>();
        ArrayList<MainElement> toRelease_anime = new ArrayList<>();
        Map<String, ArrayList<MainElement>> anime_times = new HashMap<>();
        anime_times.put("Released", released_anime);
        anime_times.put("ToRelease", toRelease_anime);
        complete_list.put("Anime", anime_times);

        ArrayList<MainElement> released_film = new ArrayList<>();
        ArrayList<MainElement> toRelease_film = new ArrayList<>();
        Map<String, ArrayList<MainElement>> film_times = new HashMap<>();
        film_times.put("Released", released_film);
        film_times.put("ToRelease", toRelease_film);
        complete_list.put("Film", film_times);

        ArrayList<MainElement> released_series = new ArrayList<>();
        ArrayList<MainElement> toRelease_series = new ArrayList<>();
        Map<String, ArrayList<MainElement>> series_times = new HashMap<>();
        series_times.put("Released", released_series);
        series_times.put("ToRelease", toRelease_series);
        complete_list.put("Series", series_times);

        ArrayList<MainElement> released_games = new ArrayList<>();
        ArrayList<MainElement> toRelease_games = new ArrayList<>();
        Map<String, ArrayList<MainElement>> games_times = new HashMap<>();
        games_times.put("Released", released_games);
        games_times.put("ToRelease", toRelease_games);
        complete_list.put("Games", games_times);
    }

    public void updateData() {
        initializeList();
        //get data from firebase
        databaseReference = databaseReference.child("Main");
        databaseReference.keepSynced(true);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //code to retrieve elements from db
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    //datasnapshot1 -> ANIME, FILM, SERIES, GAMES
                    switch (dataSnapshot1.getKey()) {
                        case "Anime":
                            for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                //datasnapshot2 -> ANIME RELEASED, ToRelease
                                switch (dataSnapshot2.getKey()) {
                                    case "Released":
                                        for (DataSnapshot dataSnapshot3 : dataSnapshot2.getChildren()) {
                                            //datasnapshot3 -> Released ANIME
                                            MainElement released_anime = dataSnapshot3.getValue(MainElement.class);
                                            released_anime.setName(dataSnapshot3.getKey());
                                            complete_list.get("Anime").get("Released").add(released_anime);
                                        }
                                        break;
                                    case "ToRelease":
                                        for (DataSnapshot dataSnapshot3 : dataSnapshot2.getChildren()) {
                                            //datasnapshot3 -> ToRelease ANIME
                                            MainElement toRelease_anime = dataSnapshot3.getValue(MainElement.class);
                                            toRelease_anime.setName(dataSnapshot3.getKey());
                                            complete_list.get("Anime").get("ToRelease").add(toRelease_anime);
                                        }
                                        break;
                                }
                            }
                            break;
                        case "Film":
                            for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                //datasnapshot2 -> FILM RELEASED, ToRelease
                                switch (dataSnapshot2.getKey()) {
                                    case "Released":
                                        for (DataSnapshot dataSnapshot3 : dataSnapshot2.getChildren()) {
                                            //datasnapshot3 -> Released FILM
                                            MainElement released_film = dataSnapshot3.getValue(MainElement.class);
                                            released_film.setName(dataSnapshot3.getKey());
                                            complete_list.get("Film").get("Released").add(released_film);
                                        }
                                        break;
                                    case "ToRelease":
                                        for (DataSnapshot dataSnapshot3 : dataSnapshot2.getChildren()) {
                                            //datasnapshot3 -> ToRelease FILM
                                            MainElement toRelease_film = dataSnapshot3.getValue(MainElement.class);
                                            toRelease_film.setName(dataSnapshot3.getKey());
                                            complete_list.get("Film").get("ToRelease").add(toRelease_film);
                                        }
                                        break;
                                }
                            }
                            break;
                        case "Series":
                            for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                //datasnapshot2 -> SERIES RELEASED, ToRelease
                                switch (dataSnapshot2.getKey()) {
                                    case "Released":
                                        for (DataSnapshot dataSnapshot3 : dataSnapshot2.getChildren()) {
                                            //datasnapshot3 -> Released SERIES
                                            MainElement released_series = dataSnapshot3.getValue(MainElement.class);
                                            released_series.setName(dataSnapshot3.getKey());
                                            complete_list.get("Series").get("Released").add(released_series);
                                        }
                                        break;
                                    case "ToRelease":
                                        for (DataSnapshot dataSnapshot3 : dataSnapshot2.getChildren()) {
                                            //datasnapshot3 -> ToRelease SERIES
                                            MainElement toRelease_series = dataSnapshot3.getValue(MainElement.class);
                                            toRelease_series.setName(dataSnapshot3.getKey());
                                            complete_list.get("Series").get("ToRelease").add(toRelease_series);
                                        }
                                        break;
                                }
                            }
                            break;
                        case "Games":
                            for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                //datasnapshot2 -> GAMES RELEASED, ToRelease
                                switch (dataSnapshot2.getKey()) {
                                    case "Released":
                                        for (DataSnapshot dataSnapshot3 : dataSnapshot2.getChildren()) {
                                            //datasnapshot3 -> Released GAMES
                                            MainElement released_games = dataSnapshot3.getValue(MainElement.class);
                                            released_games.setName(dataSnapshot3.getKey());
                                            complete_list.get("Games").get("Released").add(released_games);
                                        }
                                        break;
                                    case "ToRelease":
                                        for (DataSnapshot dataSnapshot3 : dataSnapshot2.getChildren()) {
                                            //datasnapshot3 -> ToRelease GAMES
                                            MainElement toRelease_games = dataSnapshot3.getValue(MainElement.class);
                                            toRelease_games.setName(dataSnapshot3.getKey());
                                            complete_list.get("Games").get("ToRelease").add(toRelease_games);
                                        }
                                        break;
                                }
                            }
                            break;
                    }
                }

                //setting empty elements if lists are empty
                String[] types = new String[] {"Anime", "Film", "Series", "Games"};
                String[] times = new String[] {"Released", "ToRelease"};
                for (String type : types) {
                    boolean empty = true;
                    for (String time : times) {
                        if (!(complete_list.get(type).get(time).isEmpty())) {
                            empty = false;
                            break;
                        }
                    }
                    if (empty) {
                        MainElement element = new MainElement("Empty Element",
                                "Empty Element",
                                "Empty Element",
                                type,
                                "Released",
                                false,
                                false,
                                "no_future",
                                0);
                        complete_list.get(type).get("Released").add(element);
                    } else {
                        if (!(complete_list.get(type).get("Released").size() == 0) && complete_list.get(type).get("Released").get(0).getName().equals("Empty Element")) {
                            complete_list.get(type).get("Released").remove(0);
                        }
                    }
                }

                viewPager.setAdapter(new ViewPagerFragmentSateAdapter(getActivity(), complete_list));
                new TabLayoutMediator(
                        tabLayout, viewPager, (tab, position) -> tab.setText(titles[position])
                ).attach();

                if (tab_to_go != 999) {
                    viewPager.setCurrentItem(tab_to_go, false);
                }

                setNotifications();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //set code to show an error
                //Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setNotifications() {
        List<MainElement> futures_elements = new ArrayList<>();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            actual_date_in_seconds = Instant.now().getEpochSecond();
        }

        //getting futures elements
        String[] types = new String[] {"Anime", "Film", "Series", "Games"};
        for (String type : types) {
            futures_elements.addAll(complete_list.get(type).get("ToRelease"));
        }

        for (MainElement element : futures_elements) {
            if (element.getFuture_release_date() != null && filterForNotifications(element.getFuture_release_date(), element.getName())) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                Date date = null;
                try {
                    date = df.parse(element.future_release_date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long future_date_in_seconds = date.getTime()/1000;
                long notification_delay_in_seconds = future_date_in_seconds-actual_date_in_seconds;
                String time_remaining = (notification_delay_in_seconds/3600) + ":" + ((notification_delay_in_seconds/60)%60) + ":" + (notification_delay_in_seconds%60);
                System.out.println("[NAME]  " + element.getName());
                System.out.println("   [TODAY - FUTURE_DATE]");
                System.out.println("       " + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " - " + element.getFuture_release_date());
                System.out.println("   [TODAY - FUTURE_DATE @SECONDS]");
                System.out.println("       " + actual_date_in_seconds + " - " + future_date_in_seconds);
                System.out.println("   [TODAY - FUTURE_DATE @DIFF IN SECONDS]");
                System.out.println("       " + notification_delay_in_seconds);
                System.out.println("   [TODAY - FUTURE_DATE @DIFF IN TIME]");
                System.out.println("       " + time_remaining);
                if (notification_delay_in_seconds < 0) {
                    System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                    //if here then the element has not been trasposed in the right new section when its date came out
                    storageReference = FirebaseStorage.getInstance(URL_storage).getReferenceFromUrl(element.getImage_url());
                    storageReference.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            storageReference = FirebaseStorage.getInstance(URL_storage).getReference("Images/" + "Main/" + (element.getType()+"/") + "Released/" + element.getName());
                            storageReference.putBytes(bytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            System.out.println("Element created and uploaded with success");
                                            storageReference = FirebaseStorage.getInstance(URL_storage).getReferenceFromUrl(element.getImage_url());
                                            databaseReference = FirebaseDatabase.getInstance(URL).getReference().child("Main");
                                            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    System.out.println("Element images removed successfully from firebase");
                                                    databaseReference.child(element.getType()).child(element.getTime()).child(element.getName()).removeValue();
                                                    databaseReference = FirebaseDatabase.getInstance(URL).getReference().child("Main").child(element.getType()).child("Released");
                                                    databaseReference.child(element.getName()).child("description").setValue(element.getDescription());
                                                    databaseReference.child(element.getName()).child("type").setValue(element.getType());
                                                    databaseReference.child(element.getName()).child("time").setValue("Released");
                                                    databaseReference.child(element.getName()).child("important_to_watch").setValue(element.isImportant_to_watch());
                                                    databaseReference.child(element.getName()).child("future_release_date").setValue("null");
                                                    databaseReference.child(element.getName()).child("image_url").setValue(uri.toString());
                                                    databaseReference.child(element.getName()).child("done").setValue(false);
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                        databaseReference.child(element.getName()).child("element_date").setValue(Instant.now().getEpochSecond());
                                                    }
                                                    databaseReference = FirebaseDatabase.getInstance(URL).getReference().child("Extra").child("Notifications");
                                                    databaseReference.child(element.getName()).child("image_url").setValue(uri.toString());
                                                    databaseReference.child(element.getName()).child("desc").setValue(element.getDescription());
                                                    databaseReference = FirebaseDatabase.getInstance(URL).getReference();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    System.out.println("ERROR: Element images remove fail from firebase");
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            System.out.println("ERROR: element not created");
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        }
                    });
                }
                else {
                    System.out.println("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB");
                    System.out.println(notification_delay_in_seconds);
                    createWorkRequest(element.getName(), element.getType(), notification_delay_in_seconds);
                    final Timer time = new Timer();
                    test_notifications_seconds = notification_delay_in_seconds;
                    time.scheduleAtFixedRate(new TimerTask() {
                        public void run() {
                            if (test_notifications_seconds == 0) {
                                System.out.println("work finished");
                                time.cancel();
                                time.purge();
                            } else {
                                System.out.println(--test_notifications_seconds);
                            }
                        }
                    }, 1000, 1000);
                    //Toast.makeText((Context) MainActivity.this, (CharSequence)"Reminder set", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean filterForNotifications(String time, String name) {
        Date now_plus = null;
        Date release_date = null;
        Date today = new Date();
        String tomorrow = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Calendar c = Calendar.getInstance();
            //System.out.println("before: " + new SimpleDateFormat("yyyy-MM-dd").format(c.getTime()));
            c.add(Calendar.MONTH, 1);
            tomorrow = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
            //System.out.println("after: " + tomorrow);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                now_plus = new SimpleDateFormat("yyyy-MM-dd").parse(tomorrow);
                //System.out.println("-1 " + new SimpleDateFormat("yyyy-MM-dd").format(now_plus.getTime()));
            } catch (Exception e) {

            }
        }
        try {
            release_date = new SimpleDateFormat("yyyy-MM-dd").parse(time);
            //System.out.println("-2 " + new SimpleDateFormat("yyyy-MM-dd").format(release_date.getTime()));
        } catch (Exception e) {

        }

        System.out.println("[NAME]  " + name);
        System.out.println("   [TODAY]  " + today.toString());
        System.out.println("   [RELEASE DATE]  " + release_date.toString());
        System.out.println("   [TODAY PLUS 1 MONTH]  " + now_plus.toString());
        System.out.println("   [REL.COMPARE(TODAY)]  " + (release_date.compareTo(today) >= 0));
        System.out.println("   [REL.COMPARE(PLUS)]  " + (release_date.compareTo(now_plus) < 0));

        //if (release_date.compareTo(now_plus) < 0 && release_date.compareTo(today) >= 0) {
        if (release_date.compareTo(now_plus) < 0) {
            System.out.println("RELEASE DATE 30 DAYS FROM TODAY");
            return true;
        } else {
            System.out.println("RELEASE DATE @not 30 DAYS FROM TODAY");
            return false;
        }
    }

    private void createWorkRequest(String name, String type, long timeDelayInSeconds) {
        OneTimeWorkRequest temp = new OneTimeWorkRequest.Builder(ReminderWorker.class)
                .setInitialDelay(timeDelayInSeconds, TimeUnit.SECONDS)
                .setInputData(new Data.Builder()
                        .putString("Name", name)
                        .putString("Type", type)
                        .build()
                )
                .build();
        WorkManager.getInstance(getContext()).enqueue(temp);
    }

}