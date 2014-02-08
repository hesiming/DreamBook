package cn.retech.domainbean_model.book_search;

import cn.retech.domainbean_model.booklist_in_bookstores.BookListInBookstoresParseNetRespondStringToDomainBean;

import cn.retech.global_data_cache.UrlConstantForThisProject;
import cn.retech.my_domainbean_engine.domainbean_tools.IDomainBeanAbstractFactory;
import cn.retech.my_domainbean_engine.domainbean_tools.IParseDomainBeanToDataDictionary;
import cn.retech.my_domainbean_engine.domainbean_tools.IParseNetRespondStringToDomainBean;

/**
 * 书籍搜索
 * 
 * @author skyduck
 * 
 */
public class BookSearchDomainBeanToolsFactory implements IDomainBeanAbstractFactory {

	@Override
	public IParseDomainBeanToDataDictionary getParseDomainBeanToDDStrategy() {
		return new BookSearchInfoParseDomainBeanToDD();
	}

	@Override
	public IParseNetRespondStringToDomainBean getParseNetRespondStringToDomainBeanStrategy() {
		return new BookListInBookstoresParseNetRespondStringToDomainBean();
	}

	@Override
	public String getSpecialPath() {
		return UrlConstantForThisProject.kUrlConstant_SpecialPath_book_query;
	}

}
