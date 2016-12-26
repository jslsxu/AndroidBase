package com.haocai.app.network.base.presenter;

import android.support.v4.widget.SwipeRefreshLayout;

import com.haocai.app.view.LoadMoreRecyclerView;

import java.util.List;

/**
 * Created by jslsxu on 16/12/21.
 */

public class RecyclerViewPresenter implements LoadMoreRecyclerView.LoadMoreListener {

    public static final int REQUEST_REFRESH = 0;    //刷新
    public static final int REQUEST_GETMORE = 1;    //加载更多
    private boolean mLoading;
    private boolean mHasMore;
    protected LoadMoreRecyclerView mRecyclerView;
    protected SwipeRefreshLayout mRefreshLayout;
    protected PresenterInterface mPresenterInterface;
    protected BaseRecyclerAdapter mAdapter;

    public RecyclerViewPresenter() {

    }

    public void bind(LoadMoreRecyclerView recyclerView, BaseRecyclerAdapter recyclerAdapter, SwipeRefreshLayout refreshLayout, PresenterInterface ptrInterface) {
        mRecyclerView = recyclerView;
        mAdapter = recyclerAdapter;
        mPresenterInterface = ptrInterface;
        mRefreshLayout = refreshLayout;
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefreshData();
            }
        });
        recyclerView.setLoadMoreListener(this);
    }

    public void endRequest(int requestType) {
        mLoading = false;
        if (requestType == REQUEST_REFRESH) {
            mRefreshLayout.setRefreshing(false);
            mRecyclerView.setAutoLoadMoreEnable(mHasMore);
        } else {
            mRecyclerView.notifyMoreFinish(mHasMore);
        }
    }

    public void endRequest(int requestType, List data, boolean hasMore) {
        mHasMore = hasMore;
        mAdapter.setData(data, requestType == REQUEST_REFRESH);
        endRequest(requestType);
    }

    public void onRefreshData() {
        if (mLoading) {
            mRefreshLayout.setRefreshing(false);
        } else {
            if (mPresenterInterface != null) {
                mLoading = true;
                mPresenterInterface.requestData(REQUEST_REFRESH);
            }
        }
    }

    @Override
    public void onLoadMore() {
        if (mLoading) {
            mRecyclerView.notifyMoreFinish(mHasMore);
        } else {
            if (mPresenterInterface != null) {
                mLoading = true;
                mPresenterInterface.requestData(REQUEST_GETMORE);
            }
        }
    }

    public interface PresenterInterface {
        void requestData(int requestType);
    }
}
