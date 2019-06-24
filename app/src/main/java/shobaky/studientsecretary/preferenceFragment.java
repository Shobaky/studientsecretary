package shobaky.studientsecretary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import android.provider.MediaStore;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;

import static android.app.Activity.RESULT_OK;


public class preferenceFragment extends PreferenceFragmentCompat {
    public static final String ringPref = "RING_PREFERENCE";
    public static final String imagePref = "IMAGE_PREFERENCE";
    public static final String namePref = "NAME_PREFERENCE";
    public static final String notesPref = "NOTES_PREFERENCE";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.mypref);
        final SharedPreferences pref = getActivity().getSharedPreferences(Settings.settingsPref,Context.MODE_PRIVATE);
        ListPreference ringtones = new ListPreference(getActivity());
        ringtones.setViewId(R.layout.pref_dialog);
        Bundle ringBundle = getArguments();
        String[] tones = ringBundle.getStringArray("ringtones");

        if(tones!= null){
            ringtones.setEntries(tones);
            ringtones.setEntryValues(tones);
        }

        PreferenceCategory reminders = (PreferenceCategory) findPreference("Reminders");
        ringtones.setTitle("Ringtone");
        ringtones.setKey("ringtone");
        ringtones.setDialogTitle("Choose a ringtone");
        ringtones.setIcon(R.drawable.ic_music_note_black_24dp);
        ringtones.setSummary(pref.getString(ringPref,""));
        ringtones.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {

                pref.edit().putString(ringPref,(String)o).apply();
                return true;
            }
        });
        Preference imagePref = findPreference("image");
        EditTextPreference name =(EditTextPreference) findPreference("name pref");
        name.setSummary(pref.getString(preferenceFragment.namePref,""));
        pref.edit().putString(preferenceFragment.namePref,name.getText()).apply();
        imagePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent fetchImage = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(fetchImage,3);
                return true;
            }
        });
        reminders.addPreference(ringtones);
        final SwitchPreference notesRemindPref = (SwitchPreference)findPreference("Notes reminder");
        notesRemindPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                pref.edit().putBoolean(notesPref,notesRemindPref.getDisableDependentsState()).apply();
                return true;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 3 && resultCode == RESULT_OK){
             SharedPreferences imgPref = getActivity().getSharedPreferences(Settings.settingsPref,Context.MODE_PRIVATE);
             imgPref.edit().putString(preferenceFragment.imagePref,data.getDataString()).apply();
        }
    }
}

