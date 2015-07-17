package com.raywang.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.raywang.interfaces.ImageLoaderAdapter;
import com.raywang.rayutils.R;
import com.raywang.rayutils.Util;


/**
 * 自定义基于SwipeRefreshLayout的下拉刷新和上拉加载下一页的控件
 * 目前是在创建的SwipeRefreshLayout的时候加入一个LinearLayout控件
 * 然后在LinearLayout中加入一个ListView，加载下一页的提示视图可以通过设置
 * Footer来实现，同时加载完成也可以使用设置Footer来展示视图，设置Footer必须要
 * 在设置Adapter之前设置，否则可能没有效果
 * @author Ray Wang
 * @date 2015年4月8日20:41:41
 * @version 1.0
 */
@SuppressLint("Recycle")
public class SwipeRefListView extends SwipeRefreshLayout {
	/** 显示数据的listview*/
	private QuickReturnListView listView;
	/** 包容Listview的LinearLayout*/
	private LinearLayout layout;
	
	private OnRefListener onRefListener;
	/** listview 的footer，可以用来当做加载下一页的视图和全部加载完成的视图*/
	private View footer= null;
	
	/** 是否正在加载下一页*/
	private boolean isLoading = false;

    private ImageLoaderAdapter adapter;

	public SwipeRefListView(Context context){
		super(context);
	}
	
	public SwipeRefListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		layout = new LinearLayout(context);
		layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		layout.setOrientation(LinearLayout.VERTICAL);
		addView(layout);
		
		listView = new QuickReturnListView(context);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
				(LayoutParams.MATCH_PARENT,0, 1.0f);
		
		layout.addView(listView);
        setEnabled(false);
		listView.setOnScrollListener(new RefOnScrollListener());

		TypedArray array = context.obtainStyledAttributes(attrs,R.styleable.SwipeRefListView);
		listView.setDivider(array.getDrawable(R.styleable.SwipeRefListView_rDivider));
		listView.setDividerHeight(array.getDimensionPixelSize(R.styleable.SwipeRefListView_rDividerHeight, 1));
		params.setMargins(array.getDimensionPixelSize(R.styleable.SwipeRefListView_rMargin, 1),
				array.getDimensionPixelSize(R.styleable.SwipeRefListView_rMargin, 1), 
				array.getDimensionPixelSize(R.styleable.SwipeRefListView_rMargin, 1), 
				array.getDimensionPixelSize(R.styleable.SwipeRefListView_rMargin, 1));
		listView.setLayoutParams(params);
	}

	
	
	public void setOnrefListener(OnRefListener listener){
		this.onRefListener = listener;
		setOnRefreshListener(listener);
	}
	
	public void setFooter(View footer){
		if(this.footer != null && this.footer != footer ){
			//footer发生了变化
			this.listView.removeFooterView(this.footer);
		}
		if(this.footer == null){
			this.listView.addFooterView(footer);
		}
		this.footer = footer;
	}
	
	/**
	 * 刷新和显示最后一行的监听
	 * @author Ray Wang
	 * @date 2015年4月7日20:05:47
	 * @version 1.0
	 */
	public interface OnRefListener extends OnRefreshListener{
		public void onLastShow();
	}
	
	public void setAdapter(BaseAdapter adapter){
		if(listView != null){
			this.listView.setAdapter(adapter);
            if(adapter instanceof ImageLoaderAdapter){
                this.adapter = (ImageLoaderAdapter) adapter;
            }
		}
	}
	/**
	 * 加载更多加载完成调用的方法
	 */
	public void onLoadFinish(){
		isLoading = false;
	}
	
	public QuickReturnListView getListView(){
		return listView;
	}
	
	/**
	 * listview 滚动的监听
	 * @author Ray Wang
	 * @date 2015年4月7日20:00:28
	 * @version 1.0
	 */
	private class RefOnScrollListener implements OnScrollListener{

        //scrollState 1 开始滚动，2滚动中 0滚动结束  我推测的
		public void onScrollStateChanged(AbsListView view, int scrollState) {

            if(scrollState == 0){
                if(adapter != null) {
                    adapter.onScrollEnd();
                }
                //滚动结束，修改状态
                if(!isEnabled() && (listView.getCount() == 0 || listView.scrollIsComputed == false
                        || listView.getComputedScrollY() == 0)){
                    setEnabled(true);
                }else if(isEnabled()){
                    setEnabled(false);
                }

                if(listView.getLastVisiblePosition() == listView.getCount() - 1){
                    if(onRefListener != null && !isLoading){
                        isLoading = true;
                        onRefListener.onLastShow();
                    }
                }
            }else if(scrollState == 1 && adapter != null){
                adapter.onScroll();
            }
        }

		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {


		}
		
	}
	

	
	public class QuickReturnListView extends ListView {

		private int mItemCount;
		private int mItemOffsetY[];
		private boolean scrollIsComputed = false;
		private int mHeight;

		public QuickReturnListView(Context context) {
			super(context);
		}

		public QuickReturnListView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		public int getListHeight() {
			return mHeight;
		}

		public void computeScrollY() {
			mHeight = 0;
            if(getAdapter() == null){
                return;
            }
			mItemCount = getAdapter().getCount();
			if (mItemOffsetY == null || mItemCount != mItemOffsetY.length) {
				mItemOffsetY = new int[mItemCount];
			}
			int height = 0;
			for (int i = 0; i < mItemCount; ++i) {
				//为了防止动画失效采取的方法
				if(i < 2 || i == mItemCount - 1){
					//是前面2个或者最后一个
					View view = getAdapter().getView(1, null, this);
					view.measure(
							MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
							MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
					mItemOffsetY[i] = mHeight;
					height = view.getMeasuredHeight();
					mHeight += height;
				}else{
					//中间的
					mItemOffsetY[i] = mHeight;
					mHeight += height;
				}
				
			}
			
			scrollIsComputed = true;
		}

		public boolean scrollYIsComputed() {
			return scrollIsComputed;
		}

		public int getComputedScrollY() {
			int pos, nScrollY, nItemY;
			View view = null;
			pos = getFirstVisiblePosition();
			view = getChildAt(0);
			nItemY = view.getTop();
			nScrollY = mItemOffsetY[pos] - nItemY;
			return nScrollY;
		}
	}
	
}
