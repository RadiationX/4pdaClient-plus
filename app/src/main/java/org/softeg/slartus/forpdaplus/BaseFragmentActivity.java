package org.softeg.slartus.forpdaplus;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.softeg.slartus.forpdaapi.search.SearchSettings;
import org.softeg.slartus.forpdacommon.ExtPreferences;
import org.softeg.slartus.forpdaplus.search.ui.SearchActivity;
import org.softeg.slartus.forpdaplus.search.ui.SearchSettingsDialogFragment;

/**
 * User: slinkin
 * Date: 14.03.12
 * Time: 12:51
 */
public class BaseFragmentActivity extends ActionBarActivity
        implements SearchSettingsDialogFragment.ISearchDialogListener {
    public static final String SENDER_ACTIVITY = "sender_activity";
    public static final String FORCE_EXIT_APPLICATION = "org.softeg.slartus.forpdaplus.FORCE_EXIT_APPLICATION";

    protected void afterCreate() {

    }

    public Context getContext() {
        return this;
    }

    /*public ActionBar getSupportActionBar() {
        return getSupportActionBar();
    }
    public void setSupportProgressBarIndeterminateVisibility(boolean b) {
        setSupportProgressBarIndeterminateVisibility(b);
    }
    public void setSupportProgressBarIndeterminate(boolean b) {
        setSupportProgressBarIndeterminate(b);
    }*/

    @Override
    public void startActivity(android.content.Intent intent) {
        intent.putExtra(BaseFragmentActivity.SENDER_ACTIVITY, getClass().toString());
        super.startActivity(intent);
    }

    @Override
    public void startActivityForResult(android.content.Intent intent, int requestCode) {
        intent.putExtra(BaseFragmentActivity.SENDER_ACTIVITY, getClass().toString());
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkIfForceKill();
    }

    private void checkIfForceKill() {
        //CHECK IF I NEED TO KILL THE APP
        // Restore preferences
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(this);
        boolean forceKill = settings.getBoolean(
                FORCE_EXIT_APPLICATION, false);

        if (forceKill) {
            //CLEAR THE FORCE_EXIT SETTINGS
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(FORCE_EXIT_APPLICATION, false);
            // Commit the edits!
            editor.commit();
            //HERE STOP ALL YOUR SERVICES
            finish();
        }
    }

    protected Bundle args = new Bundle();

    @Override
    public void doSearchDialogPositiveClick(SearchSettings searchSettings) {
        SearchActivity.startForumSearch(this, searchSettings);
    }

    @Override
    public void doSearchDialogNegativeClick() {

    }

    protected boolean isTransluent() {
        return false;
    }

    @Override
    protected void onCreate(Bundle saveInstance) {
        setTheme(isTransluent() ? App.getInstance().getTransluentThemeStyleResID() : App.getInstance().getThemeStyleResID());
        super.onCreate(saveInstance);
        if(PreferenceManager.getDefaultSharedPreferences(App.getContext()).getBoolean("statusbarTransparent",false)) {
            if (Integer.valueOf(android.os.Build.VERSION.SDK) > 19) {
                getWindow().setStatusBarColor(Color.TRANSPARENT);
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
        }else {
            if (Integer.valueOf(android.os.Build.VERSION.SDK) == 19) {
                SystemBarTintManager tintManager = new SystemBarTintManager(this);
                tintManager.setStatusBarTintEnabled(true);
                if (App.getInstance().getCurrentThemeName().equals("white")) {
                    tintManager.setTintColor(getResources().getColor(R.color.statusBar_wh));
                } else if (App.getInstance().getCurrentThemeName().equals("black")) {
                    tintManager.setTintColor(getResources().getColor(R.color.statusBar_bl));
                }
            } else {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LinearLayout statusBarLay = (LinearLayout) inflater.inflate(R.layout.statusbar, null);
                LinearLayout statusBar = (LinearLayout) statusBarLay.findViewById(R.id.statusBar);
                if (App.getInstance().getCurrentThemeName().equals("white")) {
                    statusBar.setBackgroundColor(getResources().getColor(R.color.statusBar_wh));
                } else if (App.getInstance().getCurrentThemeName().equals("black")) {
                    statusBar.setBackgroundColor(getResources().getColor(R.color.statusBar_bl));
                }
                ViewGroup decor = (ViewGroup) getWindow().getDecorView();
                decor.addView(statusBarLay);
            }
        }



        args.clear();
        if (getIntent().getExtras() != null) {
            args.putAll(getIntent().getExtras());
        }
        if (saveInstance != null) {
            args.putAll(saveInstance);
        }

        afterCreate();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        loadPreferences(prefs);
    }

    @Override
    protected void onSaveInstanceState(android.os.Bundle outState) {
        if (args != null)
            outState.putAll(args);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(android.os.Bundle outState) {
        args = outState;
        super.onRestoreInstanceState(outState);
    }

    public SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    protected void loadPreferences(SharedPreferences prefs) {
        setRequestedOrientation(ExtPreferences.parseInt(prefs, "theme.ScreenOrientation", -1));
    }


}