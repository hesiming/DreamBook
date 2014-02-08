package cn.retech.domainbean_model.book_search;

import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;
import cn.retech.my_domainbean_engine.domainbean_tools.IParseDomainBeanToDataDictionary;

public class BookSearchInfoParseDomainBeanToDD implements IParseDomainBeanToDataDictionary {

	@Override
	public Map<String, String> parseDomainBeanToDataDictionary(Object netRequestDomainBean) {

		if (null == netRequestDomainBean) {
			throw new IllegalArgumentException("netRequestDomainBean is null!");
		}

		boolean isRightObjectType = netRequestDomainBean instanceof BookSearchNetRequestBean;
		if (!isRightObjectType) {
			throw new IllegalArgumentException("传入的业务Bean的类型不符 !");
		}

		BookSearchNetRequestBean requestBean = (BookSearchNetRequestBean) netRequestDomainBean;

		Map<String, String> params = new HashMap<String, String>();
		if (!TextUtils.isEmpty(requestBean.getSearch())) {
			params.put(BookSearchDatabaseFieldsConstant.RequestBean.search.name(), requestBean.getSearch());
		}

		return params;
	}

}
