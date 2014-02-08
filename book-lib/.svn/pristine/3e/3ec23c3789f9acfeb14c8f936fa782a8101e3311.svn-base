package cn.retech.my_domainbean_engine.net_entitydata_tools.dream_book;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import cn.retech.my_domainbean_engine.net_entitydata_tools.interfaces.IServerRespondDataTest;
import cn.retech.my_domainbean_engine.net_error_handle.NetErrorBean;
import cn.retech.my_domainbean_engine.net_error_handle.NetErrorCodeEnum;

/**
 * 
 * @author skyduck
 *
 */
public final class ServerRespondDataTestDreamBook implements IServerRespondDataTest {

  @Override
  public NetErrorBean testServerRespondDataError(final String netUnpackedData) {

    // equalsIgnoreCase : 将此 String 与另一个 String
    // 进行比较，不考虑大小写。如果两个字符串的长度相等，并且两个字符串中的相应字符都相等（忽略大小写），则认为这两个字符串是相等的。
    boolean isValidate = true;
    String errorMessage = "OK";
    InputStream inputStreamOfRespondData = null;
    try {
      inputStreamOfRespondData = new ByteArrayInputStream(netUnpackedData.getBytes("UTF-8"));
      XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
      XmlPullParser parser = parserCreator.newPullParser();
      parser.setInput(inputStreamOfRespondData, null);
      int parserEvent = parser.getEventType();
      String tag = "";
      while (parserEvent != XmlPullParser.END_DOCUMENT) {

        switch (parserEvent) {
          case XmlPullParser.START_TAG:// 开始元素事件
            tag = parser.getName();
            break;
          case XmlPullParser.TEXT:// 结束元素事件
            if (tag.equalsIgnoreCase("validate")) {
              isValidate = Boolean.parseBoolean(parser.getText());
            } else if (tag.equalsIgnoreCase("error")) {
              errorMessage = parser.getText();
            }
            break;

          case XmlPullParser.END_TAG:// 结束元素事件
            tag = "";
            break;
        }

        parserEvent = parser.next();
      }
    } catch (Exception e) {
      isValidate = false;
    } finally {
      if (inputStreamOfRespondData != null) {
        try {
          inputStreamOfRespondData.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    if (isValidate) {
      // 一切OK 
      return new NetErrorBean();
    } else {
      NetErrorBean netError = new NetErrorBean(NetErrorCodeEnum.kNetErrorCodeEnum_Server_Custom_Error.getValue(), errorMessage);
      return netError;
    }

  }

}
