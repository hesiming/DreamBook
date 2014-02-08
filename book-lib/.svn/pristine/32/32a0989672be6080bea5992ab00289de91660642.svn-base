package cn.retech.domainbean_model.get_book_download_url;

import java.util.HashMap;
import java.util.Map;

import cn.retech.my_domainbean_engine.domainbean_tools.IParseDomainBeanToDataDictionary;

import android.text.TextUtils;

public final class GetBookDownloadUrlParseDomainBeanToDD implements IParseDomainBeanToDataDictionary {

	@Override
	public Map<String, String> parseDomainBeanToDataDictionary(Object netRequestDomainBean) {

		if (null == netRequestDomainBean) {
			throw new IllegalArgumentException("netRequestDomainBean is null!");
		}

		boolean isRightObjectType = netRequestDomainBean instanceof GetBookDownloadUrlNetRequestBean;
		if (!isRightObjectType) {
			throw new IllegalArgumentException("传入的业务Bean的类型不符 !");
		}

		GetBookDownloadUrlNetRequestBean requestBean = (GetBookDownloadUrlNetRequestBean) netRequestDomainBean;
		if (TextUtils.isEmpty(requestBean.getContentId()) || requestBean.getBindAccount() == null || TextUtils.isEmpty(requestBean.getBindAccount().getUsername()) || TextUtils.isEmpty(requestBean.getBindAccount().getPassword())) {
			throw new IllegalArgumentException("必须的数据字段不完整 ! ");
		}

		Map<String, String> params = new HashMap<String, String>();
		// 必须参数
		params.put(GetBookDownloadUrlDatabaseFieldsConstant.RequestBean.contentId.name(), requestBean.getContentId());
		params.put(GetBookDownloadUrlDatabaseFieldsConstant.RequestBean.user_id.name(), requestBean.getBindAccount().getUsername());
		params.put(GetBookDownloadUrlDatabaseFieldsConstant.RequestBean.user_password.name(), requestBean.getBindAccount().getPassword());

		return params;
	}
}
