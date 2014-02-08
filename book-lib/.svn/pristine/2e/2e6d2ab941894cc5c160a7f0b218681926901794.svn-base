package cn.retech.domainbean_model.book_categories;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import cn.retech.my_domainbean_engine.domainbean_tools.IParseNetRespondStringToDomainBean;

import android.text.TextUtils;

public final class BookCategoriesParseNetRespondStringToDomainBean implements IParseNetRespondStringToDomainBean {

	@Override
	public Object parseNetRespondStringToDomainBean(String netRespondString) throws Exception {

		if (TextUtils.isEmpty(netRespondString)) {
			throw new IllegalArgumentException("netRespondString is empty ! ");
		}

		InputStream inputStreamOfRespondData = new ByteArrayInputStream(netRespondString.getBytes("UTF-8"));

		XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
		XmlPullParser parser = parserCreator.newPullParser();
		parser.setInput(inputStreamOfRespondData, null);

		List<BookCategory> categories = new ArrayList<BookCategory>();
		String id = "";
		String name = "";
		int bookcount = 0;

		int parserEvent = parser.getEventType();
		while (parserEvent != XmlPullParser.END_DOCUMENT) {

			switch (parserEvent) {
			case XmlPullParser.START_TAG:// 开始元素事件
				if (parser.getName().equalsIgnoreCase(BookCategoriesDatabaseFieldsConstant.RespondBean.id.name())) {
					id = parser.nextText();
				} else if (parser.getName().equalsIgnoreCase(BookCategoriesDatabaseFieldsConstant.RespondBean.name.name())) {
					name = parser.nextText();
				} else if (parser.getName().equalsIgnoreCase(BookCategoriesDatabaseFieldsConstant.RespondBean.bookcount.name())) {
					bookcount = Integer.valueOf(parser.nextText());
				}
				break;

			case XmlPullParser.END_TAG:// 结束元素事件
				if (parser.getName().equalsIgnoreCase(BookCategoriesDatabaseFieldsConstant.RespondBean.category.name())) {
					categories.add(new BookCategory(id, name, bookcount));
				}
				break;
			}

			parserEvent = parser.next();
		}
		inputStreamOfRespondData.close();

		return new BookCategoriesNetRespondBean(categories);
	}
}
