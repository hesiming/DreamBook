package cn.retech.custom_control;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public class AutoLoadListener implements OnScrollListener {
	public enum ScrollTypeEnum {
		SCROLL_TOP, SCROLL_BOTTOM, SCROLLING
	}

	public interface AutoLoadCallBack {
		void execute(ScrollTypeEnum scrollTypeEnum);
	}

	private AutoLoadCallBack mCallback;

	public AutoLoadListener(AutoLoadCallBack callback) {
		this.mCallback = callback;
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		ScrollTypeEnum scrollTypeEnum = ScrollTypeEnum.SCROLLING;
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			// 滚动到底部
			if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
				scrollTypeEnum = ScrollTypeEnum.SCROLL_BOTTOM;
			} else if (view.getFirstVisiblePosition() == 0) {
				scrollTypeEnum = ScrollTypeEnum.SCROLL_TOP;
			}
		}
		mCallback.execute(scrollTypeEnum);
	}

	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

	}

}
