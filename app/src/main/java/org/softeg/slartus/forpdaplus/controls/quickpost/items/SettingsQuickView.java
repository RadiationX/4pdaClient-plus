package org.softeg.slartus.forpdaplus.controls.quickpost.items;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import org.softeg.slartus.forpdaplus.App;
import org.softeg.slartus.forpdaplus.R;
import org.softeg.slartus.forpdaplus.post.EditPostActivity;
import org.softeg.slartus.forpdaplus.prefs.Preferences;


public class SettingsQuickView extends BaseQuickView {

    public SettingsQuickView(Context context) {
        super(context);
    }

    @Override
    View createView() {

        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setBackgroundResource(App.getInstance().getThemeBackgroundColorRes());
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        enableEmotics = new AppCompatCheckBox(getContext());
        enableEmotics.setText("Включить смайлики");
        enableEmotics.setChecked(Preferences.Topic.Post.getEnableEmotics());
        enableEmotics.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Preferences.Topic.Post.setEnableEmotics(b);
            }
        });
        enableEmotics.setLayoutParams(params);
        linearLayout.addView(enableEmotics);

        enableSign = new AppCompatCheckBox(getContext());
        enableSign.setText("Добавить подпись");
        enableSign.setChecked(Preferences.Topic.Post.getEnableSign());
        enableSign.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Preferences.Topic.Post.setEnableSign(b);
            }
        });
        enableSign.setLayoutParams(params);
        linearLayout.addView(enableSign);

        extendedFormButton = new AppCompatButton(getContext());
        extendedFormButton.setText("Расширенная форма");
        extendedFormButton.setLayoutParams(params);
        extendedFormButton.setTextColor(getResources().getColor(R.color.black));
        extendedFormButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getTopicId() == null || getAuthKey() == null || getPostBody() == null)
                    return;
                EditPostActivity.newPost((Activity) getContext(), getForumId() == null ? null : getForumId().toString(),
                        getTopicId().toString(), getAuthKey().toString(),
                        getPostBody().toString());
            }
        });
        linearLayout.addView(extendedFormButton);

//        attachesButton = new Button(getContext());
//        attachesButton.setText("Прикреплённые файлы");
//        attachesButton.setLayoutParams(params);
//        attachesButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                EditPostActivity.newPost((Activity)getContext(), getForumId().toString(), getTopicId().toString(), getAuthKey().toString(),
//                        getPostBody().toString());
//            }
//        });
//        linearLayout.addView(attachesButton);

        return linearLayout;
    }


    CheckBox enableEmotics;
    CheckBox enableSign;
    Button extendedFormButton;
    Button attachesButton;


    private CharSequence getPostBody() {
        return getEditor().getText() == null ? "" : getEditor().getText().toString();
    }


}
