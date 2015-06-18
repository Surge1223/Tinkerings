package android.dwitherell.tinkerings;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.annotation.NonNull;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by devonwitherell on 6/14/2015.
 */
public class BuildPropFragment extends ListFragment {
    private ListView listView;

    private class LoadProp extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog = null;
        private Context context = null;
        private ListView mList = null;
        private boolean mTryCatchFail;
        private boolean mIsRestore;
        private Properties prop;
        private String[] pTitle;
        private ArrayList<Map<String, String>> proplist;
        private String[] from = { "title", "description" };
        private int[] to = { R.id.prop_title, R.id.prop_desc };

        public LoadProp setInits(Context context, ListView list, Boolean restore) {
            this.context = context;
            mList = list;
            mIsRestore = restore;
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
            // copy over backup if from restore trigger
            if (mIsRestore) {
                try {
                    restorefile();
                } catch (Exception e) {
                    mTryCatchFail = true;
                }
            }
            // this loads up the listview - can take a sec
            String fileloc = createTempFile();
            if (fileloc.equalsIgnoreCase("error")) {
                mTryCatchFail = true;
            }

            prop = new Properties();
            File file = new File(fileloc);
            try {
                prop.load(new FileInputStream(file));
                pTitle = (String[])prop.keySet().toArray(new String[prop.keySet().size()]);
                final List<String> pDesc = new ArrayList<String>();
                for (int i = 0; i < pTitle.length; i++) {
                    pDesc.add(prop.getProperty(pTitle[i]));
                }

                proplist = buildData(pTitle, pDesc);
            } catch (IOException e) {
                mTryCatchFail = true;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //if it worked - set up adapter and onitemclick
            if (!mTryCatchFail) {
                SimpleAdapter adapter = new SimpleAdapter(context, proplist, R.layout.propeditlistitem, from, to);
                setListAdapter(adapter);

                mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        showEdit(pTitle[position], prop.getProperty(pTitle[position]));
                    }
                });
            }

            dialog.dismiss();
            if (mTryCatchFail) {
                Toast.makeText(context, "Error occurred", Toast.LENGTH_SHORT).show();
            } else if (mIsRestore){
                Toast.makeText(context, "build.prop restored from " + Environment.getExternalStorageDirectory().getAbsolutePath() + "/build.prop.bak", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return (View) inflater.inflate(R.layout.propeditmain, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = getListView();
        listView.setTextFilterEnabled(true);
        (new LoadProp()).setInits(getActivity(), listView, false).execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_restore:
                (new LoadProp()).setInits(getActivity(), listView, true).execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public BuildPropFragment() {}

    public ArrayList<Map<String, String>> buildData(String[] t, List<String> d) {
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

        for (int i = 0; i < t.length; ++i) {
            list.add(putData(t[i], d.get(i)));
        }

        return list;
    }

    public HashMap<String, String> putData(String title, String description) {
        HashMap<String, String> item = new HashMap<String, String>();

        item.put("title", title);
        item.put("description", description);

        return item;
    }

    public void restorefile() {
        try {
            Shell.SU.run("mount -o remount,rw  /system");
            Shell.SU.run("mv -f /system/build.prop /system/build.prop.bak");
            Shell.SU.run("cp -f " + Environment.getExternalStorageDirectory().getAbsolutePath() + "/build.prop.bak /system/build.prop");
            Shell.SU.run("chmod 644 /system/build.prop");
            Shell.SU.run("mount -o remount,ro  /system");
        } catch (Exception e) {
        }
    }

    public void showEdit(String name, String key) {
        ((TinkerActivity)getActivity()).displayEditProp(name, key);
    }

    public String createTempFile() {
        try {
            Shell.SU.run("cp -f /system/build.prop " + Environment.getExternalStorageDirectory().getAbsolutePath() + "/buildprop.tmp");
            Shell.SU.run("chmod 777 " + Environment.getExternalStorageDirectory().getAbsolutePath() + "/buildprop.tmp");
            return (String) Environment.getExternalStorageDirectory().getAbsolutePath() + "/buildprop.tmp";
        } catch (Exception e) {
            return (String) "error";
        }
    }
}
