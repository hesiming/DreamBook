package cn.retech.custom_control;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import cn.retech.activity.R;

public class MyRowLinearLayout extends LinearLayout {
	protected int margin;

	public MyRowLinearLayout(Context context) {
		super(context);
		this.setOrientation(LinearLayout.HORIZONTAL);
		calculateParmenters();
	}

	// 计算margin和columnCount值
	public void calculateParmenters() {
		int screenWidth = getResources().getDisplayMetrics().widthPixels;
		int bookCellWidth = (int) getResources().getDimension(R.dimen.book_folder_width);
		// int eachColumnBookNumber = screenWidth / (bookCellWidth + 2 * 10);
		int eachColumnBookNumber = Integer.parseInt(getResources().getString(R.string.eachColumnBookNumber));
		margin = (screenWidth - eachColumnBookNumber * bookCellWidth) / (2 * eachColumnBookNumber);

	}

	@Override
	public void addView(View child, int index, android.view.ViewGroup.LayoutParams params) {
		calculateParmenters();// 实时计算margin

		super.addView(child, index, params);

		LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
		layoutParams.width = LayoutParams.WRAP_CONTENT;
		layoutParams.height = LayoutParams.WRAP_CONTENT;
		layoutParams.topMargin = margin / 2;
		layoutParams.leftMargin = margin;
		layoutParams.rightMargin = margin;
		layoutParams.bottomMargin = margin / 2;
	}

}
