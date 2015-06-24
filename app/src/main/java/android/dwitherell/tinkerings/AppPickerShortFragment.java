package android.dwitherell.tinkerings;

import android.app.Activity;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.dwitherell.tinkerings.utils.AppPickerAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by devonwitherell on 6/14/2015.
 */
public class AppPickerShortFragment extends ListFragment {

    private static final int REQUEST_CREATE_SHORTCUT = 3;

    private class LoadList extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog = null;
        private Activity context = null;
        private ListView mList = null;
        private PackageManager packageManager;
        private ArrayList<ResolveInfo> shortcutList = new ArrayList<ResolveInfo>();
        private ArrayList<Intent> intentList = new ArrayList<Intent>();

        public LoadList setInits(Activity context, ListView list, PackageManager pmanager) {
            this.context = context;
            mList = list;
            packageManager = pmanager;
            return this;
        }

        @Override
        protected void onPreExecute() {
            // The progress dialog here is so the user will wait until the prop stuff has loaded

            dialog = new ProgressDialog(context);
            dialog.setTitle("Hold on a sec");
            dialog.setMessage("Loading stuff...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // this loads up the listview - can take a sec

            List<ResolveInfo> shorts = packageManager.queryIntentActivities(new Intent(Intent.ACTION_CREATE_SHORTCUT),0);
            //alphabetizes list
            Collections.sort(shorts, new ResolveInfo.DisplayNameComparator(packageManager));

            for (ResolveInfo info : shorts) {
                shortcutList.add(info);
                // creates intent list with shortcut creating intents
                Intent shortintent = new Intent(Intent.ACTION_CREATE_SHORTCUT);
                shortintent.setComponent(new ComponentName(info.activityInfo.applicationInfo.packageName, info.activityInfo.name));
                intentList.add(shortintent);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //if it worked - set up adapter and onitemclick
            final AppPickerAdapter adapter = new AppPickerAdapter(context, packageManager, null, shortcutList, 0, 0);
            setListAdapter(adapter);

            mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    startActivityForResult(intentList.get(position), REQUEST_CREATE_SHORTCUT);
                }
            });
            dialog.dismiss();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return (View) inflater.inflate(R.layout.listviewmain, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView listView = getListView();
        listView.setTextFilterEnabled(true);
        (new LoadList()).setInits(getActivity(), listView, getActivity().getPackageManager()).execute();
    }

    @Override
    public void onActivityResult(int paramRequest, int paramResult, Intent paramData) {
        super.onActivityResult(paramRequest, paramResult, paramData);
        if (paramResult != -1 || paramData == null) {
            return;
        }
        switch (paramRequest)
        {
            case REQUEST_CREATE_SHORTCUT:
                completeSetCustom(paramData);
                return;
            default:
        }
    }

    public AppPickerShortFragment() {}

    private void completeSetCustom(Intent intentkey) {
        // pull out relevant details from original intent
        Intent localIntent = intentkey.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);
        localIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, intentkey.getStringExtra(Intent.EXTRA_SHORTCUT_NAME));

        String keystring = localIntent.toUri(0).replaceAll("com.android.contacts.action.QUICK_CONTACT", Intent.ACTION_VIEW);

        Settings.Global.putString(getActivity().getContentResolver(), TinkerActivity.mPrefKey, keystring);
        ((TinkerActivity) getActivity()).onBackPressed();
    }
}
