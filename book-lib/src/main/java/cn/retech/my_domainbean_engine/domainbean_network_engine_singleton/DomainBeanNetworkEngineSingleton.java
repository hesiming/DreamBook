package cn.retech.my_domainbean_engine.domainbean_network_engine_singleton;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import cn.retech.domainbean_model.login.LogonNetRespondBean;
import cn.retech.global_data_cache.GlobalDataCacheForMemorySingleton;
import cn.retech.global_data_cache.UrlConstantForThisProject;
import cn.retech.my_domainbean_engine.domainbean_tools.DomainBeanAbstractFactoryCacheSingleton;
import cn.retech.my_domainbean_engine.domainbean_tools.IDomainBeanAbstractFactory;
import cn.retech.my_domainbean_engine.domainbean_tools.IParseDomainBeanToDataDictionary;
import cn.retech.my_domainbean_engine.http_engine.HttpEngineFactoryMethodSingleton;
import cn.retech.my_domainbean_engine.http_engine.IHttpEngine;
import cn.retech.my_domainbean_engine.http_engine.IHttpRespondSyncListener;
import cn.retech.my_domainbean_engine.http_engine.concrete.HttpClientForSupportSSL;
import cn.retech.my_domainbean_engine.net_entitydata_tools.NetEntityDataToolsFactoryMethodSingleton;
import cn.retech.my_domainbean_engine.net_error_handle.NetErrorBean;
import cn.retech.my_domainbean_engine.net_error_handle.NetErrorCodeEnum;
import cn.retech.toolutils.DebugLog;
import cn.retech.toolutils.ToolsFunctionForThisProgect;

/**
 * 封装整个业务协议网络访问相关的子系统的外观层
 * 
 * @author skyduck
 * 
 */
public enum DomainBeanNetworkEngineSingleton {
  getInstance;

  private final String TAG = this.getClass().getSimpleName();

  /**
   * 网络请求结果监听
   * 
   * @author skyduck
   * 
   */
  public static interface OnNetRequestResultListener {
    // 网络请求激活成功(如果发起一个网络请求失败的话, 是不会执行这个回调的)
    public void onNetRequestAttached();

    // 网络请求完成(成功或者失败都算完成)
    public void onNetRequestCompleted();
  }

  /**
   * 用于标识某次网络请求的状态(控制层定义一个 NetRequestIndex 用于标识某次网络请求,
   * 控制层对于这个NetRequestIndex对象是只读使用的, 也就是判断 NetRequestIndex 的 getIndex() 属性是否是
   * IDLE_NETWORK_REQUEST_ID来判断某次网络请求是否正处于进行中.
   * 
   * @author skyduck
   * 
   */
  public static class NetRequestIndex {
    /**
     * 发起一个网络请求失败的标识
     */
    private static final int IDLE_NETWORK_REQUEST_ID = -2012;
    private int index = IDLE_NETWORK_REQUEST_ID;

    /**
     * 复位
     */
    private void reset() {
      index = IDLE_NETWORK_REQUEST_ID;
    }

    /**
     * 当前网络接口是否空闲, 如果成功发起一次网络请求, 那么这个属性就是false, 直到这次网络请求完成后, 它才会被设置成true
     * 
     * @return
     */
    public boolean idle() {
      return (index == IDLE_NETWORK_REQUEST_ID) ? true : false;
    }
  };

  /**
   * 网络请求索引计数器, 不断的自动累加.
   */
  private static final AtomicInteger nextSerialNum = new AtomicInteger();

  /**
   * 当前正在并发执行的 "网络请求操作对象" 缓存集合, 缓存这些网络请求的目的是可以取消它们
   */
  private Map<String, ExecutorService> synchronousNetRequestBuf = new HashMap<String, ExecutorService>();

  private Handler handler = new Handler(Looper.getMainLooper());

