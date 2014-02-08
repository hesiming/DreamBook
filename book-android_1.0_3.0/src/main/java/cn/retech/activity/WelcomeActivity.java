package cn.retech.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import cn.retech.custom_control.ICustomControlDelegate;
import cn.retech.custom_control.WelcomeCustomControl;
import cn.retech.custom_control.WelcomeCustomControl.WelcomeCustomControlTypeEnum;
import cn.retech.toolutils.DebugLog;

import com.umeng.analytics.MobclickAgent;

public class WelcomeActivity extends Activity implements ICustomControlDelegate {
	private final String TAG = this.getClass().getSimpleName();
	private WelcomeCustomControl welcomeCustomControl;

	@Override
	public void customControlOnAction(Object contorl, Object actionTypeEnum) {
		if (actionTypeEnum instanceof WelcomeCustomControlTypeEnum) {
			switch ((WelcomeCustomControlTypeEnum) actionTypeEnum) {
			case FINISH:
				Intent intent = new Intent();
				intent.setClass(WelcomeActivity.this, BookShelfActivity.class);
				WelcomeActivity.this.startActivity(intent);
				WelcomeActivity.this.finish();

				// overridePendingTransition(android.R.anim.fade_in,
				// android.R.anim.fade_out);

				break;
			default:
				break;
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DebugLog.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.welcome_layout);

		welcomeCustomControl = (WelcomeCustomControl) findViewById(R.id.welcome_custon_control);
		welcomeCustomControl.setDelegate(this);
	}

	@Override
	protected void onDestroy() {
		DebugLog.i(TAG, "onDestroy");

		super.onDestroy();
	}

	@Override
	protected void onPause() {
		DebugLog.i(TAG, "onPause");
		super.onPause();

		MobclickAgent.onPause(this);
	}

	@Override
	protected void onRestart() {
		DebugLog.i(TAG, "onRestart");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		DebugLog.i(TAG, "onResume");
		super.onResume();

		MobclickAgent.onResume(this);
	}

	@Override
	protected void onStart() {
		DebugLog.i(TAG, "onStart");
		super.onStart();
	}

	@Override
	protected void onStop() {
		DebugLog.i(TAG, "onStop");
		super.onStop();
	}

}
