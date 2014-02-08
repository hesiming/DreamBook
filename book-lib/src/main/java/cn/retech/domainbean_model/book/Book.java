package cn.retech.domainbean_model.book;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Observable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import cn.retech.domainbean_model.booklist_in_bookstores.BookInfo;
import cn.retech.domainbean_model.get_book_download_url.GetBookDownloadUrlNetRequestBean;
import cn.retech.domainbean_model.get_book_download_url.GetBookDownloadUrlNetRespondBean;
import cn.retech.domainbean_model.login.LogonNetRespondBean;
import cn.retech.global_data_cache.GlobalDataCacheForMemorySingleton;
import cn.retech.global_data_cache.GlobalDataCacheForNeedSaveToFileSystem;
import cn.retech.global_data_cache.LocalCacheDataPathConstant;
import cn.retech.my_domainbean_engine.domainbean_network_engine_singleton.DomainBeanNetworkEngineSingleton;
import cn.retech.my_domainbean_engine.domainbean_network_engine_singleton.DomainBeanNetworkEngineSingleton.NetRequestIndex;
import cn.retech.my_domainbean_engine.domainbean_network_engine_singleton.IDomainBeanAsyncNetRespondListener;
import cn.retech.my_domainbean_engine.domainbean_network_engine_singleton.IFileAsyncHttpResponseHandler;
import cn.retech.my_domainbean_engine.net_error_handle.NetErrorBean;
import cn.retech.toolutils.DebugLog;
import cn.retech.toolutils.SimpleSDCardTools;

public final class Book extends Observable {

	private final String TAG = this.getClass().getSimpleName();

	private Handler handler = new Handler(Looper.getMainLooper());
	private NetRequestIndex netRequestIndexForDownloadBookFile = new NetRequestIndex();
	private NetRequestIndex netRequestIndexForGetBookDownloadUrl = new NetRequestIndex();

	public static enum ObserverEnum {
		// 书籍解压进度
		kBookDecompressProgress,
		// 书籍下载进度
		kBookDownloadProgress,
		// 书籍状态
		kBookState
	}

	// 书籍状态枚举
	public static enum BookStateEnum {

		// 收费
		kBookStateEnum_Unpaid,
		// 支付中....
		kBookStateEnum_Paiding,
		// 已支付
		kBookStateEnum_Paid,
		// 免费
		kBookStateEnum_Free,
		// 有可以更新的内容
		kBookStateEnum_Update,

		// 等待下载中...(此时并没有调用网络引擎发起网络请求)
		kBookStateEnum_WaitForDownload,

		// 正在获取用于书籍下载的URL中...
		kBookStateEnum_GetBookDownloadUrl,
		// 正在下载中...
		kBookStateEnum_Downloading,
		// 暂停(也就是未下载完成, 可以进行断电续传)
		kBookStateEnum_Pause,
		// 未安装(已经下载完成, 还未完成安装)
		kBookStateEnum_NotInstalled,
		// 解压书籍zip资源包中....
		kBookStateEnum_Unziping,
		// 已安装(已经解压开的书籍, 可以正常阅读了)
		kBookStateEnum_Installed

	};

	public Book(BookInfo info) {
		// 进行 "数据保护"
		this.info = info.clone();
		double price = 0.0;
		try {
			price = Double.valueOf(info.getPrice());
		} catch (NumberFormatException e) {
			price = 0.0;
		}
		if (price > 0) {
			state = BookStateEnum.kBookStateEnum_Unpaid;
		} else {
			state = BookStateEnum.kBookStateEnum_Free;
		}

	}

	// 书籍信息(从服务器获取的, 这个属性在初始化 LocalBook 时被赋值, 之后就是只读数据了)
	private BookInfo info;

	public BookInfo getInfo() {
		return info;
	}

	public void setInfo(BookInfo info) {
		this.info = info;
	}

	// 解压进度, 100% 数值是 1, 外部可以这样计算完成百分比 downloadProgress * 100
	private int decompressProgress;

