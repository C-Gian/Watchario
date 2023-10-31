package com.example.rememberme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class EditElementFragment extends BaseFragment {

    private MainElement elemento, new_element;
    private ImageView image_editelement_editelement, editelement_background_image;
    private EditText editelement_edit_name, editelement_edit_desc;
    private TextInputLayout textinputlayout_autocomplete_date_day_edit_editelement, textinputlayout_autocomplete_date_month_edit_editelement, textinputlayout_autocomplete_date_year_edit_editelement, textinputlayout_editelement_edit_name, textinputlayout_editelement_edit_desc, textinputlayout_autocomplete_type_edit_editelement, textinputlayout_autocomplete_time_edit_editelement;
    private AutoCompleteTextView element_dropdown_edit_date_day, element_dropdown_edit_date_month, element_dropdown_edit_type_editelement, element_dropdown_edit_time_editelement, element_dropdown_edit_date_year;
    private ImageButton back_arrow, editelement_remove_button, editelement_cancelbutton, editelement_savebutton;
    private ConstraintLayout editelement_date_edit_container;
    private Switch editelement_urgency_switch;
    private String URL = "https://rememberapp-13f74-default-rtdb.europe-west1.firebasedatabase.app/";
    private String URL_storage = "gs://rememberapp-13f74.appspot.com";
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance(URL).getReference();
    private StorageReference storageReference = FirebaseStorage.getInstance(URL_storage).getReference();
    private ArrayList<String> items_time_add = new ArrayList<String>(Arrays.asList(new String[] {"Released", "ToRelease"}));
    private ArrayAdapter<String> adapter_items_time, days_adapter, months_adapter, years_adapter;
    private byte[] downsizedImageBytes;
    private boolean new_image_uploaded;
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
                        Bitmap fullBitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
                        // 2. Get the downsized image content as a byte[]
                        int scaleWidth = fullBitmap.getWidth() / scaleDivider;
                        int scaleHeight = fullBitmap.getHeight() / scaleDivider;
                        downsizedImageBytes = getDownsizedImageBytes(fullBitmap, scaleWidth, scaleHeight);
                        String s = new String(downsizedImageBytes, "UTF-8");
                        new_image_uploaded = true;
                        image_editelement_editelement.setImageURI(photoUri);
                        Glide.with(this)
                                .load(photoUri)
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                .skipMemoryCache(false)
                                .apply(RequestOptions.bitmapTransform(new BlurTransformation(10, 3)))
                                .into(editelement_background_image);
                    }
                    catch (IOException ioEx) {
                        System.out.println("-----------------------------------------------POPI");
                    }
                }
            }
    );

    public EditElementFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_element, container, false);

        //findbyviews
        if (true) {
            image_editelement_editelement = view.findViewById(R.id.image_editelement);
            back_arrow = view.findViewById(R.id.back_arrow);
            editelement_background_image = view.findViewById(R.id.editelement_background_image);
            editelement_edit_name = view.findViewById(R.id.editelement_edit_name);
            editelement_edit_desc = view.findViewById(R.id.editelement_edit_desc);
            textinputlayout_autocomplete_date_day_edit_editelement = view.findViewById(R.id.textinputlayout_autocomplete_date_day_edit_editelement);
            textinputlayout_autocomplete_date_month_edit_editelement = view.findViewById(R.id.textinputlayout_autocomplete_date_month_edit_editelement);
            textinputlayout_autocomplete_date_year_edit_editelement = view.findViewById(R.id.textinputlayout_autocomplete_date_year_edit_editelement);
            textinputlayout_editelement_edit_name = view.findViewById(R.id.textinputlayout_editelement_edit_name);
            textinputlayout_editelement_edit_desc = view.findViewById(R.id.textinputlayout_editelement_edit_desc);
            textinputlayout_autocomplete_type_edit_editelement = view.findViewById(R.id.textinputlayout_autocomplete_type_edit_editelement);
            textinputlayout_autocomplete_time_edit_editelement = view.findViewById(R.id.textinputlayout_autocomplete_time_edit_editelement);
            element_dropdown_edit_date_day = view.findViewById(R.id.element_dropdown_edit_date_day);
            element_dropdown_edit_date_month = view.findViewById(R.id.element_dropdown_edit_date_month);
            element_dropdown_edit_type_editelement = view.findViewById(R.id.element_dropdown_edit_type_editelement);
            element_dropdown_edit_time_editelement = view.findViewById(R.id.element_dropdown_edit_time_editelement);
            element_dropdown_edit_date_year = view.findViewById(R.id.element_dropdown_edit_date_year);
            editelement_remove_button = view.findViewById(R.id.editelement_remove_button);
            editelement_cancelbutton = view.findViewById(R.id.editelement_cancelbutton);
            editelement_savebutton = view.findViewById(R.id.editelement_savebutton);
            editelement_date_edit_container = view.findViewById(R.id.editelement_date_edit_container);
            editelement_urgency_switch = view.findViewById(R.id.editelement_urgency_switch);
        }

        //retrive element from showelementfragment
        if (true) {
            Bundle b = getArguments();
            if (b != null) {
                elemento = (MainElement) b.getSerializable("element");
            }
        }

        //settings view component with element fields
        if (true) {
            Glide.with(this)
                    .load(elemento.getImage_url())
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .skipMemoryCache(false)
                    .into(image_editelement_editelement); //holder.itemView.getContext()

            Glide.with(this)
                    .load(elemento.getImage_url())
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .skipMemoryCache(false)
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(10, 3)))
                    .into(editelement_background_image); //holder.itemView.getContext()
            editelement_edit_name.setText(capitalizer(elemento.getName()));
            editelement_edit_desc.setText(capitalizer(elemento.getDescription()));
            element_dropdown_edit_type_editelement.setText(elemento.getType(), false);
            adapter_items_time = new ArrayAdapter<String>(getContext(), R.layout.list_item, items_time_add);
            element_dropdown_edit_time_editelement.setAdapter(adapter_items_time);
            element_dropdown_edit_time_editelement.setText(elemento.getTime(), false);
            if (elemento.getTime().equals("ToRelease")) {
                editelement_date_edit_container.setVisibility(View.VISIBLE);
                years_adapter = new ArrayAdapter<String>(getContext(), R.layout.list_item, setYearsList());
                element_dropdown_edit_date_year.setAdapter(years_adapter);
                element_dropdown_edit_date_year.setText(elemento.getFuture_release_date().split("-")[0], false);

                element_dropdown_edit_date_month.setText(elemento.getFuture_release_date().split("-")[1], false);

                element_dropdown_edit_date_day.setText(elemento.getFuture_release_date().split("-")[2], false);
            }
            editelement_urgency_switch.setChecked(elemento.isImportant_to_watch());
        }

        //handling the dropdown functionalities
        if (true) {
            //time dropdown
            element_dropdown_edit_time_editelement.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    textinputlayout_autocomplete_time_edit_editelement.setErrorEnabled(false);

                    //resetting date text and errors
                    if (true) {
                        textinputlayout_autocomplete_date_year_edit_editelement.setEnabled(false);
                        textinputlayout_autocomplete_date_month_edit_editelement.setEnabled(false);
                        textinputlayout_autocomplete_date_day_edit_editelement.setEnabled(false);
                        element_dropdown_edit_date_year.setText("", false);
                        element_dropdown_edit_date_month.setText("", false);
                        element_dropdown_edit_date_day.setText("", false);
                    }

                    if (element_dropdown_edit_time_editelement.getText().toString().equals("ToRelease")) {
                        editelement_date_edit_container.setVisibility(View.VISIBLE);
                    } else {
                        editelement_date_edit_container.setVisibility(View.GONE);
                    }
                }
            });

            //date dropdowns
            if (true) {
                element_dropdown_edit_date_year.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        textinputlayout_autocomplete_date_year_edit_editelement.setErrorEnabled(false);
                        element_dropdown_edit_date_month.setText("", false);
                        element_dropdown_edit_date_day.setText("", false);

                        months_adapter = new ArrayAdapter<String>(getContext(), R.layout.list_item, setMonthsList());
                        element_dropdown_edit_date_month.setAdapter(months_adapter);
                        textinputlayout_autocomplete_date_month_edit_editelement.setEnabled(true);
                    }
                });

                element_dropdown_edit_date_month.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        textinputlayout_autocomplete_date_month_edit_editelement.setErrorEnabled(false);
                        element_dropdown_edit_date_day.setText("", false);

                        days_adapter = new ArrayAdapter<String>(getContext(), R.layout.list_item, setDaysList());
                        element_dropdown_edit_date_day.setAdapter(days_adapter);
                        textinputlayout_autocomplete_date_day_edit_editelement.setEnabled(true);
                    }
                });

                element_dropdown_edit_date_day.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        textinputlayout_autocomplete_date_day_edit_editelement.setErrorEnabled(false);
                    }
                });
            }
        }

        //image changing functionality
        image_editelement_editelement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                launcher.launch(intent);
            }
        });

        editelement_edit_desc.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });

        //handling the cancel, remove and save edits functionalities
        if (true) {
            editelement_remove_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder showelement_remove_dialog = new AlertDialog.Builder(getContext());
                    showelement_remove_dialog.setTitle("ATTENTION!");
                    showelement_remove_dialog.setMessage("Remove the element?");
                    showelement_remove_dialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            StorageReference storageReference = FirebaseStorage.getInstance(URL_storage).getReferenceFromUrl(elemento.getImage_url());
                            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    System.out.println("EDITING ELEMENT -> REMOVING ELEMENT -> SUCCESS");
                                    databaseReference.child("Main").child(elemento.getType()).child(elemento.getTime()).child(elemento.getName()).removeValue();
                                    databaseReference = FirebaseDatabase.getInstance(URL).getReference().child("Extra").child("Logs");
                                    databaseReference.child(String.valueOf(elemento.getElement_date())).child("actor1").setValue(elemento.getName());
                                    databaseReference.child(String.valueOf(elemento.getElement_date())).child("action").setValue("REMOVE FROM");
                                    databaseReference.child(String.valueOf(elemento.getElement_date())).child("actor2").setValue(elemento.getType());
                                    FirebaseDatabase.getInstance(URL).getReference().child("Extra").child("Notifications").child(elemento.getName()).removeValue();
                                    databaseReference = FirebaseDatabase.getInstance(URL).getReference();
                                    dialog.cancel();
                                    BaseFragment fragment = new MainFragment();
                                    Bundle bundle = new Bundle();
                                    fragment.setArguments(bundle);
                                    switch (elemento.getType()) {
                                        case "Anime" :
                                            bundle.putInt("tab", 0);
                                            break;
                                        case "Film" :
                                            bundle.putInt("tab", 1);
                                            break;
                                        case "Series" :
                                            bundle.putInt("tab", 2);
                                            break;
                                        default:
                                            bundle.putInt("tab", 3);
                                            break;
                                    }
                                    getActivity().getSupportFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.fragmentHolder,  fragment)
                                            .commit();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    System.out.println("EDITING ELEMENT -> REMOVING ELEMENT -> ERROR");
                                }
                            });
                        }
                    });

                    showelement_remove_dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.out.println("EDITING ELEMENT -> REMOVING ELEMENT -> ABORTED");
                        }
                    });

                    showelement_remove_dialog.show();
                }
            });

            editelement_cancelbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShowElementFragment showElementFragment = new ShowElementFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("element", elemento);
                    showElementFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentHolder, showElementFragment)
                            .commit();
                }
            });

            editelement_savebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //need to remove slashes from user input text because it would cause the born of a new child inside firebase, causing the app crash
                    String name_verified = editelement_edit_name.getText().toString().replace("\\", "-").replace("/", "-");
                    String desc_verified = editelement_edit_desc.getText().toString().replace("\\", "-").replace("/", "-");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        if (name_verified.isEmpty() || name_verified.chars().allMatch(Character::isWhitespace) || name_verified == null) {
                            textinputlayout_editelement_edit_name.setError("Input required");
                            textinputlayout_editelement_edit_name.setErrorEnabled(true);
                        } else {
                            textinputlayout_editelement_edit_name.setErrorEnabled(false);
                        }
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        if (desc_verified.isEmpty() || desc_verified.chars().allMatch(Character::isWhitespace) || desc_verified == null) {
                            textinputlayout_editelement_edit_desc.setError("Input required");
                            textinputlayout_editelement_edit_desc.setErrorEnabled(true);
                        } else {
                            textinputlayout_editelement_edit_desc.setErrorEnabled(false);
                        }
                    }

                    //only time because I don't let user change type
                    if (element_dropdown_edit_time_editelement.getText().toString().isEmpty() || element_dropdown_edit_time_editelement.getText().toString() == null) {
                        textinputlayout_autocomplete_time_edit_editelement.setError("Input required");
                        textinputlayout_autocomplete_time_edit_editelement.setErrorEnabled(true);
                    } else {
                        textinputlayout_autocomplete_time_edit_editelement.setErrorEnabled(false);
                    }

                    if (!(textinputlayout_editelement_edit_name.isErrorEnabled() || textinputlayout_editelement_edit_desc.isErrorEnabled() ||
                            textinputlayout_autocomplete_time_edit_editelement.isErrorEnabled() || textinputlayout_autocomplete_date_year_edit_editelement.isErrorEnabled() ||
                            textinputlayout_autocomplete_date_month_edit_editelement.isErrorEnabled() || textinputlayout_autocomplete_date_day_edit_editelement.isErrorEnabled())) {

                        //if no error then creating a new mainelement with the updated data except for
                        //image_url because I need to call storage to get new link
                        if (true) {
                            new_element = new MainElement();
                            new_element.setName(name_verified);
                            new_element.setDescription(desc_verified);
                            new_element.setType(element_dropdown_edit_type_editelement.getText().toString());
                            new_element.setTime(element_dropdown_edit_time_editelement.getText().toString());
                            new_element.setImportant_to_watch(editelement_urgency_switch.isChecked());
                            if (element_dropdown_edit_time_editelement.getText().toString().equals("ToRelease")) {
                                new_element.setFuture_release_date(element_dropdown_edit_date_year.getText().toString()+"-"+element_dropdown_edit_date_month.getText().toString()+"-"+element_dropdown_edit_date_day.getText().toString());
                            } else {
                                new_element.setFuture_release_date("---");
                            }
                            new_element.setDone(false);
                            //getting current date
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                new_element.setElement_date(Instant.now().getEpochSecond());
                            }
                        }

                        //time or name changed, so I need to remove the element to change times node or to change its name
                        storageReference = storageReference.getStorage().getReferenceFromUrl(elemento.getImage_url());
                        storageReference.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                System.out.println("EDITING ELEMENT -> SAVE BUTTON -> OLD IMAGE DOWNLOADED SUCCESSFULLY");
                                storageReference = FirebaseStorage.getInstance(URL_storage).getReferenceFromUrl(elemento.getImage_url());
                                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        System.out.println("EDITING ELEMENT -> SAVE BUTTON -> OLD IMAGE REMOVED SUCCESSFULLY");
                                        storageReference = FirebaseStorage.getInstance(URL_storage).getReference("Images/" + "Main/" + new_element.getType() + "/" + new_element.getTime() + "/" + name_verified);
                                        UploadTask update_task;
                                        if (new_image_uploaded) {
                                            update_task = storageReference.putBytes(downsizedImageBytes);
                                        } else {
                                            update_task = storageReference.putBytes(bytes);
                                        }
                                        update_task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                System.out.println("EDITING ELEMENT -> SAVE BUTTON -> NEW IMAGE UPLOADED SUCCESSFULLY");
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        System.out.println("EDITING ELEMENT -> SAVE BUTTON -> GOT NEW IMAGE URI SUCCESSFULLY");
                                                        new_element.setImage_url(uri.toString());
                                                        databaseReference.child("Main").child(elemento.getType()).child(elemento.getTime()).child(elemento.getName()).removeValue();
                                                        databaseReference.child("Main").child(new_element.getType()).child(new_element.getTime()).child(name_verified).child("image_url").setValue(uri.toString());
                                                        databaseReference.child("Main").child(new_element.getType()).child(new_element.getTime()).child(name_verified).child("description").setValue(desc_verified);
                                                        databaseReference.child("Main").child(new_element.getType()).child(new_element.getTime()).child(name_verified).child("type").setValue(new_element.getType());
                                                        databaseReference.child("Main").child(new_element.getType()).child(new_element.getTime()).child(name_verified).child("time").setValue(new_element.getTime());
                                                        databaseReference.child("Main").child(new_element.getType()).child(new_element.getTime()).child(name_verified).child("important_to_watch").setValue(new_element.isImportant_to_watch());
                                                        databaseReference.child("Main").child(new_element.getType()).child(new_element.getTime()).child(name_verified).child("future_release_date").setValue(new_element.getFuture_release_date());
                                                        databaseReference.child("Main").child(new_element.getType()).child(new_element.getTime()).child(name_verified).child("done").setValue(new_element.isDone());
                                                        databaseReference.child("Main").child(new_element.getType()).child(new_element.getTime()).child(name_verified).child("element_date").setValue(new_element.getElement_date());
                                                        databaseReference = FirebaseDatabase.getInstance(URL).getReference();
                                                        new Thread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Glide.get(getContext()).clearDiskCache();
                                                                ShowElementFragment showElementFragment = new ShowElementFragment();
                                                                Bundle bundle = new Bundle();
                                                                bundle.putSerializable("element", new_element);
                                                                showElementFragment.setArguments(bundle);
                                                                getActivity().getSupportFragmentManager()
                                                                        .beginTransaction()
                                                                        .replace(R.id.fragmentHolder, showElementFragment)
                                                                        .commit();
                                                            }
                                                        }).start();
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
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        System.out.println("ERROR: Element images remove fail from firebase");
                                    }
                                });
                            }
                        });
                    }
                }
            });
        }

        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowElementFragment showElementFragment = new ShowElementFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("element", elemento);
                showElementFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentHolder,  showElementFragment)
                        .commit();
            }
        });

        return view;
    }

    public ArrayList<String> setDaysList() {
        ArrayList<String> days = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int current_year = Year.now().getValue();
            int current_month = ZonedDateTime.now(ZoneId.systemDefault()).getMonthValue();
            int actual_day = ZonedDateTime.now(ZoneId.systemDefault()).getDayOfMonth();
            int month_selected = Integer.parseInt(element_dropdown_edit_date_month.getText().toString());
            int year_selected = Integer.parseInt(element_dropdown_edit_date_year.getText().toString());
            if (month_selected == 1 || month_selected == 3 ||
                    month_selected == 5 || month_selected == 7 ||
                    month_selected == 8 || month_selected == 10 ||
                    month_selected == 12) {
                if ( year_selected == current_year && month_selected == current_month ) {
                    for (int i = (actual_day+1); i <= 31; i++) {
                        days.add(String.valueOf(i));
                    }
                } else {
                    for (int i = 1; i <= 31; i++) {
                        days.add(String.valueOf(i));
                    }
                }
            } else if (month_selected == 4 || month_selected == 6 ||
                    month_selected == 7 || month_selected == 11) {
                if ( year_selected == current_year && month_selected == current_month ) {
                    for (int i = (actual_day+1); i <= 30; i++) {
                        days.add(String.valueOf(i));
                    }
                } else {
                    for (int i = 1; i <= 30; i++) {
                        days.add(String.valueOf(i));
                    }
                }
            } else if (month_selected == 2) {
                if ( year_selected == current_year && month_selected == current_month ) {
                    for (int i = (actual_day+1); i <= 29; i++) {
                        days.add(String.valueOf(i));
                    }
                } else {
                    for (int i = 1; i <= 29; i++) {
                        days.add(String.valueOf(i));
                    }
                }
            }
        }
        return days;
    }

    private ArrayList<String> setMonthsList() {
        ArrayList<String> months = new ArrayList<>();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int current_year = Year.now().getValue();
            List<Integer> months_temp = new ArrayList<Integer>(Arrays.asList(new Integer[] {1,2,3,4,5,6,7,8,9,10,11,12}));
            if (element_dropdown_edit_date_year.getText().equals(String.valueOf(current_year))) {
                //if current year selected then show months from current to end, so user can't choose month passed
                for (Integer i : months_temp.subList(Calendar.getInstance().get(Calendar.MONTH), 12)) {
                    months.add(String.valueOf(i));
                }
            } else {
                //if else all months should be avaible
                for (Integer i : months_temp) {
                    months.add(String.valueOf(i));
                }
            }
        }
        return months;
    }

    private ArrayList<String> setYearsList() {
        ArrayList<String> years = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int current_year = Year.now().getValue();
            for (int i = current_year; i<=2030; i++) {
                years.add(String.valueOf(i));
            }
        }
        return years;
    }

    public String capitalizer(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
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