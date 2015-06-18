package android.dwitherell.tinkerings;
/**
 * Created by devonwitherell on 12/31/2014.
 */

import android.content.ContentResolver;
import android.content.Context;
import android.dwitherell.tinkerings.utils.ColorPickerPreference;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import java.util.Arrays;

public class StatusbarClockCarFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String STATUSBAR_ATT_CARRIER = "statusbar_att_carrier";
    private static final String STATUSBAR_CARRIER_COLOR = "statusbar_carrier_color";
    private static final String STATUSBAR_CARRIER_TEXT = "statusbar_carrier_text";
    private static final String STATUSBAR_CLOCK_COLOR = "statusbar_clock_color";
    private static final String STATUSBAR_CLOCK_POSITION = "statusbar_clock_position";
    private static final String STATUSBAR_CLOCK_STYLE = "statusbar_clock_style";
    private static final String STATUSBAR_COLOR_CUSTOM = "statusbar_color_custom";
    private static final String STATUSBAR_CUSTOM_CARRIER = "statusbar_custom_carrier";
    private static final String STATUSBAR_DAY_SIZE = "statusbar_day_size";
    private static final String STATUSBAR_SHOW_DAY = "statusbar_show_day";

    private static final String DEFAULT_EDIT_TEXT = "TSM Tweaked";

    Context context;

    private CheckBoxPreference mATTCarrier;
    private ColorPickerPreference mCarrierColor;
    private EditTextPreference mCarrierText;
    private ColorPickerPreference mClockColor;
    private ListPreference mClockPosition;
    private ListPreference mClockStyle;
    private CheckBoxPreference mCustomCarrier;
    private ListPreference mShowDayFormat;
    private ListPreference mShowDaySize;

    private void setListSummary(ListPreference paramListPreference) {
        int i;
        int j;
        String str;
        String[] strarray;

        if ((paramListPreference == null) || (paramListPreference == this.mClockPosition)) {
            i = Settings.Global.getInt(getActivity().getContentResolver(), STATUSBAR_CLOCK_POSITION, 0);
            strarray = getResources().getStringArray(R.array.clock_position_values);
            j = Arrays.asList(strarray).indexOf(String.valueOf(i));
            strarray = getResources().getStringArray(R.array.clock_position_entries);
            str = strarray[j];
            this.mClockPosition.setSummary(str);
            if (paramListPreference != null) {
                return;
            }
        }

        if ((paramListPreference == null) || (paramListPreference == this.mClockStyle)) {
            i = Settings.Global.getInt(getActivity().getContentResolver(), STATUSBAR_CLOCK_STYLE, 0);
            strarray = getResources().getStringArray(R.array.clock_entries);
            str = strarray[i];
            this.mClockStyle.setSummary(str);
            if (paramListPreference != null) {
                return;
            }
        }

        if ((paramListPreference == null) || (paramListPreference == this.mShowDayFormat)) {
            i = Settings.Global.getInt(getActivity().getContentResolver(), STATUSBAR_SHOW_DAY, 0);
            strarray = getResources().getStringArray(R.array.dow_entries);
            str = strarray[i];
            this.mShowDayFormat.setSummary(str);
            if (paramListPreference != null) {
                return;
            }
        }

        if ((paramListPreference == null) || (paramListPreference == this.mShowDaySize)) {
            i = Settings.Global.getInt(getActivity().getContentResolver(), STATUSBAR_DAY_SIZE, 0);
            strarray = getResources().getStringArray(R.array.dowsize_entries);
            str = strarray[i];
            this.mShowDaySize.setSummary(str);
            if (paramListPreference != null) {
                return;
            }
        }
    }

    private void setPrefSummaryColor(ColorPickerPreference ColorPref) {
        if ((ColorPref == null) || (ColorPref == this.mClockColor)) {
            this.mClockColor.setSummary(this.mClockColor.getSummaryText() + ColorPickerPreference.convertToARGB(Settings.Global.getInt(getActivity().getContentResolver(), STATUSBAR_CLOCK_COLOR, mClockColor.getPrefDefault())));
            if (ColorPref != null) {
                return;
            }
        }

        if ((ColorPref == null) || (ColorPref == this.mCarrierColor)) {
            this.mCarrierColor.setSummary(this.mCarrierColor.getSummaryText() + ColorPickerPreference.convertToARGB(Settings.Global.getInt(getActivity().getContentResolver(), STATUSBAR_CARRIER_COLOR, mCarrierColor.getPrefDefault())));
            if (ColorPref != null) {
                return;
            }
        }
    }

    private void updateClockStylePrefs() {
        boolean bool1 = (Settings.Global.getInt(getActivity().getContentResolver(), STATUSBAR_CLOCK_POSITION, 0) != 3);
        boolean bool2 = (Settings.Global.getInt(getActivity().getContentResolver(), STATUSBAR_DAY_SIZE, 0) != 0);

        this.mClockColor.setEnabled(bool1);
        // Stuck the clock color preview bit in here...
        this.mClockColor.setPreviewDim(bool1);
        this.mClockStyle.setEnabled(bool1);
        this.mShowDaySize.setEnabled(bool1);
        this.mShowDayFormat.setEnabled(bool1 && bool2);
    }

    public StatusbarClockCarFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Load the preferences from an XML resource */
        addPreferencesFromResource(R.xml.statusbar_clockcar_fragment);
        this.context = getActivity().getApplicationContext();
        PreferenceScreen localPreferenceScreen = getPreferenceScreen();
        ContentResolver localContentResolver = getActivity().getContentResolver();
        int i;
        String str;

        /* Set up edittext pref */
        this.mCarrierText = ((EditTextPreference) findPreference(STATUSBAR_CARRIER_TEXT));

        str = Settings.Global.getString(localContentResolver, STATUSBAR_CARRIER_TEXT);
        if (TextUtils.isEmpty(str)) {
            str = DEFAULT_EDIT_TEXT;
            Settings.Global.putString(localContentResolver, STATUSBAR_CARRIER_TEXT, str);
        }
        this.mCarrierText.setSummary(str);
        this.mCarrierText.setText(str);
        this.mCarrierText.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference paramPreference, Object paramChangedText) {
                Settings.Global.putString(StatusbarClockCarFragment.this.getActivity().getContentResolver(), STATUSBAR_CARRIER_TEXT, (String) paramChangedText);
                paramPreference.setSummary((String) paramChangedText);
                return true;
            }
        });

        /* Set up checkbox stuff */
        this.mCustomCarrier = ((CheckBoxPreference) localPreferenceScreen.findPreference(STATUSBAR_CUSTOM_CARRIER));
        if (Settings.Global.getInt(localContentResolver, STATUSBAR_CUSTOM_CARRIER, 0) != 0) {
            mCustomCarrier.setChecked(true);
        } else {
            mCustomCarrier.setChecked(false);
        }

        this.mATTCarrier = ((CheckBoxPreference) localPreferenceScreen.findPreference(STATUSBAR_ATT_CARRIER));
        if (Settings.Global.getInt(localContentResolver, STATUSBAR_ATT_CARRIER, 0) != 0) {
            mATTCarrier.setChecked(true);
        } else {
            mATTCarrier.setChecked(false);
        }

        /* Here are for list preferences */
        this.mClockStyle = ((ListPreference) localPreferenceScreen.findPreference(STATUSBAR_CLOCK_STYLE));
        i = Settings.Global.getInt(localContentResolver, STATUSBAR_CLOCK_STYLE, 0);
        this.mClockStyle.setValue(String.valueOf(i));
        this.mClockStyle.setOnPreferenceChangeListener(this);

        this.mClockPosition = ((ListPreference) localPreferenceScreen.findPreference(STATUSBAR_CLOCK_POSITION));
        i = Settings.Global.getInt(localContentResolver, STATUSBAR_CLOCK_POSITION, 0);
        this.mClockPosition.setValue(String.valueOf(i));
        this.mClockPosition.setOnPreferenceChangeListener(this);

        this.mShowDayFormat = ((ListPreference) localPreferenceScreen.findPreference(STATUSBAR_SHOW_DAY));
        i = Settings.Global.getInt(localContentResolver, STATUSBAR_SHOW_DAY, 0);
        this.mShowDayFormat.setValue(String.valueOf(i));
        this.mShowDayFormat.setOnPreferenceChangeListener(this);

        this.mShowDaySize = ((ListPreference) localPreferenceScreen.findPreference(STATUSBAR_DAY_SIZE));
        i = Settings.Global.getInt(localContentResolver, STATUSBAR_DAY_SIZE, 0);
        this.mShowDaySize.setValue(String.valueOf(i));
        this.mShowDaySize.setOnPreferenceChangeListener(this);

        /* Here are for preferences related to color picker */
        this.mClockColor = ((ColorPickerPreference) localPreferenceScreen.findPreference(STATUSBAR_CLOCK_COLOR));
        this.mClockColor.setOnPreferenceChangeListener(this);

        this.mCarrierColor = ((ColorPickerPreference) localPreferenceScreen.findPreference(STATUSBAR_CARRIER_COLOR));
        this.mCarrierColor.setOnPreferenceChangeListener(this);

        /* Get preference enabled/summary things all set up */
        updateClockStylePrefs();
        setListSummary(null);
        setPrefSummaryColor(null);
    }

    @Override
    public boolean onPreferenceChange(Preference paramPreference, Object paramObject) {
        int i;
        int m;
        String t;

        /* Put stuff for list preferences here */
        if (paramPreference == this.mClockStyle) {
            i = Integer.valueOf((String) paramObject);
            Settings.Global.putInt(getActivity().getContentResolver(), STATUSBAR_CLOCK_STYLE, i);
            setListSummary(this.mClockStyle);
            return true;
        }

        if (paramPreference == this.mClockPosition) {
            i = Integer.valueOf((String) paramObject);
            Settings.Global.putInt(getActivity().getContentResolver(), STATUSBAR_CLOCK_POSITION, i);
            setListSummary(this.mClockPosition);
            updateClockStylePrefs();
            return true;
        }

        if (paramPreference == this.mShowDayFormat) {
            i = Integer.valueOf((String) paramObject);
            Settings.Global.putInt(getActivity().getContentResolver(), STATUSBAR_SHOW_DAY, i);
            setListSummary(this.mShowDayFormat);
            return true;
        }

        if (paramPreference == this.mShowDaySize) {
            i = Integer.valueOf((String) paramObject);
            Settings.Global.putInt(getActivity().getContentResolver(), STATUSBAR_DAY_SIZE, i);
            setListSummary(this.mShowDaySize);
            updateClockStylePrefs();
            return true;
        }

        /* For changes in color picker prefs... */
        if (paramPreference == this.mClockColor) {
            Settings.Global.putInt(getActivity().getContentResolver(), STATUSBAR_CLOCK_COLOR, (Integer) paramObject);
            t = mClockColor.getPrefFlag();
            if (!TextUtils.isEmpty(t)) {
                m = Settings.Global.getInt(getActivity().getContentResolver(), t, 1);
                m *= -1;
                Settings.Global.putInt(getActivity().getContentResolver(), t, m);
            }
            setPrefSummaryColor(mClockColor);
            return true;
        }

        if (paramPreference == this.mCarrierColor) {
            Settings.Global.putInt(getActivity().getContentResolver(), STATUSBAR_CARRIER_COLOR, (Integer) paramObject);
            t = mCarrierColor.getPrefFlag();
            if (!TextUtils.isEmpty(t)) {
                m = Settings.Global.getInt(getActivity().getContentResolver(), t, 1);
                m *= -1;
                Settings.Global.putInt(getActivity().getContentResolver(), t, m);
            }
            setPrefSummaryColor(mCarrierColor);
            return true;
        }

        return true;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, @NonNull Preference paramPreference) {
        int i;
        ContentResolver localContentResolver = getActivity().getContentResolver();

        /* Deal with checkbox stuff first */
        if (paramPreference == this.mCustomCarrier) {
            if (this.mCustomCarrier.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, STATUSBAR_CUSTOM_CARRIER, i);
            return true;
        }

        if (paramPreference == this.mATTCarrier) {
            if (this.mATTCarrier.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, STATUSBAR_ATT_CARRIER, i);
            return true;
        }

        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateClockStylePrefs();
        setListSummary(null);
        setPrefSummaryColor(null);
    }
}