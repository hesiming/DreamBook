package cn.retech.my_domainbean_engine.domainbean_network_engine_singleton;

import cn.retech.my_domainbean_engine.net_error_handle.NetErrorBean;

/**
 * 网络业务接口, 异步响应监听
 * 
 * @author skyduck
 * 
 */
public interface IDomainBeanAsyncNetRespondListener {
  /**
   * 成功
   * 
   * @param respondDomainBean
   *          一个符合要求的业务Bean
   */
  public void onSuccess(final Object respondDomainBean);

  /**
   * 失败
   * 
   * @param error
   */
  public void onFailure(final NetErrorBean error);

  /**
   * 取消
   */
  //public void onCancel();
}
