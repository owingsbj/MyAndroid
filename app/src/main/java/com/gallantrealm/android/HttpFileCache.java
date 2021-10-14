package com.gallantrealm.android;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.content.Context;

/**
 * Provides caching of files obtained via Https. Files are kept in the app internal file cache.
 */
public final class HttpFileCache {

	public static File getFile(String urlString, Context context) {
		boolean fileCached = false;
		String urlFileName = urlString.replace("/", "_").replace(":", "_").replace("%", "_");
		File file = new File(context.getCacheDir(), urlFileName);
		try {
			HttpURLConnection connection = (HttpURLConnection) (new URL(urlString)).openConnection();
			if (file.exists()) {
				fileCached = true;
				long date = file.lastModified();
				connection.setIfModifiedSince(date);
			}
			int response = ((HttpURLConnection) connection).getResponseCode();
			if (response == HttpURLConnection.HTTP_NOT_FOUND) {
				System.out.println(urlString+" is not found");
				if (fileCached) {
					System.out.println("deleting cached version");
					file.delete();
				}
				return null;
			}
			if (response == HttpURLConnection.HTTP_UNAVAILABLE) {
				System.out.println(urlString+" is not available");
				if (fileCached) {
					return file;
				} else {
					return null;
				}
			}
			if (response == HttpURLConnection.HTTP_NOT_MODIFIED) {
				System.out.println(urlString+" has not modified.  Using cached version.");
				return file;
			}
			System.out.println("Downloading and caching "+urlString);
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
		return file;
	}

}
