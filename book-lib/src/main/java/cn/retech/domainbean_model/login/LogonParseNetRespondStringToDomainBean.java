package cn.retech.domainbean_model.login;

import cn.retech.my_domainbean_engine.domainbean_tools.IParseNetRespondStringToDomainBean;

import android.text.TextUtils;

public final class LogonParseNetRespondStringToDomainBean implements IParseNetRespondStringToDomainBean {

	@Override
	public Object parseNetRespondStringToDomainBean(String netRespondString) throws Exception {

		if (TextUtils.isEmpty(netRespondString)) {
			throw new IllegalArgumentException("netRespondString is empty ! ");
		}

		return new LogonNetRespondBean();
	}
}