	private void setDecompressProgress(int decompressProgress) {
		this.decompressProgress = decompressProgress;
		handler.post(new Runnable() {
			@Override
			public void run() {
				setChanged();
				notifyObservers(ObserverEnum.kBookDecompressProgress);
			}
		});

	}

	public int getDecompressProgress() {
		return decompressProgress;
	}

	// 下载进度, 100% 数值是 1, 外部可以这样计算完成百分比 downloadProgress * 100
	private float downloadProgress;

	private void setDownloadProgress(float downloadProgress) {
		this.downloadProgress = downloadProgress;
		handler.post(new Runnable() {

			@Override
			public void run() {
				setChanged();
				notifyObservers(ObserverEnum.kBookDownloadProgress);
				clearChanged();
			}
		});

	}

	public float getDownloadProgress() {
		return downloadProgress;
	}

	// 书籍状态
	private BookStateEnum state;

	public BookStateEnum getState() {
		return state;
	}

	public void setState(BookStateEnum state) {
		this.state = state;
		handler.post(new Runnable() {

			@Override
			public void run() {
				setChanged();
				notifyObservers(ObserverEnum.kBookState);
			}
		});

	}

	// 书籍保存文件夹路径
	public String bookSaveDirPath() {
		return LocalCacheDataPathConstant.localBookCachePathInSDCard() + "/" + info.getContent_id();
	}

	private static final String kTmpDownloadBookFileName = "tmp.zip";

	private String bookTmpZipResFilePath() {
		return bookSaveDirPath() + "/" + kTmpDownloadBookFileName;
	}

	// 删除书籍下载临时文件
	private void removeBookTmpZipResFile() {
		File file = new File(bookTmpZipResFilePath());
		if (file.exists()) {
			if (!file.delete()) {
				DebugLog.e(TAG, "删除缓存的未下载完成的书籍数据失败!");
			}
		}
	}

	// 从书城中, 点击一本还未下载的书籍时, 这本书籍会被加入
	// "本地书籍列表(在 GlobalDataCacheForMemorySingleton->localBookList 中保存)"
	// 目前有两个需求:
	// 1) 当A账户登录书城下载书籍时, 如果此时A账户退出了(或者被B账户替换了), 那么就要暂停正在进行下载的所有跟A账户绑定的书籍;
	// 这里考虑的一点是, 如果A/B账户切换时, 当前账户是希望独享下载网速的.
	// 但是, 对于跟 "公共账户" 绑定的书籍, 是不需要停止下载的.
	// 2) 已经存在于 "本地书籍列表" 中的未下载完成的书籍, 再次进行断点续传时, 需要将跟这本书绑定的账号信息传递给服务器,
	// 才能获取到最新的书籍下载地址.
	// 因为服务器为了防止盗链, 所以每次断点续传时, 都需要重新获取目标书籍的最新下载地址.
	private LogonNetRespondBean bindAccount;

	public LogonNetRespondBean getBindAccount() {
		return bindAccount;
	}

	public void setBindAccount(LogonNetRespondBean bindAccount) {
		this.bindAccount = bindAccount;
	}

	// 设置当前书籍最新的版本(可以通过书籍的版本来确定服务器是否有可以下载的更新包)
	public void setBookVersion(String bookLatestVersion) {

	};

	// 付费之后的收据
	private byte[] receipt;

	// 开始下载一本书籍
	public boolean startDownloadBook() {
		return startDownloadBookWithReceipt(receipt);
	}

