package org.softeg.slartus.forpdaplus;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.softeg.slartus.forpdaapi.IHttpClient;
import org.softeg.slartus.forpdaapi.ProfileApi;
import org.softeg.slartus.forpdacommon.*;
import org.softeg.slartus.forpdaplus.common.AppLog;
import org.softeg.slartus.forpdaplus.prefs.Preferences;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShortUserInfo {
    private Activity mActivity;
    private SharedPreferences prefs;
    private ImageView imgAvatar;
    TextView textView;

    public ShortUserInfo(Activity activity) {
        enableStrictMode();
        prefs = PreferenceManager.getDefaultSharedPreferences(App.getInstance());

        mActivity = activity;
        textView = (TextView) findViewById(R.id.viewa);
        imgAvatar = (ImageView) findViewById(R.id.imgAvatara);

        updateInfo();

    }

    private Context getContext() {
        return mActivity;
    }

    private View findViewById(int id) {
        return mActivity.findViewById(id);
    }

    public void enableStrictMode() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    private void updateInfo(){
        try {
            if (Preferences.isLoadShortUserInfo()) {
                new Picasso.Builder(App.getInstance()).build()
                        .load(prefs.getString("shortAvatarUrl", "http://s.4pda.to/img/qms/logo.png"))
                        .error(R.drawable.no_image)
                        .into(imgAvatar);

            } else {
                String avatarUrl = ProfileApi.getUserAvatar(Client.getInstance(), Client.getInstance().UserId);

                Picasso.Builder builder = new Picasso.Builder(App.getInstance());
                builder.downloader(new Downloader() {
                    @Override
                    public Response load(Uri uri, int networkPolicy) throws IOException {
                        HttpResponse httpResponse = new HttpHelper().getDownloadResponse(uri.toString(), 0);
                        return new Response(httpResponse.getEntity().getContent(), false, httpResponse.getEntity().getContentLength());
                    }

                    @Override
                    public void shutdown() {
                    }
                });
                builder.build()
                        .load(avatarUrl)
                        .error(R.drawable.no_image)
                        .into(imgAvatar);
                prefs.edit().putBoolean("isLoadShortUserInfo", true).apply();
                prefs.edit().putString("shortAvatarUrl", avatarUrl).apply();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
