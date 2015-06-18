package android.dwitherell.tinkerings;

/**
 * Created by devonwitherell on 1/8/2015.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

public class AboutFragment extends PreferenceFragment {
    private static final String INFOLINK = "infolink";
    private static final String ODLOGO = "od_logo";

    Context context;
    private Preference mInfoDialog;
    private Preference mLogoLayout;
    private AlertDialog popUpInfo;
    private int clickCount;

    private AlertDialog getDialog(boolean isStart) {
        // custom title textview
        TextView alertTitle = new TextView(context);
        alertTitle.setText(isStart ? getString(R.string.alertdiagtitle) : getString(R.string.alertthankstitle));
        alertTitle.setBackgroundColor(Color.BLACK);
        alertTitle.setTextColor(Color.WHITE);
        alertTitle.setGravity(Gravity.CENTER);
        alertTitle.setPadding(10, 10, 10, 10);
        alertTitle.setTextSize(25);

        // custom message webview
        WebView alertView = new WebView(context);
        alertView.setBackgroundColor(Color.BLACK);
        alertView.loadDataWithBaseURL(null, isStart ? getString(R.string.changelog) : getString(R.string.credits), "text/html", "UTF-8", null);

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this.getActivity().getWindow().getContext(), android.R.style.Theme_Dialog));
        builder.setCustomTitle(alertTitle);
        builder.setView(alertView);
        builder.setCancelable(false); // This forces button press to clear dialog

        // Ok button
        builder.setPositiveButton(R.string.setpositive, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        // Credits button, but only on initial dialog
        if (isStart) {
            builder.setNegativeButton(R.string.setnegative, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    getThanksDialog().show();
                }
            });
        }

        popUpInfo = builder.create();

        return popUpInfo;
    }

    private AlertDialog getStartDialog() {
        return getDialog(true);
    }

    private AlertDialog getThanksDialog() {
        return getDialog(false);
    }

    public AboutFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Load the preferences from an XML resource */
        addPreferencesFromResource(R.xml.about_fragment);

        this.context = getActivity().getApplicationContext();
        PreferenceScreen localPreferenceScreen = getPreferenceScreen();

        this.popUpInfo = null;
        clickCount = 0;

        // This is for miscellaneous preferences
        this.mInfoDialog = localPreferenceScreen.findPreference(INFOLINK);
        this.mLogoLayout = localPreferenceScreen.findPreference(ODLOGO);
        this.mLogoLayout.setSelectable(false);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, @NonNull Preference paramPreference) {
        /* For miscellaneous pref things */
        if (paramPreference == this.mInfoDialog) {
            if (popUpInfo == null || !popUpInfo.isShowing()) {
                getStartDialog().show();
            }
            this.mLogoLayout.setSelectable(true);
            return true;
        }

        if (paramPreference == this.mLogoLayout) {
            clickCount += 1;

            if (clickCount > 5) {
                clickCount = 0;
                ((TinkerActivity)getActivity()).displayExtra();
            }
            return true;
        }

        return false;
    }

}