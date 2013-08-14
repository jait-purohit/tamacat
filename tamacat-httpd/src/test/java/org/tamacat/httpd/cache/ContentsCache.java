package org.tamacat.httpd.cache;

import java.io.IOException;

import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.impl.client.cache.BasicHttpCacheStorage;
import org.apache.http.impl.client.cache.CacheConfig;

public class ContentsCache {

	BasicHttpCacheStorage storage;

	public ContentsCache() {
		CacheConfig cacheConfig = CacheConfig.custom()
			.setMaxCacheEntries(1000).setMaxObjectSize(8192).build();
		storage = new BasicHttpCacheStorage(cacheConfig);
	}

	public void put(String path, HttpCacheEntry entry) throws IOException {
		//HttpCacheEntry entry = new HttpCacheEntry(requestDate, responseDate, statusLine, responseHeaders, resource);
		storage.putEntry(path, entry);
	}
	public HttpCacheEntry get(String path) throws IOException {
		return storage.getEntry(path);
	}

}
