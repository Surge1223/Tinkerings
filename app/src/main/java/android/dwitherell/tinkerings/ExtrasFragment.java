package android.dwitherell.tinkerings;

/**
 * Created by devonwitherell on 2/6/2015.
 */

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;

import eu.chainfire.libsuperuser.Shell;

public class ExtrasFragment extends PreferenceFragment {
    private static final String TSMPENWINDOW = "tsm_parts_penwindow";
    private static final String PROXSENSORSCRIPT = "script_proxsensor";
    private static final String PROXSENSORSCRIPT2 = "script_proxsensor2";
    private static final String BUILDPROPEDITOR = "buildpropeditor";

    private Preference mPenWindow;
    private Preference mProxScript;
    private Preference mProxScript2;
    private Preference mBuildPropEditor;

    private Rect mDrawRect;

    public ExtrasFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Load the preferences from an XML resource */
        addPreferencesFromResource(R.xml.extras_fragment);

        PreferenceScreen localPreferenceScreen = getPreferenceScreen();

        this.mDrawRect = null;

        // This is for miscellaneous preferences
        this.mPenWindow = localPreferenceScreen.findPreference(TSMPENWINDOW);
        this.mProxScript = localPreferenceScreen.findPreference(PROXSENSORSCRIPT);
        this.mProxScript2 = localPreferenceScreen.findPreference(PROXSENSORSCRIPT2);
        this.mBuildPropEditor = localPreferenceScreen.findPreference(BUILDPROPEDITOR);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, @NonNull Preference paramPreference) {
        /* For miscellaneous pref things */
        if (paramPreference == this.mPenWindow) {
            this.mDrawRect = new Rect(300, 400, 800, 1000);
            Intent localIntent = new Intent("com.sec.android.action.MULTIWINDOW_SMART_WINDOW_LAUNCH");
            localIntent.setClassName("com.sec.android.app.FlashBarService", "com.sec.android.app.FlashBarService.MultiWindowTrayService");
            Bundle localBundle = new Bundle();
            localBundle.putParcelable("cropRect", this.mDrawRect);
            localIntent.putExtras(localBundle);
            getActivity().startService(localIntent);

            return true;
        }

        if (paramPreference == this.mProxScript) {
            String str = "proxsensorfix";
            Shell.SU.run(str);

            return true;
        }

        if (paramPreference == this.mProxScript2) {
            String str = "proxsensorfix2";
            Shell.SU.run(str);

            return true;
        }

        if (paramPreference == this.mBuildPropEditor) {
            ((TinkerActivity)getActivity()).displayBuildPropEditor();

            return true;
        }

        return false;
    }

}
