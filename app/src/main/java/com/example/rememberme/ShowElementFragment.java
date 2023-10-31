package com.example.rememberme;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
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
import java.text.DateFormatSymbols;
import java.time.Instant;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class ShowElementFragment extends BaseFragment {

    private MainElement elemento;
    private ImageView showelement_image, showelement_background_image;
    private TextView name_showelement, desc_showelement, showelement_details_type_text, showelement_urgency_text, showelement_details_time_text, showelement_details_date_text;
    private ImageButton back_arrow, showelement_edit_button;
    private boolean desc_expanded;
    private String from;
    private ConstraintLayout showelement_desc_container, showelement_time_date_labels_container;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_show_element, parent, false);

        //findbyviews
        if (true) {
            showelement_image = view.findViewById(R.id.image_showelement_edit);
            back_arrow = view.findViewById(R.id.back_arrow);
            showelement_background_image = view.findViewById(R.id.showelement_background_image);
            showelement_details_type_text = view.findViewById(R.id.showelement_details_type_text);
            showelement_details_time_text = view.findViewById(R.id.showelement_details_time_text);
            name_showelement = view.findViewById(R.id.name_showelement);
            desc_showelement = view.findViewById(R.id.desc_showelement);
            showelement_edit_button = view.findViewById(R.id.showelement_edit_button);
            showelement_desc_container = view.findViewById(R.id.showelement_desc_container);
            showelement_time_date_labels_container = view.findViewById(R.id.showelement_time_date_labels_container);
            showelement_details_date_text = view.findViewById(R.id.showelement_details_date_text);
            showelement_urgency_text = view.findViewById(R.id.showelement_urgency_text);
        }

        //retrive element from mainfragment
        if (true) {
            //showelement_background_image.setTransitionName("example_transition");
            Bundle b = getArguments();
            if (b != null) {
                elemento = (MainElement) b.getSerializable("element");
                from = b.getString("from");
            }
        }

        //setting each component of the view with element fields
        if (true) {
            //settings images with glide
            if (true) {
                Glide.with(this)
                        .load(elemento.getImage_url())
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .skipMemoryCache(false)
                        .apply(RequestOptions.bitmapTransform(new BlurTransformation(10, 3)))
                        .into(showelement_background_image);

                Glide.with(this)
                        .load(elemento.getImage_url())
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .skipMemoryCache(false)
                        .into(showelement_image); //holder.itemView.getContext()

        /*Fade fade = new Fade();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);*/
            }

            name_showelement.setText(capitalizer(elemento.getName()));
            desc_showelement.setText(capitalizer(elemento.getDescription()));
            showelement_details_type_text.setText(capitalizer(elemento.getType()));
            if (elemento.isImportant_to_watch()) {
                showelement_urgency_text.setText("Yes");
            } else {
                showelement_urgency_text.setText("No");
            }
            showelement_details_time_text.setText(capitalizer(elemento.getTime()));
            if (elemento.getTime().equals("ToRelease")) {
                showelement_time_date_labels_container.setVisibility(View.VISIBLE);
                showelement_details_date_text.setText(elemento.getFuture_release_date());
            }
        }

        //handling desc behaviour
        if (true) {
            desc_showelement.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    desc_showelement.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int height = desc_showelement.getHeight();
                    showelement_desc_container.requestLayout();
                    if (height > 400) {
                        showelement_desc_container.getLayoutParams().height = 450;
                    } else {
                        System.out.println("c");
                        showelement_desc_container.requestLayout();
                        showelement_desc_container.getLayoutParams().height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                    }
                }
            });
            desc_showelement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showelement_desc_container.requestLayout();
                    if (desc_expanded) {
                        showelement_desc_container.getLayoutParams().height = 680;
                        desc_expanded = false;
                    } else {
                        showelement_desc_container.getLayoutParams().height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                        desc_expanded = true;
                    }
                }
            });
        }

        //handling edit function
        showelement_edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditElementFragment editElementFragment = new EditElementFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("element", elemento);
                editElementFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentHolder,  editElementFragment)
                        .commit();
            }
        });

        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseFragment fragment;
                if (from != null) {
                    if (from.equals("search")) {
                        //return to search fragment
                        fragment = new SearchingFragment();
                    } else if (from.equals("upcom")) {
                        //return to search upcomings
                        fragment = new UpcomingFragment();
                    } else {
                        //return to notifications fragment
                        fragment = new NotificationFragment();
                    }
                } else {
                    fragment = new MainFragment();
                }
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentHolder,  fragment)
                        .commit();
            }
        });

        return view;
    }

    public String capitalizer(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }
}
