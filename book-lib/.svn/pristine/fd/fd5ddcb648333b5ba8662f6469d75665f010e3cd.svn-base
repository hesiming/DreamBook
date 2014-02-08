package cn.retech.global_data_cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;
import cn.retech.domainbean_model.book.Book;
import cn.retech.domainbean_model.book.BookList;
import cn.retech.domainbean_model.book.for_serializable.BookForSerializable;
import cn.retech.domainbean_model.login.LogonNetRespondBean;
import cn.retech.toolutils.DebugLog;

/**
 * 这里序列化对象的保存目录是 : /data/data/<包名>/files/ , 这个目录会在用户在 "应用程序管理" 中点击 "清理数据" 按钮后被清理
 * 
 * @author computer
 * 
 */
public final class GlobalDataCacheForNeedSaveToFileSystem {
	private final static String TAG = GlobalDataCacheForNeedSaveToFileSystem.class.getSimpleName();

	private GlobalDataCacheForNeedSaveToFileSystem() {

	}

	private enum CacheDataNameForSaveToFile {
		// 用户是否是首次启动App
		FirstStartApp,
		// 用户最后一次成功登录时得到的响应业务Bean
		PrivateAccountLogonNetRespondBean,

		// 本地书籍
		LocalBook,
		// 当前app版本号, 用了防止升级app时, 本地缓存的序列化数据恢复出错.
		LocalAppVersion
	};

	public static synchronized void readAppConfingInfo() {
		//
		DebugLog.i(TAG, "start loading --> isFirstStartApp");
		final Boolean isFirstStartApp = (Boolean) deserializeObjectFromDeviceFileSystem(CacheDataNameForSaveToFile.FirstStartApp.name());
		if (isFirstStartApp != null) {
			DebugLog.i(TAG, "isFirstStartApp=" + isFirstStartApp.toString());
			GlobalDataCacheForMemorySingleton.getInstance.setFirstStartApp(isFirstStartApp.booleanValue());
		} else {
			DebugLog.i(TAG, "isFirstStartApp is null");
		}

	}

	/**
	 * 读取本地缓存的 用户登录信息
	 */
	public static void readUserLoginInfoToGlobalDataCacheForMemorySingleton() {
		// 私有用户登录成功后, 服务器返回的信息(判断此对象是否为空, 来确定当前是否有企业账户处于登录状态)
		DebugLog.i(TAG, "start loading --> privateAccountLogonNetRespondBean");
		final LogonNetRespondBean privateAccountLogonNetRespondBean = (LogonNetRespondBean) deserializeObjectFromDeviceFileSystem(CacheDataNameForSaveToFile.PrivateAccountLogonNetRespondBean.name());
		GlobalDataCacheForMemorySingleton.getInstance.setPrivateAccountLogonNetRespondBean(privateAccountLogonNetRespondBean);
	}

	/**
	 * 读取本地缓存的 书籍列表
	 */
	public static void readLocalBookListToGlobalDataCacheForMemorySingleton() {
		BookList localBookList = new BookList();

		File localBookCacheDirInSDCard = new File(LocalCacheDataPathConstant.localBookCachePathInSDCard());
		for (File localBookCacheDir : localBookCacheDirInSDCard.listFiles()) {
			for (File tempFile : localBookCacheDir.listFiles()) {
				if (tempFile.isFile() && tempFile.getName().equals(CacheDataNameForSaveToFile.LocalBook.name())) {
					FileInputStream fileInputStream = null;
					try {
						fileInputStream = new FileInputStream(tempFile);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
						fileInputStream = null;
					}
					if (fileInputStream == null) {
						continue;
					}
					final BookForSerializable bookForSerializable = (BookForSerializable) deserializeObjectFromFile(fileInputStream);
					if (bookForSerializable == null) {
						continue;
					}
					Book book = new Book(bookForSerializable.getBookInfo());
					book.setState(bookForSerializable.getBookStateEnum());
					book.setBindAccount(bookForSerializable.getBindAccount());
					localBookList.addBook(book);
				}
			}
		}

		GlobalDataCacheForMemorySingleton.getInstance.setLocalBookList(localBookList);
	}

