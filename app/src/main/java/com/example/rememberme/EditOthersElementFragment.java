package com.example.rememberme;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class EditOthersElementFragment extends BaseFragment {

    private TextInputLayout textinputlayout_edit_others_element_name, textinputlayout_edit_others_element_desc, textinputlayout_edit_others_element_tags;
    private TextInputEditText edit_others_element_name, edit_others_element_desc, edit_others_element_tags;
    private ImageButton back_arrow, edit_others_element_tag_add_button, edit_others_element_remove_button, edit_others_element_cancel_button, edit_others_element_save_button;
    private ChipGroup edit_others_element_chipgroup;
    private OtherElement element;
    private String URL = "https://rememberapp-13f74-default-rtdb.europe-west1.firebasedatabase.app/";
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance(URL).getReference();
    private boolean desc_expanded = false;

    public EditOthersElementFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_element_others, container, false);

        //findbyviews
        if (true) {
            back_arrow = view.findViewById(R.id.back_arrow);
            textinputlayout_edit_others_element_name = view.findViewById(R.id.textinputlayout_edit_others_element_name);
            textinputlayout_edit_others_element_desc = view.findViewById(R.id.textinputlayout_edit_others_element_desc);
            textinputlayout_edit_others_element_tags = view.findViewById(R.id.textinputlayout_edit_others_element_tags);
            edit_others_element_name = view.findViewById(R.id.edit_others_element_name);
            edit_others_element_desc = view.findViewById(R.id.edit_others_element_desc);
            edit_others_element_tags = view.findViewById(R.id.edit_others_element_tags);
            edit_others_element_tag_add_button = view.findViewById(R.id.edit_others_element_tag_add_button);
            edit_others_element_remove_button = view.findViewById(R.id.edit_others_element_remove_button);
            edit_others_element_cancel_button = view.findViewById(R.id.edit_others_element_cancel_button);
            edit_others_element_save_button = view.findViewById(R.id.edit_others_element_save_button);
            edit_others_element_chipgroup = view.findViewById(R.id.edit_others_element_chipgroup);
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
            edit_others_element_name.setText(capitalizer(element.getName()));
            edit_others_element_desc.setText(capitalizer(element.getDesc()));
            String[] tags = element.getTags().split("\\.");
            edit_others_element_chipgroup.removeAllViews();
            for (String tag : tags) {
                createChip(tag);
            }
        }

        //handling text change errors
        if (true) {
            edit_others_element_name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.length() <= 0) {
                        textinputlayout_edit_others_element_name.setErrorEnabled(true);
                    } else {
                        textinputlayout_edit_others_element_name.setErrorEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            edit_others_element_chipgroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
                @Override
                public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                    if (edit_others_element_chipgroup.getChildCount() <= 0) {
                        textinputlayout_edit_others_element_tags.setErrorEnabled(true);
                    } else {
                        textinputlayout_edit_others_element_tags.setErrorEnabled(false);
                    }
                }
            });
        }

        edit_others_element_tag_add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newTag = edit_others_element_tags.getText().toString().replace("\\", "-").replace("/", "-").toLowerCase();
                setChips(newTag);
                edit_others_element_tags.setText("");
            }
        });

        //handling the cancel, remove and save edits functionalities
        if (true) {
            edit_others_element_remove_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    android.app.AlertDialog.Builder edit_element_dialog = new android.app.AlertDialog.Builder(getContext());
                    edit_element_dialog.setTitle("ATTENTION!");
                    edit_element_dialog.setMessage("Remove the element?");
                    edit_element_dialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseReference.child("Others").child(element.getName()).removeValue();
                        }
                    });

                    edit_element_dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.out.println("EDITING ELEMENT -> REMOVING ELEMENT -> ABORTED");
                        }
                    });

                    edit_element_dialog.show();
                }
            });

            edit_others_element_cancel_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShowOthersElementFragment showOthersElementFragment = new ShowOthersElementFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("element", element);
                    showOthersElementFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentHolder, showOthersElementFragment)
                            .commit();
                }
            });

            edit_others_element_save_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!(textinputlayout_edit_others_element_name.isErrorEnabled()) && !(textinputlayout_edit_others_element_tags.isErrorEnabled())) {
                        String name_verified = edit_others_element_name.getText().toString().replace("\\", "-").replace("/", "-");
                        String desc_verified = edit_others_element_desc.getText().toString().replace("\\", "-").replace("/", "-");
                        String tags_verified = getTags();

                        OtherElement new_others_element = new OtherElement();
                        new_others_element.setName(name_verified);
                        new_others_element.setDesc(desc_verified);
                        new_others_element.setTags(tags_verified);

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance(URL).getReference().child("Others");
                        databaseReference.child(element.getName()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                databaseReference.child(new_others_element.getName()).child("desc").setValue(new_others_element.getDesc());
                                databaseReference.child(new_others_element.getName()).child("tags").setValue(new_others_element.getTags());
                                ShowOthersElementFragment showOthersElementFragment = new ShowOthersElementFragment();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("element", new_others_element);
                                showOthersElementFragment.setArguments(bundle);
                                getActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.fragmentHolder,  showOthersElementFragment)
                                        .commit();
                            }
                        });
                    }
                }
            });
        }

        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowOthersElementFragment showOthersElementFragment = new ShowOthersElementFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("element", element);
                showOthersElementFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentHolder,  showOthersElementFragment)
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
        ChipDrawable style = ChipDrawable.createFromAttributes(getContext(), null, 0, R.style.edit_tag_chip_Style);
        chip.setChipDrawable(style);
        edit_others_element_chipgroup.addView(chip);
    }

    public void setChips(String s) {
        final Chip chip = (Chip) this.getLayoutInflater().inflate(R.layout.chip_element_others, null, false);
        chip.setTypeface(Typeface.create(ResourcesCompat.getFont(getContext(),R.font.futura_book),Typeface.NORMAL));
        chip.setTextSize(14);
        chip.setText(s);
        chip.setClickable(false);
        chip.setCheckable(false);
        chip.setEnabled(false);
        ChipDrawable style = ChipDrawable.createFromAttributes(getContext(), null, 0, R.style.edit_tag_chip_Style);
        chip.setChipDrawable(style);

        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_others_element_chipgroup.removeView(chip);
                if (edit_others_element_chipgroup.getChildCount() <= 0) {
                    textinputlayout_edit_others_element_tags.setErrorEnabled(true);
                } else {
                    textinputlayout_edit_others_element_tags.setErrorEnabled(false);
                }
            }
        });
        textinputlayout_edit_others_element_tags.setErrorEnabled(false);
        edit_others_element_chipgroup.addView(chip);
    }

    private String getTags() {
        String tags = "";
        int i=0;
        while (i < edit_others_element_chipgroup.getChildCount()) {
            Chip chip = (Chip) edit_others_element_chipgroup.getChildAt(i);
            tags+=chip.getText().toString() + ".";
            i++;
        }
        return tags;
    }

    public String capitalizer(String string) {
        if (string.length() > 0) {
            return string.substring(0, 1).toUpperCase() + string.substring(1);
        }
        return "";
    }
}