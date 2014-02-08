package cn.retech.domainbean_model.get_book_download_url;

import cn.retech.global_data_cache.UrlConstantForThisProject;
import cn.retech.my_domainbean_engine.domainbean_tools.IDomainBeanAbstractFactory;
import cn.retech.my_domainbean_engine.domainbean_tools.IParseDomainBeanToDataDictionary;
import cn.retech.my_domainbean_engine.domainbean_tools.IParseNetRespondStringToDomainBean;

/**
 * 获取书籍下载URL
 * 
 */
public final class GetBookDownloadUrlDomainBeanToolsFactory implements IDomainBeanAbstractFactory {
	@Override
	public IParseDomainBeanToDataDictionary getParseDomainBeanToDDStrategy() {
		return new GetBookDownloadUrlParseDomainBeanToDD();
	}

	@Override
	public IParseNetRespondStringToDomainBean getParseNetRespondStringToDomainBeanStrategy() {
		return new GetBookDownloadUrlParseNetRespondStringToDomainBean();
	}

	@Override
	public String getSpecialPath() {
		return UrlConstantForThisProject.kUrlConstant_SpecialPath_book_downlaod_url;
	}
}
