package com.example.rememberme;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shuhart.stepview.StepView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.time.Instant;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class AddActivity extends AppCompatActivity {

    ImageButton add_backarrow, add_prev_button, add_next_button, add_save_button;
    ImageView element_image_add;
    Uri image_to_up;
    TextInputEditText element_name_add, element_desc_add;
    private Switch add_urgency_switch;
    private boolean important_to_watch;
    byte[] downsizedImageBytes;
    String item_type = null;
    String item_time = null;
    boolean image_uploaded = false;
    boolean isDouble;
    String future_release_date = "";
    long date;
    String year_selected, month_selected, day_selected;
    int counter = 0;
    ViewSwitcher add_next_save_buttons_switcher;
    ConstraintLayout add_urgency_container, add_date_container, add_image_container, add_type_time_date_container, add_name_desc_container, constraintLayout5, add_next_button_container, add_save_button_container;
    TextInputLayout textinputlayout_autocomplete_date_year, textinputlayout_autocomplete_date_month, textinputlayout_autocomplete_date_day, textinputlayout_autocomplete_type, textinputlayout_autocomplete_time, textinputlayout_edit_desc, textinputlayout_edit_name;
    ArrayList<String> items_type_add = new ArrayList<String>(Arrays.asList(new String[] {"Anime", "Film", "Series", "Games"}));
    ArrayList<String> items_time_add = new ArrayList<String>(Arrays.asList(new String[] {"Released", "toReleasetoRelease"}));
    AutoCompleteTextView element_dropdown_add_date_year, element_dropdown_add_date_month, element_dropdown_add_date_day, element_dropdown_add_type, element_dropdown_add_time;
    ArrayList<String> days = new ArrayList<>();
    ArrayList<String> months = new ArrayList<>();
    ArrayList<String> years = new ArrayList<>();
    StepView stepView;
    ArrayAdapter<String> days_adapter, months_adapter, years_adapter, adapter_items_type, adapter_items_time;
    String URL = "https://rememberapp-13f74-default-rtdb.europe-west1.firebasedatabase.app/";
    String URL_storage = "gs://rememberapp-13f74.appspot.com";
    DatabaseReference databaseReference = FirebaseDatabase.getInstance(URL).getReference();
    StorageReference dbStorageRef = FirebaseStorage.getInstance(URL_storage).getReference();
    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK
                        && result.getData() != null) {
                    Uri photoUri = result.getData().getData();
                    try {
                        // aiming for ~500kb max. assumes typical device image size is around 2megs
                        int scaleDivider = 4;
                        // 1. Convert uri to bitmap
                        Bitmap fullBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), photoUri);
                        // 2. Get the downsized image content as a byte[]
                        int scaleWidth = fullBitmap.getWidth() / scaleDivider;
                        int scaleHeight = fullBitmap.getHeight() / scaleDivider;
                        downsizedImageBytes = getDownsizedImageBytes(fullBitmap, scaleWidth, scaleHeight);
                        String s = new String(downsizedImageBytes, "UTF-8");
                        Uri uri = Uri.parse(s);
                        image_to_up = uri;
                        image_uploaded = true;
                        element_image_add.setImageURI(photoUri);
                        LinearLayout add_image_text_container = findViewById(R.id.add_image_text_container);
                        add_image_text_container.setVisibility(View.GONE);
                    }
                    catch (IOException ioEx) {
                        System.out.println("-----------------------------------------------POPI");
                    }
                    /*image_to_up = photoUri;
                    image_uploaded = true;
                    element_image_add.setImageURI(photoUri);
                    LinearLayout add_image_text_container = findViewById(R.id.add_image_text_container);
                    add_image_text_container.setVisibility(View.GONE);*/
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        getSupportActionBar().hide();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        //findviews
        if (true) {
            add_save_button = findViewById(R.id.add_save_button);
            element_image_add = findViewById(R.id.element_image_add);
            element_name_add = findViewById(R.id.element_name_add);
            element_desc_add = findViewById(R.id.element_desc_add);
            element_dropdown_add_type = findViewById(R.id.element_dropdown_add_type);
            element_dropdown_add_time = findViewById(R.id.element_dropdown_add_time);
            //add_date_edittext = findViewById(R.id.add_date_edittext);
            textinputlayout_autocomplete_type = (TextInputLayout) findViewById(R.id.textinputlayout_autocomplete_type);
            textinputlayout_autocomplete_time = (TextInputLayout) findViewById(R.id.textinputlayout_autocomplete_time);
            textinputlayout_edit_name = (TextInputLayout) findViewById(R.id.textinputlayout_edit_name);
            textinputlayout_edit_desc = (TextInputLayout) findViewById(R.id.textinputlayout_edit_desc);
            add_backarrow = findViewById(R.id.add_backarrow);
            add_prev_button = findViewById(R.id.add_prev_button);
            add_next_button = findViewById(R.id.add_next_button);
            add_save_button = findViewById(R.id.add_save_button);
            add_next_button_container = findViewById(R.id.add_next_button_container);
            add_save_button_container = findViewById(R.id.add_save_button_container);
            constraintLayout5 = findViewById(R.id.others_remove_button_container);
            add_image_container = findViewById(R.id.add_image_container);
            add_type_time_date_container = findViewById(R.id.add_type_time_date_container);
            add_name_desc_container = findViewById(R.id.add_name_desc_container);
            add_next_save_buttons_switcher = findViewById(R.id.add_next_save_buttons_switcher);
            add_date_container = findViewById(R.id.add_date_container);
            stepView = findViewById(R.id.step_view);
            element_dropdown_add_date_year = findViewById(R.id.element_dropdown_add_date_year);
            element_dropdown_add_date_month = findViewById(R.id.element_dropdown_add_date_month);
            element_dropdown_add_date_day = findViewById(R.id.element_dropdown_add_date_day);
            textinputlayout_autocomplete_date_year = findViewById(R.id.textinputlayout_autocomplete_date_year);
            textinputlayout_autocomplete_date_month = findViewById(R.id.textinputlayout_autocomplete_date_month);
            textinputlayout_autocomplete_date_day = findViewById(R.id.textinputlayout_autocomplete_date_day);
            add_urgency_switch = findViewById(R.id.add_urgency_switch);
            add_urgency_container = findViewById(R.id.add_urgency_container);
        }


        //TODO REFACTORING COME PER EDIT

        //setting name from searched not matched query
        element_name_add.setText(getIntent().getStringExtra("name"));

        //stepviews handler
        if (true) {
            stepView.getState()
                    .stepsNumber(3)
                    .animationDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                    .stepLineWidth(getResources().getDimensionPixelSize(R.dimen.dp1))
                    .textSize(getResources().getDimensionPixelSize(R.dimen.sp14))
                    .stepNumberTextSize(getResources().getDimensionPixelSize(R.dimen.sp16))
                    .typeface(ResourcesCompat.getFont(this, R.font.nunito))
                    // other state methods are equal to the corresponding xml attributes
                    .commit();

            setViews(counter);

            add_prev_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    counter--;
                    stepView.done(false);
                    stepView.go(counter, true);
                    setViews(counter);
                }
            });

            add_next_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (validateViewData(counter)) {
                        counter++;
                        stepView.go(counter, true);
                        setViews(counter);
                    }
                }
            });

            //step listener
            stepView.setOnStepClickListener(new StepView.OnStepClickListener() {
                @Override
                public void onStepClick(int step) {
                    if (step <= stepView.getCurrentStep()) {
                        stepView.go(step, true);
                        counter = step;
                        setViews(step);
                    }
                }
            });
        }

        //add name handler
        element_name_add.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textinputlayout_edit_name.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //add desc handler
        element_desc_add.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textinputlayout_edit_desc.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //type handler
        if (true) {
            adapter_items_type = new ArrayAdapter<String>(this, R.layout.list_item, items_type_add);
            element_dropdown_add_type.setAdapter(adapter_items_type);
            element_dropdown_add_type.setDropDownBackgroundDrawable(
                    AddActivity.this.getResources().getDrawable(R.drawable.transparent_background));
            element_dropdown_add_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    textinputlayout_autocomplete_type.setErrorEnabled(false);
                    item_type = adapterView.getItemAtPosition(i).toString();

                    //date reset because if type change time will be resetted if before was clicked
                    add_date_container.setVisibility(View.GONE);

                    add_urgency_container.setVisibility(View.GONE);
                    add_urgency_switch.setChecked(false);

                    //setting time visible and resetting time
                    textinputlayout_autocomplete_time.setVisibility(View.VISIBLE);
                    items_time_add = new ArrayList<String>(Arrays.asList("Released", "ToRelease"));
                    adapter_items_time.clear();
                    adapter_items_time.addAll(items_time_add);
                    adapter_items_time = new ArrayAdapter<String>(AddActivity.this, R.layout.list_item, items_time_add);
                    element_dropdown_add_time.setAdapter(adapter_items_time);
                    adapter_items_time.notifyDataSetChanged();
                    element_dropdown_add_time.setText("");
                    item_time = "";
                }
            });
        }

        //switch urgency handler
        if (true) {
            add_urgency_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(important_to_watch) {
                        important_to_watch = false;
                    } else {
                        important_to_watch = true;
                    }
                }
            });
        }

        //time handler
        if (true) {
            adapter_items_time = new ArrayAdapter<String>(this, R.layout.list_item, items_time_add);
            element_dropdown_add_time.setAdapter(adapter_items_time);
            element_dropdown_add_time.setDropDownBackgroundDrawable(
                    AddActivity.this.getResources().getDrawable(R.drawable.transparent_background));
            element_dropdown_add_time.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    textinputlayout_autocomplete_time.setErrorEnabled(false);
                    item_time = adapterView.getItemAtPosition(i).toString();
                    add_urgency_container.setVisibility(View.VISIBLE);
                    if (item_time.equals("ToRelease")) {
                        add_date_container.setVisibility(View.VISIBLE);
                    } else {
                        add_date_container.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }

        //date handler
        if (true) {
            year_selected = "";
            month_selected = "";
            day_selected = "";

            setYearsList();

            months_adapter = new ArrayAdapter<String>(AddActivity.this, R.layout.list_item, months);
            years_adapter = new ArrayAdapter<String>(AddActivity.this, R.layout.list_item, years);
            days_adapter = new ArrayAdapter<String>(AddActivity.this, R.layout.list_item, days);

            element_dropdown_add_date_year.setAdapter(years_adapter);
            element_dropdown_add_date_year.setDropDownBackgroundDrawable(
                    AddActivity.this.getResources().getDrawable(R.drawable.transparent_background));
            element_dropdown_add_date_month.setAdapter(months_adapter);
            element_dropdown_add_date_month.setDropDownBackgroundDrawable(
                    AddActivity.this.getResources().getDrawable(R.drawable.transparent_background));
            element_dropdown_add_date_day.setAdapter(days_adapter);
            element_dropdown_add_date_day.setDropDownBackgroundDrawable(
                    AddActivity.this.getResources().getDrawable(R.drawable.transparent_background));

            element_dropdown_add_date_year.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    year_selected = adapterView.getItemAtPosition(i).toString();
                    setMonthsList();
                    textinputlayout_autocomplete_date_year.setErrorEnabled(false);
                    textinputlayout_autocomplete_date_month.setEnabled(true);
                }
            });

            element_dropdown_add_date_month.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String string_month = adapterView.getItemAtPosition(i).toString();
                    month_selected = Integer.toString(Arrays.asList(new DateFormatSymbols().getShortMonths()).indexOf(string_month)+1);
                    setDaysList();
                    textinputlayout_autocomplete_date_month.setErrorEnabled(false);
                    textinputlayout_autocomplete_date_day.setEnabled(true);
                }
            });

            element_dropdown_add_date_day.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    day_selected = adapterView.getItemAtPosition(i).toString();
                    textinputlayout_autocomplete_date_day.setErrorEnabled(false);
                }
            });
        }

        //add image handler
        element_image_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                launcher.launch(intent);
            }
        });

        //save button handler
        add_save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getting current date
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    date = Instant.now().getEpochSecond();
                }

                //check if name and desc are ok, without special char
                String name_verified = element_name_add.getText().toString().replace("\\", "-").replace("/", "-");
                String desc_verified = element_desc_add.getText().toString().replace("\\", "-").replace("/", "-");

                //setting server reference
                databaseReference = databaseReference.child("Main").child(item_type).child(item_time);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //check if the element is already in the server
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            if (name_verified.equals(dataSnapshot1.getKey())) {
                                isDouble = true;
                                break;
                            }
                        }

                        //if is double then alert the user
                        if (isDouble) {
                            AlertDialog.Builder dialog_double_element = new AlertDialog.Builder(AddActivity.this); //  , R.style.MyAlertDialogStyle
                            dialog_double_element.setTitle("Error: " + name_verified + " Already exist");
                            dialog_double_element.setMessage("Double are not allowed!");
                            dialog_double_element.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            dialog_double_element.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    Intent intent = new Intent(AddActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            });

                            dialog_double_element.show();
                            isDouble = false;

                        } else {
                            //if not double then...

                            //check if element is ToRelease
                            if (item_time.equals("ToRelease")) {
                                future_release_date = year_selected+"-"+month_selected+"-"+day_selected;
                            } else {
                                future_release_date = "null";
                            }

                            dbStorageRef = FirebaseStorage.getInstance(URL_storage).getReference("Images/" + "Main/" + item_type + "/" + item_time + "/" + name_verified);
                            UploadTask update_task;
                            if (image_uploaded) {
                                update_task = dbStorageRef.putBytes(downsizedImageBytes);
                            } else {
                                image_to_up = (new Uri.Builder())
                                        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                                        .authority(getResources().getResourcePackageName(R.drawable.default_image))
                                        .appendPath(getResources().getResourceTypeName(R.drawable.default_image))
                                        .appendPath(getResources().getResourceEntryName(R.drawable.default_image))
                                        .build();
                                update_task = dbStorageRef.putFile(image_to_up);
                            }
                            update_task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    dbStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            System.out.println("Element created and uploaded with success");
                                            dataSnapshot.getRef().child(name_verified).child("description").setValue(desc_verified);
                                            dataSnapshot.getRef().child(name_verified).child("type").setValue(item_type);
                                            dataSnapshot.getRef().child(name_verified).child("time").setValue(item_time);
                                            dataSnapshot.getRef().child(name_verified).child("important_to_watch").setValue(important_to_watch);
                                            dataSnapshot.getRef().child(name_verified).child("future_release_date").setValue(future_release_date);
                                            dataSnapshot.getRef().child(name_verified).child("image_url").setValue(uri.toString());
                                            dataSnapshot.getRef().child(name_verified).child("done").setValue(false);
                                            dataSnapshot.getRef().child(name_verified).child("element_date").setValue(date);
                                            databaseReference = FirebaseDatabase.getInstance(URL).getReference().child("Extra").child("Logs");
                                            databaseReference.child(String.valueOf(date)).child("actor").setValue(name_verified);
                                            databaseReference.child(String.valueOf(date)).child("actions").setValue("ADD TO " + item_type + " TIME " + item_time);
                                            databaseReference = FirebaseDatabase.getInstance(URL).getReference();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            System.out.println("ERROR: element not created");
                                        }
                                    });
                                    System.out.println("Immagine caricata con successo");
                                    Intent intent = new Intent(AddActivity.this, MainActivity.class);
                                    switch (item_type) {
                                        case "Anime":
                                            intent.putExtra("tab", 0);
                                            break;
                                        case "Film":
                                            intent.putExtra("tab", 1);
                                            break;
                                        case "Series":
                                            intent.putExtra("tab", 2);
                                            break;
                                        case "Games":
                                            intent.putExtra("tab", 3);
                                            break;
                                    }
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        public void run() {
                                            startActivity(intent);
                                            finish();
                                        }
                                    }, 500);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    System.out.println("Immagine non caricata, errore");
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        add_backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void setViews(int counter) {
        switch (counter) {
            case 0 :
                add_name_desc_container.setVisibility(View.VISIBLE);
                add_type_time_date_container.setVisibility(View.GONE);
                add_image_container.setVisibility(View.GONE);
                //prev button
                constraintLayout5.setVisibility(View.INVISIBLE);
                if (add_next_save_buttons_switcher.getCurrentView() == add_save_button_container) {
                    //if the current button is save button
                    add_next_save_buttons_switcher.showNext();
                }
                break;
            case 1 :
                add_name_desc_container.setVisibility(View.GONE);
                add_type_time_date_container.setVisibility(View.VISIBLE);
                add_image_container.setVisibility(View.GONE);
                //prev button
                constraintLayout5.setVisibility(View.VISIBLE);
                if (add_next_save_buttons_switcher.getCurrentView() == add_save_button_container) {
                    //if the current button is save button
                    add_next_save_buttons_switcher.showNext();
                }
                break;
            case 2 :
                add_name_desc_container.setVisibility(View.GONE);
                add_type_time_date_container.setVisibility(View.GONE);
                add_image_container.setVisibility(View.VISIBLE);
                add_image_container.setClickable(true);
                add_image_container.setEnabled(true);
                element_image_add.setClickable(true);
                element_image_add.setEnabled(true);
                //prev button
                constraintLayout5.setVisibility(View.VISIBLE);
                if (add_next_save_buttons_switcher.getCurrentView() == add_next_button_container) {
                    //if the current button is next button
                    add_next_save_buttons_switcher.showNext();
                }
                break;
        }
    }

    public boolean validateViewData(int counter) {
        boolean is_validate = false;
        switch (counter) {
            case 0:
                //name and desc view
                //need to remove slashes from user input text because it would cause the born of a new child inside firebase, causing the app crash
                String name_verified = element_name_add.getText().toString().replace("\\", "-").replace("/", "-");
                String desc_verified = element_desc_add.getText().toString().replace("\\", "-").replace("/", "-");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (desc_verified.isEmpty() || desc_verified.chars().allMatch(Character::isWhitespace) ||
                    name_verified.isEmpty() || name_verified.chars().allMatch(Character::isWhitespace)) {
                        //something between name and desc can't be validate
                        if (name_verified.isEmpty() || name_verified.chars().allMatch(Character::isWhitespace)) {
                            textinputlayout_edit_name.setError("Name required");
                            textinputlayout_edit_name.setErrorEnabled(true);
                        } else if (desc_verified.isEmpty() || desc_verified.chars().allMatch(Character::isWhitespace)) {
                            textinputlayout_edit_desc.setError("Desc required");
                            textinputlayout_edit_desc.setErrorEnabled(true);
                        }
                    } else {
                        //no error so is validate
                        is_validate = true;
                    }
                }
                break;
            case 1:
                if (item_type == null || item_type.isEmpty()) {
                    //no type selected
                    textinputlayout_autocomplete_type.setError("Type required");
                    textinputlayout_autocomplete_type.setErrorEnabled(true);
                } else {
                    if (item_time == null || item_time.isEmpty()) {
                        textinputlayout_autocomplete_time.setError("Time required");
                        textinputlayout_autocomplete_time.setErrorEnabled(true);
                    } else {
                        if (item_time.equals("ToRelease")) {
                            if (day_selected == null || day_selected.isEmpty() ||
                                    month_selected == null || month_selected.isEmpty() ||
                                    year_selected == null || year_selected.isEmpty()) {
                                if (day_selected.isEmpty()) {
                                    textinputlayout_autocomplete_date_day.setError("Day required");
                                    textinputlayout_autocomplete_date_day.setErrorEnabled(true);
                                } else if (month_selected == null || month_selected.isEmpty()) {
                                    textinputlayout_autocomplete_date_month.setError("Month required");
                                    textinputlayout_autocomplete_date_month.setErrorEnabled(true);
                                } else if (year_selected == null || year_selected.isEmpty()) {
                                    textinputlayout_autocomplete_date_year.setError("Year required");
                                    textinputlayout_autocomplete_date_year.setErrorEnabled(true);
                                }
                            } else {
                                //validate is ok
                                is_validate = true;
                            }
                        } else {
                            //validate is ok
                            is_validate = true;
                        }
                    }
                }
                break;
            case 2:
                is_validate = true;
                break;
        }
        return is_validate;
    }

    public void setDaysList() {
        days.clear();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int current_year = Year.now().getValue();
            int current_month = ZonedDateTime.now(ZoneId.systemDefault()).getMonthValue();
            int actual_day = ZonedDateTime.now(ZoneId.systemDefault()).getDayOfMonth();
            if (month_selected.equals("1") || month_selected.equals("3") ||
                    month_selected.equals("5") || month_selected.equals("7") ||
                    month_selected.equals("8") || month_selected.equals("10") ||
                    month_selected.equals("12")) {
                if ( (Integer.parseInt(year_selected) == current_year) && (Integer.parseInt(month_selected) == current_month) ) {
                    for (int i = (actual_day+1); i <= 31; i++) {
                        days.add(i + "");
                    }
                } else {
                    for (int i = 1; i <= 31; i++) {
                        days.add(i + "");
                    }
                }
            } else if (month_selected.equals("4") || month_selected.equals("6") ||
                    month_selected.equals("7") || month_selected.equals("11")) {
                if ( (Integer.parseInt(year_selected) == current_year) && (Integer.parseInt(month_selected) == current_month) ) {
                    for (int i = (actual_day+1); i <= 30; i++) {
                        days.add(i + "");
                    }
                } else {
                    for (int i = 1; i <= 30; i++) {
                        days.add(i + "");
                    }
                }
            } else if (month_selected.equals("2")) {
                if ( (Integer.parseInt(year_selected) == current_year) && (Integer.parseInt(month_selected) == current_month) ) {
                    for (int i = (actual_day+1); i <= 29; i++) {
                        days.add(i + "");
                    }
                } else {
                    for (int i = 1; i <= 29; i++) {
                        days.add(i + "");
                    }
                }
            }
        }
    }

    private void setMonthsList() {
        months.clear();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int current_year = Year.now().getValue();
            if (year_selected.equals(current_year+"")) {
                //if current year selected then show months from current to end, so user can't choose month passed
                List<String> months_temp = Arrays.asList(new DateFormatSymbols().getShortMonths());
                months.addAll(months_temp.subList(Calendar.getInstance().get(Calendar.MONTH), 12));
                System.out.println(months);
            } else {
                //if else all months should be avaible
                months.addAll(Arrays.asList(new DateFormatSymbols().getShortMonths()));
            }
        }
    }

    private void setYearsList() {
        years.clear();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int current_year = Year.now().getValue();
            for (int i = current_year; i<=2030; i++) {
                years.add(i+"");
            }
        }
    }

    public byte[] getDownsizedImageBytes(Bitmap fullBitmap, int scaleWidth, int scaleHeight) throws IOException {

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(fullBitmap, scaleWidth, scaleHeight, true);

        // 2. Instantiate the downsized image content as a byte[]
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] downsizedImageBytes = baos.toByteArray();

        return downsizedImageBytes;
    }
}