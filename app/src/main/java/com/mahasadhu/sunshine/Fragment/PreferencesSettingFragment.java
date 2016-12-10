package com.mahasadhu.sunshine.Fragment;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.mahasadhu.sunshine.R;

/**
 * Created by Mahasadhu on 12/5/2016.
 */

public class PreferencesSettingFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_sunshine);
    }
}
