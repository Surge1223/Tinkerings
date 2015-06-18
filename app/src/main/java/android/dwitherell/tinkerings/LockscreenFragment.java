package android.dwitherell.tinkerings;
/**
 * Created by devonwitherell on 12/31/2014.
 */

import android.content.ContentResolver;
import android.content.Context;
import android.dwitherell.tinkerings.utils.ColorPickerPreference;
import android.dwitherell.tinkerings.utils.SeekBarPreference;
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
import java.math.BigDecimal;

public class LockscreenFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String ALLOW_CAMERA_WIDGET = "allow_camera_widget";
    private static final String CUSTOM_LOCK_TIMEOUT = "custom_lock_timeout";
    private static final String CUSTOM_SVIEW_BACKGROUND = "custom_sview_background";
    private static final String ENABLE_LOCKSCREEN_TORCH = "enable_lockscreen_torch";
    private static final String LOCKSCREEN_CUSTOM_MSG = "lockscreen_custom_msg";
    private static final String LOCKSCREEN_DISABLE = "lockscreen_disable";
    private static final String LOCKSCREEN_FINGER_INK = "lockscreen_finger_ink";
    private static final String LOCKSCREEN_HAPTIC = "lockscreen_haptic";
    private static final String LOCKSCREEN_HOME_WAKE = "lockscreen_home_wake";
    private static final String LOCKSCREEN_INK_COLOR = "lockscreen_ink_color";
    private static final String LOCKSCREEN_INK_CUSTOM = "lockscreen_ink_custom";
    private static final String LOCKSCREEN_MSG_COLOR = "lockscreen_msg_color";
    private static final String LOCKSCREEN_MSG_TYPE = "lockscreen_msg_type";
    private static final String LOCKSCREEN_STYLE_TYPE = "lockscreen_style_type";
    private static final String LOCKSCREEN_TIMEOUT_PREF = "lockscreen_timeout_pref";
    private static final String LOCKSCREEN_VOLUME_WAKE = "lockscreen_volume_wake";
    private static final String LOCK_SCREEN_ROTATION = "lock_screen_rotation";
    private static final String PMENU_LOCKBLOCK = "pmenu_lockblock";
    private static final String QUICK_PINPASS = "quick_pinpass";
    private static final String SVIEW_VERIFY = "sview_verify";
    private static final String DEFAULT_EDIT_TEXT = "TSM Tweaked";

    private static final String SVIEW_COLOR_USE_ALL = "sview_color_use_all";
    private static final String SVIEW_COVER_AVAILABLE = "sview_cover_available";
    private static final String LOCKSCREEN_SETTINGS_DISABLE = "lockscreen_settings_disable";
    private static final String LOCKSCREEN_SWIPE_ON = "lockscreen_swipe_on";
    private static final String LOCKSCREEN_RIPPLE_EFFECT = "lockscreen_ripple_effect";

    Context context;
    private SeekBarPreference lockscreen_timeout_pref;
    private CheckBoxPreference mAllowCamWidg;
    private CheckBoxPreference mCustomLockTime;
    private EditTextPreference mCustomMsg;
    private ColorPickerPreference mCustomMsgColor;
    private CheckBoxPreference mCustomMsgType;
    private CheckBoxPreference mCustomSView;
    private CheckBoxPreference mHomeWake;
    private CheckBoxPreference mLockHaptic;
    private CheckBoxPreference mLockScreenDisable;
    private CheckBoxPreference mLockScreenFingerInk;
    private ColorPickerPreference mLockScreenInkColor;
    private CheckBoxPreference mLockScreenInkCustom;
    private CheckBoxPreference mLockScreenRotate;
    private ListPreference mLockScreenStylePref;
    private CheckBoxPreference mLockScreenTorch;
    private CheckBoxPreference mPMenuBlock;
    private CheckBoxPreference mQuickPinPass;
    private CheckBoxPreference mSViewForce;
    private CheckBoxPreference mVolumeWake;

    private int mOrigSViewSetting;

    private void changeSView(int paramInt) {
        int i = Settings.System.getInt(getActivity().getContentResolver(), SVIEW_COLOR_USE_ALL, 0);

        if (paramInt != 0) {
            this.mOrigSViewSetting = i;
            i = 0;
        } else {
            if (i == this.mOrigSViewSetting) {
                return;
            } else {
                i = this.mOrigSViewSetting;
            }
        }
        Settings.System.putInt(getActivity().getContentResolver(), SVIEW_COLOR_USE_ALL, i);
    }

    private void setListSummary(ListPreference paramListPreference) {
        int i;
        String str;
        String[] strarray;

        if ((paramListPreference == null) || (paramListPreference == this.mLockScreenStylePref)) {
            i = Settings.Global.getInt(getActivity().getContentResolver(), LOCKSCREEN_STYLE_TYPE, 0);
            strarray = getResources().getStringArray(R.array.lock_screen_entries);
            str = strarray[i];
            this.mLockScreenStylePref.setSummary(str);
            if (paramListPreference != null) {
                return;
            }
        }
    }

    private void setPrefSummaryColor(ColorPickerPreference ColorPref)
    {
        if ((ColorPref == null) || (ColorPref == this.mLockScreenInkColor))
        {
            this.mLockScreenInkColor.setSummary(this.mLockScreenInkColor.getSummaryText() + ColorPickerPreference.convertToARGB(Settings.Global.getInt(getActivity().getContentResolver(), LOCKSCREEN_INK_COLOR, mLockScreenInkColor.getPrefDefault())));
            if (ColorPref != null) {
                return;
            }
        }

        if ((ColorPref == null) || (ColorPref == this.mCustomMsgColor))
        {
            this.mCustomMsgColor.setSummary(this.mCustomMsgColor.getSummaryText() + ColorPickerPreference.convertToARGB(Settings.Global.getInt(getActivity().getContentResolver(), LOCKSCREEN_MSG_COLOR, mCustomMsgColor.getPrefDefault())));
            if (ColorPref != null) {
                return;
            }
        }
    }

    private void updateCustomMsgPrefs() {
        this.mCustomMsg.setEnabled(Settings.Global.getInt(getActivity().getContentResolver(), LOCKSCREEN_MSG_TYPE, 0) != 0);
    }

    private void updateSViewPrefs() {
        mCustomSView.setEnabled((Settings.Global.getInt(getActivity().getContentResolver(), SVIEW_VERIFY, 0) == 1) && (Settings.Global.getInt(getActivity().getContentResolver(), SVIEW_COVER_AVAILABLE, 0) == 1));
    }

    private void updateStylePrefs() {
        // Everything is disabled when this = 1, so it's false when != 1 (i.e. 0) and true when = 0
        boolean bool1 = (Settings.Global.getInt(getActivity().getContentResolver(), LOCKSCREEN_SETTINGS_DISABLE, 0) == 0);
        // If this (using swipe lock) = 0 then last 4 are all F, so it's true when != 0
        boolean bool2 = (Settings.Global.getInt(getActivity().getContentResolver(), LOCKSCREEN_SWIPE_ON, 0) != 0);
        // If style = 1 its aosp lock, so haptic listens for !bool3, otherwise non-aosp is true
        boolean bool3 = (Settings.Global.getInt(getActivity().getContentResolver(), LOCKSCREEN_STYLE_TYPE, 0) == 0);
        // Only for fingerink items - must be on ripple unlock effect (2) - if so it's true
        boolean bool4 = (Settings.System.getInt(getActivity().getContentResolver(), LOCKSCREEN_RIPPLE_EFFECT, 1) == 2);

        this.mLockScreenDisable.setEnabled(bool1);
        this.mLockScreenStylePref.setEnabled(bool1);
        this.mCustomMsgType.setEnabled(bool1);
        this.mCustomMsg.setEnabled(bool1);
        this.mCustomMsgColor.setEnabled(bool1);
        this.mQuickPinPass.setEnabled(bool1);
        this.mAllowCamWidg.setEnabled(bool1);
        this.mPMenuBlock.setEnabled(bool1);
        this.mLockScreenTorch.setEnabled(bool1);
        this.mVolumeWake.setEnabled(bool1);
        this.mCustomLockTime.setEnabled(bool1);
        this.lockscreen_timeout_pref.setEnabled(bool1);
        this.mLockScreenFingerInk.setEnabled(bool1 && bool2 && bool3 && bool4);
        this.mLockScreenInkCustom.setEnabled(bool1 && bool2 && bool3 && bool4);
        this.mLockScreenInkColor.setEnabled(bool1 && bool2 && bool3 && bool4);
        this.mLockHaptic.setEnabled(bool1 && bool2 && !bool3);

        this.mCustomMsgColor.setPreviewDim(bool1 && !this.mLockScreenDisable.isChecked());
        this.mLockScreenInkColor.setPreviewDim(bool1 && bool2 && bool3 && bool4 && this.mLockScreenInkCustom.isChecked());
    }

    public LockscreenFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Load the preferences from an XML resource */
        addPreferencesFromResource(R.xml.lockscreen_fragment);
        this.context = getActivity().getApplicationContext();
        PreferenceScreen localPreferenceScreen = getPreferenceScreen();
        ContentResolver localContentResolver = getActivity().getContentResolver();
        int i;
        String str;
        BigDecimal k;
        BigDecimal l;

        this.mOrigSViewSetting = Settings.System.getInt(localContentResolver, SVIEW_COLOR_USE_ALL, 0);

        /* Set up edittext pref */
        this.mCustomMsg = ((EditTextPreference) findPreference(LOCKSCREEN_CUSTOM_MSG));

        str = Settings.Global.getString(localContentResolver, LOCKSCREEN_CUSTOM_MSG);
        if (TextUtils.isEmpty(str)) {
            str = DEFAULT_EDIT_TEXT;
            Settings.Global.putString(localContentResolver, LOCKSCREEN_CUSTOM_MSG, str);
        }
        this.mCustomMsg.setSummary(str);
        this.mCustomMsg.setText(str);
        this.mCustomMsg.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference paramPreference, Object paramChangedText) {
                Settings.Global.putString(LockscreenFragment.this.getActivity().getContentResolver(), LOCKSCREEN_CUSTOM_MSG, (String) paramChangedText);
                paramPreference.setSummary((String) paramChangedText);
                return true;
            }
        });

        /* Set up checkbox stuff */
        this.mLockScreenDisable = ((CheckBoxPreference) localPreferenceScreen.findPreference(LOCKSCREEN_DISABLE));
        if (Settings.Global.getInt(localContentResolver, LOCKSCREEN_DISABLE, 0) != 0) {
            mLockScreenDisable.setChecked(true);
        } else {
            mLockScreenDisable.setChecked(false);
        }

        this.mVolumeWake = ((CheckBoxPreference) localPreferenceScreen.findPreference(LOCKSCREEN_VOLUME_WAKE));
        if (Settings.Global.getInt(localContentResolver, LOCKSCREEN_VOLUME_WAKE, 0) != 0) {
            mVolumeWake.setChecked(true);
        } else {
            mVolumeWake.setChecked(false);
        }

        this.mCustomMsgType = ((CheckBoxPreference) localPreferenceScreen.findPreference(LOCKSCREEN_MSG_TYPE));
        if (Settings.Global.getInt(localContentResolver, LOCKSCREEN_MSG_TYPE, 0) != 0) {
            mCustomMsgType.setChecked(true);
        } else {
            mCustomMsgType.setChecked(false);
        }

        this.mLockScreenFingerInk = ((CheckBoxPreference) localPreferenceScreen.findPreference(LOCKSCREEN_FINGER_INK));
        if (Settings.Global.getInt(localContentResolver, LOCKSCREEN_FINGER_INK, 0) != 0) {
            mLockScreenFingerInk.setChecked(true);
        } else {
            mLockScreenFingerInk.setChecked(false);
        }

        this.mLockScreenInkCustom = ((CheckBoxPreference) localPreferenceScreen.findPreference(LOCKSCREEN_INK_CUSTOM));
        if (Settings.Global.getInt(localContentResolver, LOCKSCREEN_INK_CUSTOM, 0) != 0) {
            mLockScreenInkCustom.setChecked(true);
        } else {
            mLockScreenInkCustom.setChecked(false);
        }

        this.mLockScreenTorch = ((CheckBoxPreference) localPreferenceScreen.findPreference(ENABLE_LOCKSCREEN_TORCH));
        if (Settings.Global.getInt(localContentResolver, ENABLE_LOCKSCREEN_TORCH, 0) != 0) {
            mLockScreenTorch.setChecked(true);
        } else {
            mLockScreenTorch.setChecked(false);
        }

        this.mPMenuBlock = ((CheckBoxPreference) localPreferenceScreen.findPreference(PMENU_LOCKBLOCK));
        if (Settings.Global.getInt(localContentResolver, PMENU_LOCKBLOCK, 0) != 0) {
            mPMenuBlock.setChecked(true);
        } else {
            mPMenuBlock.setChecked(false);
        }

        this.mQuickPinPass = ((CheckBoxPreference) localPreferenceScreen.findPreference(QUICK_PINPASS));
        if (Settings.Global.getInt(localContentResolver, QUICK_PINPASS, 0) != 0) {
            mQuickPinPass.setChecked(true);
        } else {
            mQuickPinPass.setChecked(false);
        }

        this.mCustomLockTime = ((CheckBoxPreference) localPreferenceScreen.findPreference(CUSTOM_LOCK_TIMEOUT));
        if (Settings.Global.getInt(localContentResolver, CUSTOM_LOCK_TIMEOUT, 0) != 0) {
            mCustomLockTime.setChecked(true);
        } else {
            mCustomLockTime.setChecked(false);
        }

        this.mCustomSView = ((CheckBoxPreference) localPreferenceScreen.findPreference(CUSTOM_SVIEW_BACKGROUND));
        if (Settings.Global.getInt(localContentResolver, CUSTOM_SVIEW_BACKGROUND, 0) != 0) {
            mCustomSView.setChecked(true);
        } else {
            mCustomSView.setChecked(false);
        }

        this.mAllowCamWidg = ((CheckBoxPreference) localPreferenceScreen.findPreference(ALLOW_CAMERA_WIDGET));
        if (Settings.Global.getInt(localContentResolver, ALLOW_CAMERA_WIDGET, 0) != 0) {
            mAllowCamWidg.setChecked(true);
        } else {
            mAllowCamWidg.setChecked(false);
        }

        this.mLockHaptic = ((CheckBoxPreference) localPreferenceScreen.findPreference(LOCKSCREEN_HAPTIC));
        if (Settings.Global.getInt(localContentResolver, LOCKSCREEN_HAPTIC, 0) != 0) {
            mLockHaptic.setChecked(true);
        } else {
            mLockHaptic.setChecked(false);
        }

        this.mHomeWake = ((CheckBoxPreference) localPreferenceScreen.findPreference(LOCKSCREEN_HOME_WAKE));
        if (Settings.Global.getInt(localContentResolver, LOCKSCREEN_HOME_WAKE, 0) != 0) {
            mHomeWake.setChecked(true);
        } else {
            mHomeWake.setChecked(false);
        }

        this.mSViewForce = ((CheckBoxPreference) localPreferenceScreen.findPreference(SVIEW_VERIFY));
        if (Settings.Global.getInt(localContentResolver, SVIEW_VERIFY, 0) != 0) {
            mSViewForce.setChecked(true);
        } else {
            mSViewForce.setChecked(false);
        }

        this.mLockScreenRotate = ((CheckBoxPreference) localPreferenceScreen.findPreference(LOCK_SCREEN_ROTATION));
        if (Settings.Global.getInt(localContentResolver, LOCK_SCREEN_ROTATION, 0) != 0) {
            mLockScreenRotate.setChecked(true);
        } else {
            mLockScreenRotate.setChecked(false);
        }

        /* Here are for list preferences */
        this.mLockScreenStylePref = ((ListPreference) localPreferenceScreen.findPreference(LOCKSCREEN_STYLE_TYPE));
        i = Settings.Global.getInt(localContentResolver, LOCKSCREEN_STYLE_TYPE, 0);
        this.mLockScreenStylePref.setValue(String.valueOf(i));
        this.mLockScreenStylePref.setOnPreferenceChangeListener(this);

        /* Here are for preferences related to color picker */
        this.mCustomMsgColor = ((ColorPickerPreference) localPreferenceScreen.findPreference(LOCKSCREEN_MSG_COLOR));
        this.mCustomMsgColor.setOnPreferenceChangeListener(this);

        this.mLockScreenInkColor = ((ColorPickerPreference) localPreferenceScreen.findPreference(LOCKSCREEN_INK_COLOR));
        this.mLockScreenInkColor.setOnPreferenceChangeListener(this);

        /* Here are for preferences related to number picker */
        this.lockscreen_timeout_pref = ((SeekBarPreference) localPreferenceScreen.findPreference(LOCKSCREEN_TIMEOUT_PREF));
        i = Settings.Global.getInt(localContentResolver, LOCKSCREEN_TIMEOUT_PREF, this.lockscreen_timeout_pref.getDefault() * this.lockscreen_timeout_pref.getFactor());
        k = new BigDecimal(i);
        if (this.lockscreen_timeout_pref.getPercent()) {
            l = new BigDecimal(100);
            k = k.multiply(l);
        }
        l = this.lockscreen_timeout_pref.getPercent() ? new BigDecimal(this.lockscreen_timeout_pref.getMax() - this.lockscreen_timeout_pref.getMin()) : new BigDecimal(this.lockscreen_timeout_pref.getFactor() * Math.pow(10, (double)this.lockscreen_timeout_pref.getDecimals()));
        k = k.divide(l, this.lockscreen_timeout_pref.getDecimals(), BigDecimal.ROUND_HALF_EVEN);
        this.lockscreen_timeout_pref.setSummary(k + " " + this.lockscreen_timeout_pref.getUnits());
        this.lockscreen_timeout_pref.setOnPreferenceChangeListener(this);

        updateSViewPrefs();
        updateStylePrefs();
        updateCustomMsgPrefs();
        setListSummary(null);
        setPrefSummaryColor(null);
    }

    @Override
    public boolean onPreferenceChange(Preference paramPreference, Object paramObject) {
        int i;
        BigDecimal k;
        BigDecimal l;
        String t;

        /* Put stuff for list preferences here */
        if (paramPreference == this.mLockScreenStylePref) {
            i = Integer.valueOf((String) paramObject);
            Settings.Global.putInt(getActivity().getContentResolver(), LOCKSCREEN_STYLE_TYPE, i);
            setListSummary(this.mLockScreenStylePref);
            updateStylePrefs();
            return true;
        }

        /* Process changes made in seekbar preferences */
        if (paramPreference == this.lockscreen_timeout_pref) {
            i = (this.lockscreen_timeout_pref.getMin() + (Integer) paramObject * this.lockscreen_timeout_pref.getStep()) * this.lockscreen_timeout_pref.getFactor();
            k = new BigDecimal(i);
            if (this.lockscreen_timeout_pref.getPercent()) {
                l = new BigDecimal(100);
                k = k.multiply(l);
            }
            l = this.lockscreen_timeout_pref.getPercent() ? new BigDecimal(this.lockscreen_timeout_pref.getMax() - this.lockscreen_timeout_pref.getMin()) : new BigDecimal(this.lockscreen_timeout_pref.getFactor() * Math.pow(10, (double)this.lockscreen_timeout_pref.getDecimals()));
            k = k.divide(l, this.lockscreen_timeout_pref.getDecimals(), BigDecimal.ROUND_HALF_EVEN);
            Settings.Global.putInt(getActivity().getContentResolver(), LOCKSCREEN_TIMEOUT_PREF, i);
            this.lockscreen_timeout_pref.setSummary(k + " " + lockscreen_timeout_pref.getUnits());
            return true;
        }

        /* For color picker... */
        if (paramPreference == this.mCustomMsgColor) {
            int z;
            Settings.Global.putInt(getActivity().getContentResolver(), LOCKSCREEN_MSG_COLOR, (Integer) paramObject);
            t = mCustomMsgColor.getPrefFlag();
            if (!TextUtils.isEmpty(t)) {
                z = Settings.Global.getInt(getActivity().getContentResolver(), t, 1);
                z *= -1;
                Settings.Global.putInt(getActivity().getContentResolver(), t, z);
            }
            setPrefSummaryColor(mCustomMsgColor);
            return true;
        }

        if (paramPreference == this.mLockScreenInkColor) {
            int z;
            Settings.Global.putInt(getActivity().getContentResolver(), LOCKSCREEN_INK_COLOR, (Integer) paramObject);
            t = mLockScreenInkColor.getPrefFlag();
            if (!TextUtils.isEmpty(t)) {
                z = Settings.Global.getInt(getActivity().getContentResolver(), t, 1);
                z *= -1;
                Settings.Global.putInt(getActivity().getContentResolver(), t, z);
            }
            setPrefSummaryColor(mLockScreenInkColor);
            return true;
        }

        return true;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, @NonNull Preference paramPreference) {
        int i;
        ContentResolver localContentResolver = getActivity().getContentResolver();

        /* Deal with checkbox stuff first */
        if (paramPreference == this.mLockScreenDisable) {
            if (this.mLockScreenDisable.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, LOCKSCREEN_DISABLE, i);
            updateStylePrefs();
            return true;
        }

        if (paramPreference == this.mVolumeWake) {
            if (this.mVolumeWake.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, LOCKSCREEN_VOLUME_WAKE, i);
            return true;
        }

        if (paramPreference == this.mCustomMsgType) {
            if (this.mCustomMsgType.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, LOCKSCREEN_MSG_TYPE, i);
            updateCustomMsgPrefs();
            return true;
        }

        if (paramPreference == this.mLockScreenFingerInk) {
            if (this.mLockScreenFingerInk.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, LOCKSCREEN_FINGER_INK, i);
            return true;
        }

        if (paramPreference == this.mLockScreenInkCustom) {
            if (this.mLockScreenInkCustom.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, LOCKSCREEN_INK_CUSTOM, i);
            this.mLockScreenInkColor.setPreviewDim(this.mLockScreenInkColor.isEnabled());
            return true;
        }

        if (paramPreference == this.mLockScreenTorch) {
            if (this.mLockScreenTorch.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, ENABLE_LOCKSCREEN_TORCH, i);
            return true;
        }

        if (paramPreference == this.mPMenuBlock) {
            if (this.mPMenuBlock.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, PMENU_LOCKBLOCK, i);
            return true;
        }

        if (paramPreference == this.mQuickPinPass) {
            if (this.mQuickPinPass.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, QUICK_PINPASS, i);
            return true;
        }

        if (paramPreference == this.mCustomLockTime) {
            if (this.mCustomLockTime.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, CUSTOM_LOCK_TIMEOUT, i);
            return true;
        }

        if (paramPreference == this.mCustomSView) {
            if (this.mCustomSView.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, CUSTOM_SVIEW_BACKGROUND, i);
            changeSView(i);
            return true;
        }

        if (paramPreference == this.mAllowCamWidg) {
            if (this.mAllowCamWidg.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, ALLOW_CAMERA_WIDGET, i);
            return true;
        }

        if (paramPreference == this.mLockHaptic) {
            if (this.mLockHaptic.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, LOCKSCREEN_HAPTIC, i);
            return true;
        }

        if (paramPreference == this.mHomeWake) {
            if (this.mHomeWake.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, LOCKSCREEN_HOME_WAKE, i);
            return true;
        }

        if (paramPreference == this.mSViewForce) {
            if (this.mSViewForce.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, SVIEW_VERIFY, i);
            updateSViewPrefs();
            return true;
        }

        if (paramPreference == this.mLockScreenRotate) {
            if (this.mLockScreenRotate.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, LOCK_SCREEN_ROTATION, i);
            return true;
        }

        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateStylePrefs();
        updateSViewPrefs();
        setListSummary(null);
        setPrefSummaryColor(null);
    }

}