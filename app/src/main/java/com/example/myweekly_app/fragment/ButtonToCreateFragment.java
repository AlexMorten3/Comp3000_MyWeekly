package com.example.myweekly_app.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myweekly_app.GeneratorActivity;
import com.example.myweekly_app.R;
import com.google.android.material.button.MaterialButton;

public class ButtonToCreateFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ButtonToCreateFragment() {
        // Empty public constructor
    }

    public static ButtonToCreateFragment newInstance(String param1, String param2) {
        ButtonToCreateFragment fragment = new ButtonToCreateFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_button_to_create, container, false);

        MaterialButton goToCreatorButton = rootView.findViewById(R.id.goToCreatorButton);

        goToCreatorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), GeneratorActivity.class);
                startActivity(intent);

            }
        });

        return rootView;
    }
}