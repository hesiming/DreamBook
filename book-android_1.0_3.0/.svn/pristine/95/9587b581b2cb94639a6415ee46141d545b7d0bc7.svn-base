package cn.retech.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import cn.retech.adapter.BookGridViewAdapter;
import cn.retech.adapter.MyFragmentPagerAdapter;
import cn.retech.custom_control.MyViewPaper;
import cn.retech.custom_control.MyViewPaper.OnScrollListener;
import cn.retech.custom_control.TabNavigation;
import cn.retech.custom_control.TabNavigation.OnScrollFullListener;
import cn.retech.custom_control.TabNavigation.OnTabChangeListener;
import cn.retech.custom_control.TitleBarForBookStore;
import cn.retech.domainbean_model.book.Book;
import cn.retech.domainbean_model.book_categories.BookCategoriesNetRequestBean;
import cn.retech.domainbean_model.book_categories.BookCategoriesNetRespondBean;
import cn.retech.domainbean_model.book_categories.BookCategory;
import cn.retech.domainbean_model.login.LogonNetRequestBean;
import cn.retech.domainbean_model.login.LogonNetRespondBean;
import cn.retech.global_data_cache.GlobalDataCacheForMemorySingleton;
import cn.retech.global_data_cache.GlobalDataCacheForNeedSaveToFileSystem;
import cn.retech.my_domainbean_engine.domainbean_network_engine_singleton.DomainBeanNetworkEngineSingleton;
import cn.retech.my_domainbean_engine.domainbean_network_engine_singleton.DomainBeanNetworkEngineSingleton.NetRequestIndex;
import cn.retech.my_domainbean_engine.domainbean_network_engine_singleton.IDomainBeanAsyncNetRespondListener;
import cn.retech.my_domainbean_engine.net_error_handle.NetErrorBean;
import cn.retech.toolutils.DebugLog;
import cn.retech.toolutils.GlobalConstantForThisProject;

import com.umeng.analytics.MobclickAgent;

public class BookStoreActivity extends FragmentActivity {
	private final String TAG = this.getClass().getSimpleName();

	private final NetRequestIndex netRequestIndexForLogin = new NetRequestIndex();
	private final NetRequestIndex netRequestIndexForBookCategories = new NetRequestIndex();

	// tab bar 左右阴影图片, 所以tab bar还可以滑动
	private ImageView shadowSide;
	private TabNavigation tabNavigation;
	private final List<BookCategory> bookCategories = new ArrayList<BookCategory>();
	private MyFragmentPagerAdapter viewPaperAdapter;
	private MyViewPaper viewPager;
	private BookSearchFragment searchFragment;// 书籍搜索碎片

