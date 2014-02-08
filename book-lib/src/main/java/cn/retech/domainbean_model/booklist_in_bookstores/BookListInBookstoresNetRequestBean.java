package cn.retech.domainbean_model.booklist_in_bookstores;

public final class BookListInBookstoresNetRequestBean {
	// 书籍分类ID, 如果不设置此ID, 就是一次性获取全部的书籍
	private String category_id;

	public String getCategory_id() {
		return category_id;
	}

	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}

	@Override
	public String toString() {
		return "BookListInBookstoresNetRequestBean [category_id=" + category_id + "]";
	}

}
