package cn.retech.my_domainbean_engine.net_entitydata_tools;

import cn.retech.my_domainbean_engine.net_entitydata_tools.interfaces.INetRequestEntityDataPackage;
import cn.retech.my_domainbean_engine.net_entitydata_tools.interfaces.INetRespondRawEntityDataUnpack;
import cn.retech.my_domainbean_engine.net_entitydata_tools.interfaces.IServerRespondDataTest;

/**
 * 网络访问过程中, 请求和返回的 "实体数据" 相关的工具类
 * 
 * @author skyduck
 */
public interface INetEntityDataTools {
  // 将数据字典集合, 打包成网络请求字符串, (可以在这里完成数据的加密工作)
  public INetRequestEntityDataPackage getNetRequestEntityDataPackage();

  // 将网络返回的数据, 解压成可识别的字符串(在这里完成数据的解密)
  public INetRespondRawEntityDataUnpack getNetRespondEntityDataUnpack();

  // 测试从服务器端返回的数据是否是有效的(数据要先解包, 然后再根据错误码做判断)
  public IServerRespondDataTest getServerRespondDataTest();
}
