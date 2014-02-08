package cn.retech.my_domainbean_engine.net_error_handle;

import java.io.Serializable;

import org.apache.http.HttpStatus;

/**
 * 网络访问过程中出现错误时的数据Bean
 * 
 * @author skyduck
 */
public final class NetErrorBean implements Serializable, Cloneable {

  /**
	 * 
	 */
  private static final long serialVersionUID = 4841567150604927632L;

  // 错误代码
  private int errorCode = HttpStatus.SC_OK;
  // 错误描述信息
  private String errorMessage = "OK";

  public NetErrorBean() {
  }

  public NetErrorBean(int errorCode, String errorMessage) {
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
  }

  /**
   * 重新初始化
   * 
   * @param srcObject
   */
  public void reinitialize(final NetErrorBean srcObject) {
    if (srcObject != null) {
      this.errorCode = srcObject.errorCode;
      this.errorMessage = srcObject.errorMessage;
    } else {
      this.errorCode = HttpStatus.SC_OK;
      this.errorMessage = "OK";
    }
  }

  public int getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(int errorCode) {
    this.errorCode = errorCode;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public NetErrorBean clone() {
    NetErrorBean o = null;
    try {
      o = (NetErrorBean) super.clone();// Object 中的clone()识别出你要复制的是哪一个对象。
      o.errorCode = this.errorCode;
      o.errorMessage = this.errorMessage;
    } catch (CloneNotSupportedException e) {
      System.out.println(e.toString());
    }
    return o;
  }

  @Override
  public String toString() {
    return "NetErrorBean [errorCode=" + errorCode + ", errorMessage=" + errorMessage + "]";
  }

}
