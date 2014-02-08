package cn.retech.domainbean_model.book;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;
import cn.retech.global_data_cache.LocalCacheDataPathConstant;
import cn.retech.toolutils.DebugLog;

//本地书籍列表(本地书籍包括, 已经下载完全, 并且已经解压开, 可以正常阅读的书籍; 也包括那些未下载完全的书籍(可以进行断电续传)).
public final class BookList {
  private final String TAG = this.getClass().getSimpleName();
  // 外部可以使用KVO来监听localBookList, 当localBookList属性 增加/删除 一本书时, 都会触发KVO
  private List<Book> bookList = new ArrayList<Book>();

  @SuppressWarnings("unchecked")
  public List<Book> getCloneBookList() {
    return (List<Book>) (((ArrayList<Book>) bookList).clone());
  }

  @Deprecated
  public int size() {
    return bookList.size();
  }

  @Deprecated
  public Book get(int location) {
    if (location < 0 || location >= size()) {
      return null;
    }

    return bookList.get(location);
  }

  // 对外的接口方法 (操作列表)
  public Book bookByContentID(String contentIDString) {
    if (TextUtils.isEmpty(contentIDString)) {
      assert false : "入参错误 contentIDString !";
      return null;
    }

    Book result = null;
    for (Book localBook : bookList) {
      if (contentIDString.equals(localBook.getInfo().getContent_id())) {
        result = localBook;
        break;
      }
    }

    return result;
  }

  public boolean addBook(Book newBook) {
    do {
      if (newBook == null) {
        assert false : "入参 newBook 非法.";
        break;
      }

      if (bookList.contains(newBook)) {
        DebugLog.e(TAG, "重复插入同一本书籍, 书籍ID=" + newBook.getInfo().getContent_id());
        break;
      }
      bookList.add(newBook);
      return true;
    } while (false);

    return false;
  }

  public void removeBook(Book book) {
    if (book == null) {
      assert false : "入参 book 非法.";
      return;
    }

    removeBookByContentID(book.getInfo().getContent_id());
  }

  public boolean removeBookAtIndex(int index) {
    if (index >= bookList.size() || index < 0) {
      assert false : "入参 index 数组越界.";
      return false;
    }

    Book book = bookList.get(index);
    removeBookByContentID(book.getInfo().getContent_id());
    return true;
  }

  /**
   * 根据书籍ID, 删除本地书籍列表中的书籍对象(所有删除书籍对象的方法, 最终都要调用这个方法, 因为只有这个方法, 才会将书籍从文件系统中删除)
   * 
   * @param contentIDString
   * 
   * @return
   */
  public boolean removeBookByContentID(String contentIDString) {
    if (TextUtils.isEmpty(contentIDString)) {
      assert false : "入参 contentIDString 非法.";
      return false;
    }

    for (Book book : bookList) {
      if (contentIDString.equals(book.getInfo().getContent_id())) {

        // 删除文件系统中保存的书籍
        deleteBookFromFileSystemWithContentID(book.getInfo().getContent_id());
        // 删除内存中保存的书籍
        bookList.remove(book);
        return true;
      }
    }

    return false;
  }

  public int indexOfBookByContentID(String contentIDString) {
    do {
      if (TextUtils.isEmpty(contentIDString)) {
        assert false : "入参 contentIDString 非法.";
        break;
      }

      for (int i = 0; i < bookList.size(); i++) {
        Book book = bookList.get(i);
        if (contentIDString.equals(book.getInfo().getContent_id())) {
          return i;
        }
      }
    } while (false);

    return -1;
  }

  /**
   * 从文件系统中删除一本书籍
   * 
   * @param contentIDString
   */
  private void deleteBookFromFileSystemWithContentID(String contentIDString) {
    do {
      if (TextUtils.isEmpty(contentIDString)) {
        break;
      }
      String bookDirPath = LocalCacheDataPathConstant.localBookCachePathInSDCard() + "/" + contentIDString;
      File bookDir = new File(bookDirPath);
      if (!bookDir.exists()) {
        break;
      }
      deleteDirectory(bookDirPath);

    } while (false);

  }

  /**
   * 删除单个文件
   * 
   * @param fileName
   *          被删除文件的文件名
   * @return 单个文件删除成功返回true,否则返回false
   */
  private boolean deleteFile(String fileName) {
    File file = new File(fileName);
    if (file.isFile() && file.exists()) {
      file.delete();
      return true;
    } else {
      DebugLog.e(TAG, "删除单个文件" + fileName + "失败！");
      return false;
    }
  }

  /**
   * 删除目录（文件夹）以及目录下的文件
   * 
   * @param dir
   *          被删除目录的文件路径
   * @return 目录删除成功返回true,否则返回false
   */
  private boolean deleteDirectory(String dir) {
    // 如果dir不以文件分隔符结尾，自动添加文件分隔符
    if (!dir.endsWith(File.separator)) {
      dir = dir + File.separator;
    }
    File dirFile = new File(dir);
    // 如果dir对应的文件不存在，或者不是一个目录，则退出
    if (!dirFile.exists() || !dirFile.isDirectory()) {
      DebugLog.e(TAG, "删除目录失败" + dir + "目录不存在！");
      return false;
    }
    boolean flag = true;
    // 删除文件夹下的所有文件(包括子目录)
    File[] files = dirFile.listFiles();
    for (int i = 0; i < files.length; i++) {
      // 删除子文件
      if (files[i].isFile()) {
        flag = deleteFile(files[i].getAbsolutePath());
        if (!flag) {
          break;
        }
      }
      // 删除子目录
      else {
        flag = deleteDirectory(files[i].getAbsolutePath());
        if (!flag) {
          break;
        }
      }
    }

    if (!flag) {
      DebugLog.e(TAG, "删除目录失败");
      return false;
    }

    // 删除当前目录
    if (dirFile.delete()) {
      return true;
    } else {
      DebugLog.e(TAG, "删除目录" + dir + "失败！");
      return false;
    }
  }

  public void deleteObservers() {
    for (Book book : bookList) {
      book.deleteObservers();
    }
  }

  // 支持书籍下载队列, 最大并发3本
  // 书籍列表中, 处于下载状态的书籍数量
  public int downlaodingBookNumber() {
    int number = 0;
    for (Book book : bookList) {
      if (Book.BookStateEnum.kBookStateEnum_GetBookDownloadUrl == book.getState() || Book.BookStateEnum.kBookStateEnum_Downloading == book.getState()) {
        number++;
      }
    }

    return number;
  }

  // 开始下载一本处于 "等待下载" 状态的书籍, 并且要忽略目标书籍(因为目前调用此方法是在 LocalBook中,
  // 所以当前一本书籍暂停或者出现其他情况时, 才会触发另一本处于等待状态的书籍的下载, 而此时当前书籍的状态可能还没从 WaitForDownload 更新
  public boolean startDownloadForWaitStateBookWithIgnoreBook(Book ignoreBook) {
    for (Book book : bookList) {
      // 防止出现引用死循环, 因为是在 startDownloadBook 方法中, 调用 startDownloadForWaitStateBookWithIgnoreBook 方法.
      if (book.equals(ignoreBook)) {
        continue;
      }
      if (Book.BookStateEnum.kBookStateEnum_WaitForDownload == book.getState()) {
        if (book.startDownloadBook()) {
          return true;
        }
      }
    }

    return false;

  }
}
