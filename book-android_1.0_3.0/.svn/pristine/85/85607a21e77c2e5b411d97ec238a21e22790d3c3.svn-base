package cn.retech.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import kr.co.netntv.playercore.ContentInfo;
import kr.co.netntv.playercore.CoreLib;
import kr.co.netntv.playercore.MTopLayout;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ShowBookActivity extends Activity {
	boolean mIsTestMode = false;
	boolean mDeleteImages = false;
	public static final String EXTRA_ZIP_FILE = "EXTRA_ZIP_FILE";
	MTopLayout mLayout;
	Dialog mProgressDlg;
	ProgressBar mProgressBar;
	TextView mProgressText;

	boolean mRunMainLoop = true;
	Handler mMainLoopHandler = new Handler();

	class HandlerCallback implements Runnable {
		public void run() {
			if (mRunMainLoop) {
				mMainLoopHandler.post(this);
			}
			if (mLayout != null) {
				if (mLayout.appObjectsChanged()) {
					mLayout.invalidate();
				} else {
					mLayout.update();
				}
			}
		}
	}

	HandlerCallback mMainLoopCallback = new HandlerCallback();

	private static final String DOC_FILE = "document.st";
	public static final String ZIP_FILE = "content.zip";
	public static final String EXTRA_CONTENTS_PATH = "contents_path";

	private String mContentsPath;

	boolean mIsLocalFilePlaying;

	public void repositionContent(ContentInfo contentInfo) {

		switch (contentInfo.orientation) {
		case ContentInfo.OrientationHorizontal:
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			break;
		case ContentInfo.OrientationVertical:
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			break;
		case ContentInfo.OrientationAdaptive:
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			break;
		}

		if (mLayout == null) {
			mLayout = new MTopLayout(ShowBookActivity.this);
			FrameLayout fl = (FrameLayout) findViewById(R.id.main_frame);
			fl.addView(mLayout);
			FrameLayout.LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			lp.gravity = Gravity.CENTER;
			mLayout.setLayoutParams(lp);
			mLayout.startContent(mContentsPath, mIsLocalFilePlaying);
		}
		mLayout.setContentInfo(contentInfo);
	}

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		CoreLib.init();

		setContentView(R.layout.main);

		Intent i = getIntent();
		mContentsPath = i.getStringExtra(EXTRA_ZIP_FILE);
		Log.i("test", "path : " + mContentsPath);
		startContent(false);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mLayout != null) {
				if (mLayout.isPlayingFullscreenVideo()) {
					mLayout.stopVideo();
					return true;
				} else {
					new AlertDialog.Builder(this)
							// .setIcon()
							.setTitle(R.string.notification).setMessage(R.string.really_quit)
							.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									ShowBookActivity.this.finish();
								}
							}).setNegativeButton(R.string.no, null).show();

					return true;
				}
			} else {
				new AlertDialog.Builder(this)
						// .setIcon()
						.setTitle(R.string.notification).setMessage(R.string.really_quit)
						.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								ShowBookActivity.this.finish();
							}
						}).setNegativeButton(R.string.no, null).show();

				return true;
			}
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {

		super.onConfigurationChanged(newConfig);
		if (mLayout != null) {
			mLayout.notifyOrientationChanged();
		}
	}

	public void startContent(boolean isLocalFilePlaying) {
		mIsLocalFilePlaying = isLocalFilePlaying;
		String docPath = mContentsPath + "/" + DOC_FILE;
		File file = new File(docPath);
		if (file.exists() == false) {
			String message = getString(R.string.file_not_found) + "( " + docPath + " )";
			new AlertDialog.Builder(this)
			// .setIcon()
					.setTitle(R.string.warning).setMessage(message).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							ShowBookActivity.this.finish();
						}
					}).show();
			return;
		}

		String errStr = CoreLib.loadContent(docPath, 0, 0);
		if (errStr.contentEquals("Could not open file: Object version mismatch")) {
			String message = getString(R.string.object_version_mismatch);
			new AlertDialog.Builder(this)
			// .setIcon()
					.setTitle(R.string.warning).setMessage(message).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							ShowBookActivity.this.finish();
						}
					}).show();
			return;
		} else if (errStr.contentEquals("NO_ERROR") == false) {
			Toast.makeText(ShowBookActivity.this, errStr, Toast.LENGTH_SHORT).show();
			finish();
		}

		ContentInfo contentInfo = new ContentInfo();
		CoreLib.getContentInfo(contentInfo);

		repositionContent(contentInfo);

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mLayout != null) {
			mLayout.onPause();
		}
		mRunMainLoop = false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mLayout != null) {
			mLayout.onResume();
		}
		mRunMainLoop = true;
		mMainLoopHandler.post(mMainLoopCallback);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mLayout != null) {
			mLayout.onDestroy();
		}
		CoreLib.uninit();
	}

	private class ZipProgress {
		String currentFilename;
		int currentPercent;
		int currentIndex;
		int totalPercent;
	}

	@SuppressWarnings("unused")
	private class ExtractZipFileTaskInSdcard extends AsyncTask<String, ZipProgress, String> {
		File zipFile;
		private static final long INTERVAL = 500;
		int entryCount = 0;
		ZipProgress zipProgress = new ZipProgress();
		long lastCall = 0;

		public ExtractZipFileTaskInSdcard(File zipFile) {
			this.zipFile = zipFile;
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				createDocumentFileInSdCard(zipFile);
				// getFileStreamPath(ZIP_FILE).delete();
				Log.d("LOG", "Contents File Proc Complete............");
				return "Success";
			} catch (Exception e) {
				e.printStackTrace();
				return "Fail";
			}
		}

		@Override
		protected void onProgressUpdate(ZipProgress... values) {
			ZipProgress zip = values[0];
			String currentText = getString(R.string.progress_current) + " : " + zip.currentFilename + "(" + zip.currentPercent + "%)";
			((TextView) findViewById(R.id.current_progress_text)).setText(currentText);
			String totalText = getString(R.string.progress_total) + " : " + zip.currentIndex + " / " + entryCount + "(" + zip.totalPercent
					+ "%)";
			((TextView) findViewById(R.id.progress_text)).setText(totalText);
		}

		@Override
		protected void onPostExecute(String result) {
			findViewById(R.id.progress_layout).setVisibility(View.GONE);
			if (result.contentEquals("Success") == false) {
				errorMessage();
				return;
			}
			startContent(false);
		}

		private void createDocumentFileInSdCard(File zipFile) throws Exception {
			ZipFile file = new ZipFile(zipFile);
			entryCount = file.size();
			@SuppressWarnings("resource")
			ZipInputStream zip = new ZipInputStream(new FileInputStream(zipFile));
			ZipEntry ze;
			String zipFileParent = zipFile.getParent();
			int index = 0;

			while ((ze = zip.getNextEntry()) != null) {
				String path = ze.getName();
				zipProgress.currentFilename = path;

				index++;
				int percent = index * 100 / entryCount;
				zipProgress.currentIndex = index;
				zipProgress.totalPercent = percent;

				if (ze.getName().indexOf("/") != -1) {
					File parent = new File(path).getParentFile();
					if (!parent.exists())
						if (!parent.mkdirs())
							throw new IOException("Unable to create folder " + parent);
				}

				Log.d("LOG", path);
				FileOutputStream fout = new FileOutputStream(zipFileParent + "/" + path);
				byte[] bytes = new byte[4096];
				long entrysize = ze.getSize();
				long total = 0;

				for (int c = zip.read(bytes); c != -1; c = zip.read(bytes)) {
					fout.write(bytes, 0, c);
					total += c;
					zipProgress.currentPercent = (int) (total * 100 / entrysize);
					callPublishProgress(false);
				}
				zip.closeEntry();
				fout.close();
				// new File(mContentsPath, ZIP_FILE).delete();

				callPublishProgress(true);
			}
		}

		private void callPublishProgress(boolean isFileCompleted) {
			long current = System.currentTimeMillis();
			if (isFileCompleted || current > lastCall + INTERVAL) {
				publishProgress(zipProgress);
				lastCall = current;
			}
		}
	}

	private void errorMessage() {
		Toast.makeText(ShowBookActivity.this, R.string.contents_construct_error, Toast.LENGTH_SHORT).show();
		finish();
	}

}
