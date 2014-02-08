package cn.retech.custom_control;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class PullDownAndUpView extends ScrollView {
	private boolean isPullToTop = false;// 标记是否已拖动至顶部
	private boolean isPullToBottom = false;// 标记是否已拖动至底部
	private boolean isPulling = false;// 标记是否正在拉动中

	public PullDownAndUpView(Context context) {
		super(context);

		init();
	}

	public PullDownAndUpView(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	public PullDownAndUpView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		init();
	}

	protected final void init() {
		this.setOnTouchListener(new OnTouchListener() {
			private float startPointY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (null == v.getTag()) {
					v.setTag(v.getY());
				}

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startPointY = event.getY();

					break;
				case MotionEvent.ACTION_UP:
					// v.animate().y((Float) v.getTag());// 复位动画
					ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(v, "translationY", (Float) v.getTag());
					objectAnimator.setDuration(300);
					objectAnimator.start();

					// 重置各种标记位
					isPulling = false;
					isPullToTop = false;
					isPullToBottom = false;

					break;
				case MotionEvent.ACTION_MOVE:
					float move = event.getY() - startPointY;
					startPointY = event.getY();

					do {
						// 可移动的条件:(拖动至顶部||拖动至底部) && 位移量>3
						if ((isPullToTop || isPullToBottom) && Math.abs(move) > 3) {
							v.setY(v.getY() + move * 1 / 5);

							isPulling = true;// 标记正在拉动中,这样即可拦截ACTION_MOVE事件
						}
					} while (false);

					break;
				default:
					break;
				}

				return isPulling;
			}
		});
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);

		do {
			if (t != oldt) {
				isPullToTop = false;
				isPullToBottom = false;

				break;
			}

			// top值为0且top=oldt时标记为拖动至顶部
			if (t == 0) {
				isPullToTop = true;
			}

			// top值"不"为0且top=oldt时标记为拖动至底部
			if (t != 0) {
				isPullToBottom = true;
			}

		} while (false);
	}
}
