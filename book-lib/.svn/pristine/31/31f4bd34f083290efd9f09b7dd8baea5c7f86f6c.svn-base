package cn.retech.my_domainbean_engine.http_engine;

import java.util.concurrent.ExecutorService;

import cn.retech.my_domainbean_engine.net_error_handle.NetErrorBean;

/**
 * Htpp 请求响应监听(同步回调)
 * 
 * @author skyduck
 * 
 */
public interface IHttpRespondSyncListener {
	/**
	 * 请求完成
	 * 
	 * @param executor
	 * @param responseData
	 */
	public void onCompletion(final ExecutorService executor, final byte[] responseData);

	/**
	 * 请求失败
	 * 
	 * @param executor
	 * @param error
	 */
	public void onError(final ExecutorService executor, final NetErrorBean error);
}
