package org.softeg.slartus.forpdaplus.listtemplates;/*
 * Created by slinkin on 07.05.2014.
 */

import android.support.v4.app.Fragment;

import org.softeg.slartus.forpdaplus.R;
import org.softeg.slartus.forpdaplus.qms.QmsContactsListFragment;

public class QmsContactsBrickInfo extends BrickInfo {
    public static final String NAME = "QmsContacts";

    @Override
    public String getTitle() {
        return "Контакты";
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_close_white_24dp;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Fragment createFragment() {
        return new QmsContactsListFragment().setBrickInfo(this);
    }
}
