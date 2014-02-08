package cn.retech.domainbean_model;

import cn.retech.domainbean_model.book_categories.BookCategoriesDomainBeanToolsFactory;
import cn.retech.domainbean_model.book_categories.BookCategoriesNetRequestBean;
import cn.retech.domainbean_model.book_search.BookSearchDomainBeanToolsFactory;
import cn.retech.domainbean_model.book_search.BookSearchNetRequestBean;
import cn.retech.domainbean_model.booklist_in_bookstores.BookListInBookstoresDomainBeanToolsFactory;
import cn.retech.domainbean_model.booklist_in_bookstores.BookListInBookstoresNetRequestBean;
import cn.retech.domainbean_model.get_book_download_url.GetBookDownloadUrlDomainBeanToolsFactory;
import cn.retech.domainbean_model.get_book_download_url.GetBookDownloadUrlNetRequestBean;
import cn.retech.domainbean_model.login.LogonDomainBeanToolsFactory;
import cn.retech.domainbean_model.login.LogonNetRequestBean;
import cn.retech.my_domainbean_engine.domainbean_strategy_mapping.StrategyClassNameMappingBase;

/**
 * 所有业务接口, 要在这里完成NetRequestBean和DomainBeanToolsFactory的映射
 * 
 * @author skyduck
 * 
 */
public final class DomainBeanHelperClassNameMapping extends StrategyClassNameMappingBase {

	// 注意：
	// Key : 网络请求业务Bean
	// Value : 是该网络接口对应的抽象工厂类
	public DomainBeanHelperClassNameMapping() {

		/**
		 * 用户登录
		 */
		strategyClassesNameMappingList.put(LogonNetRequestBean.class.getName(), LogonDomainBeanToolsFactory.class.getName());

		/**
		 * 书籍分类列表
		 */
		strategyClassesNameMappingList.put(BookCategoriesNetRequestBean.class.getName(), BookCategoriesDomainBeanToolsFactory.class.getName());

		/**
		 * 书籍列表
		 */
		strategyClassesNameMappingList.put(BookListInBookstoresNetRequestBean.class.getName(), BookListInBookstoresDomainBeanToolsFactory.class.getName());

		/**
		 * 获取书籍下载URL
		 */
		strategyClassesNameMappingList.put(GetBookDownloadUrlNetRequestBean.class.getName(), GetBookDownloadUrlDomainBeanToolsFactory.class.getName());

		/**
		 * 书籍查询
		 */
		strategyClassesNameMappingList.put(BookSearchNetRequestBean.class.getName(), BookSearchDomainBeanToolsFactory.class.getName());
	}
}
