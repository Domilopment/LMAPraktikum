package de.dmmm.lmapraktikum.settings;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreferenceCompat;
import java.util.ArrayList;
import de.dmmm.lmapraktikum.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    PreferenceCategory c;
    ArrayList<Preference> locationManagerPref = new ArrayList<>();
    ArrayList<Preference> FusedLocationProviderPref = new ArrayList<>();

    public SettingsFragment() {
        // Required empty public constructo
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        c = findPreference("gps_category");

        SwitchPreferenceCompat switchGPS = new SwitchPreferenceCompat(getPreferenceScreen().getContext());
        switchGPS.setTitle("GPS");
        switchGPS.setSummary("GPS zur Lokalisierung verwenden.");
        switchGPS.setKey("switch_gps");
        locationManagerPref.add(switchGPS);

        SwitchPreferenceCompat switchNetwork = new SwitchPreferenceCompat(getPreferenceScreen().getContext());
        switchNetwork.setTitle("Netzwerklokalisierung");
        switchNetwork.setSummary("Kabellose Netzwerke zur Lokalisierung verwenden.");
        switchNetwork.setKey("switch_network");
        locationManagerPref.add(switchNetwork);

        SeekBarPreference seekBarGPS = new SeekBarPreference(getPreferenceScreen().getContext());
        seekBarGPS.setKey("seekbar_gps");
        seekBarGPS.setMax(60000);
        seekBarGPS.setSummary("Sample-Frequenz des GPS in ms");
        seekBarGPS.setTitle("GPS-Sampling-Frequenz");
        seekBarGPS.setShowSeekBarValue(true);
        seekBarGPS.setMin(1000);
        locationManagerPref.add(seekBarGPS);

        SwitchPreferenceCompat switchFused = new SwitchPreferenceCompat(getPreferenceScreen().getContext());
        switchFused.setTitle("Enable");
        switchFused.setSummary("Fused Location Provider activieren oder deaktivieren");
        switchFused.setKey("switch_fuesed");
        FusedLocationProviderPref.add(switchFused);

        ListPreference listPreferenceAccuracy = new ListPreference(getPreferenceScreen().getContext());
        listPreferenceAccuracy.setEntries(R.array.Fuesed_Priority);
        listPreferenceAccuracy.setEntryValues(R.array.Fuesed_Priority_values);
        listPreferenceAccuracy.setKey("accuracy_switch");
        listPreferenceAccuracy.setTitle("Select Accuracy");
        listPreferenceAccuracy.setSummary("Auswahl der Accuracy für Fused Location Provider");
        listPreferenceAccuracy.setDefaultValue("100");
        FusedLocationProviderPref.add(listPreferenceAccuracy);

        SeekBarPreference seekBarFuesed = new SeekBarPreference(getPreferenceScreen().getContext());
        seekBarFuesed.setKey("seekbar_fuesed");
        seekBarFuesed.setMax(60000);
        seekBarFuesed.setSummary("Sample-Frequenz des Fuesed in ms");
        seekBarFuesed.setTitle("Fuesed-Sampling-Frequenz");
        seekBarFuesed.setShowSeekBarValue(true);
        seekBarFuesed.setMin(1000);
        FusedLocationProviderPref.add(seekBarFuesed);

        switchSettings(getPreferenceManager().getSharedPreferences().getString("receiver_switch", ""));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch(key)
        {
            case "receiver_switch":
                switchSettings(sharedPreferences.getString("receiver_switch", ""));
                break;
        }
    }

    public void switchSettings(String name){
        if (name.equals("LocationManager")) {
            c.setTitle(name);
            c.removeAll();
            for(Preference pref: locationManagerPref){
                c.addPreference(pref);
            }
        } else if (name.equals("FusedLocationProvider")) {
            c.setTitle(name);
            c.removeAll();
            for(Preference pref: FusedLocationProviderPref){
                c.addPreference(pref);
            }
        }
    }
}