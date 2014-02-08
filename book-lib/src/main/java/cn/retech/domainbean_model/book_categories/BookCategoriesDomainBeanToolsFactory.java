package cn.retech.domainbean_model.book_categories;

import cn.retech.global_data_cache.UrlConstantForThisProject;
import cn.retech.my_domainbean_engine.domainbean_tools.IDomainBeanAbstractFactory;
import cn.retech.my_domainbean_engine.domainbean_tools.IParseDomainBeanToDataDictionary;
import cn.retech.my_domainbean_engine.domainbean_tools.IParseNetRespondStringToDomainBean;

/**
 * 获取图书分类
 * 
 */
public final class BookCategoriesDomainBeanToolsFactory implements IDomainBeanAbstractFactory {
	@Override
	public IParseDomainBeanToDataDictionary getParseDomainBeanToDDStrategy() {
		return null;
	}

	@Override
	public IParseNetRespondStringToDomainBean getParseNetRespondStringToDomainBeanStrategy() {
		return new BookCategoriesParseNetRespondStringToDomainBean();
	}

	@Override
	public String getSpecialPath() {
		return UrlConstantForThisProject.kUrlConstant_SpecialPath_book_categories;
	}
}
