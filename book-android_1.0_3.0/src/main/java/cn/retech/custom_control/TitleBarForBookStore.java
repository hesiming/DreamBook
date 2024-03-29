package cn.retech.custom_control;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.retech.activity.R;

public class TitleBarForBookStore extends LinearLayout {
	public static interface OnButtonClickListener {
		public void OnBackButtonClicked();

		public void OnSearchButtonClicked();

		public void OnRefreshButtonClicked();
	}

	private OnButtonClickListener onButtonClickListener;

	public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
		this.onButtonClickListener = onButtonClickListener;
	}

	private TextView titleTextView;

	public TitleBarForBookStore(final Context context) {
		super(context);

		init(context);
	}

	public TitleBarForBookStore(Context context, AttributeSet attrs) {
		super(context, attrs);

		init(context);
	}

	public void setTitle(String title) {
		titleTextView.setText(title);
	}

	@SuppressLint("ResourceAsColor")
	private void init(Context context) {
		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.page_title_layout, this);

		titleTextView = (TextView) findViewById(R.id.title_TextView);

		// 返回按钮
		Button backButton = (Button) findViewById(R.id.back_Button);
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onButtonClickListener.OnBackButtonClicked();
			}
		});

		// 刷新按钮
		ImageView refreshButton = (ImageView) findViewById(R.id.refresh_ImageView);
		refreshButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onButtonClickListener.OnRefreshButtonClicked();
			}
		});

		// 搜索按钮
		ImageView searchButton = (ImageView) findViewById(R.id.search_ImageView);
		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onButtonClickListener.OnSearchButtonClicked();
			}
		});
	}
}
