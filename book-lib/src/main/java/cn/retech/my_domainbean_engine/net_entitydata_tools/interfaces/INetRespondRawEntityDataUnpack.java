package cn.retech.my_domainbean_engine.net_entitydata_tools.interfaces;


/**
 * 将网络返回的数据, 解压成可识别的字符串(在这里完成数据的解密)
 * 
 * @author skyduck
 * 
 */
public interface INetRespondRawEntityDataUnpack {
  public String unpackNetRespondRawEntityData(final byte[] rawData);
}
