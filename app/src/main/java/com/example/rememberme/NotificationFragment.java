package com.example.rememberme;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Collections;

public class NotificationFragment extends BaseFragment {
    private RecyclerView recycler_view_notifications;
    ImageButton back_arrow, notifications_clear_button;
    private NotificationAdapter adapter;
    private ArrayList<NotificationElement> notifications = new ArrayList<>();
    private String URL = "https://rememberapp-13f74-default-rtdb.europe-west1.firebasedatabase.app/";
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance(URL).getReference();
    private TextView notifications_title_log_button, notification_new_release_name, notification_new_release_desc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstancesState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        //findviews
        if (true) {
            notifications_title_log_button = view.findViewById(R.id.textView11);
            recycler_view_notifications = view.findViewById(R.id.recycler_view_notifications);
            notifications_clear_button = view.findViewById(R.id.notifications_clear_button);
            back_arrow = view.findViewById(R.id.back_arrow);
        }

        recycler_view_notifications.setHasFixedSize(true);
        LinearLayoutManager layoutManager_anime_crucial = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recycler_view_notifications.setLayoutManager(layoutManager_anime_crucial);
        recycler_view_notifications.setItemAnimator(new DefaultItemAnimator());
        adapter = new NotificationAdapter(notifications, getContext());
        adapter.setDataSet(notifications);
        recycler_view_notifications.setAdapter(adapter);
        recycler_view_notifications.scheduleLayoutAnimation();
        adapter.notifyDataSetChanged();

        getNotificationsFromDb();

        notifications_title_log_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), LogActivity.class));
            }
        });

        notifications_clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder notification_clear_dialog = new AlertDialog.Builder(getContext());
                notification_clear_dialog.setTitle("ATTENTION! Clear Notifications?");
                notification_clear_dialog.setMessage("All Notifications will be removed from the server");
                notification_clear_dialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance(URL).getReference().child("Extra").child("Notifications").setValue(null);
                        for (NotificationElement element : notifications) {
                            FirebaseStorage.getInstance().getReferenceFromUrl(element.getImage_url()).delete();
                        }
                        notifications.clear();
                        adapter.notifyDataSetChanged();
                    }
                });
                notification_clear_dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                notification_clear_dialog.show();
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

    private void getNotificationsFromDb() {
        notifications.clear();
        //get data from firebase
        databaseReference = databaseReference.child("Extra").child("Notifications");
        databaseReference.keepSynced(true);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    System.out.println(dataSnapshot1.getKey());
                    //datasnapshot1 is the notification, key is date and childrean are the fields
                    NotificationElement element = dataSnapshot1.getValue(NotificationElement.class);
                    element.setName(dataSnapshot1.getKey());
                    notifications.add(element);
                }
                Collections.sort(notifications, Collections.reverseOrder());
                adapter.setDataSet(notifications);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //set code to show an error
                //Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}