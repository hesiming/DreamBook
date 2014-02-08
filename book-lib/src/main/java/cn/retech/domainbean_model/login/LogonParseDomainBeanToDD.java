package cn.retech.domainbean_model.login;

import java.util.HashMap;
import java.util.Map;

import cn.retech.my_domainbean_engine.domainbean_tools.IParseDomainBeanToDataDictionary;

import android.text.TextUtils;

public final class LogonParseDomainBeanToDD implements IParseDomainBeanToDataDictionary {

	@Override
	public Map<String, String> parseDomainBeanToDataDictionary(Object netRequestDomainBean) {

		if (null == netRequestDomainBean) {
			throw new IllegalArgumentException("netRequestDomainBean is null!");
		}

		boolean isRightObjectType = netRequestDomainBean instanceof LogonNetRequestBean;
		if (!isRightObjectType) {
			throw new IllegalArgumentException("传入的业务Bean的类型不符 !");
		}

		LogonNetRequestBean requestBean = (LogonNetRequestBean) netRequestDomainBean;
		String username = requestBean.getUsername();
		String password = requestBean.getPassword();
		if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
			throw new IllegalArgumentException("必须的数据字段不完整 ! ");
		}

		Map<String, String> params = new HashMap<String, String>();
		// 必须参数
		params.put(LogonDatabaseFieldsConstant.RequestBean.user_id.name(), username);
		params.put(LogonDatabaseFieldsConstant.RequestBean.user_password.name(), password);

		return params;
	}
}
