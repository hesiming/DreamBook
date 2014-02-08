package cn.retech.my_domainbean_engine.net_entitydata_tools.dream_book;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import cn.retech.my_domainbean_engine.net_entitydata_tools.interfaces.INetRequestEntityDataPackage;

/**
 * 
 * @author skyduck
 *
 */
public class NetRequestEntityDataPackageForDreamBook implements INetRequestEntityDataPackage {

  @Override
  public HttpEntity packageNetRequestEntityData(final Map<String, String> domainDD) {

    do {
      if (domainDD == null || domainDD.size() <= 0) {
        break;
      }

      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(domainDD.size());
      Set<Entry<String, String>> entrySetOfHeaders = domainDD.entrySet();
      for (Entry<String, String> entry : entrySetOfHeaders) {
        nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
      }

      try {
        HttpEntity httpEntity = new UrlEncodedFormEntity(nameValuePairs, "utf-8");
        return httpEntity;
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
    } while (false);

    return null;
  }
}
