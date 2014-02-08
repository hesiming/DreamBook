package cn.retech.domainbean_model.booklist_in_bookstores;

import java.util.HashMap;
import java.util.Map;

import cn.retech.my_domainbean_engine.domainbean_tools.IParseDomainBeanToDataDictionary;

import android.text.TextUtils;

public final class BookListInBookstoresParseDomainBeanToDD implements IParseDomainBeanToDataDictionary {

	@Override
	public Map<String, String> parseDomainBeanToDataDictionary(Object netRequestDomainBean) {

		if (null == netRequestDomainBean) {
			throw new IllegalArgumentException("netRequestDomainBean is null!");
		}

		boolean isRightObjectType = netRequestDomainBean instanceof BookListInBookstoresNetRequestBean;
		if (!isRightObjectType) {
			throw new IllegalArgumentException("传入的业务Bean的类型不符 !");
		}

		BookListInBookstoresNetRequestBean requestBean = (BookListInBookstoresNetRequestBean) netRequestDomainBean;

		Map<String, String> params = new HashMap<String, String>();
		if (!TextUtils.isEmpty(requestBean.getCategory_id())) {
			params.put(BookListInBookstoresDatabaseFieldsConstant.RequestBean.category_id.name(), requestBean.getCategory_id());
		}

		return params;
	}
}
