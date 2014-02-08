package cn.retech.domainbean_model.booklist_in_bookstores;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.text.TextUtils;
import cn.retech.my_domainbean_engine.domainbean_tools.IParseNetRespondStringToDomainBean;

public final class BookListInBookstoresParseNetRespondStringToDomainBean implements IParseNetRespondStringToDomainBean {

	@Override
	public Object parseNetRespondStringToDomainBean(String netRespondString) throws Exception {

		if (TextUtils.isEmpty(netRespondString)) {
			throw new IllegalArgumentException("netRespondString is empty ! ");
		}

		InputStream inputStreamOfRespondData = new ByteArrayInputStream(netRespondString.getBytes("UTF-8"));

		XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
		XmlPullParser parser = parserCreator.newPullParser();
		parser.setInput(inputStreamOfRespondData, null);

		List<BookInfo> categories = new ArrayList<BookInfo>();
		// 一本书籍的唯一性 标识ID
		String content_id = "";
		// 书籍名称
		String name = "";
		// 书籍发行日期
		String published = "";
		// 书籍过期时间
		String expired = "";
		// 作者
		String author = "";
		// 价钱
		String price = "";
		// 书籍对应的产品ID, 用于收费书籍的购买行为
		String productid = "";
		// 书籍归属的类别ID
		String categoryid = "";
		// 出版社/发行人
		String publisher = "";
		// 书籍封面图片URL地址
		String thumbnail = "";
		// 书籍描述
		String description = "";
		// 书籍zip资源包大小, 以byte为单位.
		String size = "";

		int parserEvent = parser.getEventType();
		while (parserEvent != XmlPullParser.END_DOCUMENT) {

			switch (parserEvent) {
			case XmlPullParser.START_TAG:// 开始元素事件
				if (parser.getName().equalsIgnoreCase(BookListInBookstoresDatabaseFieldsConstant.RespondBean.content_id.name())) {
					content_id = parser.nextText();
				} else if (parser.getName().equalsIgnoreCase(BookListInBookstoresDatabaseFieldsConstant.RespondBean.name.name())) {
					name = parser.nextText();
				} else if (parser.getName().equalsIgnoreCase(BookListInBookstoresDatabaseFieldsConstant.RespondBean.published.name())) {
					published = parser.nextText();
				} else if (parser.getName().equalsIgnoreCase(BookListInBookstoresDatabaseFieldsConstant.RespondBean.expired.name())) {
					expired = parser.nextText();
				} else if (parser.getName().equalsIgnoreCase(BookListInBookstoresDatabaseFieldsConstant.RespondBean.author.name())) {
					author = parser.nextText();
				} else if (parser.getName().equalsIgnoreCase(BookListInBookstoresDatabaseFieldsConstant.RespondBean.price.name())) {
					price = parser.nextText();
				} else if (parser.getName().equalsIgnoreCase(BookListInBookstoresDatabaseFieldsConstant.RespondBean.productid.name())) {
					productid = parser.nextText();
				} else if (parser.getName().equalsIgnoreCase(BookListInBookstoresDatabaseFieldsConstant.RespondBean.categoryid.name())) {
					categoryid = parser.nextText();
				} else if (parser.getName().equalsIgnoreCase(BookListInBookstoresDatabaseFieldsConstant.RespondBean.publisher.name())) {
					publisher = parser.nextText();
				} else if (parser.getName().equalsIgnoreCase(BookListInBookstoresDatabaseFieldsConstant.RespondBean.thumbnail.name())) {
					thumbnail = parser.nextText();
				} else if (parser.getName().equalsIgnoreCase(BookListInBookstoresDatabaseFieldsConstant.RespondBean.description.name())) {
					description = parser.nextText();
				} else if (parser.getName().equalsIgnoreCase(BookListInBookstoresDatabaseFieldsConstant.RespondBean.size.name())) {
					size = parser.nextText();
				}
				break;

			case XmlPullParser.END_TAG:// 结束元素事件
				if (parser.getName().equalsIgnoreCase(BookListInBookstoresDatabaseFieldsConstant.RespondBean.book.name())) {
					categories.add(new BookInfo(content_id, name, published, expired, author, price, productid, categoryid, publisher, thumbnail, description, size));
				}
				break;
			}

			parserEvent = parser.next();
		}
		inputStreamOfRespondData.close();

		return new BookListInBookstoresNetRespondBean(categories);
	}
}
