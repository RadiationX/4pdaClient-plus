package org.softeg.slartus.forpdaplus.mainnotifiers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.webkit.WebView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.softeg.slartus.forpdacommon.DateExtensions;
import org.softeg.slartus.forpdacommon.ExtPreferences;
import org.softeg.slartus.forpdacommon.Http;
import org.softeg.slartus.forpdaplus.App;
import org.softeg.slartus.forpdaplus.classes.AlertDialogBuilder;
import org.softeg.slartus.forpdaplus.common.AppLog;
import org.softeg.slartus.forpdaplus.prefs.Preferences;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Created by slartus on 03.06.2014.
 */
public class TopicAttentionNotifier extends MainNotifier {
    public TopicAttentionNotifier(NotifiersManager notifiersManager) {
        super(notifiersManager, "TopicAttentionNotifier", 2);
    }

    public void start(Context context) {
        if (!isTime())
            return;
        saveTime();
        showNotify(context);
    }

    @Override
    protected boolean isTime() {
        GregorianCalendar lastShowpromoCalendar = new GregorianCalendar();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        Date lastCheckDate = ExtPreferences.getDateTime(prefs, "notifier." + name, null);
        if (lastCheckDate == null) {
            saveTime();
            return true;
        }

        lastShowpromoCalendar.setTime(lastCheckDate);

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        int hours = DateExtensions.getHoursBetween(calendar.getTime(), lastShowpromoCalendar.getTime());
        return hours >= period;
    }


    public void showNotify(final Context context) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            public void run() {
                try {
                    String url = "http://4pda.ru/forum/index.php?showtopic=271502";
                    String page = Http.getPage(url, "windows-1251");
                    Matcher m = Pattern
                            .compile("<a name=\"(attention_\\d+_\\d+_\\d+_\\d+)\" title=\"attention_\\d+_\\d+_\\d+_\\d+\">.*?</a>(.*?)<br\\s*/>\\s*---+\\s*<br\\s*/>",
                                    Pattern.CASE_INSENSITIVE).matcher(page);
                    if (!m.find())
                        return;

                    String topicAttentionId = m.group(1);
                    String lastAttentionId = Preferences.Attention.getAttentionId();

                    if (topicAttentionId.equals(lastAttentionId))
                        return;

                    Preferences.Attention.setAttentionId(topicAttentionId);

                    final String topicAttention = m.group(2);
                    handler.post(new Runnable() {
                        public void run() {
                            final WebView webView = new WebView(context);
                            MaterialDialog alertDialog =
                                    new MaterialDialog.Builder(context)
                                            .title("Объявление клиента")
                                            .cancelable(false)
                                            .customView(webView)
                                            .positiveText("Я прочитал").showListener(new DialogInterface.OnShowListener() {
                                        @Override
                                        public void onShow(DialogInterface dialog) {
                                            StringBuilder body = new StringBuilder();
                                            body.append("<http>\n");
                                            body.append("<head>\n");
                                            body.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=windows-1251\" />\n");
                                            body.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, user-scalable=no\">\n");
                                            body.append("</head>");
                                            body.append("<body style=\"background-color:#f5f5f5\">");
                                            body.append(topicAttention);
                                            body.append("</body>");
                                            body.append("</html>");

                                            webView.getSettings().supportZoom();
                                            webView.loadDataWithBaseURL("http://4pda.ru/forum/", body.toString(), "text/html", "UTF-8", null);
                                        }
                                    }).build();
                            addToStack(alertDialog);


                        }
                    });

                } catch (Throwable ignored) {
                    AppLog.e(null, ignored);
                }
            }
        }).start();

    }

}
