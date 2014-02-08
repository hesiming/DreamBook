package cn.retech.toolutils;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;
import android.util.TypedValue;
import cn.retech.global_data_cache.GlobalDataCacheForMemorySingleton;
import cn.retech.global_data_cache.GlobalDataCacheForNeedSaveToFileSystem;

/**
 * 这里只放置, 在当前项目中会被用到的方法
 * 
 * @author zhihua.tang
 */
@SuppressLint("SimpleDateFormat")
public final class ToolsFunctionForThisProgect {

  private ToolsFunctionForThisProgect() {

  }

  public static synchronized void stopServiceWithThisApp() {
    // Intent intent = new Intent(MyApplication.getApplication(),
    // PreLoadedDataService.class);
    // MyApplication.getApplication().stopService(intent);
  }

  public static synchronized void quitApp(final Activity activity) {
    activity.finish();

    // 停止和当前App相关的所有服务
    ToolsFunctionForThisProgect.stopServiceWithThisApp();

    // 在这里保存数据
    GlobalDataCacheForNeedSaveToFileSystem.writeAllCacheData();

    // 杀死当前app进程
    int nPid = android.os.Process.myPid();
    android.os.Process.killProcess(nPid);
  }

  // 获取当前设备的UA信息
  public static synchronized String getUserAgent() {
    String version = "5022435";
    // String platFormHardware = Build.MODEL;
    String platFormHardware = "Android";
    String platFormOSversion = "Android" + Build.VERSION.RELEASE;
    String userAgent = "DreamBook_" + version + "_" + platFormHardware + "_" + platFormOSversion;
    return userAgent;
  }

  /**
   * 获取本机IP
   * 
   */
  public static String getDeviceIP() {
    String ipaddress = "";
    try {
      for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
        NetworkInterface intf = en.nextElement();
        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
          InetAddress inetAddress = enumIpAddr.nextElement();
          if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
            ipaddress = inetAddress.getHostAddress().toString();
            break;
          }
        }

        if (!TextUtils.isEmpty(ipaddress)) {
          break;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ipaddress;
  }

  /**
   * byte(字节)根据长度转成kb(千字节)和mb(兆字节)
   * 
   * @param bytes
   * @return
   */
  public static String bytesToKbOrMb(long bytes) {
    BigDecimal filesize = new BigDecimal(bytes);
    BigDecimal megabyte = new BigDecimal(1024 * 1024);
    float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP).floatValue();
    if (returnValue > 1)
      return (returnValue + "MB");
    BigDecimal kilobyte = new BigDecimal(1024);
    returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP).floatValue();
    return (returnValue + "KB");
  }

  /**
   * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
   */
  public static int dipToPx(float dpValue) {
    final float scale = GlobalDataCacheForMemorySingleton.getInstance.getApplication().getResources().getDisplayMetrics().density;
    return (int) (dpValue * scale + 0.5f);
  }

  /**
   * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
   */
  public static int pxToDip(float pxValue) {
    final float scale = GlobalDataCacheForMemorySingleton.getInstance.getApplication().getResources().getDisplayMetrics().density;
    return (int) (pxValue / scale + 0.5f);
  }

  /**
   * 获取当前分辨率下指定单位对应的像素大小（根据设备信息） px,dip,sp -> px
   * 
   * Paint.setTextSize()单位为px
   * 
   * 代码摘自：TextView.setTextSize()
   * 
   * @param unit
   *          TypedValue.COMPLEX_UNIT_*
   * @param size
   * @return
   */
  public static float getRawSize(int unit, float size) {
    Context c = GlobalDataCacheForMemorySingleton.getInstance.getApplication();
    Resources r;

    if (c == null)
      r = Resources.getSystem();
    else
      r = c.getResources();

    return TypedValue.applyDimension(unit, size, r.getDisplayMetrics());
  }

}
