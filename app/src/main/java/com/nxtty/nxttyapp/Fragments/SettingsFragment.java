package com.nxtty.nxttyapp.Fragments;

import android.app.Fragment;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nxtty.nxttyapp.Adapters.SettingListAdapter;
import com.nxtty.nxttyapp.Constants;
import com.nxtty.nxttyapp.R;

/**
 * Created by Raymond on 02/03/2015.
 */
public class SettingsFragment extends Fragment {

    private ViewGroup profileSettings;
    private RecyclerView settingsListView;
    private Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View settingsView = inflater.inflate(R.layout.settings_fragment_layout,null);

        toolbar = (Toolbar)settingsView.findViewById(R.id.settings_toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_left);

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        toolbar.setTitle(getResources().getString(R.string.action_settings));

        settingsListView = (RecyclerView)settingsView.findViewById(R.id.settings_list_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        settingsListView.setLayoutManager(linearLayoutManager);

        SettingListAdapter settingListAdapter = new SettingListAdapter(getActivity());

        settingsListView.setAdapter(settingListAdapter);


        return settingsView;
    }

}