	// 开始下载一本书籍(需要 "收据", 对应那种收费的书籍)
	public boolean startDownloadBookWithReceipt(byte[] receipt) {
		do {
			if (state != BookStateEnum.kBookStateEnum_Paid && state != BookStateEnum.kBookStateEnum_Free && state != BookStateEnum.kBookStateEnum_Update && state != BookStateEnum.kBookStateEnum_Pause
					&& state != BookStateEnum.kBookStateEnum_WaitForDownload) {
				// 只有书籍处于 Paid / Free / Update / Pause / WaitForDownload 状态时,
				// 才有可能触发书籍下载
				break;
			}

			if (!netRequestIndexForGetBookDownloadUrl.idle()) {
				// 已经在获取书籍的URL, 不需要重复发起书籍下载请求
				break;
			}

			if (GlobalDataCacheForMemorySingleton.getInstance.getLocalBookList().downlaodingBookNumber() >= 3) {
				// 更新书籍状态->WaitForDownload
				// 要控制同时并发下载的书籍的数量, 防止阻塞正常的网络访问.
				if (state != BookStateEnum.kBookStateEnum_WaitForDownload) {
					setState(BookStateEnum.kBookStateEnum_WaitForDownload);
				}

				this.receipt = receipt;
				break;
			}

			GetBookDownloadUrlNetRequestBean netRequestBeanForGetBookDownloadUrl = new GetBookDownloadUrlNetRequestBean(getInfo().getContent_id(), getBindAccount());
			netRequestBeanForGetBookDownloadUrl.setReceipt(receipt);
			DomainBeanNetworkEngineSingleton.getInstance.requestDomainProtocol(netRequestIndexForGetBookDownloadUrl, netRequestBeanForGetBookDownloadUrl, new IDomainBeanAsyncNetRespondListener() {

				@Override
				public void onFailure(NetErrorBean error) {
					DebugLog.e(TAG, "获取书籍下载URL失败." + error.toString());

					// 激活另一本处于下载等待状态的书籍
					GlobalDataCacheForMemorySingleton.getInstance.getLocalBookList().startDownloadForWaitStateBookWithIgnoreBook(Book.this);

					//
					bookDownloadOrUnzipErrorHandlerWithMessage(error.getErrorMessage());

				}

				@Override
				public void onSuccess(Object respondDomainBean) {
					DebugLog.i(TAG, "获取要下载的书籍URL 成功!");
					GetBookDownloadUrlNetRespondBean netRespondBeanForGetBookDownloadUrl = (GetBookDownloadUrlNetRespondBean) respondDomainBean;
					startDownloadBookWithURLString(netRespondBeanForGetBookDownloadUrl.getBookDownloadUrl());
				}
			});

			// 更新书籍状态->GetBookDownloadUrl
			setState(BookStateEnum.kBookStateEnum_GetBookDownloadUrl);
			return true;
		} while (false);

		return false;
	}

	// 开始下载一本书籍(为了防止盗链, 所以每次下载书籍时的URL都是一次性的)
	private boolean startDownloadBookWithURLString(final String urlString) {
		String messageOfCustomError = "";
		do {
			if (!SimpleSDCardTools.isHasSDCard()) {
				messageOfCustomError = "SD卡不存在!";
				break;
			}

			if (TextUtils.isEmpty(urlString)) {
				assert false : "入参urlString为空!";
				messageOfCustomError = "入参urlString为空!";
				break;
			}
			DebugLog.i(TAG, "要下载的书籍URL = " + urlString);

			//
			DomainBeanNetworkEngineSingleton.getInstance.cancelNetRequestByRequestIndex(netRequestIndexForDownloadBookFile);

			// 创建书籍保存路径
			File file = new File(bookSaveDirPath());
			if (!file.exists()) {
				if (!file.mkdir()) {
					// 创建特定书籍文件夹失败, 此时没有必要再进行下载了
					messageOfCustomError = "创建要下载到本地的书籍的保存文件夹失败!";
					break;
				}
			}

			DebugLog.i(TAG, "开始下载书籍 : " + info.getName() + ", id = " + info.getContent_id());
			// 开始下载目标书籍 bookTmpZipResFilePath()
			DomainBeanNetworkEngineSingleton.getInstance.requestBookDownlaod(netRequestIndexForDownloadBookFile, urlString, bindAccount, bookTmpZipResFilePath(), new IFileAsyncHttpResponseHandler() {

				@Override
				public void onFailure(final NetErrorBean error) {
					DebugLog.e(TAG, "书籍下载失败 error=" + error.toString());

					// 激活另一本处于下载等待状态的书籍
					GlobalDataCacheForMemorySingleton.getInstance.getLocalBookList().startDownloadForWaitStateBookWithIgnoreBook(Book.this);

					bookDownloadOrUnzipErrorHandlerWithMessage(error.getErrorMessage());

					// 删除临时文件
					removeBookTmpZipResFile();
				}

				@Override
				public void onProgress(final long bytesWritten, final long totalSize) {

					float pro = (float) bytesWritten / totalSize * 100.0f;

					setDownloadProgress(pro);
				}

				@Override
				public void onSuccess(final File file) {
					DebugLog.i(TAG, "书籍下载成功 : " + info.getName() + ", id = " + info.getContent_id());

					receipt = null;

					// 激活另一本处于下载等待状态的书籍
					GlobalDataCacheForMemorySingleton.getInstance.getLocalBookList().startDownloadForWaitStateBookWithIgnoreBook(Book.this);

					// 更新书籍状态 --> Unziping
					setState(BookStateEnum.kBookStateEnum_Unziping);

					// 当书籍下载成功后自动在后台进行解压操作
					UnzipBookZipPackageToSDCardAsyncTask unzipBookZipPackageToSDCardAsyncTask = new UnzipBookZipPackageToSDCardAsyncTask();
					unzipBookZipPackageToSDCardAsyncTask.execute();
				}
			});

			// 更新书籍状态->Downloading
			setState(BookStateEnum.kBookStateEnum_Downloading);
			return true;
		} while (false);

		// 启动下载一本书籍的操作失败
		DebugLog.e(TAG, "启动一本书籍下载失败，原因:" + messageOfCustomError);
		bookDownloadOrUnzipErrorHandlerWithMessage(messageOfCustomError);
		return false;
	}

