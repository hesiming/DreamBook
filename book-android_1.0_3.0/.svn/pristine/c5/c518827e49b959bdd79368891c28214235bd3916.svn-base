package cn.retech.custom_control;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import cn.retech.activity.R;

public class MyGridView extends LinearLayout {
	protected int margin;
	private DataSetObserver dataSetObserver;
	protected BaseAdapter adapter;
	protected OnItemClickListener onItemClickListener;
	protected OnItemLongClickListener onItemLongClickListener;
	private Context mContext;
	private int eachColumnBookNumber = 0;

	public MyGridView(final Context context) {
		super(context);
		init(context);
	}

	public MyGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		this.mContext = context;
		eachColumnBookNumber = Integer.parseInt(getResources().getString(R.string.eachColumnBookNumber));
		this.setOrientation(LinearLayout.VERTICAL);
		dataSetObserver = new DataSetObserver() {
			@Override
			public void onChanged() {
				updateData();
			}

			@Override
			public void onInvalidated() {
				updateData();
			}
		};
	}

	public void updateData() {
		if (null != adapter) {
			this.removeAllViews();
			int count = 100;
			MyRowLinearLayout myRowLinearLayout = null;
			for (int i = 0; i < adapter.getCount(); i++) {
				final View view = adapter.getView(i, null, this);
				final int position = i;
				final long id = adapter.getItemId(position);

				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (null != onItemClickListener) {
							onItemClickListener.onItemClick(null, view, position, id);
						}
					}
				});
				view.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						if (null != onItemLongClickListener) {
							onItemLongClickListener.onItemLongClick(null, view, position, id);
						}
						return false;
					}
				});
				if (count > eachColumnBookNumber) {
					myRowLinearLayout = new MyRowLinearLayout(mContext);
					this.addView(myRowLinearLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
					count = 1;
				}
				myRowLinearLayout.addView(view);
				count++;
			}
		}
	}

	public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
		this.onItemLongClickListener = onItemLongClickListener;
	}

	/**
	 * @param adapter
	 *            the adapter to set
	 */
	public void setAdapter(BaseAdapter newAdapter) {
		adapter = newAdapter;

		adapter.registerDataSetObserver(dataSetObserver);

		updateData();
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}
}
