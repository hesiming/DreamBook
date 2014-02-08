package cn.retech.image_loader;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import cn.retech.global_data_cache.GlobalDataCacheForMemorySingleton;
import cn.retech.my_domainbean_engine.http_engine.concrete.HttpClientForSupportSSL;

import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

/**
 * 
 * @author skyduck
 *
 */
public class HttpClientImageDownloaderForSSL extends BaseImageDownloader {

	public HttpClientImageDownloaderForSSL(Context context) {
		super(context);
	}

	@Override
	protected InputStream getStreamFromNetwork(String imageUri, Object extra) throws IOException {
		DefaultHttpClient client = HttpClientForSupportSSL.getDefaultHttpClient();
		if (GlobalDataCacheForMemorySingleton.getInstance.getCookieStore() != null) {
			client.setCookieStore(GlobalDataCacheForMemorySingleton.getInstance.getCookieStore());
		}
		HttpGet httpRequest = new HttpGet(imageUri);
		HttpResponse response = client.execute(httpRequest);
		HttpEntity entity = response.getEntity();
		BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
		return bufHttpEntity.getContent();
	}
}