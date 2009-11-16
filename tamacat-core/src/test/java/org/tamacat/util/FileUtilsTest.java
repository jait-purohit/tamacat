package org.tamacat.util;

import static org.junit.Assert.*;

import java.io.File;

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
}
