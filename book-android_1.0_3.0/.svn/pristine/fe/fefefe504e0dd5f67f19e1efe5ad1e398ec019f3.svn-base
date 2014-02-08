package cn.retech.custom_control;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class MyViewPaper extends ViewPager {

	public interface OnScrollListener {
		void onScroll(int l, int oldl, int width);
	}

	private OnScrollListener onScrollListener;

	public MyViewPaper(Context context) {
		super(context);
	}

	public MyViewPaper(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @param onScrollListener
	 *            the onScrollListener to set
	 */
	public void setOnScrollListener(OnScrollListener onScrollListener) {
		this.onScrollListener = onScrollListener;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);

		if (null != onScrollListener) {
			onScrollListener.onScroll(l, oldl, this.getWidth());
		}
	}
}
