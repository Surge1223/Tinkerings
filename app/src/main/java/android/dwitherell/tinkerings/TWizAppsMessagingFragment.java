package android.dwitherell.tinkerings;
/**
 * Created by devonwitherell on 12/31/2014.
 */

import android.content.ContentResolver;
import android.content.Context;
import android.dwitherell.tinkerings.utils.ColorPickerPreference;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;

public class TWizAppsMessagingFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String MMSSMS_LOGBLOCK = "mmssms_logblock";
    private static final String MMS_CONVO_COLOR = "mms_convo_color";
    private static final String MMS_RECEIVE_CONVO_COLOR = "mms_receive_convo_color";
    private static final String MMS_RECEIVE_THREAD_COLOR = "mms_receive_thread_color";
    private static final String MMS_SEND_CONVO_COLOR = "mms_send_convo_color";
    private static final String MMS_SEND_THREAD_COLOR = "mms_send_thread_color";
    private static final String MMS_SENT_STAMP = "mms_sent_stamp";
    private static final String MMS_THREAD_COLOR = "mms_thread_color";
    private static final String SMILEY_MMS = "smiley_mms";

    Context context;

    private CheckBoxPreference mMmsColorConvo;
    private ColorPickerPreference mMmsColorConvoReceive;
    private ColorPickerPreference mMmsColorConvoSend;
    private CheckBoxPreference mMmsColorThread;
    private ColorPickerPreference mMmsColorThreadReceive;
    private ColorPickerPreference mMmsColorThreadSend;
    private CheckBoxPreference mMmsLogBlock;
    private CheckBoxPreference mMmsSmiley;
    private CheckBoxPreference mMmsTime;

    private void setPrefSummaryColor(ColorPickerPreference ColorPref) {
        if ((ColorPref == null) || (ColorPref == this.mMmsColorConvoSend)) {
            this.mMmsColorConvoSend.setSummary(this.mMmsColorConvoSend.getSummaryText() + ColorPickerPreference.convertToARGB(Settings.Global.getInt(getActivity().getContentResolver(), MMS_SEND_CONVO_COLOR, mMmsColorConvoSend.getPrefDefault())));
            if (ColorPref != null) {
                return;
            }
        }

        if ((ColorPref == null) || (ColorPref == this.mMmsColorConvoReceive)) {
            this.mMmsColorConvoReceive.setSummary(this.mMmsColorConvoReceive.getSummaryText() + ColorPickerPreference.convertToARGB(Settings.Global.getInt(getActivity().getContentResolver(), MMS_RECEIVE_CONVO_COLOR, mMmsColorConvoReceive.getPrefDefault())));
            if (ColorPref != null) {
                return;
            }
        }

        if ((ColorPref == null) || (ColorPref == this.mMmsColorThreadSend)) {
            this.mMmsColorThreadSend.setSummary(this.mMmsColorThreadSend.getSummaryText() + ColorPickerPreference.convertToARGB(Settings.Global.getInt(getActivity().getContentResolver(), MMS_SEND_THREAD_COLOR, mMmsColorThreadSend.getPrefDefault())));
            if (ColorPref != null) {
                return;
            }
        }

        if ((ColorPref == null) || (ColorPref == this.mMmsColorThreadReceive)) {
            this.mMmsColorThreadReceive.setSummary(this.mMmsColorThreadReceive.getSummaryText() + ColorPickerPreference.convertToARGB(Settings.Global.getInt(getActivity().getContentResolver(), MMS_RECEIVE_THREAD_COLOR, mMmsColorThreadReceive.getPrefDefault())));
            if (ColorPref != null) {
                return;
            }
        }
    }

    private void updateMmsPrefs() {
        boolean bool1 = this.mMmsColorConvo.isChecked();
        boolean bool2 = this.mMmsColorThread.isChecked();

        this.mMmsColorConvoSend.setPreviewDim(bool1);
        this.mMmsColorConvoReceive.setPreviewDim(bool1);
        this.mMmsColorThreadSend.setPreviewDim(bool2);
        this.mMmsColorThreadReceive.setPreviewDim(bool2);
    }


    public TWizAppsMessagingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Load the preferences from an XML resource */
        addPreferencesFromResource(R.xml.twizapps_mms_fragment);
        this.context = getActivity().getApplicationContext();
        PreferenceScreen localPreferenceScreen = getPreferenceScreen();
        ContentResolver localContentResolver = getActivity().getContentResolver();

        /* Set up checkbox stuff */
        this.mMmsColorConvo = ((CheckBoxPreference) localPreferenceScreen.findPreference(MMS_CONVO_COLOR));
        if (Settings.Global.getInt(localContentResolver, MMS_CONVO_COLOR, 0) != 0) {
            mMmsColorConvo.setChecked(true);
        } else {
            mMmsColorConvo.setChecked(false);
        }

        this.mMmsColorThread = ((CheckBoxPreference) localPreferenceScreen.findPreference(MMS_THREAD_COLOR));
        if (Settings.Global.getInt(localContentResolver, MMS_THREAD_COLOR, 0) != 0) {
            mMmsColorThread.setChecked(true);
        } else {
            mMmsColorThread.setChecked(false);
        }

        this.mMmsLogBlock = ((CheckBoxPreference) localPreferenceScreen.findPreference(MMSSMS_LOGBLOCK));
        if (Settings.Global.getInt(localContentResolver, MMSSMS_LOGBLOCK, 0) != 0) {
            mMmsLogBlock.setChecked(true);
        } else {
            mMmsLogBlock.setChecked(false);
        }

        this.mMmsSmiley = ((CheckBoxPreference) localPreferenceScreen.findPreference(SMILEY_MMS));
        if (Settings.Global.getInt(localContentResolver, SMILEY_MMS, 0) != 0) {
            mMmsSmiley.setChecked(true);
        } else {
            mMmsSmiley.setChecked(false);
        }

        this.mMmsTime = ((CheckBoxPreference) localPreferenceScreen.findPreference(MMS_SENT_STAMP));
        if (Settings.Global.getInt(localContentResolver, MMS_SENT_STAMP, 0) != 0) {
            mMmsTime.setChecked(true);
        } else {
            mMmsTime.setChecked(false);
        }

        /* Here are for preferences related to color picker */
        this.mMmsColorConvoSend = ((ColorPickerPreference) localPreferenceScreen.findPreference(MMS_SEND_CONVO_COLOR));
        this.mMmsColorConvoSend.setOnPreferenceChangeListener(this);

        this.mMmsColorConvoReceive = ((ColorPickerPreference) localPreferenceScreen.findPreference(MMS_RECEIVE_CONVO_COLOR));
        this.mMmsColorConvoReceive.setOnPreferenceChangeListener(this);

        this.mMmsColorThreadSend = ((ColorPickerPreference) localPreferenceScreen.findPreference(MMS_SEND_THREAD_COLOR));
        this.mMmsColorThreadSend.setOnPreferenceChangeListener(this);

        this.mMmsColorThreadReceive = ((ColorPickerPreference) localPreferenceScreen.findPreference(MMS_RECEIVE_THREAD_COLOR));
        this.mMmsColorThreadReceive.setOnPreferenceChangeListener(this);

        updateMmsPrefs();
        setPrefSummaryColor(null);
    }

    @Override
    public boolean onPreferenceChange(Preference paramPreference, Object paramObject) {
        int z;
        String t;

        /* For changes in color picker prefs... */
        if (paramPreference == this.mMmsColorConvoSend) {
            Settings.Global.putInt(getActivity().getContentResolver(), MMS_SEND_CONVO_COLOR, (Integer) paramObject);
            t = mMmsColorConvoSend.getPrefFlag();
            if (!TextUtils.isEmpty(t)) {
                z = Settings.Global.getInt(getActivity().getContentResolver(), t, 1);
                z *= -1;
                Settings.Global.putInt(getActivity().getContentResolver(), t, z);
            }
            setPrefSummaryColor(mMmsColorConvoSend);
            return true;
        }

        if (paramPreference == this.mMmsColorConvoReceive) {
            Settings.Global.putInt(getActivity().getContentResolver(), MMS_RECEIVE_CONVO_COLOR, (Integer) paramObject);
            t = mMmsColorConvoReceive.getPrefFlag();
            if (!TextUtils.isEmpty(t)) {
                z = Settings.Global.getInt(getActivity().getContentResolver(), t, 1);
                z *= -1;
                Settings.Global.putInt(getActivity().getContentResolver(), t, z);
            }
            setPrefSummaryColor(mMmsColorConvoReceive);
            return true;
        }

        if (paramPreference == this.mMmsColorThreadSend) {
            Settings.Global.putInt(getActivity().getContentResolver(), MMS_SEND_THREAD_COLOR, (Integer) paramObject);
            t = mMmsColorThreadSend.getPrefFlag();
            if (!TextUtils.isEmpty(t)) {
                z = Settings.Global.getInt(getActivity().getContentResolver(), t, 1);
                z *= -1;
                Settings.Global.putInt(getActivity().getContentResolver(), t, z);
            }
            setPrefSummaryColor(mMmsColorThreadSend);
            return true;
        }

        if (paramPreference == this.mMmsColorThreadReceive) {
            Settings.Global.putInt(getActivity().getContentResolver(), MMS_RECEIVE_THREAD_COLOR, (Integer) paramObject);
            t = mMmsColorThreadReceive.getPrefFlag();
            if (!TextUtils.isEmpty(t)) {
                z = Settings.Global.getInt(getActivity().getContentResolver(), t, 1);
                z *= -1;
                Settings.Global.putInt(getActivity().getContentResolver(), t, z);
            }
            setPrefSummaryColor(mMmsColorThreadReceive);
            return true;
        }

        return true;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, @NonNull Preference paramPreference) {
        int i;
        String str1;
        String str2;
        ContentResolver localContentResolver = getActivity().getContentResolver();

        /* Deal with checkbox stuff first */
        if (paramPreference == this.mMmsColorConvo) {
            if (this.mMmsColorConvo.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, MMS_CONVO_COLOR, i);
            updateMmsPrefs();
            return true;
        }

        if (paramPreference == this.mMmsColorThread) {
            if (this.mMmsColorThread.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, MMS_THREAD_COLOR, i);
            updateMmsPrefs();
            return true;
        }

        if (paramPreference == this.mMmsLogBlock) {
            if (this.mMmsLogBlock.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, MMSSMS_LOGBLOCK, i);
            return true;
        }

        if (paramPreference == this.mMmsSmiley) {
            if (this.mMmsSmiley.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, SMILEY_MMS, i);
            return true;
        }

        if (paramPreference == this.mMmsTime) {
            if (this.mMmsTime.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, MMS_SENT_STAMP, i);
            return true;
        }

        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateMmsPrefs();
        setPrefSummaryColor(null);
    }
}