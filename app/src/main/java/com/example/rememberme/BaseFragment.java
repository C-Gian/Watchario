package com.example.rememberme;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.concurrent.TimeUnit;

public class BaseFragment extends Fragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*// we don't need to change transition animations when method calls again!
        if (savedInstanceState != null) return;

        // here we pause enter transition animation to load view completely
        postponeEnterTransition();

        // here we start transition using a handler
        // to make sure transition animation won't be lagged
        view.post(() -> postponeEnterTransition(0, TimeUnit.MILLISECONDS));*/
    }

}
