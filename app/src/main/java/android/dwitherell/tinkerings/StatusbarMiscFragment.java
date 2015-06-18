package android.dwitherell.tinkerings;
/**
 * Created by devonwitherell on 12/31/2014.
 */

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.support.annotation.NonNull;

public class StatusbarMiscFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String STATUSBAR_HIDE_ALARM = "statusbar_hide_alarm";
    private static final String STATUSBAR_HIDE_BTOOTH = "statusbar_hide_btooth";
    private static final String STATUSBAR_HIDE_IME = "statusbar_hide_ime";
    private static final String STATUSBAR_HIDE_NFC = "statusbar_hide_nfc";
    private static final String STATUSBAR_HIDE_NOSIM = "statusbar_hide_nosim";
    private static final String STATUSBAR_HIDE_VIB = "statusbar_hide_vib";
    private static final String STATUSBAR_HIDE_WIFICALL = "statusbar_hide_wificall";

    Context context;

    private CheckBoxPreference mHideAlarm;
    private CheckBoxPreference mHideBTooth;
    private CheckBoxPreference mHideIME;
    private CheckBoxPreference mHideNFC;
    private CheckBoxPreference mHideSim;
    private CheckBoxPreference mHideVib;
    private CheckBoxPreference mHideWifiCall;

    public StatusbarMiscFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Load the preferences from an XML resource */
        addPreferencesFromResource(R.xml.statusbar_misc_fragment);
        this.context = getActivity().getApplicationContext();
        PreferenceScreen localPreferenceScreen = getPreferenceScreen();
        ContentResolver localContentResolver = getActivity().getContentResolver();

        /* Set up checkbox stuff */
        this.mHideSim = ((CheckBoxPreference) localPreferenceScreen.findPreference(STATUSBAR_HIDE_NOSIM));
        if (Settings.Global.getInt(localContentResolver, STATUSBAR_HIDE_NOSIM, 0) != 0) {
            mHideSim.setChecked(true);
        } else {
            mHideSim.setChecked(false);
        }

        this.mHideAlarm = ((CheckBoxPreference) localPreferenceScreen.findPreference(STATUSBAR_HIDE_ALARM));
        if (Settings.Global.getInt(localContentResolver, STATUSBAR_HIDE_ALARM, 0) != 0) {
            mHideAlarm.setChecked(true);
        } else {
            mHideAlarm.setChecked(false);
        }

        this.mHideIME = ((CheckBoxPreference) localPreferenceScreen.findPreference(STATUSBAR_HIDE_IME));
        if (Settings.Global.getInt(localContentResolver, STATUSBAR_HIDE_IME, 0) != 0) {
            mHideIME.setChecked(true);
        } else {
            mHideIME.setChecked(false);
        }

        this.mHideWifiCall = ((CheckBoxPreference) localPreferenceScreen.findPreference(STATUSBAR_HIDE_WIFICALL));
        if (Settings.Global.getInt(localContentResolver, STATUSBAR_HIDE_WIFICALL, 0) != 0) {
            mHideWifiCall.setChecked(true);
        } else {
            mHideWifiCall.setChecked(false);
        }

        this.mHideNFC = ((CheckBoxPreference) localPreferenceScreen.findPreference(STATUSBAR_HIDE_NFC));
        if (Settings.Global.getInt(localContentResolver, STATUSBAR_HIDE_NFC, 0) != 0) {
            mHideNFC.setChecked(true);
        } else {
            mHideNFC.setChecked(false);
        }

        this.mHideBTooth = ((CheckBoxPreference) localPreferenceScreen.findPreference(STATUSBAR_HIDE_BTOOTH));
        if (Settings.Global.getInt(localContentResolver, STATUSBAR_HIDE_BTOOTH, 0) != 0) {
            mHideBTooth.setChecked(true);
        } else {
            mHideBTooth.setChecked(false);
        }

        this.mHideVib = ((CheckBoxPreference) localPreferenceScreen.findPreference(STATUSBAR_HIDE_VIB));
        if (Settings.Global.getInt(localContentResolver, STATUSBAR_HIDE_VIB, 0) != 0) {
            mHideVib.setChecked(true);
        } else {
            mHideVib.setChecked(false);
        }

        this.mHideVib = ((CheckBoxPreference) localPreferenceScreen.findPreference(STATUSBAR_HIDE_VIB));
        if (Settings.Global.getInt(localContentResolver, STATUSBAR_HIDE_VIB, 0) != 0) {
            mHideVib.setChecked(true);
        } else {
            mHideVib.setChecked(false);
        }

    }

    @Override
    public boolean onPreferenceChange(Preference paramPreference, Object paramObject) {
        return true;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, @NonNull Preference paramPreference) {
        int i;
        ContentResolver localContentResolver = getActivity().getContentResolver();

        /* Deal with checkbox stuff first */
        if (paramPreference == this.mHideAlarm) {
            if (this.mHideAlarm.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, STATUSBAR_HIDE_ALARM, i);
            return true;
        }

        if (paramPreference == this.mHideBTooth) {
            if (this.mHideBTooth.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, STATUSBAR_HIDE_BTOOTH, i);
            return true;
        }

        if (paramPreference == this.mHideVib) {
            if (this.mHideVib.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, STATUSBAR_HIDE_VIB, i);
            return true;
        }

        if (paramPreference == this.mHideIME) {
            if (this.mHideIME.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, STATUSBAR_HIDE_IME, i);
            return true;
        }

        if (paramPreference == this.mHideSim) {
            if (this.mHideSim.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, STATUSBAR_HIDE_NOSIM, i);
            return true;
        }

        if (paramPreference == this.mHideWifiCall) {
            if (this.mHideWifiCall.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, STATUSBAR_HIDE_WIFICALL, i);
            return true;
        }

        if (paramPreference == this.mHideNFC) {
            if (this.mHideNFC.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, STATUSBAR_HIDE_NFC, i);
            return true;
        }

        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}