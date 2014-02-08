package cn.retech.custom_control;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import cn.retech.activity.R;

@SuppressLint("HandlerLeak")
public class WelcomeCustomControl extends FrameLayout {

	public enum WelcomeCustomControlTypeEnum {
		FINISH
	}

	private final ImageView moveBackgroundImage;
	// 设置动画前界面显示时间
	private int intervalTime = 500;
	// 设置图片移动时间
	private int durationTime = 2000;

	private ICustomControlDelegate delegate;
	TimerTask task = new TimerTask() {
		@Override
		public void run() {
			Message message = new Message();
			message.what = 1;
			handler.sendMessage(message);
		}
	};
	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				TranslateAnimation translateAnimation = new TranslateAnimation(moveBackgroundImage.getX(),
						moveBackgroundImage.getX() - 200, moveBackgroundImage.getY(), moveBackgroundImage.getY());
				translateAnimation.setDuration(1500);

				final AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0f);
				alphaAnimation.setDuration(1000);

				translateAnimation.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						moveBackgroundImage.setX(getX() - 200);
						moveBackgroundImage.startAnimation(alphaAnimation);
					}
				});

				alphaAnimation.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						moveBackgroundImage.setAlpha(0f);
						if (delegate != null) {
							delegate.customControlOnAction(this, WelcomeCustomControlTypeEnum.FINISH);
						}
					}
				});

				moveBackgroundImage.startAnimation(translateAnimation);

				break;
			}
			super.handleMessage(msg);
		}
	};

	public WelcomeCustomControl(Context context, AttributeSet attrs) {
		super(context, attrs);
		final LayoutInflater inflaterInstance = LayoutInflater.from(context);
		inflaterInstance.inflate(R.layout.welcome_coustom_control_layout, this);

		moveBackgroundImage = (ImageView) findViewById(R.id.move_imageView); // ImageView对象
		Timer timer = new Timer(true);
		timer.schedule(task, intervalTime); // 延时1000ms后执行，1000ms执行一次

	}

	public int getDurationTime() {
		return durationTime;
	}

	public int getIntervalTime() {
		return intervalTime;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}

	public void setDelegate(ICustomControlDelegate delegate) {
		this.delegate = delegate;
	}

	public void setDurationTime(int durationTime) {
		this.durationTime = durationTime;
	}

	public void setIntervalTime(int intervalTime) {
		this.intervalTime = intervalTime;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);

	}
}