  /**
   * 取消一个业务层的网络请求
   * 
   * @param netRequestIndexToOut
   */
  public void cancelNetRequestByRequestIndex(final NetRequestIndex netRequestIndexToOut) {
    do {
      if (netRequestIndexToOut == null) {
        assert false : "入参 netRequestIndex 为空！";
        break;
      }

      if (netRequestIndexToOut.idle()) {
        break;
      }

      final ExecutorService executor = synchronousNetRequestBuf.get(Integer.toString(netRequestIndexToOut.index));
      if (executor != null) {
        executor.shutdown();
      }

      netRequestIndexToOut.reset();
    } while (false);

  }

  /**
   * 发起一个业务层的网络请求
   * 
   * @param netRequestIndexToOut
   *          控制层用于标识某次网络请求是否还在进行中的标志位
   * @param netRequestDomainBean
   *          网络请求业务Bean
   * @param asyncNetRespondListener
   *          异步网络响应监听(通过这个监听返回的代码已经处于UI线程了)
   */
  public void requestDomainProtocol(final NetRequestIndex netRequestIndexToOut, final Object netRequestDomainBean, final IDomainBeanAsyncNetRespondListener asyncNetRespondListener) {

    final int netRequestIndex = nextSerialNum.getAndIncrement();

    DebugLog.i(TAG, " ");
    DebugLog.i(TAG, " ");
    DebugLog.i(TAG, " ");
    DebugLog.i(TAG, "<<<<<<<<<<     Request a domain protocol begin (" + netRequestIndex + ")     >>>>>>>>>>");
    DebugLog.i(TAG, " ");

    try {
      // effective for java 38 检查参数有效性, 对于共有的方法,
      // 要使用异常机制来通知调用方发生了入参错误.
      if (netRequestIndexToOut == null || netRequestDomainBean == null) {
        throw new NullPointerException("入参为空.");
      }

      if (!netRequestIndexToOut.idle()) {
        throw new IllegalArgumentException("入参netRequestIndexToOut不是idle状态, 证明之前发起的网络请求还未结束!");
      }

      /**
       * 将 "网络请求业务Bean" 的 完整class name 作为和这个业务Bean对应的"业务接口"
       * 绑定的所有相关的处理算法的唯一识别Key
       */
      final String abstractFactoryMappingKey = netRequestDomainBean.getClass().getName();
      DebugLog.i(TAG, "abstractFactoryMappingKey--> " + abstractFactoryMappingKey);

      // 这里的设计使用了 "抽象工厂" 设计模式
      final IDomainBeanAbstractFactory domainBeanAbstractFactoryObject = DomainBeanAbstractFactoryCacheSingleton.getInstance.getDomainBeanAbstractFactoryObjectByKey(abstractFactoryMappingKey);
      if (domainBeanAbstractFactoryObject == null || !(domainBeanAbstractFactoryObject instanceof IDomainBeanAbstractFactory)) {
        throw new NullPointerException("必须实现 IDomainBeanAbstractFactory 接口");
      }

      // 获取当前业务网络接口, 对应的URL
      final String url = UrlConstantForThisProject.kUrlConstant_MainUrl + "/" + UrlConstantForThisProject.kUrlConstant_MainPtah + "/" + domainBeanAbstractFactoryObject.getSpecialPath();
      DebugLog.i(TAG, "url-->" + url);

      // HTTP 请求方法类型, 默认是GET
      String httpRequestMethod = "GET";

      /**
       * 处理HTTP 请求实体数据, 如果有实体数据的话, 就设置 RequestMethod 为 "POST" 目前POST 和
       * GET的认定标准是, 有附加参数就使用POST, 没有就使用GET(这里要跟后台开发团队事先约定好)
       */
      final IParseDomainBeanToDataDictionary parseDomainBeanToDataDictionary = domainBeanAbstractFactoryObject.getParseDomainBeanToDDStrategy();
      final Map<String, String> fullDataDictionary = new HashMap<String, String>();
      do {
        if (null == parseDomainBeanToDataDictionary) {
          // 没有额外的数据需要上传服务器
          break;
        }

        /**
         * 首先获取目标 "网络请求业务Bean" 对应的 "业务协议参数字典 domainParams"
         * (字典由K和V组成,K是"终端应用与后台通信接口协议.doc" 文档中的业务协议关键字, V就是具体的值.)
         */
        final Map<String, String> domainDD = parseDomainBeanToDataDictionary.parseDomainBeanToDataDictionary(netRequestDomainBean);
        if (null == domainDD || domainDD.size() <= 0) {
          // 没有额外的数据需要上传服务器
          break;
        }
        DebugLog.i(TAG, "domainParams-->" + domainDD.toString());

        fullDataDictionary.putAll(domainDD);

        // 最终确认确实需要使用POST方式发送数据
        httpRequestMethod = "POST";
      } while (false);
      DebugLog.i(TAG, "httpRequestMethod-->" + httpRequestMethod);

      // //////////////////////////////////////////////////////////////////////////////
      // 设置 公用的http header
      final Map<String, String> httpHeaders = new HashMap<String, String>();
      httpHeaders.put("User-Agent", ToolsFunctionForThisProgect.getUserAgent());

      // //////////////////////////////////////////////////////////////////////////////

      final IHttpEngine httpEngine = HttpEngineFactoryMethodSingleton.getInstance.getHttpEngine();
      if (httpEngine == null || !(httpEngine instanceof IHttpEngine)) {
        throw new NullPointerException("必须实现 IHttpEngine 接口");
      }

      final ExecutorService executor = httpEngine.createHttpExecutor(url, netRequestDomainBean, httpHeaders, fullDataDictionary, httpRequestMethod, new IHttpRespondSyncListener() {

        @Override
        public void onCompletion(final ExecutorService executor, final byte[] responseData) {

          final NetErrorBean error = new NetErrorBean();

          do {

            // ------------------------------------- >>>
            if (executor.isShutdown()) {
              // 本次网络请求被取消了
              break;
            }
            // ------------------------------------- >>>

            // ------------------------------------- >>>
            // 将具体网络引擎层返回的 "原始未加工数据byte[]" 解包成 "可识别数据字符串(一般是utf-8)".
            // 这里要考虑网络传回的原生数据有加密的情况, 比如MD5加密的数据, 那么在这里先解密成可识别的字符串
            final String netUnpackedData = NetEntityDataToolsFactoryMethodSingleton.getInstance.getNetRespondEntityDataUnpack().unpackNetRespondRawEntityData(responseData);
            if (TextUtils.isEmpty(netUnpackedData)) {
              DebugLog.e(TAG, "解析服务器端返回的实体数据失败.");
              error.setErrorCode(NetErrorCodeEnum.kNetErrorCodeEnum_Server_UnpackedResponseDataFailed.getValue());
              error.setErrorMessage("解析服务器端返回的实体数据失败.");
              break;
            }
            // ------------------------------------- >>>

            // ------------------------------------- >>>
            // 检查服务器返回的数据是否有效, 如果无效, 要获取服务器返回的错误码和错误描述信息
            // (比如说某次网络请求成功了, 但是服务器那边没有有效的数据给客户端,
            // 所以服务器会返回错误码和描述信息告知客户端访问结果)
            error.reinitialize(NetEntityDataToolsFactoryMethodSingleton.getInstance.getServerRespondDataTest().testServerRespondDataError(netUnpackedData));
            if (error.getErrorCode() != NetErrorCodeEnum.kNetErrorCodeEnum_Success.getValue()) {
              // 如果服务器没有有效的数据到客户端, 那么就不需要创建 "网络响应业务Bean"
              DebugLog.e(TAG, "服务器端告知客户端, 本次网络访问未获取到有效数据(具体情况, 可以查看服务器端返回的错误代码和错误信息)");
              DebugLog.e(TAG, error.toString());
              break;
            }
            // ------------------------------------- >>>

            // ------------------------------------- >>>
            // 将 "已经解包的可识别数据字符串" 解析成 "具体的业务响应数据Bean"
            // note : 将服务器返回的数据字符串(已经解密, 解码完成了), 解析成对应的 "网络响应业务Bean"
            try {
              final Object respondDomainBean = domainBeanAbstractFactoryObject.getParseNetRespondStringToDomainBeanStrategy().parseNetRespondStringToDomainBean(netUnpackedData);
              DebugLog.i(TAG, "netRespondDomainBean->" + respondDomainBean.toString());

              // 一切OK
              handler.post(new Runnable() {

                @Override
                public void run() {

                  // ------------------------------------- >>>
                  if (!executor.isShutdown()) {
                    // 本次网络请求结束, 设置NetRequestIndex为IDLE状态.
                    netRequestIndexToOut.reset();

                    if (asyncNetRespondListener != null) {
                      asyncNetRespondListener.onSuccess(respondDomainBean);
                    }
                  }
                  // ------------------------------------- >>>

                  // 清理缓存数据
                  synchronousNetRequestBuf.remove(Integer.toString(netRequestIndex));

                }
              });

              return;
            } catch (Exception e) {
              DebugLog.e(TAG, "创建 网络响应业务Bean失败, 出现这种情况的业务Bean是 --> " + abstractFactoryMappingKey);
              error.setErrorCode(NetErrorCodeEnum.kNetErrorCodeEnum_Server_ParseNetRespondStringToDomainBeanFailed.getValue());
              error.setErrorMessage("将网络返回的数据字符串解析成业务Bean失败.");
            }
            // ------------------------------------- >>>
          } while (false);

          // 出现了错误
          handler.post(new Runnable() {

            @Override
            public void run() {

              // ------------------------------------- >>>
              if (!executor.isShutdown()) {
                // 本次网络请求结束, 设置NetRequestIndex为IDLE状态.
                netRequestIndexToOut.reset();

                if (asyncNetRespondListener != null) {
                  asyncNetRespondListener.onFailure(error);
                }
              }
              // ------------------------------------- >>>

              // 清理缓存数据
              synchronousNetRequestBuf.remove(Integer.toString(netRequestIndex));

            }
          });
        }

        @Override
        public void onError(final ExecutorService executor, final NetErrorBean error) {

          handler.post(new Runnable() {

            @Override
            public void run() {

              // ------------------------------------- >>>
              if (!executor.isShutdown()) {
                netRequestIndexToOut.reset();

                if (asyncNetRespondListener != null) {
                  asyncNetRespondListener.onFailure(error);
                }
              }
              // ------------------------------------- >>>

              // 清理缓存数据
              synchronousNetRequestBuf.remove(Integer.toString(netRequestIndex));

            }
          });

        }
      });
      /**
       * 将这个 "网络请求操作对象" 缓存到并发网络请求集合synchronousNetRequestEventBuf中
       */
      synchronousNetRequestBuf.put(Integer.toString(netRequestIndex), executor);

      // 本次网络请求发起成功
      netRequestIndexToOut.index = netRequestIndex;

      DebugLog.i(TAG, "成功发起网络请求-->" + abstractFactoryMappingKey);
    } catch (Exception e) {
      DebugLog.e(TAG, "发起网络请求失败, 错误原因-->" + e.getLocalizedMessage());
    } finally {

    }

    //
    DebugLog.i(TAG, " ");
    DebugLog.i(TAG, "         ----- Request a domain protocol end (" + netRequestIndex + ") -----          ");
    DebugLog.i(TAG, " ");
    DebugLog.i(TAG, " ");
    DebugLog.i(TAG, " ");
  }

