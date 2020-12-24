package app.olauncher.light;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;

public class Prefs {

    private static final String PREF = "app.olauncher.light";
    private static final String HOME_ALIGNMENT = "HOME_ALIGNMENT";
    private static final String SHOW_LOCK_POPUP = "SHOW_LOCK_POPUP";
    private static final String SCREEN_TIMEOUT = "SCREEN_TIMEOUT";

    private static final String APP_NAME_1 = "APP_NAME_1";
    private static final String APP_NAME_2 = "APP_NAME_2";
    private static final String APP_NAME_3 = "APP_NAME_3";
    private static final String APP_NAME_4 = "APP_NAME_4";
    private static final String APP_NAME_5 = "APP_NAME_5";
    private static final String APP_NAME_6 = "APP_NAME_6";
    private static final String APP_PACKAGE_1 = "APP_PACKAGE_1";
    private static final String APP_PACKAGE_2 = "APP_PACKAGE_2";
    private static final String APP_PACKAGE_3 = "APP_PACKAGE_3";
    private static final String APP_PACKAGE_4 = "APP_PACKAGE_4";
    private static final String APP_PACKAGE_5 = "APP_PACKAGE_5";
    private static final String APP_PACKAGE_6 = "APP_PACKAGE_6";
    private static final String APP_USER_HANDLE_1 = "APP_USER_HANDLE_1";
    private static final String APP_USER_HANDLE_2 = "APP_USER_HANDLE_2";
    private static final String APP_USER_HANDLE_3 = "APP_USER_HANDLE_3";
    private static final String APP_USER_HANDLE_4 = "APP_USER_HANDLE_4";
    private static final String APP_USER_HANDLE_5 = "APP_USER_HANDLE_5";
    private static final String APP_USER_HANDLE_6 = "APP_USER_HANDLE_6";


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

    public boolean getShowLockPopup() {
        return getSharedPref().getBoolean(SHOW_LOCK_POPUP, true);
    }

    public void setShowLockPopup(boolean value) {
        getSharedPref().edit().putBoolean(SHOW_LOCK_POPUP, value).apply();
    }

    public int getScreenTimeout() {
        return getSharedPref().getInt(SCREEN_TIMEOUT, 30000);
    }

    public void setScreenTimeout(int value) {
        getSharedPref().edit().putInt(SCREEN_TIMEOUT, value).apply();
    }

    public String getAppName(int location) {
        switch (location) {
            case 1:
                return getSharedPref().getString(APP_NAME_1, "");
            case 2:
                return getSharedPref().getString(APP_NAME_2, "");
            case 3:
                return getSharedPref().getString(APP_NAME_3, "");
            case 4:
                return getSharedPref().getString(APP_NAME_4, "");
            case 5:
                return getSharedPref().getString(APP_NAME_5, "");
            case 6:
                return getSharedPref().getString(APP_NAME_6, "");
            default:
                return "";
        }
    }

    public String getAppPackage(int location) {
        switch (location) {
            case 1:
                return getSharedPref().getString(APP_PACKAGE_1, "");
            case 2:
                return getSharedPref().getString(APP_PACKAGE_2, "");
            case 3:
                return getSharedPref().getString(APP_PACKAGE_3, "");
            case 4:
                return getSharedPref().getString(APP_PACKAGE_4, "");
            case 5:
                return getSharedPref().getString(APP_PACKAGE_5, "");
            case 6:
                return getSharedPref().getString(APP_PACKAGE_6, "");
            default:
                return "";
        }
    }

    public String getAppUserHandle(int location) {
        switch (location) {
            case 1:
                return getSharedPref().getString(APP_USER_HANDLE_1, "");
            case 2:
                return getSharedPref().getString(APP_USER_HANDLE_2, "");
            case 3:
                return getSharedPref().getString(APP_USER_HANDLE_3, "");
            case 4:
                return getSharedPref().getString(APP_USER_HANDLE_4, "");
            case 5:
                return getSharedPref().getString(APP_USER_HANDLE_5, "");
            case 6:
                return getSharedPref().getString(APP_USER_HANDLE_6, "");
            default:
                return "";
        }
    }

    public void setHomeApp(MainActivity.AppModel app, int location) {
        switch (location) {
            case 1:
                getSharedPref().edit().putString(APP_NAME_1, app.appLabel).apply();
                getSharedPref().edit().putString(APP_PACKAGE_1, app.appPackage).apply();
                getSharedPref().edit().putString(APP_USER_HANDLE_1, app.userHandle.toString()).apply();
                break;
            case 2:
                getSharedPref().edit().putString(APP_NAME_2, app.appLabel).apply();
                getSharedPref().edit().putString(APP_PACKAGE_2, app.appPackage).apply();
                getSharedPref().edit().putString(APP_USER_HANDLE_2, app.userHandle.toString()).apply();
                break;
            case 3:
                getSharedPref().edit().putString(APP_NAME_3, app.appLabel).apply();
                getSharedPref().edit().putString(APP_PACKAGE_3, app.appPackage).apply();
                getSharedPref().edit().putString(APP_USER_HANDLE_3, app.userHandle.toString()).apply();
                break;
            case 4:
                getSharedPref().edit().putString(APP_NAME_4, app.appLabel).apply();
                getSharedPref().edit().putString(APP_PACKAGE_4, app.appPackage).apply();
                getSharedPref().edit().putString(APP_USER_HANDLE_4, app.userHandle.toString()).apply();
                break;
            case 5:
                getSharedPref().edit().putString(APP_NAME_5, app.appLabel).apply();
                getSharedPref().edit().putString(APP_PACKAGE_5, app.appPackage).apply();
                getSharedPref().edit().putString(APP_USER_HANDLE_5, app.userHandle.toString()).apply();
                break;
            case 6:
                getSharedPref().edit().putString(APP_NAME_6, app.appLabel).apply();
                getSharedPref().edit().putString(APP_PACKAGE_6, app.appPackage).apply();
                getSharedPref().edit().putString(APP_USER_HANDLE_6, app.userHandle.toString()).apply();
                break;
        }
    }
}
