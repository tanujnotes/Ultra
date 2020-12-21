package app.olauncher.light;


import android.app.Activity;
import android.os.Bundle;

import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(FLAG_LAYOUT_NO_LIMITS);
    }
}