package cn.retech.domainbean_model.get_book_download_url;

public final class GetBookDownloadUrlDatabaseFieldsConstant {
	private GetBookDownloadUrlDatabaseFieldsConstant() {

	}

	public static enum RequestBean {
		// 要下载的书籍ID 必填
		contentId,
		// 跟要下载的书籍绑定的账号, 这里是服务器端做的安全策略, 要检测跟目标书籍绑定的账号是否有下载权限.
		user_id, user_password
	}

	public static enum RespondBean {
		//
		content,
		//
		validate,
		//
		url
	}
}