  /**
   * 请求一个文件下载
   * 
   * @param netRequestIndexToOut
   *          标记本次文件下载是否进行中的索引
   * @param url
   *          文件下载url
   * @param fileFullSavePath
   *          文件完整的保存路径(包括文件名)
   * @param isNeedContinuingly
   *          是否需要断点续传
   * @param fileAsyncHttpResponseHandler
   *          文件异步http响应处理
   */
  public void requestFileDownlaod(final NetRequestIndex netRequestIndexToOut, final String url, final HttpEntity httpEntity, final String fileFullSavePath, final boolean isNeedContinuingly,
      final IFileAsyncHttpResponseHandler fileAsyncHttpResponseHandler) {

    final int netRequestIndex = nextSerialNum.getAndIncrement();

    DebugLog.i(TAG, " ");
    DebugLog.i(TAG, " ");
    DebugLog.i(TAG, " ");
    DebugLog.i(TAG, "<<<<<<<<<<     download a file begin (" + netRequestIndex + ")     >>>>>>>>>>");
    DebugLog.i(TAG, " ");

    try {
      // effective for java 38 检查参数有效性, 对于共有的方法,
      // 要使用异常机制来通知调用方发生了入参错误.
      if (netRequestIndexToOut == null || TextUtils.isEmpty(url) || TextUtils.isEmpty(fileFullSavePath)) {
        throw new NullPointerException("入参为空.");
      }

      if (!netRequestIndexToOut.idle()) {
        throw new IllegalArgumentException("入参netRequestIndexToOut不是idle状态, 证明之前发起的网络请求还未结束!");
      }

      final ExecutorService executor = Executors.newSingleThreadExecutor();
      executor.execute(new Runnable() {

        @Override
        public void run() {
          File file = null;
          InputStream httpInputStream = null;
          RandomAccessFile randomAccessFile = null;

          final NetErrorBean error = new NetErrorBean();

          try {
            DefaultHttpClient client = HttpClientForSupportSSL.getDefaultHttpClient();
            if (GlobalDataCacheForMemorySingleton.getInstance.getCookieStore() != null) {
              client.setCookieStore(GlobalDataCacheForMemorySingleton.getInstance.getCookieStore());
            }

            HttpUriRequest request = null;
            if (httpEntity == null) {
              HttpGet httpGet = new HttpGet(url);
              request = httpGet;
            } else {
              HttpPost httpPost = new HttpPost(url);
              request = httpPost;
              httpPost.setEntity(httpEntity);
            }

            long bytesWritten = 0;// 当前进度

            file = new File(fileFullSavePath);
            randomAccessFile = new RandomAccessFile(file, "rwd");
            if (isNeedContinuingly) {
              // 需要断点续传
              if (file.exists()) {
                request.setHeader("Range", "bytes=" + file.length() + "-");
                bytesWritten = file.length();
                randomAccessFile.seek(bytesWritten);
              }
            } else {
              // 不需要断点续传时, 要删除之前的临时文件, 好重头进行下载
              randomAccessFile.seek(0);
            }

            request.setHeader("User-Agent", ToolsFunctionForThisProgect.getUserAgent());

            // 执行 http 请求
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() != 200 && response.getStatusLine().getStatusCode() != 206) {
              request.abort();

              error.setErrorCode(response.getStatusLine().getStatusCode());
              error.setErrorMessage(response.getStatusLine().getReasonPhrase());

              // 通知外部, http下载过程中发生了错误
              if (fileAsyncHttpResponseHandler != null) {
                handler.post(new Runnable() {

                  @Override
                  public void run() {
                    fileAsyncHttpResponseHandler.onFailure(error);
                  }
                });

              }
            } else {

              httpInputStream = response.getEntity().getContent();
              final long totalSize = response.getEntity().getContentLength() + bytesWritten;
              byte[] buffer = new byte[1024];
              int byteCount = -1;
              while ((byteCount = httpInputStream.read(buffer)) != -1) {

                randomAccessFile.write(buffer, 0, byteCount);

                bytesWritten += byteCount;

                // ----------------------------------------------------------------------------
                if (executor.isShutdown()) {
                  // 本次下载被取消, 一定是优先检查暂停状态, 然后才是通知外层下载进度,
                  // 否则会形成, 在暂停状态发生后, 还会发生一次
                  // "下载进度通知事件."
                  break;
                }
                // ----------------------------------------------------------------------------

                // 通知外部, 下载进度
                if (fileAsyncHttpResponseHandler != null) {
                  final long tempBytesWritten = bytesWritten;// 当前进度
                  handler.post(new Runnable() {

                    @Override
                    public void run() {
                      fileAsyncHttpResponseHandler.onProgress(tempBytesWritten, totalSize);
                    }
                  });

                }
              }

              if (!executor.isShutdown()) {
                if (fileAsyncHttpResponseHandler != null) {
                  final File tempFile = file;
                  handler.post(new Runnable() {

                    @Override
                    public void run() {
                      fileAsyncHttpResponseHandler.onSuccess(tempFile);
                    }
                  });

                }
              }
            }
          } catch (Exception e) {
            error.setErrorCode(NetErrorCodeEnum.kNetErrorCodeEnum_Client_Error.getValue());
            error.setErrorMessage(e.getLocalizedMessage());

            if (fileAsyncHttpResponseHandler != null) {
              handler.post(new Runnable() {

                @Override
                public void run() {
                  fileAsyncHttpResponseHandler.onFailure(error);
                }
              });
            }

          } finally {
            if (httpInputStream != null) {
              try {
                httpInputStream.close();
              } catch (IOException e) {
                DebugLog.e(TAG, e.getLocalizedMessage());
              }
              httpInputStream = null;
            }
            if (randomAccessFile != null) {
              try {
                randomAccessFile.close();
              } catch (IOException e) {
                DebugLog.e(TAG, e.getLocalizedMessage());
              }
              randomAccessFile = null;
            }

            synchronousNetRequestBuf.remove(Integer.toString(netRequestIndex));
          }

        }
      });
      /**
       * 将这个 "网络请求操作对象" 缓存到集合synchronousNetRequestEventBuf中
       */
      synchronousNetRequestBuf.put(Integer.toString(netRequestIndex), executor);

      netRequestIndexToOut.index = netRequestIndex;
      DebugLog.i(TAG, "成功发起一个文件下载的请求-->" + url);
    } catch (Exception e) {
      DebugLog.e(TAG, "发起文件下载请求失败, 错误原因-->" + e.getLocalizedMessage());
    } finally {

    }

