package cn.retech.custom_control;

import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.retech.activity.R;
import cn.retech.domainbean_model.book_categories.BookCategory;

public class TabNavigation extends HorizontalScrollView implements OnClickListener {
	public interface OnScrollFullListener {
		/**
		 * 
		 * 当导航条滑动至最左边时调用
		 * 
		 */
		void onScrollFullLeft();

		/**
		 * 
		 * 当导航条滑动至最右边时调用
		 * 
		 */
		void onScrollFullRight();

		/**
		 * 正常滑动时调用
		 */
		void onScrolling();
	}

	public interface OnTabChangeListener {
		/**
		 * 当导航标签切换时调用
		 * 
		 * @param postion
		 *          The index of the view that was clicked.
		 */
		void onTabChange(int postion);
	}

	private final Context context;
	private OnTabChangeListener onTabChangeListener;
	private OnScrollFullListener onScrollFullListener;

	private TextView tabCursor;

	private LinearLayout tabNavigationLayout;
	private int eachPageButtonNum;// 每页显示的按钮数
	private int totalButtonNum;
	private int itemWidth;// 每个按钮宽度
	private int currentItemIndex = 0;// 当前选中按钮的索引

	public TabNavigation(Context context) {
		super(context);

		this.context = context;

		init();
	}

	public TabNavigation(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.context = context;

		init();
	}

	// 导航按钮的点击事件
	@Override
	public void onClick(View view) {
		changeTab(view);

		if (null != onTabChangeListener) {
			onTabChangeListener.onTabChange(tabNavigationLayout.indexOfChild(view));
		}
	}

	/**
	 * 按比例进行滚动
	 * 
	 * @param l当前left
	 * @param oldl之前的left
	 * @param width调用者的宽度
	 */
	public void scrollKit(int l, int oldl, int width) {
		// (当前left-之前left)/width是位移比例,再乘以this.getWidth()则得到需要位移的量
		int scorllDistance = (l - oldl) * this.getWidth() / width;
		// 由于ViewPaper只有3页内容,所以通过除以按钮数并且乘以算子来获取较为恰当的值
		scorllDistance = scorllDistance * Integer.parseInt(getResources().getString(R.string.tabnavigation_scroll_operator)) / totalButtonNum;
		// 开始位移
		this.scrollBy(scorllDistance, 0);

		View view = tabNavigationLayout.getChildAt(currentItemIndex);
		if (null != view) {
			int[] location = new int[2];
			view.getLocationInWindow(location);// View相对于屏幕的坐标
			// 判断当前选中导航按钮是否在屏幕外,若是,则滑动导航条。此处减去（view.getWidth()
			// *3/4）是为了让按钮即便只露出4分之1也会导致导航滑动
			if (location[0] >= this.getWidth() - itemWidth * 3 / 4 || location[0] < -itemWidth / 2) {
				this.scrollTo((int) view.getX(), 0);
			}
		}
	}

	public void setCurrentItem(int postion) {
		View view = tabNavigationLayout.getChildAt(postion);

		if (null != view) {
			changeTab(view);
		}
	}

	/**
	 * @param onScrollFullLeftListener
	 *          the onScrollFullLeftListener to set
	 */
	public void setOnScrollFullLeftListener(OnScrollFullListener onScrollFullListener) {
		this.onScrollFullListener = onScrollFullListener;
	}

	/**
	 * @param onTabChangeListener
	 *          the onTabChangeListener to set
	 */
	public void setOnTabChangeListener(OnTabChangeListener onTabChangeListener) {
		this.onTabChangeListener = onTabChangeListener;
	}

	public void showCategory(List<BookCategory> list, int eachPageButtonNum) {
		if (null == list) {
			return;
		}

		tabNavigationLayout.removeAllViews();

		// 根据"屏幕宽度"/"每页显示的按钮数"得到每个按钮宽度
		totalButtonNum = list.size();
		this.eachPageButtonNum = eachPageButtonNum;
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		itemWidth = displayMetrics.widthPixels / this.eachPageButtonNum;

		// 根据图书类别个数,添加导航按钮
		LayoutParams layoutParams = new LayoutParams(itemWidth, LayoutParams.MATCH_PARENT);
		for (BookCategory bookCategory : list) {
			TextView tab = new TextView(context);
			tab.setTag(bookCategory);

			tab.setTextSize(Float.parseFloat(getResources().getString(R.string.tabnavigation_text_size)));

			if (tabNavigationLayout.getChildCount() == 0) {
				tab.setTextColor(getResources().getColorStateList(R.color.color_ec6b56));
			} else {
				tab.setTextColor(getResources().getColorStateList(R.color.color_474747));
			}
			tab.setBackgroundResource(android.R.color.white);
			tab.setText(bookCategory.getName());
			tab.setGravity(Gravity.CENTER);
			tab.setOnClickListener(this);

			tabNavigationLayout.addView(tab, layoutParams);
		}

		tabCursor.setWidth(itemWidth);
		View currentCategory = tabNavigationLayout.getChildAt(currentItemIndex);
		if (currentCategory != null) {
			tabCursor.setX(currentCategory.getX());
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);

		if (null != tabNavigationLayout.getChildAt(currentItemIndex)) {
			tabCursor.setX(tabNavigationLayout.getChildAt(currentItemIndex).getX());
		}

	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);

		// 当滑动到最左端
		do {
			if (null == onScrollFullListener) {
				break;
			}

			if (l == 0) {
				onScrollFullListener.onScrollFullLeft();

				break;
			} else if (l == (totalButtonNum - eachPageButtonNum) * itemWidth) {
				onScrollFullListener.onScrollFullRight();

				break;
			}

			onScrollFullListener.onScrolling();
		} while (false);

	}

	private void changeTab(View view) {
		ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(tabCursor, "translationX", view.getX());
		objectAnimator.addListener(new AnimatorListener() {
			@Override
			public void onAnimationCancel(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				if (tabCursor.getX() == 0) {
					TabNavigation.this.fullScroll(View.FOCUS_LEFT);
				}
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationStart(Animator animation) {
			}
		});
		objectAnimator.start();

		// tabCursor.animate().x(view.getX()).setListener(new AnimatorListener()
		// {
		// @Override
		// public void onAnimationCancel(Animator animation) {
		// }
		//
		// @Override
		// public void onAnimationEnd(Animator animation) {
		// if (tabCursor.getX() == 0) {
		// TabNavigation.this.fullScroll(View.FOCUS_LEFT);
		// }
		// }
		//
		// @Override
		// public void onAnimationRepeat(Animator animation) {
		// }
		//
		// @Override
		// public void onAnimationStart(Animator animation) {
		// }
		// });// 导航条动画

		// 重置之前选中导航按钮的文字格式
		TextView lastItemTextView = (TextView) tabNavigationLayout.getChildAt(currentItemIndex);
		lastItemTextView.setTextColor(getResources().getColorStateList(R.color.color_474747));

		currentItemIndex = tabNavigationLayout.indexOfChild(view);// 将坐标定位于当前选中的导航按钮

		// 为当前选中的导航按钮设置文字格式
		((TextView) view).setTextColor(getResources().getColorStateList(R.color.color_ec6b56));
	}

	private void init() {
		this.setHorizontalScrollBarEnabled(false);// 隐藏滑动条

		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.tab_navigation_layout, this);

		tabNavigationLayout = (LinearLayout) findViewById(R.id.tab_navigation_radioGroup);

		tabCursor = (TextView) findViewById(R.id.tab_cursor_textView);
	}
}
