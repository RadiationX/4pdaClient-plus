package org.softeg.slartus.forpdaplus.classes;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.WebView;

/**
 * Created by IntelliJ IDEA.
 * User: slinkin
 * Date: 24.10.12
 * Time: 8:32
 * To change this template use File | Settings | File Templates.
 */
public interface IWebViewContainer {
    String Prefix();

    WebView getWebView();

    Window getWindow();

    ActionBar getSupportActionBar();

    void nextPage();

    void prevPage();

    boolean dispatchSuperKeyEvent(KeyEvent event);
}