	public static void readAllCacheData() {
		// 读取App配置信息
		readAppConfingInfo();
		// 读取本地缓存的 "用户登录信息"
		readUserLoginInfoToGlobalDataCacheForMemorySingleton();
		// 读取本地缓存的书籍列表
		readLocalBookListToGlobalDataCacheForMemorySingleton();
	}

	// ///////////////////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////

	public static synchronized void writeAppConfigInfo() {
		//
		final Boolean isFirstStartApp = Boolean.valueOf(GlobalDataCacheForMemorySingleton.getInstance.isFirstStartApp());
		serializeObjectToDeviceFileSystem(CacheDataNameForSaveToFile.FirstStartApp.name(), isFirstStartApp);
	}

	/**
	 * 保存用户登录信息到设备文件系统中
	 */
	public static void writeUserLoginInfoToFileSystem() {

		//
		final LogonNetRespondBean privateAccountLogonNetRespondBean = GlobalDataCacheForMemorySingleton.getInstance.getPrivateAccountLogonNetRespondBean();
		serializeObjectToDeviceFileSystem(CacheDataNameForSaveToFile.PrivateAccountLogonNetRespondBean.name(), privateAccountLogonNetRespondBean);
	}

	/**
	 * 保存书籍列表信息到设备文件系统中
	 */
	public static void writeLocalBookListToFileSystem() {

		final BookList localBookList = GlobalDataCacheForMemorySingleton.getInstance.getLocalBookList();
		List<Book> bookList = localBookList.getCloneBookList();
		for (Book localBook : bookList) {
			writeLocalBookToFile(localBook, SaveLocalBookStorageTypeEnum.SD_CARD);
		}

	}

	private static enum SaveLocalBookStorageTypeEnum {
		// 设备文件系统
		DEVICE_FILE_SYSTEM,
		// SD卡
		SD_CARD
	};

	private static void writeLocalBookToFile(final Book localBook, final SaveLocalBookStorageTypeEnum storageTypeEnum) {
		final BookForSerializable localBookForSerializable = new BookForSerializable();
		localBookForSerializable.setBindAccount(localBook.getBindAccount());
		localBookForSerializable.setBookInfo(localBook.getInfo());

		// 保存书籍到文件系统中时, 书籍的状态要限制成几个规定的状态
		Book.BookStateEnum bookStateEnum = localBook.getState();
		switch (bookStateEnum) {
		case kBookStateEnum_NotInstalled:
		case kBookStateEnum_Unziping:
			// 如果当前正在解压中, 用户按下home按键, 使app进入后台, 那么此时我们保存到文件系统中得状态不能是
			// Unziping, 应该是 NotInstalled(未安装).
			// 因为用户可能会前行关闭app. 那我们就设计当用户下一次进入app时, 显示 "未安装" 这个状态, 当用户点下按钮时,
			// 要重新安装书籍
			bookStateEnum = Book.BookStateEnum.kBookStateEnum_NotInstalled;
			break;
		case kBookStateEnum_Installed:
			bookStateEnum = Book.BookStateEnum.kBookStateEnum_Installed;
			break;
		default:
			bookStateEnum = Book.BookStateEnum.kBookStateEnum_Pause;
			break;
		}
		localBookForSerializable.setBookStateEnum(bookStateEnum);
		// 序列化保存到书籍目录中
		File bookSaveDir = new File(localBook.bookSaveDirPath());
		if (!bookSaveDir.exists()) {
			if (!bookSaveDir.mkdir()) {
				return;
			}
		}

		switch (storageTypeEnum) {
		case DEVICE_FILE_SYSTEM:
			serializeObjectToDeviceFileSystem(localBook.getInfo().getContent_id() + "." + CacheDataNameForSaveToFile.LocalBook.name(), localBookForSerializable);
			break;
		case SD_CARD:
			serializeObjectToSDCard(CacheDataNameForSaveToFile.LocalBook.name(), localBook.bookSaveDirPath(), localBookForSerializable);
		default:
			break;
		}
	}

