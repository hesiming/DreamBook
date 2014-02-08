package cn.retech.domainbean_model.login;

import cn.retech.global_data_cache.UrlConstantForThisProject;
import cn.retech.my_domainbean_engine.domainbean_tools.IDomainBeanAbstractFactory;
import cn.retech.my_domainbean_engine.domainbean_tools.IParseDomainBeanToDataDictionary;
import cn.retech.my_domainbean_engine.domainbean_tools.IParseNetRespondStringToDomainBean;

/**
 * 用户登录
 * 
 */
public final class LogonDomainBeanToolsFactory implements IDomainBeanAbstractFactory {
	@Override
	public IParseDomainBeanToDataDictionary getParseDomainBeanToDDStrategy() {
		return new LogonParseDomainBeanToDD();
	}

	@Override
	public IParseNetRespondStringToDomainBean getParseNetRespondStringToDomainBeanStrategy() {
		return new LogonParseNetRespondStringToDomainBean();
	}

	@Override
	public String getSpecialPath() {
		return UrlConstantForThisProject.kUrlConstant_SpecialPath_login;
	}
}
