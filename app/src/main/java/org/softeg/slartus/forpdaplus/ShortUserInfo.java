package org.softeg.slartus.forpdaplus;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import org.softeg.slartus.forpdaapi.LoginResult;
import org.softeg.slartus.forpdaapi.ProfileApi;
import org.softeg.slartus.forpdacommon.*;
import org.softeg.slartus.forpdaplus.common.AppLog;
import org.softeg.slartus.forpdaplus.listfragments.ListFragmentActivity;
import org.softeg.slartus.forpdaplus.listtemplates.QmsContactsBrickInfo;
import org.softeg.slartus.forpdaplus.prefs.Preferences;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShortUserInfo {
    private Activity mActivity;
    private SharedPreferences prefs;
    private CircleImageView imgAvatar;
    private ImageView infoRefresh;
    private TextView userNick;
    private TextView qmsMessages;
    private TextView loginButton;
    private TextView userRep;
    private RelativeLayout textWrapper;
    private Handler mHandler=new Handler();

    public ShortUserInfo(Activity activity) {
        enableStrictMode();
        prefs = PreferenceManager.getDefaultSharedPreferences(App.getInstance());

        mActivity = activity;
        userNick = (TextView) findViewById(R.id.userNick);
        qmsMessages = (TextView) findViewById(R.id.qmsMessages);
        loginButton = (TextView) findViewById(R.id.loginButton);
        userRep = (TextView) findViewById(R.id.userRep);
        textWrapper = (RelativeLayout) findViewById(R.id.textWrapper);
        imgAvatar = (CircleImageView) findViewById(R.id.imgAvatara);
        infoRefresh = (ImageView) findViewById(R.id.infoRefresh);

        updateInfo();
        Client.getInstance().addOnUserChangedListener(new Client.OnUserChangedListener() {
            @Override
            public void onUserChanged(String user, Boolean success) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        refreshQms();
                    }
                });
            }
        });
        Client.getInstance().addOnMailListener(new Client.OnMailListener() {
            @Override
            public void onMail(int count) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        refreshQms();
                    }
                });
            }
        });
        infoRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateInfo();
            }
        });

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
            if(Client.getInstance().getLogined()){
                String[] strings = ProfileApi.getUserInfo(Client.getInstance(), Client.getInstance().UserId);
                loginButton.setVisibility(View.GONE);
                textWrapper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ListFragmentActivity.showListFragment(getContext(), QmsContactsBrickInfo.NAME, null);
                    }
                });
                userNick.setText(Client.getInstance().getUser());
                userRep.setText("Репутация:"+strings[1]);
                int qmsCount = Client.getInstance().getQmsCount();
                if(qmsCount!=0){
                    qmsMessages.setText("Новые сообщения QMS: "+qmsCount);
                }else {
                    qmsMessages.setText("Нет новых сообщений QMS");
                }
                if (Preferences.isLoadShortUserInfo()) {
                    new Picasso.Builder(App.getInstance()).build()
                            .load(prefs.getString("shortAvatarUrl", "http://s.4pda.to/img/qms/logo.png"))
                            .error(R.drawable.no_image)
                            .into(imgAvatar);

                } else {
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
                            .load(strings[0])
                            .error(R.drawable.no_image)
                            .into(imgAvatar);
                    prefs.edit().putBoolean("isLoadShortUserInfo", true).apply();
                    prefs.edit().putString("shortAvatarUrl", strings[0]).apply();
                }
            }else {
                loginButton.setVisibility(View.VISIBLE);
                loginButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LoginDialog.showDialog(getContext(),null);
                    }
                });
            }



        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void refreshQms(){
        int qmsCount = Client.getInstance().getQmsCount();
        if(qmsCount!=0){
            qmsMessages.setText("Новые сообщения QMS: "+qmsCount);
        }else {
            qmsMessages.setText("Нет новых сообщений QMS");
        }
    }
}
