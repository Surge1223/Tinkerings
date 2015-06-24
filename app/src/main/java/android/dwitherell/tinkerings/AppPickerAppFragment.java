package android.dwitherell.tinkerings;

import android.app.Activity;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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
public class AppPickerAppFragment extends ListFragment {

    private class LoadList extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog = null;
        private Activity context = null;
        private ListView mList = null;
        private PackageManager packageManager;
        private ArrayList<ApplicationInfo> packageList;

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
            List<ApplicationInfo> apps = packageManager.getInstalledApplications(0);
            packageList = new ArrayList<ApplicationInfo>();
            for (ApplicationInfo info : apps) {
                //basically only loads up things visible to launcher
                if (packageManager.getLaunchIntentForPackage(info.packageName) != null) {
                    packageList.add(info);
                }
            }
            //alphabetizes list
            Collections.sort(packageList, new ApplicationInfo.DisplayNameComparator(packageManager));

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //if it worked - set up adapter and onitemclick
            final AppPickerAdapter adapter = new AppPickerAdapter(context, packageManager, packageList, null, 0, 0);
            setListAdapter(adapter);

            mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String target = packageManager.getLaunchIntentForPackage(((ApplicationInfo)adapter.getItem(position)).packageName).toUri(0);
                    String label = packageManager.getApplicationLabel((ApplicationInfo)adapter.getItem(position)).toString();

                    completeSetCustom(target);
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

    public AppPickerAppFragment() {}

    private void completeSetCustom(String intentkey) {
        Settings.Global.putString(getActivity().getContentResolver(), TinkerActivity.mPrefKey, intentkey);
        ((TinkerActivity) getActivity()).onBackPressed();
    }
}
