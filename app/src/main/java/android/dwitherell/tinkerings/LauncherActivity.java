package android.dwitherell.tinkerings;

/**
 * Created by devonwitherell on 1/9/2015.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class LauncherActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent launch = new Intent(this, TinkerActivity.class);
        startActivity(launch);
        finish();
    }
}
