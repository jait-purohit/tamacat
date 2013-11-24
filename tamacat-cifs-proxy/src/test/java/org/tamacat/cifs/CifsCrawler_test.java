package org.tamacat.cifs;

public class CifsCrawler_test {
	
	public static void main(String[] args) throws Exception {
		String url ="smb://192.168.10.160/public/pdf/";
		CifsCrawler crawler = new CifsCrawler("./src/test/resources/index/");
		crawler.setUsername("guest");
		crawler.setPassword("guest");
		crawler.setBaseUrl("http://192.168.10.160/public/pdf/");

		crawler.crawler(url);
		String[] paths = crawler.getCifsFileManager().getAllPaths();
		for (String path : paths){
			System.out.println(path);
			CifsFile[] files = crawler.getCifsFiles(path);
			for (CifsFile f : files) {
				System.out.println(" - " + f.getName() + "\t" + f.getLength() + "\t" + f.getLastModified());
			}
		}
		crawler.close();
	}

}
