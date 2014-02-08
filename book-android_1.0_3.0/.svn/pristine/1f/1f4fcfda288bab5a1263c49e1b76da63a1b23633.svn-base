package cn.retech.adapter;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import cn.retech.activity.MyApplication;
import cn.retech.custom_control.BookShelfBookCell;
import cn.retech.custom_control.BookStoreBookCell;
import cn.retech.domainbean_model.book.Book;

public class BookGridViewAdapter extends BaseAdapter {

  /**
   * 适配器中要使用的cell的类型, 现在书架和书城都使用这个适配器, 但是需要加载的cell不同
   * 
   * @author skyduck
   * 
   */
  public static enum CellTypeEnum {
    // 用于书架的cell
    BookShelf,
    // 用于书城的cell
    BookStore
  };

  public BookGridViewAdapter(CellTypeEnum cellTypeEnum) {
    this.cellTypeEnum = cellTypeEnum;
  }

  private final CellTypeEnum cellTypeEnum;
  private List<Book> dataSource = new ArrayList<Book>();

  public void changeDataSource(final List<Book> newDataSource) {
    if (newDataSource == null) {
      assert false : "入参 newDataSource 为空. ";
      return;
    }

    this.dataSource = newDataSource;
    this.notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return dataSource.size();
  }

  @Override
  public Object getItem(int position) {
    return dataSource.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    Book book = (Book) getItem(position);
    View bookCell = null;
    switch (cellTypeEnum) {
      case BookShelf:
        bookCell = new BookShelfBookCell(MyApplication.getApplication());
        ((BookShelfBookCell) bookCell).bind(book);
        break;
      case BookStore:
        bookCell = new BookStoreBookCell(MyApplication.getApplication());
        ((BookStoreBookCell) bookCell).bind(book);
        break;
      default:
        break;
    }

    return bookCell;
  }

}
