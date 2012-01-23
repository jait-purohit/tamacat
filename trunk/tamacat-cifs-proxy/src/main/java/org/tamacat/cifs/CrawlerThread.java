package org.tamacat.cifs;

import java.util.Properties;

import org.tamacat.util.PropertyUtils;

public class CrawlerThread implements Runnable {

	CifsCrawler crawler;
	private String url;
	CrawlerThread() {
		Properties props = PropertyUtils.getProperties("crawler.properties");
		String indexDir = props.getProperty("indexDir");
		crawler = new CifsCrawler(indexDir);
		String domain = props.getProperty("domain");
		String username = props.getProperty("username");
		String password = props.getProperty("password");
		crawler.setDomain(domain);
		crawler.setUsername(username);
		crawler.setPassword(password);
		url = props.getProperty("url");
	}
	
	@Override
	public void run() {
		try {
			crawler.crawler(url);
		} finally {
			crawler.close();
		}
	}

}
