package com.example.rememberme;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import java.util.ArrayList;
import java.util.Collections;

public class LogActivity extends AppCompatActivity {
    private TextView log_element_actor1, log_edit_element1, log_edit_element2, log_edit_element3, log_edit_element4, log_edit_element5, logs_date;
    private RecyclerView recycler_view_logs;
    ImageButton logs_back_button, logs_clear_button;
    private LogAdapter adapter;
    private ArrayList<LogElement> logs = new ArrayList<>();
    private String URL = "https://rememberapp-13f74-default-rtdb.europe-west1.firebasedatabase.app/";
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        getSupportActionBar().hide();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        //findviews
        if (true) {
            log_element_actor1 = findViewById(R.id.log_element_actor);
            log_edit_element1 = findViewById(R.id.log_edit_element1);
            log_edit_element2 = findViewById(R.id.log_edit_element2);
            log_edit_element3 = findViewById(R.id.log_edit_element3);
            log_edit_element4 = findViewById(R.id.log_edit_element4);
            log_edit_element5 = findViewById(R.id.log_edit_element5);
            recycler_view_logs = findViewById(R.id.recycler_view_log);
            logs_date = findViewById(R.id.logs_date);
            logs_back_button = findViewById(R.id.log_back_button);
            logs_clear_button = findViewById(R.id.log_clear_button);
        }

        recycler_view_logs.setHasFixedSize(true);
        LinearLayoutManager layoutManager_anime_crucial = new LinearLayoutManager(LogActivity.this, LinearLayoutManager.VERTICAL, false);
        recycler_view_logs.setLayoutManager(layoutManager_anime_crucial);
        recycler_view_logs.setItemAnimator(new DefaultItemAnimator());
        adapter = new LogAdapter(logs, LogActivity.this);
        adapter.setDataSet(logs);
        recycler_view_logs.setAdapter(adapter);
        recycler_view_logs.scheduleLayoutAnimation();
        adapter.notifyDataSetChanged();

        getlogFromDb();

        logs_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        logs_clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder log_clear_dialog = new AlertDialog.Builder(LogActivity.this);
                log_clear_dialog.setTitle("ATTENTION! Clear logs?");
                log_clear_dialog.setMessage("All logs will be removed from the server");
                log_clear_dialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        logs.clear();
                        FirebaseDatabase.getInstance(URL).getReference().child("Extra").child("Logs").setValue(null);
                        adapter.notifyDataSetChanged();
                    }
                });
                log_clear_dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                log_clear_dialog.show();
            }
        });
    }

    private void getlogFromDb() {
        logs.clear();
        FirebaseApp.initializeApp(LogActivity.this);
        //get data from firebase
        databaseReference = FirebaseDatabase.getInstance(URL).getReference().child("Extra").child("Logs");
        databaseReference.keepSynced(true);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    System.out.println(dataSnapshot1.getKey());
                    //datasnapshot1 is the log, key is date and childrean are the fields
                    LogElement element = dataSnapshot1.getValue(LogElement.class);
                    element.setDate(Long.parseLong(dataSnapshot1.getKey()));
                    logs.add(element);
                }

                if (logs.size() > 0) {
                    System.out.println("ciaoooooooooooooo");
                    Collections.sort(logs, Collections.reverseOrder());
                }
                adapter.setDataSet(logs);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //set code to show an error
                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}