	// 绑定账号
	private final LogonNetRespondBean bindAccount = new LogonNetRespondBean();

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		int eachPageButtonNum = Integer.parseInt(getResources().getString(R.string.tabnavigation_button_number));
		if (bookCategories.size() != 0 && bookCategories.size() < eachPageButtonNum) {
			eachPageButtonNum = bookCategories.size();

			shadowSide.setVisibility(View.GONE);
		}
		tabNavigation.showCategory(bookCategories, eachPageButtonNum);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 获取标识位，用来设置是否支持屏幕翻转(手机不支持屏幕翻转)
		if (!getResources().getBoolean(R.bool.isSupportOverturn)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		setContentView(R.layout.book_store_layout);

		bindAccount.setUsername(GlobalDataCacheForMemorySingleton.getInstance.getUsernameForLastSuccessfulLogon());
		bindAccount.setPassword(GlobalDataCacheForMemorySingleton.getInstance.getPasswordForLastSuccessfulLogon());

		TitleBarForBookStore titleBar = (TitleBarForBookStore) findViewById(R.id.titlebar);
		String titleNameString = "";
		if (GlobalDataCacheForMemorySingleton.getInstance.getUsernameForLastSuccessfulLogon().equals(GlobalConstantForThisProject.PUBLIC_ACCOUNT_USERNAME)) {
			titleNameString = "今日书院";
		} else {
			titleNameString = "企业书院";
		}

		titleBar.setTitle(titleNameString);
		titleBar.setOnButtonClickListener(new TitleBarForBookStore.OnButtonClickListener() {
			@Override
			public void OnSearchButtonClicked() {
				searchFragment = new BookSearchFragment();
				Bundle bundle = new Bundle();
				bundle.putSerializable("type", BookGridViewAdapter.CellTypeEnum.BookStore);
				bundle.putSerializable("bindAccount", bindAccount);
				searchFragment.setArguments(bundle);
				searchFragment.setOnHandlerBookListener(new BookSearchFragment.OnHandlerListener() {
					@Override
					public void onClose() {
						final FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
						fragmentTransaction.remove(searchFragment);
						fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
						fragmentTransaction.commit();

						View view = findViewById(R.id.content_parent_store_layout);
						ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationY", 0f);
						objectAnimator.start();
					}

					@Override
					public void onBookClicked(Book book) {
						bookClickedHandler(book);
					}
				});

				final FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
				fragmentTransaction.add(R.id.search_view_layout, searchFragment);
				fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				fragmentTransaction.commit();

				View view = findViewById(R.id.content_parent_store_layout);
				ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationY", -(findViewById(R.id.titlebar).getHeight() + 3));
				objectAnimator.start();
			}

			@Override
			public void OnRefreshButtonClicked() {
				if (viewPaperAdapter != null) {
					BookStoreChannelFragment fragment = (BookStoreChannelFragment) viewPaperAdapter.getItem(viewPager.getCurrentItem());
					fragment.refresh();
				}
			}

			@Override
			public void OnBackButtonClicked() {
				finish();
			}
		});

		shadowSide = (ImageView) findViewById(R.id.shadow_side_imageView);
		tabNavigation = (TabNavigation) findViewById(R.id.tabNavigation);
		tabNavigation.setOnTabChangeListener(new OnTabChangeListener() {
			@Override
			public void onTabChange(int postion) {
				viewPager.setCurrentItem(postion);
			}
		});
		tabNavigation.setOnScrollFullLeftListener(new OnScrollFullListener() {
			@Override
			public void onScrollFullLeft() {
				shadowSide.setVisibility(View.VISIBLE);
				shadowSide.setBackgroundDrawable(getResources().getDrawable(R.drawable.shadow_side_full_left));
			}

			@Override
			public void onScrollFullRight() {
				shadowSide.setVisibility(View.VISIBLE);
				shadowSide.setBackgroundDrawable(getResources().getDrawable(R.drawable.shadow_side_full_right));
			}

			@Override
			public void onScrolling() {
				shadowSide.setVisibility(View.VISIBLE);
				shadowSide.setBackgroundDrawable(getResources().getDrawable(R.drawable.shadow_side));
			}
		});
		viewPager = (MyViewPaper) findViewById(R.id.book_list_viewPager);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageSelected(int arg0) {
				tabNavigation.setCurrentItem(arg0);
			}
		});
		viewPager.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScroll(int l, int oldl, int width) {
				tabNavigation.scrollKit(l, oldl, width);
			}
		});

		// 首先登录
		requestLogin(GlobalDataCacheForMemorySingleton.getInstance.getUsernameForLastSuccessfulLogon(), GlobalDataCacheForMemorySingleton.getInstance.getPasswordForLastSuccessfulLogon());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();

		MobclickAgent.onPause(this);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();

		MobclickAgent.onResume(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();

		DomainBeanNetworkEngineSingleton.getInstance.cancelNetRequestByRequestIndex(netRequestIndexForLogin);
		DomainBeanNetworkEngineSingleton.getInstance.cancelNetRequestByRequestIndex(netRequestIndexForBookCategories);
	}

	private void requestLogin(final String userID, final String userPassWord) {

		LogonNetRequestBean netRequestBean = new LogonNetRequestBean.Builder(userID, userPassWord).builder();
		DomainBeanNetworkEngineSingleton.getInstance.requestDomainProtocol(netRequestIndexForLogin, netRequestBean, new IDomainBeanAsyncNetRespondListener() {
			@Override
			public void onFailure(NetErrorBean error) {
				Toast.makeText(BookStoreActivity.this, error.getErrorMessage(), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess(Object respondDomainBean) {
				// 登录成功, 获取最新的书籍分类列表
				requestBookCategories();
			}

		});
	}

	private void requestBookCategories() {
		BookCategoriesNetRequestBean netRequestBean = new BookCategoriesNetRequestBean();
		DomainBeanNetworkEngineSingleton.getInstance.requestDomainProtocol(netRequestIndexForBookCategories, netRequestBean, new IDomainBeanAsyncNetRespondListener() {

			@Override
			public void onFailure(NetErrorBean error) {
				DebugLog.e(TAG, "testBookCategories = " + error.getErrorMessage());
			}

			@Override
			public void onSuccess(Object respondDomainBean) {
				// 先清理一下 书籍分类集合
				bookCategories.clear();

				BookCategoriesNetRespondBean respondBean = (BookCategoriesNetRespondBean) respondDomainBean;
				// 书城频道碎片集合
				List<Fragment> channelFragments = new ArrayList<Fragment>();

				for (BookCategory bookCategory : respondBean.getCategories()) {
					// 如果某个分类下面没有书籍的话, 就不显示该分类
					if (bookCategory.getBookcount() > 0) {
						bookCategories.add(bookCategory);

						Bundle bundle = new Bundle();
						bundle.putString("identifier", bookCategory.getIdentifier());
						bundle.putSerializable("bindAccount", bindAccount);
						BookStoreChannelFragment fragment = (BookStoreChannelFragment) Fragment.instantiate(BookStoreActivity.this, BookStoreChannelFragment.class.getName(), bundle);
						fragment.setOnHandlerListener(new BookStoreChannelFragment.OnHandlerListener() {

							@Override
							public void onBookClicked(Book book) {
								bookClickedHandler(book);
							}
						});
						channelFragments.add(fragment);
					}
				}

				// tab bar 中显示的tab 分页数量
				int eachPageButtonNum = Integer.parseInt(getResources().getString(R.string.tabnavigation_button_number));
				if (bookCategories.size() != 0 && bookCategories.size() < eachPageButtonNum) {
					eachPageButtonNum = bookCategories.size();
					shadowSide.setVisibility(View.GONE);
				}
				tabNavigation.showCategory(bookCategories, eachPageButtonNum);

				//
				viewPaperAdapter = new MyFragmentPagerAdapter(BookStoreActivity.this.getSupportFragmentManager(), channelFragments);
				viewPager.setAdapter(viewPaperAdapter);
				viewPager.setCurrentItem(0);
			}
		});
	}

	/**
	 * 打开一本书籍
	 * 
	 * @param path
	 *            书籍存储路径
	 */
	private void openThisBookByPath(final String path) {
		File tempFile = new File(path);
		if (!tempFile.exists()) {
			Toast.makeText(this, "书籍资源文件已损坏, 请删除书籍文件后重新下载.", Toast.LENGTH_SHORT).show();
			return;
		}

		Intent intent = new Intent(this, ShowBookActivity.class);
		intent.putExtra(ShowBookActivity.EXTRA_ZIP_FILE, path);
		startActivity(intent);
	}

	public void onClickForNothing(View view) {
		// 阻隔点击事件
	}

	private void bookClickedHandler(Book book) {
		switch (book.getState()) {
		case kBookStateEnum_Unpaid: {// 未付费(只针对收费的书籍, 如果是免费的书籍, 会直接到下一个状态.
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.create();
			builder.setTitle("提示");
			builder.setMessage("暂不支持付费功能，请下载免费书籍");
			builder.setPositiveButton("确认", null);
			builder.show();
		}
			break;
		case kBookStateEnum_Free:// 免费
			GlobalDataCacheForMemorySingleton.getInstance.getLocalBookList().addBook(book);
			book.startDownloadBook();
			// 及时保存发生变化时的本地书籍列表
			GlobalDataCacheForNeedSaveToFileSystem.saveLocalBookToFileSystem(book);
			break;
		case kBookStateEnum_GetBookDownloadUrl:
		case kBookStateEnum_WaitForDownload:
		case kBookStateEnum_Downloading:
			book.stopDownloadBook();
			break;
		case kBookStateEnum_Pause:// 暂停(也就是未下载完成, 可以进行断点续传)
			book.startDownloadBook();
			break;
		case kBookStateEnum_NotInstalled:// 未安装(已经下载完成, 还未完成安装)
			book.unzipBook();
			break;
		case kBookStateEnum_Installed:// 已安装(已经解压开的书籍, 可以正常阅读了)
			openThisBookByPath(book.bookSaveDirPath());
			break;
		case kBookStateEnum_Update:// 有可以更新的内容
			break;
		default:
			break;
		}
	}
}
