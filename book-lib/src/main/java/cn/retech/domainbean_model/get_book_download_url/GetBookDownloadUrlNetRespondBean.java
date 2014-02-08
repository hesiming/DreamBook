package cn.retech.domainbean_model.get_book_download_url;

public final class GetBookDownloadUrlNetRespondBean {
	// 书籍下载URL
	private final String bookDownloadUrl;

	public GetBookDownloadUrlNetRespondBean(String bookDownloadUrl) {
		this.bookDownloadUrl = bookDownloadUrl;
	}

	public String getBookDownloadUrl() {
		return bookDownloadUrl;
	}

	@Override
	public String toString() {
		return "GetBookDownloadUrlNetRespondBean [bookDownloadUrl=" + bookDownloadUrl + "]";
	}

}
