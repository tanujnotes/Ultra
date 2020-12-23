package app.olauncher.light;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;

public class Prefs {

    private static final String PREF = "app.olauncher.light";
    private static final String HOME_ALIGNMENT = "HOME_ALIGNMENT";

    private final SharedPreferences sharedPreferences;

    public Prefs(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    private SharedPreferences getSharedPref() {
        return sharedPreferences;
    }

    public int getHomeAlignment() {
        return getSharedPref().getInt(HOME_ALIGNMENT, Gravity.CENTER);
    }

    public void setHomeAlignment(int gravity) {
        getSharedPref().edit().putInt(HOME_ALIGNMENT, gravity).apply();
    }
}
