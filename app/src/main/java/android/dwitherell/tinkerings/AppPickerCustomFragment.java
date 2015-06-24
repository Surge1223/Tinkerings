package android.dwitherell.tinkerings;

import android.app.ListFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.dwitherell.tinkerings.utils.AppPickerAdapter;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.annotation.NonNull;

/**
 * Created by devonwitherell on 6/14/2015.
 */
public class AppPickerCustomFragment extends ListFragment {

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
        final PackageManager packageManager = getActivity().getPackageManager();

        final AppPickerAdapter adapter = new AppPickerAdapter(getActivity(), packageManager, null, null, TinkerActivity.mTitleArray, TinkerActivity.mIconArray);
        setListAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                completeSetCustom(position);
            }
        });
    }

    public AppPickerCustomFragment() {}

    private void completeSetCustom(int position) {
        String mEntry = null;
        String mLabel = null;
        String[] mKeys = getResources().getStringArray(TinkerActivity.mKeyArray);

        switch (position) {
            case 0:
                mEntry = mKeys[position];
                mLabel = getResources().getStringArray(TinkerActivity.mTitleArray)[position];
                break;
            case 1:
                final Intent fragintent = new Intent(getActivity(), TinkerActivity.class);
                fragintent.putExtra(TinkerActivity.EXTRA_START_FRAGMENT, 0);
                fragintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_CLEAR_TASK);

                mEntry = fragintent.toUri(0);
                mLabel = getResources().getStringArray(TinkerActivity.mTitleArray)[position];
                break;
            case 2:
                mEntry = mKeys[position];
                mLabel = getResources().getStringArray(TinkerActivity.mTitleArray)[position];
                break;
            case 3:
                mEntry = mKeys[position];
                mLabel = getResources().getStringArray(TinkerActivity.mTitleArray)[position];
                break;
        }

        Settings.Global.putString(getActivity().getContentResolver(), TinkerActivity.mPrefKey, mEntry);
        ((TinkerActivity) getActivity()).onBackPressed();
    }
}
