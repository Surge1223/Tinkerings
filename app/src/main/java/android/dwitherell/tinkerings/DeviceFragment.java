package android.dwitherell.tinkerings;

/**
 * Created by devonwitherell on 1/1/2015.
 */
import android.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class DeviceFragment extends PreferenceFragment {
    public DeviceFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.device_fragment);
    }
}
