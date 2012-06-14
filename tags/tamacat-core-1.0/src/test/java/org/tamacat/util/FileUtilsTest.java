package org.tamacat.util;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class FileUtilsTest {

	@Test
	public void testGetRelativePathToURLStringString() throws Exception {
		assertNotNull(new File(
			FileUtils.getRelativePathToURL(
				"/usr/local/tamacat/bin/./..", "htdocs/"
			).toURI())
		);
	}
	
	@Test
	public void testGetRelativePathToURL() {
		System.setProperty("user.dir", "/usr/local/tamacat");
		String name = "htdocs/";
		assertEquals("file:/usr/local/tamacat/htdocs/", 
			FileUtils.getRelativePathToURL(name).toString());
	}
	

	@Test
	public void testWriteInputStreamString() throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream("TEST".getBytes());
		String path = "./src/test/resources/test.txt";
		FileUtils.write(in, path);
		File file = new File(path);
		file.deleteOnExit();
	}
	
	@Test
	public void testWriteInputStreamFile() throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream("TEST".getBytes());
		File file = new File("./src/test/resources/test.txt");
		FileUtils.write(in, file);
		file.deleteOnExit();
	}
	
	@Test
	public void testNormalizeFileName() {
		assertEquals("/test/test.txt", FileUtils.normalizeFileName("../test/../test.txt"));
		assertEquals("/test.txt", FileUtils.normalizeFileName("/test.txt\r"));
		assertEquals("/test.txt", FileUtils.normalizeFileName("/test.txt\n"));
		assertEquals("/test.txt", FileUtils.normalizeFileName("/test.txt\r\n"));
	}
}
