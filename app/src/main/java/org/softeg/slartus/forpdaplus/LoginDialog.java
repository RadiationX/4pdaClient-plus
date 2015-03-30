package org.softeg.slartus.forpdaplus;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.softeg.slartus.forpdaplus.classes.AlertDialogBuilder;
import org.softeg.slartus.forpdaplus.classes.AppProgressDialog;
import org.softeg.slartus.forpdaplus.common.AppLog;

/**
 * User: slinkin
 * Date: 08.02.12
 * Time: 7:18
 */
public class LoginDialog {
    EditText username_edit;
    final EditText password_edit;
    CheckBox privacy_checkbox;
    View mView;

    private Context mContext;

    private LoginDialog(Context context) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.login, null);

        username_edit = (EditText) mView.findViewById(R.id.username_edit);
        password_edit = (EditText) mView.findViewById(R.id.password_edit);
        privacy_checkbox = (CheckBox) mView.findViewById(R.id.privacy_checkbox);
        loadData();
    }

    protected View getView() {
        return mView;
    }

    protected void connect(Runnable onConnectResult) {
        saveData();
        LoginTask loginTask = new LoginTask(mContext, onConnectResult);
        loginTask.execute(username_edit.getText().toString(), password_edit.getText().toString(),
                Boolean.toString(privacy_checkbox.isChecked()));
    }

    protected void saveData() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("LoginPrivacy", privacy_checkbox.isChecked());
        editor.putString("Login", username_edit.getText().toString());
        editor.commit();
    }

    protected void loadData() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        privacy_checkbox.setChecked(preferences.getBoolean("LoginPrivacy", false));
        username_edit.setText(preferences.getString("Login", ""));
    }


    public static void showDialog(final Context context, final Runnable onConnectResult) {
        final LoginDialog loginDialog = new LoginDialog(context);

        new MaterialDialog.Builder(context)
                .title("Вход")
                .customView(loginDialog.getView())
                .positiveText("Вход")
                .negativeText("Отмена")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        loginDialog.connect(onConnectResult);
                    }
                })
                .show();
    }

    public static void logout(Context context) {
        LogoutTask logoutTask = new LogoutTask(context);
        logoutTask.execute();
    }

    public class LoginTask extends AsyncTask<String, Void, Boolean> {

        Context mContext;
        private final MaterialDialog dialog;
        private Runnable m_OnConnectResult;

        public LoginTask(Context context, Runnable onConnectResult) {
            mContext = context;
            m_OnConnectResult = onConnectResult;
            dialog = new MaterialDialog.Builder(mContext)
                    .progress(true, 0)
                    .cancelable(false)
                    .content("Вход")
                    .build();
        }

        private String m_Login;
        private String m_Password;


        private Boolean m_Privacy;

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                m_Login = params[0];
                m_Password = params[1];
                m_Privacy = Boolean.parseBoolean(params[2]);

                return Client.getInstance().login(m_Login, m_Password, m_Privacy);
            } catch (Exception e) {

                ex = e;
                return false;
            }
        }

        // can use UI thread here
        protected void onPreExecute() {
            this.dialog.show();
        }

        protected void onCancelled() {
            Toast.makeText(mContext, "Отменено",
                    Toast.LENGTH_SHORT).show();
        }

        private Exception ex;
        //private Client.OnUserChangedListener m_OnUserChangedListener = null;

        private void doOnUserChangedListener(String user, Boolean success) {
            if (m_OnConnectResult != null)
                m_OnConnectResult.run();
        }

//        public void setOnUserChangedListener(Client.OnUserChangedListener p) {
//            m_OnUserChangedListener = p;
//        }

        // can use UI thread here
        protected void onPostExecute(final Boolean success) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }

            doOnUserChangedListener(m_Login, success);
            Client.getInstance().doOnUserChangedListener(m_Login, success);
            if (success) {
                Toast.makeText(mContext, "Вход выполнен",
                        Toast.LENGTH_SHORT).show();
            } else {
                if (ex != null)
                    AppLog.e(mContext, ex);
                else
                    new MaterialDialog.Builder(mContext)
                            .title("Ошибка")
                            .content(Client.getInstance().getLoginFailedReason())
                            .positiveText(android.R.string.ok)
                            .show();
            }
        }

    }

    public static class LogoutTask extends AsyncTask<String, Void, Boolean> {

        Context mContext;
        private final MaterialDialog dialog;

        public LogoutTask(Context context) {
            mContext = context;
            dialog = new MaterialDialog.Builder(mContext)
                    .progress(true, 0)
                    .cancelable(true)
                    .build();
        }

        private String m_Login;
        private String m_Password;

        private Boolean m_LoginRemember;
        private Boolean m_AutoLogin;


        @Override
        protected Boolean doInBackground(String... params) {
            try {


                return Client.getInstance().logout();
            } catch (Throwable e) {
                AppLog.e(mContext, e);
                ex = e;
                return false;
            }
        }

        // can use UI thread here
        protected void onPreExecute() {
            this.dialog.setContent("Выход...");
            this.dialog.show();
        }

        protected void onCancelled() {
            Toast.makeText(mContext, "Отменено",
                    Toast.LENGTH_SHORT).show();
        }

        private Throwable ex;


        private void doOnUserChangedListener(String user, Boolean success) {

        }

        // can use UI thread here
        protected void onPostExecute(final Boolean success) {
            if(this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
            doOnUserChangedListener(m_Login, success);

            if (success) {
                Toast.makeText(mContext, "Выход выполнен",
                        Toast.LENGTH_SHORT).show();
            } else {
                if (ex != null)
                    AppLog.i(mContext, ex);

            }
        }


    }

}