    //
    DebugLog.i(TAG, " ");
    DebugLog.i(TAG, "         ----- download a file end (" + netRequestIndex + ") -----          ");
    DebugLog.i(TAG, " ");
    DebugLog.i(TAG, " ");
    DebugLog.i(TAG, " ");
  }

  /**
   * 下载一本书籍, 为今日书院项目特殊准备的
   * 
   * @param netRequestIndexToOut
   * @param url
   * @param bindAccount
   * @param fileFullSavePath
   * @param fileAsyncHttpResponseHandler
   */
  public void requestBookDownlaod(final NetRequestIndex netRequestIndexToOut, final String url, final LogonNetRespondBean bindAccount, final String fileFullSavePath,
      final IFileAsyncHttpResponseHandler fileAsyncHttpResponseHandler) {
    if (netRequestIndexToOut == null || TextUtils.isEmpty(url) || TextUtils.isEmpty(fileFullSavePath) || fileAsyncHttpResponseHandler == null || bindAccount == null) {
      DebugLog.e(TAG, "入参为空.");
      return;
    }

    // 构造 POST HttpEntity
    Map<String, String> body = new HashMap<String, String>();
    body.put("user_id", bindAccount.getUsername());
    body.put("user_password", bindAccount.getPassword());
    HttpEntity httpEntity = NetEntityDataToolsFactoryMethodSingleton.getInstance.getNetRequestEntityDataPackage().packageNetRequestEntityData(body);
    requestFileDownlaod(netRequestIndexToOut, url, httpEntity, fileFullSavePath, true, fileAsyncHttpResponseHandler);
  }
}
