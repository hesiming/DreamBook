package cn.retech.activity;

import java.io.File;
import java.util.List;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.retech.adapter.BookGridViewAdapter;
import cn.retech.custom_control.CircleProgressObservable;
import cn.retech.custom_control.MyGridView;
import cn.retech.domainbean_model.book.Book;
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
import cn.retech.toolutils.ToolsFunctionForThisProgect;

import com.umeng.analytics.MobclickAgent;

public class BookShelfActivity extends Activity {
  private final String TAG = this.getClass().getSimpleName();
  private final NetRequestIndex netRequestIndexForLogin = new NetRequestIndex();

  private BookSearchFragment searchFragment;// 书籍搜索碎片
  private TextView publicBookstore;// 书院
  private TextView privateBookstore;// 企业
  private MyGridView gridView;// 书架中放置书籍的网格控件
  private View toolsBar;// 如果用户登录了企业账户, 就显示这个工具栏, 在此显示 "退出登录" 按钮

  // 网格控件适配器
  private final BookGridViewAdapter gridViewAdapter = new BookGridViewAdapter(BookGridViewAdapter.CellTypeEnum.BookShelf);

  // 判断横竖屏状态
  @Override
  public void onConfigurationChanged(Configuration config) {
    super.onConfigurationChanged(config);
    if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
      // 横屏显示
    } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
      // 竖屏显示
    }
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      showDialogForQuiteApp();
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    DebugLog.i(TAG, "onCreate");
    super.onCreate(savedInstanceState);
    // 获取标识位，用来设置是否支持屏幕翻转(手机不支持屏幕翻转)
    if (!getResources().getBoolean(R.bool.isSupportOverturn)) {
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    setContentView(R.layout.book_shelf_layout);

    // 书院 按钮
    publicBookstore = (TextView) findViewById(R.id.public_bookstore_textView);
    publicBookstore.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        // 用户点击 书院 直接跳转到 书城界面, 由书城界面完成登录
        gotoBookStoreActivity(GlobalConstantForThisProject.PUBLIC_ACCOUNT_USERNAME, GlobalConstantForThisProject.PUBLIC_ACCOUNT_PASSWORD);
      }
    });

    // 企业 按钮
    privateBookstore = (TextView) findViewById(R.id.private_bookstore_textView);
    privateBookstore.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {

        // 如果有企业账户处于登录中, 就直接跳转到书城界面, 否则弹出登录提示框
        LogonNetRespondBean privateAccountLogonNetRespondBean = GlobalDataCacheForMemorySingleton.getInstance.getPrivateAccountLogonNetRespondBean();
        if (privateAccountLogonNetRespondBean == null) {
          showDialogForLogin();
        } else {
          gotoBookStoreActivity(privateAccountLogonNetRespondBean.getUsername(), privateAccountLogonNetRespondBean.getPassword());
        }
      }
    });

    gridView = (MyGridView) findViewById(R.id.bookshelf_gridLayout);
    gridView.setAdapter(gridViewAdapter);
    // 设置监听
    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final List<Book> books = GlobalDataCacheForMemorySingleton.getInstance.getLocalBookList().getCloneBookList();
        final Book book = books.get(position);
        bookClickedHandler(book);
      }

    });
    gridView.setOnItemLongClickListener(new OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        final List<Book> books = GlobalDataCacheForMemorySingleton.getInstance.getLocalBookList().getCloneBookList();
        final Book book = books.get(position);
        switch (book.getState()) {
          case kBookStateEnum_Downloading:// 正在下载中...
            // 正在下载中的书籍需要先暂停
            book.stopDownloadBook();
            break;
          default:
            break;
        }
        // 删除本地书籍
        showDialogForDeleteBook(book);

        return false;
      }
    });

    toolsBar = findViewById(R.id.toolsbar_layout);
    // "退出登录" 按钮
    TextView quiteLogin = (TextView) findViewById(R.id.quite_login_textView);
    quiteLogin.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // 显示 "退出登录" 提示框
        showDialogForLogout();
      }
    });

    Button searchButton = (Button) findViewById(R.id.search_button);
    searchButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        searchFragment = new BookSearchFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("type", BookGridViewAdapter.CellTypeEnum.BookShelf);
        searchFragment.setArguments(bundle);
        searchFragment.setOnHandlerBookListener(new BookSearchFragment.OnHandlerListener() {
          @Override
          public void onClose() {
            final FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.remove(searchFragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.commit();

            View view = findViewById(R.id.content_parent_layout);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationY", 0f);
            objectAnimator.start();
            // 如果用户处于登录状态状态, 就显示toolsbar
            LogonNetRespondBean logonNetRespondBean = GlobalDataCacheForMemorySingleton.getInstance.getPrivateAccountLogonNetRespondBean();
            if (logonNetRespondBean == null) {
              toolsBar.setVisibility(View.GONE);
            } else {
              toolsBar.setVisibility(View.VISIBLE);
            }
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

        View view = findViewById(R.id.content_parent_layout);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationY", -(findViewById(R.id.titlebar_layout).getHeight() - 5));
        objectAnimator.start();
        toolsBar.setVisibility(View.GONE);
      }
    });
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

    // 如果用户处于登录状态状态, 就显示toolsbar
    LogonNetRespondBean logonNetRespondBean = GlobalDataCacheForMemorySingleton.getInstance.getPrivateAccountLogonNetRespondBean();
    if (logonNetRespondBean == null) {
      toolsBar.setVisibility(View.GONE);
    } else {
      toolsBar.setVisibility(View.VISIBLE);
    }

    // 重刷gridview控件
    gridViewAdapter.changeDataSource(GlobalDataCacheForMemorySingleton.getInstance.getLocalBookList().getCloneBookList());
    ((ViewGroup) gridView.getParent()).updateViewLayout(gridView, gridView.getLayoutParams());
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

  private void bookClickedHandler(Book book) {
    switch (book.getState()) {
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
      case kBookStateEnum_Unziping:// 解压书籍zip资源包中....
        Toast.makeText(BookShelfActivity.this, "正在解压中, 请稍等.", Toast.LENGTH_SHORT).show();
        break;
      case kBookStateEnum_Installed:// 已安装(已经解压开的书籍, 可以正常阅读了)
        openThisBookByPath(book.bookSaveDirPath());
        break;
      default:

        break;
    }
  }

  /**
   * 企业用户登录
   * 
   * @param username
   * @param password
   */
  private void requestPrivateAccountLogin(final String username, final String password, final DomainBeanNetworkEngineSingleton.OnNetRequestResultListener onNetRequestCompletedListener) {

    LogonNetRequestBean netRequestBean = new LogonNetRequestBean.Builder(username, password).builder();
    DomainBeanNetworkEngineSingleton.getInstance.requestDomainProtocol(netRequestIndexForLogin, netRequestBean, new IDomainBeanAsyncNetRespondListener() {
      @Override
      public void onFailure(NetErrorBean error) {
        // 企业用户登录失败
        Toast.makeText(BookShelfActivity.this, error.getErrorMessage(), Toast.LENGTH_SHORT).show();

        onNetRequestCompletedListener.onNetRequestCompleted();
      }

      @Override
      public void onSuccess(Object respondDomainBean) {
        // 企业用户登录成功
        LogonNetRespondBean privateAccountLogonNetRespondBean = (LogonNetRespondBean) respondDomainBean;
        privateAccountLogonNetRespondBean.setUsername(username);
        privateAccountLogonNetRespondBean.setPassword(password);
        GlobalDataCacheForMemorySingleton.getInstance.setPrivateAccountLogonNetRespondBean(privateAccountLogonNetRespondBean);

        // 跳转 "书城界面"
        gotoBookStoreActivity(username, password);

        onNetRequestCompletedListener.onNetRequestCompleted();
      }
    });

    if (!netRequestIndexForLogin.idle()) {
      onNetRequestCompletedListener.onNetRequestAttached();
    }
  }

  /**
   * 打开一本书籍
   * 
   * @param path
   *          书籍存储路径
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

  /**
   * 显示删除一本书籍的提示框
   * 
   * @param book
   */
  private void showDialogForDeleteBook(final Book book) {
    AlertDialog.Builder builder = new AlertDialog.Builder(BookShelfActivity.this);
    builder.create();
    builder.setTitle("提示");
    builder.setMessage("是否删除" + book.getInfo().getName());
    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        GlobalDataCacheForMemorySingleton.getInstance.getLocalBookList().removeBook(book);
        gridViewAdapter.changeDataSource(GlobalDataCacheForMemorySingleton.getInstance.getLocalBookList().getCloneBookList());
        // 及时保存发生变化时的本地书籍列表
        //GlobalDataCacheForNeedSaveToFileSystem.writeLocalBookToFileSystem();
      }
    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
      }
    });
    builder.show();
  }

  /**
   * 显示用户登录提示框
   */
  private void showDialogForLogin() {
    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
    View v = inflater.inflate(R.layout.login_dialog, null);

    final EditText userNameEditText = (EditText) v.findViewById(R.id.user_name_editText);
    final EditText passwordEditText = (EditText) v.findViewById(R.id.password_editText);

    if (DebugLog.logIsOpen) {
      userNameEditText.setText("appletest");
      passwordEditText.setText("appletest");
    }

    AlertDialog.Builder builder = new AlertDialog.Builder(BookShelfActivity.this);
    builder.setView(v);
    builder.create();
    builder.setTitle("用户登录");
    builder.setCancelable(false);// 这里是屏蔽用户点击back按键
    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        String errorMessage = "";
        do {
          if (TextUtils.isEmpty(userNameEditText.getText())) {
            errorMessage = "用户名不能为空!";
            break;
          }

          if (TextUtils.isEmpty(passwordEditText.getText())) {
            errorMessage = "密码不能为空!";
            break;
          }

          requestPrivateAccountLogin(userNameEditText.getText().toString(), passwordEditText.getText().toString(), new DomainBeanNetworkEngineSingleton.OnNetRequestResultListener() {

            @Override
            public void onNetRequestCompleted() {
              privateBookstore.setClickable(true);
            }

            @Override
            public void onNetRequestAttached() {
              // 发起网络请求成功, 暂时屏蔽 "企业按钮"
              privateBookstore.setClickable(false);
            }
          });

          return;
        } while (false);

        Toast.makeText(BookShelfActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
      }
    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
      }
    });
    builder.show();
  }

  /**
   * 显示 "退出企业账户" 提示框
   */
  private void showDialogForLogout() {
    AlertDialog.Builder builder = new AlertDialog.Builder(BookShelfActivity.this);
    builder.create();
    builder.setTitle("提示");
    builder.setMessage("是否退出当前企业账号?");
    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        GlobalDataCacheForMemorySingleton.getInstance.setPrivateAccountLogonNetRespondBean(null);
        toolsBar.setVisibility(View.GONE);
      }
    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
      }
    });
    builder.show();
  }

  /**
   * 显示 "退出应用程序" 提示框
   */
  private void showDialogForQuiteApp() {
    AlertDialog.Builder builder = new Builder(BookShelfActivity.this);
    builder.setTitle("提示");
    builder.setMessage("是否退出应用");
    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();

        // 先暂停用于异步保存LocalBook的线程
        GlobalDataCacheForNeedSaveToFileSystem.stopSaveLocalBookThread();

        // 这里需要先将所有正在下载的书籍暂停，然后才能退出应用
        List<Book> books = GlobalDataCacheForMemorySingleton.getInstance.getLocalBookList().getCloneBookList();
        for (Book book : books) {
          book.stopDownloadBook();
        }
        CircleProgressObservable.INSTANCE.stop();
        // 退出应用
        ToolsFunctionForThisProgect.quitApp(BookShelfActivity.this);
      }
    });
    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
      }
    });
    AlertDialog dialog = builder.create();
    dialog.show();
  }

  /**
   * 跳转到 "书城" 界面
   * 
   * @param username
   * @param password
   */
  private void gotoBookStoreActivity(String username, String password) {
    GlobalDataCacheForMemorySingleton.getInstance.setUsernameForLastSuccessfulLogon(username);
    GlobalDataCacheForMemorySingleton.getInstance.setPasswordForLastSuccessfulLogon(password);
    Intent intent = new Intent(this, BookStoreActivity.class);
    startActivity(intent);
  }

  public void onClickForNothing(View view) {
    // 阻隔点击事件
  }
}
