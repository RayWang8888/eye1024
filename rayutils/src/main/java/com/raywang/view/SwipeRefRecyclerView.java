package com.raywang.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.raywang.interfaces.ImageLoaderAdapter;
import com.raywang.rayutils.R;
import com.rey.material.widget.ProgressView;

/**
 * Created by Raye on 2015/10/21.
 */
public class SwipeRefRecyclerView extends SwipeRefreshLayout {
    private RecyclerView recyclerView;

    private RecyclerView.LayoutManager layoutManager;
    /** LinearLayoutManager*/
    private final static int LINEAR = 1;
    /** GridLayoutManager*/
    private final static int GRID = 2;
    /** StaggeredGridLayoutManager*/
    private final static int STAGGERED = 3;
    /** 当前managerType*/
    private int managerType = 1;

    private boolean isLoading = false;

    private View loadingMore;
    private ImageLoaderAdapter adapter;

    private OnSwipeRefRecyclerViewListener onSwipeRefRecyclerViewListener;
    public SwipeRefRecyclerView(Context context) {
        this(context, null);
    }

    public SwipeRefRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecyclerView getRecyclerView(){
        return recyclerView;
    }

    public void setOnRefreshListener(OnSwipeRefRecyclerViewListener listener) {
        this.onSwipeRefRecyclerViewListener = listener;
        super.setOnRefreshListener(listener);
    }

    private void init(){
        LinearLayout view = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.swiperef_recyclerview,null);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && onSwipeRefRecyclerViewListener != null
                        && !isLoading) {
                    if (adapter != null) {
                        adapter.onScrollEnd();
                    }
                    int count = recyclerView.getAdapter().getItemCount() - 1;
                    switch (managerType) {
                        case LINEAR:
                            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                            if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                                setEnabled(true);
                            } else if (linearLayoutManager.findLastCompletelyVisibleItemPosition()
                                    == count) {

                                loadingMore();
                            } else {
                                setEnabled(false);
                            }
                            break;
                        case GRID:
                            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                            if (gridLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                                setEnabled(true);
                            } else if (gridLayoutManager.findLastCompletelyVisibleItemPosition() == 0) {

                                loadingMore();
                            } else {
                                setEnabled(false);
                            }
                            break;
                        case STAGGERED:
                            StaggeredGridLayoutManager staggeredGridLayoutManager =
                                    (StaggeredGridLayoutManager) layoutManager;
                            int[] first = new int[staggeredGridLayoutManager.getSpanCount()];
                            int[] last = new int[staggeredGridLayoutManager.getSpanCount()];
                            staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(last);
                            staggeredGridLayoutManager.findFirstCompletelyVisibleItemPositions(first);
                            for (int i : first) {
                                if (i == 0) {
                                    setEnabled(true);
                                    break;
                                }
                            }
                            for (int i : last) {
                                if (i == count) {
                                    loadingMore();
                                    break;
                                }
                            }
                            break;
                    }
                } else {
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING && adapter != null) {
                        adapter.onScroll();
                    }
                    setEnabled(false);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });


        setEnabled(false);
        loadingMore = view.findViewById(R.id.loadmore);
        ((ProgressView)view.findViewById(R.id.progress)).start();
        loadingMore.setVisibility(View.GONE);
        addView(view);
    }

    private void loadingMore(){
        loadingMore.setVisibility(View.VISIBLE);
        isLoading = true;
        onSwipeRefRecyclerViewListener.onLoadMore();
    }

    public void setAdapter(RecyclerView.Adapter adapter){
        if(adapter instanceof ImageLoaderAdapter){
            this.adapter = (ImageLoaderAdapter) adapter;
        }
        recyclerView.setAdapter(adapter);
    }

    final public void loadingFinish(){
        loadingMore.setVisibility(View.GONE);
        setRefreshing(false);
        isLoading = false;
    }
    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
        if(layoutManager instanceof LinearLayoutManager){
            managerType = LINEAR;
        }else if(layoutManager instanceof GridLayoutManager){
            managerType = GRID;
        }else if(layoutManager instanceof StaggeredGridLayoutManager){
            managerType = STAGGERED;
        }
        recyclerView.setLayoutManager(layoutManager);
    }

    /**
     * 刷新和加载更多的监听
     */
    public interface OnSwipeRefRecyclerViewListener extends OnRefreshListener {
        public void onRefresh();
        public void onLoadMore();
    }
}
