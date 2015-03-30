package org.softeg.slartus.forpdaplus.listfragments;/*
 * Created by slinkin on 05.05.2014.
 */

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import org.softeg.slartus.forpdaapi.IListItem;
import org.softeg.slartus.forpdaapi.TopicApi;
import org.softeg.slartus.forpdaapi.post.PostAttach;
import org.softeg.slartus.forpdaplus.Client;
import org.softeg.slartus.forpdaplus.IntentActivity;
import org.softeg.slartus.forpdaplus.download.DownloadsService;
import org.softeg.slartus.forpdaplus.listtemplates.TopicAttachmentBrickInfo;

import java.util.ArrayList;
import java.util.List;

public class TopicAttachmentListFragment extends BaseTaskListFragment {
    public static void showActivity(Context context, CharSequence topicId){
        Bundle args=new Bundle();
        args.putString(TOPIC_ID_KEY,topicId.toString());
        ListFragmentActivity.showListFragment(context, TopicAttachmentBrickInfo.NAME,args);
    }
    public TopicAttachmentListFragment() {

        super();
    }
    public static final String TOPIC_ID_KEY="TOPIC_ID_KEY";
    @Override
    protected boolean inBackground(boolean isRefresh) throws Throwable {
        mLoadResultList = TopicApi.getTopicAttachment(Client.getInstance(), args.getString(TOPIC_ID_KEY));
        return true;
    }

    @Override
    protected void deliveryResult(boolean isRefresh) {
        if (isRefresh)
            mData.clear();
        List<CharSequence> ids=new ArrayList<>();
        for (IListItem item : mData) {
            ids.add(item.getId());
        }
        for (IListItem item : mLoadResultList) {
            if(ids.contains(item.getId()))
                continue;
            mData.add(item);
        }

        mLoadResultList.clear();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {

            getActivity().openContextMenu(v);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        Toast.makeText(getContext(), "I you father, Luke", Toast.LENGTH_LONG).show();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        if (info.id == -1) return;
        Object o = getAdapter().getItem((int) info.id );
        if (o == null)
            return;
        final IListItem item = (IListItem) o;
        if (TextUtils.isEmpty(item.getId())) return;
        final PostAttach attach=(PostAttach)item;
        menu.add("Скачать вложение")
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        DownloadsService.download(getActivity(),attach.getUrl().toString(),false);
                        return true;
                    }
                });
        menu.add("Перейти к сообщению")
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        IntentActivity.showTopic(getActivity(),attach.getPostUrl());
                        return true;
                    }
                });
    }

}
