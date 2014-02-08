package cn.retech.domainbean_model.get_book_download_url;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import cn.retech.my_domainbean_engine.domainbean_tools.IParseNetRespondStringToDomainBean;

import android.text.TextUtils;

public final class GetBookDownloadUrlParseNetRespondStringToDomainBean implements IParseNetRespondStringToDomainBean {

	@Override
	public Object parseNetRespondStringToDomainBean(String netRespondString) throws Exception {

		if (TextUtils.isEmpty(netRespondString)) {
			throw new IllegalArgumentException("netRespondString is empty ! ");
		}

		InputStream inputStreamOfRespondData = new ByteArrayInputStream(netRespondString.getBytes("UTF-8"));

		XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
		XmlPullParser parser = parserCreator.newPullParser();
		parser.setInput(inputStreamOfRespondData, null);

		// 书籍URL是否有效
		boolean validate = false;
		// 书籍下载URL
		String url = "";

		int parserEvent = parser.getEventType();
		while (parserEvent != XmlPullParser.END_DOCUMENT) {

			switch (parserEvent) {
			case XmlPullParser.START_TAG:// 开始元素事件
				if (parser.getName().equalsIgnoreCase(GetBookDownloadUrlDatabaseFieldsConstant.RespondBean.validate.name())) {
					validate = Boolean.parseBoolean(parser.nextText());
				} else if (parser.getName().equalsIgnoreCase(GetBookDownloadUrlDatabaseFieldsConstant.RespondBean.url.name())) {
					url = parser.nextText();
				}
				break;

			case XmlPullParser.END_TAG:// 结束元素事件
				break;
			}

			parserEvent = parser.next();
		}
		inputStreamOfRespondData.close();

		if (!validate || TextUtils.isEmpty(url)) {
			throw new IllegalStateException("网络返回的书籍URL为空! ");
		}
		return new GetBookDownloadUrlNetRespondBean(url);
	}
}
