package cn.retech.activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import cn.retech.adapter.BookGridViewAdapter;
import cn.retech.custom_control.MyGridView;
import cn.retech.custom_control.SearchView;
import cn.retech.custom_control.SearchView.OnCloseListener;
import cn.retech.domainbean_model.book.Book;
import cn.retech.domainbean_model.book.BookList;
import cn.retech.domainbean_model.book_search.BookSearchNetRequestBean;
import cn.retech.domainbean_model.booklist_in_bookstores.BookInfo;
import cn.retech.domainbean_model.booklist_in_bookstores.BookListInBookstoresNetRespondBean;
import cn.retech.domainbean_model.login.LogonNetRespondBean;
import cn.retech.global_data_cache.GlobalDataCacheForMemorySingleton;
import cn.retech.my_domainbean_engine.domainbean_network_engine_singleton.DomainBeanNetworkEngineSingleton;
import cn.retech.my_domainbean_engine.domainbean_network_engine_singleton.DomainBeanNetworkEngineSingleton.NetRequestIndex;
import cn.retech.my_domainbean_engine.domainbean_network_engine_singleton.IDomainBeanAsyncNetRespondListener;
import cn.retech.my_domainbean_engine.net_error_handle.NetErrorBean;
import cn.retech.toolutils.DebugLog;

public class BookSearchFragment extends Fragment {

	private final String TAG = this.getClass().getSimpleName();

	public BookSearchFragment() {
		super();
	}

	private BookGridViewAdapter.CellTypeEnum typeEnum;

	public static interface OnHandlerListener {
		public void onBookClicked(Book book);

		public void onClose();
	}

	private OnHandlerListener onHandlerListener;

	public void setOnHandlerBookListener(OnHandlerListener onHandlerListener) {
		this.onHandlerListener = onHandlerListener;

	}

	// 绑定账号
	private LogonNetRespondBean bindAccount;
	private SearchView searchView;
	private BookGridViewAdapter bookListAdapter;
	private BookList bookList = new BookList();
	private MyGridView bookListGridView;
	private RelativeLayout netResuqesttingLayout;
	private View noDataImageView;
	private TextView noDataSmg;
	private final String nO_DATA_SMGString = "没有搜到\"";
	private final NetRequestIndex netRequestIndexForBookSearch = new NetRequestIndex();

	public void doSearchByBookNameFromNet(String bookName) {
		searchBooksFromNet(bookName);
	}

	public void doSearchByBookNameFromLocal(String bookName) {
		searchBooksFromLocation(bookName);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		typeEnum = (BookGridViewAdapter.CellTypeEnum) getArguments().getSerializable("type");
		bookListAdapter = new BookGridViewAdapter(typeEnum);
		if (getArguments().containsKey("bindAccount")) {
			bindAccount = (LogonNetRespondBean) getArguments().getSerializable("bindAccount");
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		return inflater.inflate(R.layout.fragment_book_search, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		netResuqesttingLayout = (RelativeLayout) getView().findViewById(R.id.net_requestting_layout);
		noDataImageView = getView().findViewById(R.id.no_data_Layout);
		noDataSmg = (TextView) getView().findViewById(R.id.no_data_smg_textView);

		bookListGridView = (MyGridView) getView().findViewById(R.id.book_list_gridView);
		bookListGridView.setAdapter(bookListAdapter);
		bookListGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// final List<Book> books =
				// GlobalDataCacheForMemorySingleton.getInstance.getLocalBookList().getCloneBookList();
				// final Book book = books.get(position);
				searchView.showOrHidenInput(false);
				final Book book = (Book) bookListAdapter.getItem(position);
				onHandlerListener.onBookClicked(book);
			}
		});

		searchView = (SearchView) getView().findViewById(R.id.searchView);
		searchView.setOnCloseListener(new OnCloseListener() {
			@Override
			public void onClose() {
				onHandlerListener.onClose();
			}

			@Override
			public void onCancel() {
				netResuqesttingLayout.setVisibility(View.GONE);
				noDataImageView.setVisibility(View.GONE);
				bookListGridView.setVisibility(View.GONE);
			}
		});
		searchView.setOnQueryTextListener(new OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				if (query.equals("")) {
					return false;
				}

				bookListGridView.setVisibility(View.VISIBLE);

				switch (typeEnum) {
				case BookShelf:
					searchBooksFromLocation(query);

					break;
				case BookStore:
					searchBooksFromNet(query);

					break;
				default:
					break;
				}

				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});
	}

	private void searchBooksFromLocation(String searchContent) {
		bookList = new BookList();
		BookList localBookListFromLocal = GlobalDataCacheForMemorySingleton.getInstance.getLocalBookList();

		for (Book localBook : localBookListFromLocal.getCloneBookList()) {
			if (localBook.getInfo().getName().contains(searchContent)) {
				bookList.addBook(localBook);
			}
		}
		bookList.deleteObservers();
		bookListAdapter.changeDataSource(bookList.getCloneBookList());

		if (bookList.getCloneBookList().size() == 0) {
			noDataSmg.setText(nO_DATA_SMGString + searchContent + "\"");
			noDataImageView.setVisibility(View.VISIBLE);
		} else {
			noDataImageView.setVisibility(View.INVISIBLE);
		}
	}

	private void searchBooksFromNet(final String searchContent) {
		netResuqesttingLayout.setVisibility(View.VISIBLE);

		BookSearchNetRequestBean bookSearchNetRequestBean = new BookSearchNetRequestBean(searchContent);
		DomainBeanNetworkEngineSingleton.getInstance.requestDomainProtocol(netRequestIndexForBookSearch, bookSearchNetRequestBean, new IDomainBeanAsyncNetRespondListener() {

			@Override
			public void onFailure(NetErrorBean error) {
				DebugLog.e(TAG, "testSearchBook error = " + error.getErrorMessage());

				netResuqesttingLayout.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onSuccess(Object respondDomainBean) {
				DebugLog.e(TAG, "testSearchBook onSuccess = " + respondDomainBean);
				netResuqesttingLayout.setVisibility(View.INVISIBLE);

				bookList = new BookList();

				BookListInBookstoresNetRespondBean bookListInBookstoresNetRespondBean = (BookListInBookstoresNetRespondBean) respondDomainBean;
				BookList localBookListFromLocal = GlobalDataCacheForMemorySingleton.getInstance.getLocalBookList();// 本地
				for (BookInfo bookInfo : bookListInBookstoresNetRespondBean.getBookInfoList()) {
					Book newBook = localBookListFromLocal.bookByContentID(bookInfo.getContent_id());
					if (newBook == null) {
						newBook = new Book(bookInfo);

					} else {
						newBook.setInfo(bookInfo);
					}
					newBook.setBindAccount(bindAccount);
					bookList.addBook(newBook);
				}
				bookList.deleteObservers();
				bookListAdapter.changeDataSource(bookList.getCloneBookList());

				if (bookList.getCloneBookList().size() == 0) {
					noDataSmg.setText(nO_DATA_SMGString + searchContent + "\"");
					noDataImageView.setVisibility(View.VISIBLE);
				} else {
					noDataSmg.setText("");
					noDataImageView.setVisibility(View.INVISIBLE);
				}
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();

		searchView.showOrHidenInput(true);
	}

	@Override
	public void onStop() {
		super.onStop();

		searchView.showOrHidenInput(false);
	}
}