	public static void saveLocalBookToFileSystem(final Book localBook) {
		// 异步保存书籍信息到SD中, 因为向SD中写入数据很慢
		localBookSaveBlockingQueue.add(localBook.getInfo().getContent_id());
	}

	private static BlockingQueue<String> localBookSaveBlockingQueue = new LinkedBlockingQueue<String>();

	public static final void stopSaveLocalBookThread() {
		saveLocalBookThread.interrupt();
	}

	private static final Thread saveLocalBookThread = new Thread(new Runnable() {

		@Override
		public void run() {
			while (true && !saveLocalBookThread.isInterrupted()) {
				try {
					String bookID = localBookSaveBlockingQueue.take();
					Book localBook = GlobalDataCacheForMemorySingleton.getInstance.getLocalBookList().bookByContentID(bookID);
					writeLocalBookToFile(localBook, SaveLocalBookStorageTypeEnum.SD_CARD);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			DebugLog.e(TAG, "停止 saveLocalBookThread !");
		}
	});
	static {
		saveLocalBookThread.start();
	}

	public static void writeAllCacheData() {
		// 保存 "app 配置信息"
		writeAppConfigInfo();
		// 保存 "用户登录信息"
		writeUserLoginInfoToFileSystem();
		// 保存书籍列表
		writeLocalBookListToFileSystem();
	}

	// ///////////////////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////

	private static void serializeObjectToSDCard(final String fileName, final String directoryPath, final Object object) {
		File file = new File(directoryPath + "/" + fileName);
		if (file.exists()) {
			file.delete();
		}
		try {
			serializeObjectToFile(object, new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	private static void serializeObjectToDeviceFileSystem(final String fileName, final Object object) {
		File file = new File(GlobalDataCacheForMemorySingleton.getInstance.getApplication().getFilesDir() + "/" + fileName);
		if (file.exists()) {
			file.delete();
		}
		try {
			serializeObjectToFile(object, GlobalDataCacheForMemorySingleton.getInstance.getApplication().openFileOutput(fileName, Context.MODE_PRIVATE));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static void serializeObjectToFile(final Object object, final FileOutputStream fileOutputStream) {
		ObjectOutputStream objectOutputStream = null;
		try {

			do {
				if (fileOutputStream == null) {
					assert false : "入参为空!";
					break;
				}
				if (object == null) {
					break;
				}

				objectOutputStream = new ObjectOutputStream(fileOutputStream);
				objectOutputStream.writeObject(object);

			} while (false);

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (objectOutputStream != null) {
					objectOutputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				if (fileOutputStream != null) {
					fileOutputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private static Object deserializeObjectFromDeviceFileSystem(final String fileName) {
		Object object = null;
		try {
			object = deserializeObjectFromFile(GlobalDataCacheForMemorySingleton.getInstance.getApplication().openFileInput(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return object;
	}

	private static Object deserializeObjectFromSDCard(final String fileName, final String directoryPath) {
		Object object = null;
		try {
			object = deserializeObjectFromFile(new FileInputStream(new File(directoryPath + "/" + fileName)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return object;
	}

	private static Object deserializeObjectFromFile(FileInputStream fileInputStream) {
		Object object = null;
		ObjectInputStream objectInputStream = null;

		try {
			objectInputStream = new ObjectInputStream(fileInputStream);
			object = objectInputStream.readObject();
		} catch (Exception ex) {
			object = null;
			ex.printStackTrace();
		} finally {
			try {
				if (objectInputStream != null) {
					objectInputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				if (fileInputStream != null) {
					fileInputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return object;
	}

}
