package cn.retech.my_domainbean_engine.net_entitydata_tools.interfaces;

import java.util.Map;

import org.apache.http.HttpEntity;

/**
 * 将数据字典集合, 打包成网络请求字符串, (可以在这里完成数据的加密工作)
 * 
 * @author skyduck
 */
public interface INetRequestEntityDataPackage {
  public HttpEntity packageNetRequestEntityData(final Map<String, String> domainDD);
}
