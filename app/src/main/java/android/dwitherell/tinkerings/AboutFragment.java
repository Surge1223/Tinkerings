package android.dwitherell.tinkerings;

/**
 * Created by devonwitherell on 1/8/2015.
 */

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class AboutFragment extends Fragment {

    Context context;
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

        this.context = getActivity().getApplicationContext();
        this.popUpInfo = null;
        clickCount = 0;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.about_frag_card, container, false);

        final LinearLayout logo = (LinearLayout)v.findViewById(R.id.logo_card);
        LinearLayout link = (LinearLayout)v.findViewById(R.id.link1_card);
        LinearLayout thanks = (LinearLayout)v.findViewById(R.id.credits_card);

        thanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popUpInfo == null || !popUpInfo.isShowing()) {
                    getStartDialog().show();
                    logo.setClickable(true);
                }
            }
        });

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent link = new Intent(Intent.ACTION_VIEW);
                Uri url = Uri.parse(getString(R.string.link_data));
                link.setData(url);
                startActivity(link);
            }
        });

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCount++;
                if (clickCount >= 5) {
                    clickCount = 0;
                    ((TinkerActivity) getActivity()).displayExtra();
                }
            }
        });

        logo.setClickable(false);
        return v;
    }
}