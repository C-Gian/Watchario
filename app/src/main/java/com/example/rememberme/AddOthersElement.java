package com.example.rememberme;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddOthersElement extends Fragment {

    private TextInputLayout textinputlayout_edit_name, textinputlayout_edit_desc, textinputlayout_add_tag;
    private TextInputEditText element_name_add, element_desc_add, element_tag_add;
    private ImageButton add_tag_button, add_others_save_button, back_arrow;
    private ChipGroup add_chipgroup;
    private ArrayList<String> tags = new ArrayList<>();
    private String tags_as_string = "";
    private String URL = "https://rememberapp-13f74-default-rtdb.europe-west1.firebasedatabase.app/";
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance(URL).getReference();


    public AddOthersElement() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_others_element, container, false);

        //findviews
        if (true) {
            textinputlayout_edit_name = view.findViewById(R.id.textinputlayout_edit_name);
            textinputlayout_edit_desc = view.findViewById(R.id.textinputlayout_edit_desc);
            textinputlayout_add_tag  = view.findViewById(R.id.textinputlayout_add_tag);
            element_name_add = view.findViewById(R.id.element_name_add);
            element_desc_add = view.findViewById(R.id.element_desc_add);
            element_tag_add = view.findViewById(R.id.element_tag_add);
            add_tag_button = view.findViewById(R.id.add_tag_button);
            add_others_save_button = view.findViewById(R.id.add_others_save_button);
            add_chipgroup = view.findViewById(R.id.add_chipgroup);
            back_arrow = view.findViewById(R.id.back_arrow);
        }

        //handling
        if (true) {
            element_name_add.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.length() != 0) {
                        textinputlayout_edit_name.setErrorEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            add_chipgroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
                @Override
                public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                    if (add_chipgroup.getChildCount() == 0) {
                        textinputlayout_add_tag.setErrorEnabled(true);
                    } else {
                        textinputlayout_add_tag.setErrorEnabled(false);
                    }
                }
            });
        }

        add_tag_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tag_verified = element_tag_add.getText().toString().replace("\\", "-").replace("/", "-");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (!(tag_verified.isEmpty()) && !(tag_verified.chars().allMatch(Character::isWhitespace))) {
                        setChips(tag_verified);
                        element_tag_add.setText("");
                        textinputlayout_add_tag.setErrorEnabled(false);
                    }
                }
            }
        });

        add_others_save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name_verified = element_name_add.getText().toString().replace("\\", "-").replace("/", "-");
                String desc_verified = element_desc_add.getText().toString().replace("\\", "-").replace("/", "-");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (!(name_verified.isEmpty()) && !(name_verified.chars().allMatch(Character::isWhitespace))) {
                        if (add_chipgroup.getChildCount() > 0) {
                            for (String s : tags) {
                                tags_as_string += s + ".";
                            }
                            //setting server reference
                            databaseReference = databaseReference.child("Others");
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    dataSnapshot.getRef().child(name_verified).child("desc").setValue(desc_verified);
                                    dataSnapshot.getRef().child(name_verified).child("tags").setValue(tags_as_string);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            getActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragmentHolder,  new OthersFragment())
                                    .commit();
                        } else {
                            textinputlayout_add_tag.setErrorEnabled(true);
                            textinputlayout_add_tag.setError("tags can't be empty");
                        }
                    } else {
                        textinputlayout_edit_name.setErrorEnabled(true);
                        textinputlayout_edit_name.setError("name can't be empty");
                    }
                }
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

    public void setChips(String s) {
        final Chip chip = (Chip) this.getLayoutInflater().inflate(R.layout.chip_element_others, null, false);
        chip.setTypeface(Typeface.create(ResourcesCompat.getFont(getContext(),R.font.futura_book),Typeface.NORMAL));
        chip.setTextSize(14);
        chip.setText(s.toLowerCase());
        chip.setClickable(false);
        chip.setCheckable(false);
        chip.setEnabled(false);
        ChipDrawable style = ChipDrawable.createFromAttributes(getContext(), null, 0, R.style.edit_tag_chip_Style);
        chip.setChipDrawable(style);
        tags.add(s);

        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_chipgroup.removeView(chip);
                tags.remove(s);
            }
        });

        add_chipgroup.addView(chip);
    }
}