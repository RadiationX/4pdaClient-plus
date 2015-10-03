package org.softeg.slartus.forpdaplus.listfragments.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.softeg.slartus.forpdaapi.News;
import org.softeg.slartus.forpdaplus.R;
import org.softeg.slartus.forpdaplus.prefs.Preferences;

import java.util.ArrayList;

/**
 * Created by slartus on 20.02.14.
 */
public class NewsListAdapter extends BaseAdapter {
    private ArrayList<News> newsList;

    final LayoutInflater inflater;
    private ImageLoader imageLoader;
    private Boolean mLoadImages;
    private int mNewsListRowId;

    public NewsListAdapter(Context context, ArrayList<News> newsList, ImageLoader imageLoader) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mNewsListRowId = Preferences.News.List.getNewsListViewId();
        this.imageLoader = imageLoader;
        this.newsList = newsList;
        mLoadImages = Preferences.News.List.isLoadImages();
    }

    @Override
    public void notifyDataSetChanged() {

        mLoadImages = Preferences.News.List.isLoadImages();
        mNewsListRowId = Preferences.News.List.getNewsListViewId();
        super.notifyDataSetChanged();
    }

    public void setData(ArrayList<News> data) {
        this.newsList = data;
    }

    @Override
    public int getCount() {
        return newsList.size();
    }

    @Override
    public Object getItem(int p1) {
        return newsList.get(p1);
    }

    @Override
    public long getItemId(int p1) {
        return p1;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        View rowView = view;
        if (rowView == null || rowView.getId() != mNewsListRowId) {
            rowView = inflater.inflate(mNewsListRowId, null);

            holder = new ViewHolder();

            assert rowView != null;
            rowView.setId(mNewsListRowId);
            holder.image_panel = rowView.findViewById(R.id.image_panel);
            if (holder.image_panel != null)
                holder.image_panel.setVisibility(mLoadImages ? View.VISIBLE : View.GONE);
            holder.imageImage = (ImageView) rowView.findViewById(R.id.imageImage);

            holder.mProgressBar = (LinearLayout) rowView.findViewById(R.id.mProgressBar);
            holder.textSource = (TextView) rowView.findViewById(R.id.textSource);
            holder.textComments = (TextView) rowView.findViewById(R.id.textComments);
            holder.textTag = (TextView) rowView.findViewById(R.id.textTag);

            holder.textAutor = (TextView) rowView.findViewById(R.id.textAvtor);
            holder.textDate = (TextView) rowView.findViewById(R.id.textDate);
            holder.textDescription = (TextView) rowView.findViewById(R.id.textDescription);
            holder.textTitle = (TextView) rowView.findViewById(R.id.textTitle);

            rowView.setTag(holder);

        } else {
            holder = (ViewHolder) rowView.getTag();
            if (holder.image_panel != null)
                holder.image_panel.setVisibility(mLoadImages ? View.VISIBLE : View.GONE);
        }
        News data = newsList.get(position);
        if (holder.textComments != null)
            holder.textComments.setText(String.valueOf(data.getCommentsCount()));
        if (holder.textAutor != null)
            holder.textAutor.setText(data.getAuthor());
        if (holder.textDate != null)
            holder.textDate.setText(data.getNewsDate());
        if (holder.textDescription != null)
            holder.textDescription.setText(data.getDescription());
        if (holder.textTitle != null)
            holder.textTitle.setText(data.getTitle());

        if (holder.image_panel != null && data.getImgUrl() != null && mLoadImages) {
            imageLoader.displayImage(data.getImgUrl().toString(), holder.imageImage, new ImageLoadingListener() {

                @Override
                public void onLoadingStarted(String p1, View p2) {
                    //p2.setVisibility(View.GONE);
                    holder.mProgressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String p1, View p2, FailReason p3) {
                    holder.mProgressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onLoadingComplete(String p1, View p2, Bitmap p3) {
                    //p2.setVisibility(View.VISIBLE);
                    holder.mProgressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onLoadingCancelled(String p1, View p2) {

                }
            });
        }
        if (data.getTagTitle() != null && holder.textTag != null) {
            if(data.getTagTitle().equals("")){
                holder.textTag.setVisibility(View.GONE);
            }else {
                holder.textTag.setVisibility(View.VISIBLE);
                holder.textTag.setText(data.getTagTitle());
            }

        }
        if (data.getSourceTitle() != null && holder.textSource != null) {
            holder.textSource.setVisibility(View.VISIBLE);
            holder.textSource.setText("Источник: " + data.getSourceTitle());
        }

        return rowView;
    }

    public class ViewHolder {
        public View image_panel;
        public ImageView imageImage;
        public TextView textTitle;
        public TextView textDate;
        public TextView textDescription;
        public TextView textAutor;
        public TextView textTag;
        public TextView textComments;
        public TextView textSource;
        public LinearLayout mProgressBar;
    }

}

