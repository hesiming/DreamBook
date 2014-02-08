package cn.retech.my_domainbean_engine.http_engine;

import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * Http Engine 接口, 通过这个接口将具体的Http Engine分离
 * 
 * @author skyduck
 * 
 */
public interface IHttpEngine {
	/**
	 * 创建一个http executor
	 * 
	 * @param url
	 * @param netRequestDomainBean
	 *          具体接口的网络请求业务Bean
	 * @param headers
	 *          http头
	 * @param body
	 *          http数据体(method为POST时有效)
	 * @param method
	 *          http方法
	 * @param httpRespondListener
	 *          http响应监听
	 * @return
	 */
	public ExecutorService createHttpExecutor(final String url, final Object netRequestDomainBean, final Map<String, String> headers, final Map<String, String> body, final String method, final IHttpRespondSyncListener httpRespondListener);
}
