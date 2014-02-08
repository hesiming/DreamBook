package cn.retech.domainbean_model.book_categories;

public final class BookCategoriesDatabaseFieldsConstant {
	private BookCategoriesDatabaseFieldsConstant() {

	}

	public static enum RespondBean {
		//
		categories,
		//
		category,
		// 分类ID
		id,
		// 分类中文名称
		name,
		// 当前分类下面, 书籍总数(如果书籍总数为 0, 那么在书城界面, 就不要显示这个分类)
		bookcount
	}
}
