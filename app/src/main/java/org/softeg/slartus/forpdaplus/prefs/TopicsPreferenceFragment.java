package org.softeg.slartus.forpdaplus.prefs;/*
 * Created by slinkin on 16.04.2014.
 */

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.softeg.slartus.forpdaplus.R;

public class TopicsPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    public static String ListName = "";

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.topics_list_prefs);

        Preference sortPreference = findPreference("topics.list.sort");
        sortPreference.setOnPreferenceClickListener(this);
        sortPreference.setSummary(getSortTitle());
    }


    private String getSortTitle() {
        String title = "";
        Boolean asc = true;
        switch (Preferences.List.getListSort(getListName(), Preferences.List.defaultListSort())) {
            case "sortorder.desc":
                asc = false;
            case "sortorder.asc":
                title = "Как на сайте";
                break;
            case "date.desc":
                asc = false;
            case "date.asc":
                title = "По дате последнего поста";
                break;
            case "date_and_new.desc":
                asc = false;
            case "date_and_new.asc":
                title = "По дате последнего поста и непрочитанности";
                break;
            case "title.desc":
                asc = false;
            case "title.asc":
                title = "По названию топика";
                break;
        }
        return String.format("%s (%s)", title, (asc ? "По возрастанию" : "По убыванию"));
    }

    @Override
    public void onActivityCreated(android.os.Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.topics_list_prefs, false);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();

        switch (key) {
            case "topics.list.sort":
                showSortDialog();
                return true;
        }
        return false;
    }

    private void showSortDialog() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View v = inflater.inflate(R.layout.topics_sort_dialog, null);
        assert v != null;
        final RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.rgSortType);

        switch (Preferences.List.getListSort(getListName(), Preferences.List.defaultListSort())) {
            case "sortorder.desc":
            case "sortorder.asc":
                ((RadioButton) radioGroup.findViewById(R.id.rbSortBySortOrder)).setChecked(true);
                break;
            case "date.desc":
            case "date.asc":
                ((RadioButton) radioGroup.findViewById(R.id.rbSortByDate)).setChecked(true);
                break;
            case "date_and_new.desc":
            case "date_and_new.asc":
                ((RadioButton) radioGroup.findViewById(R.id.rbSortByDateAndNew)).setChecked(true);
                break;
            case "title.desc":
            case "title.asc":
                ((RadioButton) radioGroup.findViewById(R.id.rbSortByTitle)).setChecked(true);
                break;
            default:
                ((RadioButton) radioGroup.findViewById(R.id.rbSortBySortOrder)).setChecked(true);
                break;
        }

        new MaterialDialog.Builder(getActivity())
                .title("Сортировка")
                .customView(v,true)
                .positiveText("По убыванию")
                .neutralText("По возрастанию")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        String sortValue = "date";
                        switch (radioGroup.getCheckedRadioButtonId()) {
                            case R.id.rbSortBySortOrder:
                                sortValue = "sortorder";
                                break;
                            case R.id.rbSortByDate:
                                sortValue = "date";
                                break;
                            case R.id.rbSortByDateAndNew:
                                sortValue = "date_and_new";
                                break;
                            case R.id.rbSortByTitle:
                                sortValue = "title";
                                break;
                        }
                        Preferences.List.setListSort(getListName(), sortValue + ".desc");
                        Toast.makeText(getActivity(), "Необходимо обновить список для применения сортировки",
                                Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onNeutral(MaterialDialog dialog) {
                        String sortValue = "date";
                        switch (radioGroup.getCheckedRadioButtonId()) {
                            case R.id.rbSortBySortOrder:
                                sortValue = "sortorder";
                                break;
                            case R.id.rbSortByDate:
                                sortValue = "date";
                                break;
                            case R.id.rbSortByDateAndNew:
                                sortValue = "date_and_new";
                                break;
                            case R.id.rbSortByTitle:
                                sortValue = "title";
                                break;
                        }
                        Preferences.List.setListSort(getListName(), sortValue + ".asc");
                        Toast.makeText(getActivity(), "Необходимо обновить список для применения сортировки",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private String getListName() {
        return ListName;
    }

}
