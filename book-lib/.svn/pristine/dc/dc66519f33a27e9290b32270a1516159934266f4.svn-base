package cn.retech.my_domainbean_engine.http_engine.concrete;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import cn.retech.domainbean_model.get_book_download_url.GetBookDownloadUrlNetRequestBean;
import cn.retech.global_data_cache.GlobalDataCacheForMemorySingleton;
import cn.retech.my_domainbean_engine.http_engine.IHttpEngine;
import cn.retech.my_domainbean_engine.http_engine.IHttpRespondSyncListener;
import cn.retech.my_domainbean_engine.net_entitydata_tools.NetEntityDataToolsFactoryMethodSingleton;
import cn.retech.my_domainbean_engine.net_error_handle.NetErrorBean;
import cn.retech.my_domainbean_engine.net_error_handle.NetErrorCodeEnum;
import cn.retech.toolutils.DebugLog;
import cn.retech.toolutils.StreamTools;

/**
 * DreamBook 项目中使用的具体Http引擎 (DreamBook项目要考虑SSL)
 * 
 * @author skyduck
 * 
 */
public class HttpEngineForHttpClient implements IHttpEngine {
  private final String TAG = this.getClass().getSimpleName();

  @Override
  public ExecutorService createHttpExecutor(final String url, final Object netRequestDomainBean, final Map<String, String> headers, final Map<String, String> body, final String method,
      final IHttpRespondSyncListener httpRespondListener) {

    final ExecutorService executor = Executors.newSingleThreadExecutor();
    executor.execute(new Runnable() {

      @Override
      public void run() {

        byte[] responseData = null;
        NetErrorBean error = new NetErrorBean();

        try {

          DefaultHttpClient client = HttpClientForSupportSSL.getDefaultHttpClient(); // new
                                                                                     // DefaultHttpClient()
          if (GlobalDataCacheForMemorySingleton.getInstance.getCookieStore() != null) {
            client.setCookieStore(GlobalDataCacheForMemorySingleton.getInstance.getCookieStore());
          }
          HttpUriRequest request = null;
          if (method.equals("GET")) {
            HttpGet httpGet = new HttpGet(url);
            request = httpGet;
          } else {
            String urlForSpecial = url;
            if (netRequestDomainBean instanceof GetBookDownloadUrlNetRequestBean) {
              urlForSpecial = url + ((GetBookDownloadUrlNetRequestBean) netRequestDomainBean).getContentId();
            }
            HttpPost httpPost = new HttpPost(urlForSpecial);
            request = httpPost;

            // 构造 POST HttpEntity
            HttpEntity httpEntity = NetEntityDataToolsFactoryMethodSingleton.getInstance.getNetRequestEntityDataPackage().packageNetRequestEntityData(body);
            httpPost.setEntity(httpEntity);
          }

          // 设置 http 头
          Set<Entry<String, String>> entrySetOfHeaders = headers.entrySet();
          for (Entry<String, String> entry : entrySetOfHeaders) {
            request.setHeader(entry.getKey(), entry.getValue());
          }
          // 执行 http 请求
          HttpResponse response = client.execute(request);
          if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            request.abort();

            error.setErrorCode(response.getStatusLine().getStatusCode());
            error.setErrorMessage(response.getStatusLine().getReasonPhrase());
          } else {

            // 保存 cookie
            GlobalDataCacheForMemorySingleton.getInstance.setCookieStore(client.getCookieStore());

            // 读取网络侧返回的数据
            responseData = StreamTools.readInputStream(response.getEntity().getContent());

            if (responseData == null) {
              // 从服务器端获得的实体数据为空(EntityData), 这种情况有可能是正常的, 比如 退出登录 接口,
              // 服务器就只是通知客户端访问成功, 而不发送任何实体数据.
              error.setErrorCode(NetErrorCodeEnum.kNetErrorCodeEnum_Server_NoResponseData.getValue());
              error.setErrorMessage("从服务器端获得的实体数据为空(EntityData)!");
            }
          }

        } catch (Exception e) {
          DebugLog.e(TAG, "调用HttpEngine发起Http请求出现了异常-->" + e.toString());
          error.setErrorCode(NetErrorCodeEnum.kNetErrorCodeEnum_Server_Error.getValue());
          error.setErrorMessage(e.getLocalizedMessage());
        } finally {

          // 通知上层
          if (error.getErrorCode() == NetErrorCodeEnum.kNetErrorCodeEnum_Success.getValue()) {
            httpRespondListener.onCompletion(executor, responseData);
          } else {
            httpRespondListener.onError(executor, error);
          }
        }

      }

    });

    return executor;
  }
}
