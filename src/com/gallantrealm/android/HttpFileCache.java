package com.gallantrealm.android;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.content.Context;

/**
 * Provides caching of files obtained via Https.  Files are kept in the app internal file cache.
 */
public final class HttpFileCache {

	public static File getFile(String urlString, Context context) {
		String urlFileName = urlString.replace("/", "_").replace(":","_").replace("%","_");
		File file = new File(context.getCacheDir(), urlFileName);
		if (!file.exists()) {
			try {
				HttpURLConnection connection = (HttpURLConnection)(new URL(urlString)).openConnection();
				InputStream inputStream = connection.getInputStream();
				OutputStream outputStream = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int len = 0;
				while (len >= 0) {
					len = inputStream.read(buffer);
					if (len > 0) {
						outputStream.write(buffer, 0, len);
					}
				}
				outputStream.close();
				inputStream.close();
			} catch (Exception e) {
				System.err.println(e);
				return null;
			}
		} else {
			System.out.println("HttpFileCache: "+urlString+" retrieved from file cache.");
			// need to check for stale files
		}
		return file;
	}

}
