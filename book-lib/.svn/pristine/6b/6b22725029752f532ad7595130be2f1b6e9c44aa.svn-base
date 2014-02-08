package cn.retech.domainbean_model.booklist_in_bookstores;

import cn.retech.global_data_cache.UrlConstantForThisProject;
import cn.retech.my_domainbean_engine.domainbean_tools.IDomainBeanAbstractFactory;
import cn.retech.my_domainbean_engine.domainbean_tools.IParseDomainBeanToDataDictionary;
import cn.retech.my_domainbean_engine.domainbean_tools.IParseNetRespondStringToDomainBean;

/**
 * 获取图书列表
 * 
 */
public final class BookListInBookstoresDomainBeanToolsFactory implements IDomainBeanAbstractFactory {
	@Override
	public IParseDomainBeanToDataDictionary getParseDomainBeanToDDStrategy() {
		return new BookListInBookstoresParseDomainBeanToDD();
	}

	@Override
	public IParseNetRespondStringToDomainBean getParseNetRespondStringToDomainBeanStrategy() {
		return new BookListInBookstoresParseNetRespondStringToDomainBean();
	}

	@Override
	public String getSpecialPath() {
		return UrlConstantForThisProject.kUrlConstant_SpecialPath_book_list_in_bookstores;
	}
}
