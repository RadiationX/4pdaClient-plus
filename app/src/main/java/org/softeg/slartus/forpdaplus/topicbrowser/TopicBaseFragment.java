package org.softeg.slartus.forpdaplus.topicbrowser;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;

import org.softeg.slartus.forpdaplus.R;
import org.softeg.slartus.forpdaplus.controls.quickpost.QuickPostFragment;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/*
 * Created by slartus on 06.06.2014.
 */
public class TopicBaseFragment extends Fragment {


    protected TopicWebView mWebView;

    protected uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout mPullToRefreshLayout;


    @Override
    public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, android.os.Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.topic_fragment, container, false);
        assert v != null;

        mWebView = (TopicWebView) v.findViewById(R.id.webView);
        mPullToRefreshLayout = (PullToRefreshLayout) v.findViewById(R.id.ptr_layout);
        v.findViewById(R.id.btnUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebView.pageUp(true);
            }
        });
        v.findViewById(R.id.btnDown).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebView.pageDown(true);
            }
        });
        v.findViewById(R.id.btnShowHideEditPost).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                TopicFragment fragment = (TopicFragment) getSupportFragmentManager().findFragmentById(R.id.topic_fragment);
//                if (fragment != null) return;
//                fragment = TopicFragment.newInstance(args);
//                getSupportFragmentManager().beginTransaction().add(R.id.topic_fragment, fragment).commit();
            }
        });
        mWebView.setOnScrollChangedCallback(new TopicWebView.OnScrollChangedCallback() {
            @Override
            public void onScrollDown() {
                if (((ActionBarActivity)getActivity()).getSupportActionBar().isShowing()) {
                    mHideHandler.removeCallbacks(mHideRunnable);
                    ((ActionBarActivity)getActivity()).getSupportActionBar().hide();
                }
            }

            @Override
            public void onScrollUp() {
                if (!((ActionBarActivity)getActivity()).getSupportActionBar().isShowing()) {
                    mHideHandler.removeCallbacks(mHideRunnable);
                    ((ActionBarActivity)getActivity()).getSupportActionBar().show();
                }
            }

            @Override
            public void onTouch() {
                if (!((ActionBarActivity)getActivity()).getSupportActionBar().isShowing()) {
                    ((ActionBarActivity)getActivity()).getSupportActionBar().show();
                    mHideHandler.removeCallbacks(mHideRunnable);
                    mHideHandler.postDelayed(mHideRunnable, 3000);
                }
            }
        });

        return v;
    }

    private QuickPostFragment mQuickPostFragment;

    private void createQuickPostFragment() {
        if (mQuickPostFragment == null) {
            mQuickPostFragment=new QuickPostFragment();

            mQuickPostFragment.setOnPostSendListener(new QuickPostFragment.PostSendListener() {
                @Override
                public void onPostExecute(org.softeg.slartus.forpdaplus.controls.quickpost.PostTask.PostResult postResult) {
//                    if (postResult.Success) {
//                        hideMessagePanel();
//                        if (Client.getInstance().getRedirectUri() == null)
//                            android.util.Log.e("ThemeActivity", "redirect is null");
//                        m_Topic = postResult.ExtTopic;
//                        setThemeParams(Client.getInstance().getRedirectUri() == null ? m_LastUrl : Client.getInstance().getRedirectUri().toString());
//
//                        if (postResult.TopicBody == null)
//                            android.util.Log.e("ThemeActivity", "TopicBody is null");
//                        showThemeBody(postResult.TopicBody);
//
//                    } else {
//                        if (postResult.Exception != null)
//                            Log.e(getActivity(), postResult.Exception, new Runnable() {
//                                @Override
//                                public void run() {
//                                    mQuickPostFragment.post();
//                                }
//                            });
//                        else if (!TextUtils.isEmpty(postResult.ForumErrorMessage))
//                            new AlertDialogBuilder(getActivity())
//                                    .setTitle("Сообщение форума")
//                                    .setMessage(postResult.ForumErrorMessage)
//                                    .create().show();
//                    }
                }
            });
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.quick_post_fragment,mQuickPostFragment).commit();
        }

    }

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            if (getActivity() != null)
                ((ActionBarActivity)getActivity()).getSupportActionBar().hide();
        }
    };

    @Override
    public void onActivityCreated(android.os.Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mWebView.setActionBarheight(((ActionBarActivity)getActivity()).getSupportActionBar().getHeight());
        setupPullToRefreshLayout();

    }

    private void setupPullToRefreshLayout() {
        ActionBarPullToRefresh.from(getActivity())
                .options(Options.create().scrollDistance(0.3f).refreshOnUp(true).build())
                .allChildrenArePullable()
                .listener(new OnRefreshListener() {
                    @Override
                    public void onRefreshStarted(View view) {
                        reloadTopic();
                    }
                }).setup(mPullToRefreshLayout);
    }

    protected void reloadTopic() {

    }


    public void onBtnUpClick(View view) {
        mWebView.pageUp(true);
    }

    public void onBtnDownClick(View view) {
        mWebView.pageDown(true);
    }
}
