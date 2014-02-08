package cn.retech.domainbean_model.book_categories;

public final class BookCategory {
	// 分类ID
	private final String identifier;
	// 分类名称
	private final String name;
	// 当前分类下面, 书籍总数(如果书籍总数为 0, 那么在书城界面, 就不要显示这个分类)
	private final int bookcount;

	public BookCategory(String identifier, String name, int bookcount) {
		this.identifier = identifier;
		this.name = name;
		this.bookcount = bookcount;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getName() {
		return name;
	}

	public int getBookcount() {
		return bookcount;
	}

	@Override
	public String toString() {
		return "BookCategory [identifier=" + identifier + ", name=" + name + ", bookcount=" + bookcount + "]";
	}

}
