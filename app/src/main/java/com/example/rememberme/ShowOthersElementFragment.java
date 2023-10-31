package com.example.rememberme;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ShowOthersElementFragment extends BaseFragment {

    private OtherElement element;
    private ImageButton back_arrow, edit_others_element_cancel_button;
    private TextView showelement_others_name, desc_showelement_others;
    private ChipGroup showelement_others_chipgroup;
    private boolean desc_expanded = false;
    private String URL = "https://rememberapp-13f74-default-rtdb.europe-west1.firebasedatabase.app/";

    public ShowOthersElementFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_element_others, container, false);

        //findbyviews
        if (true) {
            back_arrow = view.findViewById(R.id.back_arrow);
            showelement_others_name = view.findViewById(R.id.showelement_others_name);
            desc_showelement_others = view.findViewById(R.id.desc_showelement_others);
            edit_others_element_cancel_button = view.findViewById(R.id.edit_others_element_cancel_button);
            showelement_others_chipgroup = view.findViewById(R.id.showelement_others_chipgroup);
        }

        //retrive element from mainfragment
        if (true) {
            //showelement_background_image.setTransitionName("example_transition");
            Bundle b = getArguments();
            if (b != null) {
                element = (OtherElement) b.getSerializable("element");
            }
        }

        //settings view component with element fields
        if (true) {
            showelement_others_name.setText(capitalizer(element.getName()));
            desc_showelement_others.setText(capitalizer(element.getDesc()));
            String[] tags = element.getTags().split("\\.");
            showelement_others_chipgroup.removeAllViews();
            for (String tag : tags) {
                createChip(tag);
            }
        }

        //handling desc behaviour
        if (true) {
            desc_showelement_others.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    desc_showelement_others.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int height = desc_showelement_others.getHeight();
                    desc_showelement_others.requestLayout();
                    if (height > 400) {
                        desc_showelement_others.getLayoutParams().height = 450;
                    } else {
                        desc_showelement_others.requestLayout();
                        desc_showelement_others.getLayoutParams().height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                    }
                }
            });
            desc_showelement_others.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    desc_showelement_others.requestLayout();
                    if (desc_expanded) {
                        desc_showelement_others.getLayoutParams().height = 680;
                        desc_expanded = false;
                    } else {
                        desc_showelement_others.getLayoutParams().height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                        desc_expanded = true;
                    }
                }
            });
        }

        //TODO PER DESCRIZIONE AGGIUNGERE CHE SE DESC ESPANSA ALLORA BUTTONS CONTAINER è CONSTRAINT BOTTOBOT OF PARENT ELSE TOPTOBOT OF TAGS
        //TODO controllare se tag sono vuoti e nel caso triggerare errore + fare stessa cosa per name e desc

        edit_others_element_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditOthersElementFragment editOthersElementFragment = new EditOthersElementFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("element", element);
                editOthersElementFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentHolder,  editOthersElementFragment)
                        .commit();
            }
        });

        back_arrow.setOnClickListener(new View.OnClickListener() {
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

    public void createChip(String s) {
        final Chip chip = (Chip) this.getLayoutInflater().inflate(R.layout.chip_element_others, null, false);
        chip.setTypeface(Typeface.create(ResourcesCompat.getFont(getContext(),R.font.futura_book),Typeface.NORMAL));
        chip.setTextSize(14);
        chip.setText(s);
        chip.setClickable(false);
        chip.setCheckable(false);
        chip.setEnabled(false);
        ChipDrawable style = ChipDrawable.createFromAttributes(getContext(), null, 0, R.style.show_tag_chip_Style);
        chip.setChipDrawable(style);
        showelement_others_chipgroup.addView(chip);
    }

    public String capitalizer(String string) {
        if (string.length() > 0) {
            return string.substring(0, 1).toUpperCase() + string.substring(1);
        }
        return "";
    }
}