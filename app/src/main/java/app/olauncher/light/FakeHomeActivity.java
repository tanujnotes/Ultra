package app.olauncher.light;

import android.app.Activity;
import android.os.Bundle;

public class FakeHomeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake);
    }
}
