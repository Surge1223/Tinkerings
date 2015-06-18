package android.dwitherell.tinkerings;
/**
 * Created by devonwitherell on 12/31/2014.
 */
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.dwitherell.tinkerings.utils.ColorPickerPreference;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;

public class SignalFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String HIDE_SIGNAL_ICON = "hide_signal_icon";
    private static final String SHOW_DBM_TEXT = "show_dbm_text";
    private static final String SHOW_STATUS_DBM = "show_status_dbm";
    private static final String STATUSBAR_DATA_ICON = "statusbar_data_icon";
    private static final String STATUSBAR_DATA_STAY = "statusbar_data_stay";
    private static final String DBM_AUTO_COLOR = "dbm_auto_color";
    private static final String DBM_AUTO_COLOR_0 = "dbm_auto_color_0";
    private static final String DBM_AUTO_COLOR_1 = "dbm_auto_color_1";
    private static final String DBM_AUTO_COLOR_2 = "dbm_auto_color_2";
    private static final String DBM_AUTO_COLOR_3 = "dbm_auto_color_3";
    private static final String DBM_AUTO_COLOR_4 = "dbm_auto_color_4";
    private static final String DBM_COLOR = "dbm_color";

    private static final String STORE_HIDE_SIGNAL_ICON = "store_hide_signal_icon";

    Context context;
    private CheckBoxPreference mDataIcon;
    private CheckBoxPreference mDataStay;
    private CheckBoxPreference mHideSignal;
    private CheckBoxPreference mShowDbm;
    private CheckBoxPreference mShowDbmText;
    private ColorPickerPreference mAuto0;
    private ColorPickerPreference mAuto1;
    private ColorPickerPreference mAuto2;
    private ColorPickerPreference mAuto3;
    private ColorPickerPreference mAuto4;
    private ColorPickerPreference mColor;
    private CheckBoxPreference mDbmAutoColor;

    private void setPrefSummaryColor(ColorPickerPreference ColorPref) {
        if ((ColorPref == null) || (ColorPref == this.mAuto0)) {
            this.mAuto0.setSummary(this.mAuto0.getSummaryText() + ColorPickerPreference.convertToARGB(Settings.Global.getInt(getActivity().getContentResolver(), DBM_AUTO_COLOR_0, mAuto0.getPrefDefault())));
            if (ColorPref != null) {
                return;
            }
        }

        if ((ColorPref == null) || (ColorPref == this.mAuto1)) {
            this.mAuto1.setSummary(this.mAuto1.getSummaryText() + ColorPickerPreference.convertToARGB(Settings.Global.getInt(getActivity().getContentResolver(), DBM_AUTO_COLOR_1, mAuto1.getPrefDefault())));
            if (ColorPref != null) {
                return;
            }
        }

        if ((ColorPref == null) || (ColorPref == this.mAuto2)) {
            this.mAuto2.setSummary(this.mAuto2.getSummaryText() + ColorPickerPreference.convertToARGB(Settings.Global.getInt(getActivity().getContentResolver(), DBM_AUTO_COLOR_2, mAuto2.getPrefDefault())));
            if (ColorPref != null) {
                return;
            }
        }

        if ((ColorPref == null) || (ColorPref == this.mAuto3)) {
            this.mAuto3.setSummary(this.mAuto3.getSummaryText() + ColorPickerPreference.convertToARGB(Settings.Global.getInt(getActivity().getContentResolver(), DBM_AUTO_COLOR_3, mAuto3.getPrefDefault())));
            if (ColorPref != null) {
                return;
            }
        }

        if ((ColorPref == null) || (ColorPref == this.mAuto4)) {
            this.mAuto4.setSummary(this.mAuto4.getSummaryText() + ColorPickerPreference.convertToARGB(Settings.Global.getInt(getActivity().getContentResolver(), DBM_AUTO_COLOR_4, mAuto4.getPrefDefault())));
            if (ColorPref != null) {
                return;
            }
        }

        if ((ColorPref == null) || (ColorPref == this.mColor)) {
            this.mColor.setSummary(this.mColor.getSummaryText() + ColorPickerPreference.convertToARGB(Settings.Global.getInt(getActivity().getContentResolver(), DBM_COLOR, mColor.getPrefDefault())));
            if (ColorPref != null) {
                return;
            }
        }

    }

    private void updateSigStylePrefs() {
        // This is to listen for hiding signal icon when on wifi calling, and disabling all related options
        boolean bool1 = (Settings.Global.getInt(getActivity().getContentResolver(), STORE_HIDE_SIGNAL_ICON, 2) == 2);
        // These are for making sure color preview bits get enabled/disabled with dependency stuff
        boolean bool2 = mShowDbm.isChecked();
        boolean bool3 = mDbmAutoColor.isChecked();

        this.mHideSignal.setEnabled(bool1);
        // most are dependent on this so acting on it will act on the rest
        this.mShowDbm.setEnabled(bool1);

        // This one has an opposite relationship from the rest
        this.mColor.setEnabled(bool1 && bool2 && !bool3);
        this.mColor.setPreviewDim(bool1 && bool2 && !bool3);

        this.mAuto0.setPreviewDim(bool1 && bool2 && bool3);
        this.mAuto1.setPreviewDim(bool1 && bool2 && bool3);
        this.mAuto2.setPreviewDim(bool1 && bool2 && bool3);
        this.mAuto3.setPreviewDim(bool1 && bool2 && bool3);
        this.mAuto4.setPreviewDim(bool1 && bool2 && bool3);
    }

    public SignalFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Load the preferences from an XML resource */
        addPreferencesFromResource(R.xml.signal_fragment);
        this.context = getActivity().getApplicationContext();
        PreferenceScreen localPreferenceScreen = getPreferenceScreen();
        ContentResolver localContentResolver = getActivity().getContentResolver();

        /* Set up checkbox stuff */
        this.mDataIcon = ((CheckBoxPreference) localPreferenceScreen.findPreference(STATUSBAR_DATA_ICON));
        if (Settings.Global.getInt(localContentResolver, STATUSBAR_DATA_ICON, 0) != 0) {
            mDataIcon.setChecked(true);
        } else {
            mDataIcon.setChecked(false);
        }

        this.mDataStay = ((CheckBoxPreference) localPreferenceScreen.findPreference(STATUSBAR_DATA_STAY));
        if (Settings.Global.getInt(localContentResolver, STATUSBAR_DATA_STAY, 0) != 0) {
            mDataStay.setChecked(true);
        } else {
            mDataStay.setChecked(false);
        }

        this.mHideSignal = ((CheckBoxPreference) localPreferenceScreen.findPreference(HIDE_SIGNAL_ICON));
        if (Settings.Global.getInt(localContentResolver, HIDE_SIGNAL_ICON, 0) != 0) {
            mHideSignal.setChecked(true);
        } else {
            mHideSignal.setChecked(false);
        }

        this.mShowDbm = ((CheckBoxPreference) localPreferenceScreen.findPreference(SHOW_STATUS_DBM));
        if (Settings.Global.getInt(localContentResolver, SHOW_STATUS_DBM, 0) != 0) {
            mShowDbm.setChecked(true);
        } else {
            mShowDbm.setChecked(false);
        }

        this.mShowDbmText = ((CheckBoxPreference) localPreferenceScreen.findPreference(SHOW_DBM_TEXT));
        if (Settings.Global.getInt(localContentResolver, SHOW_DBM_TEXT, 0) != 0) {
            mShowDbmText.setChecked(true);
        } else {
            mShowDbmText.setChecked(false);
        }

        this.mDbmAutoColor = ((CheckBoxPreference) localPreferenceScreen.findPreference(DBM_AUTO_COLOR));
        if (Settings.Global.getInt(localContentResolver, DBM_AUTO_COLOR, 0) != 0) {
            mDbmAutoColor.setChecked(true);
        } else {
            mDbmAutoColor.setChecked(false);
        }

        /* Here are for preferences related to color picker */
        this.mColor = ((ColorPickerPreference) localPreferenceScreen.findPreference(DBM_COLOR));
        this.mColor.setOnPreferenceChangeListener(this);

        this.mAuto0 = ((ColorPickerPreference) localPreferenceScreen.findPreference(DBM_AUTO_COLOR_0));
        this.mAuto0.setOnPreferenceChangeListener(this);

        this.mAuto1 = ((ColorPickerPreference) localPreferenceScreen.findPreference(DBM_AUTO_COLOR_1));
        this.mAuto1.setOnPreferenceChangeListener(this);

        this.mAuto2 = ((ColorPickerPreference) localPreferenceScreen.findPreference(DBM_AUTO_COLOR_2));
        this.mAuto2.setOnPreferenceChangeListener(this);

        this.mAuto3 = ((ColorPickerPreference) localPreferenceScreen.findPreference(DBM_AUTO_COLOR_3));
        this.mAuto3.setOnPreferenceChangeListener(this);

        this.mAuto4 = ((ColorPickerPreference) localPreferenceScreen.findPreference(DBM_AUTO_COLOR_4));
        this.mAuto4.setOnPreferenceChangeListener(this);

        updateSigStylePrefs();
        setPrefSummaryColor(null);
    }

    @Override
    public boolean onPreferenceChange(Preference paramPreference, Object paramObject) {
        int z;
        String t;

        /* For changes in color picker prefs... */
        if (paramPreference == this.mColor) {
            Settings.Global.putInt(getActivity().getContentResolver(), DBM_COLOR, (Integer) paramObject);
            t = mColor.getPrefFlag();
            if (!TextUtils.isEmpty(t)) {
                z = Settings.Global.getInt(getActivity().getContentResolver(), t, 1);
                z *= -1;
                Settings.Global.putInt(getActivity().getContentResolver(), t, z);
            }
            setPrefSummaryColor(mColor);
            return true;
        }

        if (paramPreference == this.mAuto0) {
            Settings.Global.putInt(getActivity().getContentResolver(), DBM_AUTO_COLOR_0, (Integer) paramObject);
            t = mAuto0.getPrefFlag();
            if (!TextUtils.isEmpty(t)) {
                z = Settings.Global.getInt(getActivity().getContentResolver(), t, 1);
                z *= -1;
                Settings.Global.putInt(getActivity().getContentResolver(), t, z);
            }
            setPrefSummaryColor(mAuto0);
            return true;
        }

        if (paramPreference == this.mAuto1) {
            Settings.Global.putInt(getActivity().getContentResolver(), DBM_AUTO_COLOR_1, (Integer) paramObject);
            t = mAuto1.getPrefFlag();
            if (!TextUtils.isEmpty(t)) {
                z = Settings.Global.getInt(getActivity().getContentResolver(), t, 1);
                z *= -1;
                Settings.Global.putInt(getActivity().getContentResolver(), t, z);
            }
            setPrefSummaryColor(mAuto1);
            return true;
        }

        if (paramPreference == this.mAuto2) {
            Settings.Global.putInt(getActivity().getContentResolver(), DBM_AUTO_COLOR_2, (Integer) paramObject);
            t = mAuto2.getPrefFlag();
            if (!TextUtils.isEmpty(t)) {
                z = Settings.Global.getInt(getActivity().getContentResolver(), t, 1);
                z *= -1;
                Settings.Global.putInt(getActivity().getContentResolver(), t, z);
            }
            setPrefSummaryColor(mAuto2);
            return true;
        }

        if (paramPreference == this.mAuto3) {
            Settings.Global.putInt(getActivity().getContentResolver(), DBM_AUTO_COLOR_3, (Integer) paramObject);
            t = mAuto3.getPrefFlag();
            if (!TextUtils.isEmpty(t)) {
                z = Settings.Global.getInt(getActivity().getContentResolver(), t, 1);
                z *= -1;
                Settings.Global.putInt(getActivity().getContentResolver(), t, z);
            }
            setPrefSummaryColor(mAuto3);
            return true;
        }

        if (paramPreference == this.mAuto4) {
            Settings.Global.putInt(getActivity().getContentResolver(), DBM_AUTO_COLOR_4, (Integer) paramObject);
            t = mAuto4.getPrefFlag();
            if (!TextUtils.isEmpty(t)) {
                z = Settings.Global.getInt(getActivity().getContentResolver(), t, 1);
                z *= -1;
                Settings.Global.putInt(getActivity().getContentResolver(), t, z);
            }
            setPrefSummaryColor(mAuto4);
            return true;
        }

        return true;
    }


    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, @NonNull Preference paramPreference) {
        int i;
        ContentResolver localContentResolver = getActivity().getContentResolver();

        /* Deal with checkbox stuff first */
        if (paramPreference == this.mDataIcon) {
            if (this.mDataIcon.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, STATUSBAR_DATA_ICON, i);
            return true;
        }

        if (paramPreference == this.mDataStay) {
            if (this.mDataStay.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, STATUSBAR_DATA_STAY, i);
            return true;
        }

        if (paramPreference == this.mHideSignal) {
            if (this.mHideSignal.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, HIDE_SIGNAL_ICON, i);
            return true;
        }

        if (paramPreference == this.mShowDbm) {
            if (this.mShowDbm.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, SHOW_STATUS_DBM, i);
            updateSigStylePrefs();
            return true;
        }

        if (paramPreference == this.mShowDbmText) {
            if (this.mShowDbmText.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, SHOW_DBM_TEXT, i);
            return true;
        }

        if (paramPreference == this.mDbmAutoColor) {
            if (this.mDbmAutoColor.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, DBM_AUTO_COLOR, i);
            updateSigStylePrefs();
            return true;
        }

        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateSigStylePrefs();
        setPrefSummaryColor(null);
    }

}