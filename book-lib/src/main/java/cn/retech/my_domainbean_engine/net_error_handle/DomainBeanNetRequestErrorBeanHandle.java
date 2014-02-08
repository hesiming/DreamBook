package cn.retech.my_domainbean_engine.net_error_handle;

/**
 * @author skyduck
 */
public final class DomainBeanNetRequestErrorBeanHandle {
  private DomainBeanNetRequestErrorBeanHandle() {

  }

  public static void handleNetRequestBean(final NetErrorBean netErrorBean) {
    if (netErrorBean == null) {
      return;
    }

    String errorMessage = "";

    netErrorBean.setErrorMessage(errorMessage);
  }
}