	// 停止下载一本书籍
	public void stopDownloadBook() {
		if (state != BookStateEnum.kBookStateEnum_Downloading && state != BookStateEnum.kBookStateEnum_GetBookDownloadUrl && state != BookStateEnum.kBookStateEnum_WaitForDownload) {
			// 只有处于 "Downloading / GetBookDownloadUrl / WaitForDownload" 状态的书籍,
			// 才能被暂停.
			return;
		}

		// 先取消之前的网络请求
		DomainBeanNetworkEngineSingleton.getInstance.cancelNetRequestByRequestIndex(netRequestIndexForGetBookDownloadUrl);
		DomainBeanNetworkEngineSingleton.getInstance.cancelNetRequestByRequestIndex(netRequestIndexForDownloadBookFile);
		// 更新书籍状态->Pause
		setState(BookStateEnum.kBookStateEnum_Pause);
		// 激活另一本处于下载等待状态的书籍
		GlobalDataCacheForMemorySingleton.getInstance.getLocalBookList().startDownloadForWaitStateBookWithIgnoreBook(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((info.getContent_id() == null) ? 0 : info.getContent_id().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Book other = (Book) obj;
		if (info == null) {
			if (other.info != null)
				return false;
		} else if (!info.getContent_id().equals(other.getInfo().getContent_id()))
			return false;
		return true;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private class UnzipBookZipPackageToSDCardAsyncTask extends AsyncTask<String, Integer, String> {
		private int entryCount;

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected String doInBackground(String... params) {
			String resultString = "Success";
			if (!unzipBookZipResToSDCard()) {
				resultString = "Fail";
			}
			// 删除临时文件
			removeBookTmpZipResFile();

			return resultString;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			int progress = values[0] * 100 / entryCount;
			setDecompressProgress(progress);
		}

		@Override
		protected void onPostExecute(String result) {
			if (result.equals("Success")) {
				setState(BookStateEnum.kBookStateEnum_Installed);
				GlobalDataCacheForNeedSaveToFileSystem.saveLocalBookToFileSystem(Book.this);
				DebugLog.i(TAG, "书籍解压完成.");

			} else {
				DebugLog.e(TAG, "解压书籍失败!");
				bookDownloadOrUnzipErrorHandlerWithMessage("解压书籍失败.");
			}
		}

		private boolean unzipBookZipResToSDCard() {
			boolean isOK = true;
			ZipInputStream zipInputStream = null;
			try {
				File bookTmpZipResFile = new File(bookTmpZipResFilePath());
				ZipFile zipFile = new ZipFile(bookTmpZipResFile);
				entryCount = zipFile.size();
				// 获取输入流
				zipInputStream = new ZipInputStream(new FileInputStream(bookTmpZipResFile));

				// 正在解压的文件索引
				int index = 0;

				while (true) {
					ZipEntry zipEntry = zipInputStream.getNextEntry();
					if (zipEntry == null) {
						break;
					}
					// 获取出zip文件名的路径，并创建输出流
					FileOutputStream fileOutputStream = new FileOutputStream(bookSaveDirPath() + "/" + zipEntry.getName());
					byte[] bytes = new byte[4096];
					// 循环将输入流离的数据写入到输出流中
					while (true) {
						int readLength = zipInputStream.read(bytes);
						if (readLength == -1) {
							break;
						}
						fileOutputStream.write(bytes, 0, readLength);
					}
					// ????
					zipInputStream.closeEntry();
					//
					fileOutputStream.close();

					// 通知外层当前解压进度
					publishProgress(++index);

				}
			} catch (Exception e) {
				DebugLog.e(TAG, e.toString());
				isOK = false;
			} finally {
				if (zipInputStream != null) {
					try {
						zipInputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			return isOK;
		}
	}

	// 书籍下载解压过程中, 如果发生错误时, 通知控制层的块
	public interface IBookDownloadErrorBlockHandler {
		public void onError(final String errorMessage);
	}

	private IBookDownloadErrorBlockHandler bookDownloadErrorBlockHandler;

	// 书籍下载/解压过程中发生了错误时的处理方法
	// 此方法作用 :
	// 1. 通知外层发生了错误
	// 2. 复位书籍状态-->Pause, 好让用户可以重新下载
	public void setBookDownloadErrorBlockHandler(IBookDownloadErrorBlockHandler bookDownloadErrorBlockHandler) {
		this.bookDownloadErrorBlockHandler = bookDownloadErrorBlockHandler;
	}

	private void bookDownloadOrUnzipErrorHandlerWithMessage(final String message) {
		if (bookDownloadErrorBlockHandler != null) {

			// 通知外层, 发生了错误
			bookDownloadErrorBlockHandler.onError(message);
		}

		// 复位下载进度, 此时不需要通知外层
		downloadProgress = 0;

		// 复位当前书籍状态, 好让用户可以重新下载
		setState(BookStateEnum.kBookStateEnum_Pause);
	}

	// 解压一本书籍(只有当上次解压一本书籍, 没有完成时, 退出了app, 此时app的状态为 kBookStateEnum_Unziping 时,
	// 这个方法才有意义
	public void unzipBook() {
		String messageOfCustomError = "";
		do {
			if (state != BookStateEnum.kBookStateEnum_NotInstalled) {
				// 如果当前书籍不是 NotInstalled(未安装), 那么这个方法无效
				messageOfCustomError = "当前书籍状态不是 未安装 状态.";
				break;
			}

			File bookTmpZipResFile = new File(bookTmpZipResFilePath());
			if (!bookTmpZipResFile.exists()) {
				// 如果书籍临时压缩包已经不存在了, 此方法也无效
				messageOfCustomError = "书籍临时压缩包文件丢失!";
				break;
			}

			// 更新书籍状态 --> Unziping
			setState(BookStateEnum.kBookStateEnum_Unziping);
			// 在后台线程中解压缩书籍zip资源包.
			UnzipBookZipPackageToSDCardAsyncTask unzipBookZipPackageToSDCardAsyncTask = new UnzipBookZipPackageToSDCardAsyncTask();
			unzipBookZipPackageToSDCardAsyncTask.execute();
			return;
		} while (false);

		// 复位书籍状态, 给用户重新下载书籍的机会
		bookDownloadOrUnzipErrorHandlerWithMessage(messageOfCustomError);
		return;
	}

}
