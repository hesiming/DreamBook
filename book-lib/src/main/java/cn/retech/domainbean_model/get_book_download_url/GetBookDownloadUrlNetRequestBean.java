package cn.retech.domainbean_model.get_book_download_url;

import java.util.Arrays;

import cn.retech.domainbean_model.login.LogonNetRespondBean;

public final class GetBookDownloadUrlNetRequestBean {
  // 书籍ID
  private final String contentId;
  // 跟要下载的书籍绑定的账号, 这里是服务器端做的安全策略, 要检测跟目标书籍绑定的账号是否有下载权限.
  private final LogonNetRespondBean bindAccount;
  //需要付费的书籍, 在付费后得到的收据
  private byte[] receipt;

  public byte[] getReceipt() {
    return receipt;
  }

  public void setReceipt(byte[] receipt) {
    this.receipt = receipt;
  }

  public GetBookDownloadUrlNetRequestBean(String contentId, LogonNetRespondBean bindAccount) {
    this.contentId = contentId;
    this.bindAccount = bindAccount;
  }

  public String getContentId() {
    return contentId;
  }

  public LogonNetRespondBean getBindAccount() {
    return bindAccount;
  }

  @Override
  public String toString() {
    return "GetBookDownloadUrlNetRequestBean [contentId=" + contentId + ", bindAccount=" + bindAccount + ", receipt=" + Arrays.toString(receipt) + "]";
  }

}
