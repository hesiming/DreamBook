package cn.retech.my_domainbean_engine.domainbean_network_engine_singleton;

import java.io.File;

import cn.retech.my_domainbean_engine.net_error_handle.NetErrorBean;

/**
 * 文件下载异步响应处理监听
 * 
 * @author skyduck
 * 
 */
public interface IFileAsyncHttpResponseHandler {
	/**
	 * 文件下载完成
	 * 
	 * @param file
	 */
	public void onSuccess(final File file);

	/**
	 * 文件下载失败
	 * 
	 * @param error
	 */
	public void onFailure(final NetErrorBean error);

	/**
	 * 文件下载进度
	 * 
	 * @param bytesWritten
	 *          已完成的数据长度
	 * @param totalSize
	 *          要下载的数据总长度
	 */
	public void onProgress(final long bytesWritten, final long totalSize);
}
