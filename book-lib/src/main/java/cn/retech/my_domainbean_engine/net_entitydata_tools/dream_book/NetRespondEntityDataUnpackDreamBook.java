package cn.retech.my_domainbean_engine.net_entitydata_tools.dream_book;

import java.io.UnsupportedEncodingException;

import cn.retech.my_domainbean_engine.net_entitydata_tools.interfaces.INetRespondRawEntityDataUnpack;
import cn.retech.toolutils.DebugLog;

/**
 * 
 * @author skyduck
 *
 */
public final class NetRespondEntityDataUnpackDreamBook implements INetRespondRawEntityDataUnpack {
  private final String TAG = this.getClass().getSimpleName();

  @Override
  public String unpackNetRespondRawEntityData(final byte[] rawData) {
    String netUnpackedData = null;
    try {
      netUnpackedData = new String(rawData, "utf-8");
    } catch (UnsupportedEncodingException e) {
      DebugLog.e(TAG, "解包 NetRespondRawEntityData 失败!");
    }

    return netUnpackedData;
  }

}
