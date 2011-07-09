package org.tamacat.io;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.util.IOUtils;

public class MessageDigestInputStreamTest {

	ByteArrayInputStream bi;

	@Before
	public void setUp() throws Exception {
		bi = new ByteArrayInputStream("TEST".getBytes());
	}

	@After
	public void tearDown() throws Exception {
		IOUtils.close(bi);
	}

	@Test
	public void testRead() throws IOException {
		MessageDigestInputStream in = new MessageDigestInputStream(bi);
		int result = in.read();
		assertEquals(84, result);
	}

	@Test
	public void testReadByteArrayIntInt() throws IOException {
		ByteArrayInputStream bi = new ByteArrayInputStream("TEST".getBytes());
		MessageDigestInputStream in = new MessageDigestInputStream(bi);
		byte[] buf = new byte[256];
		int off = 0;
		int len = "TEST".length();
		int result = in.read(buf, off, len);
		assertEquals(4, result);
	}

	@Test
	public void testSkip() throws IOException {
		ByteArrayInputStream bi = new ByteArrayInputStream("TEST".getBytes());
		MessageDigestInputStream in = new MessageDigestInputStream(bi);
		long result = in.skip(2);
		assertEquals(2, result);
		
		assertEquals(0, in.skip(-999));
	}

	@Test
	public void testMessageDigestInputStreamInputStream() {
		ByteArrayInputStream bi = new ByteArrayInputStream("TEST".getBytes());
		MessageDigestInputStream in = new MessageDigestInputStream(bi);
		try {
			assertEquals("d41d8cd98f00b204e9800998ecf8427e", in.getDigest());
		} finally {
			IOUtils.close(bi);
		}
	}

	@Test
	public void testMessageDigestInputStreamInputStreamString() {
		ByteArrayInputStream bi = new ByteArrayInputStream("TEST".getBytes());
		MessageDigestInputStream in = new MessageDigestInputStream(bi, "SHA");
		assertEquals("da39a3ee5e6b4b0d3255bfef95601890afd80709", in.getDigest());

		try {
			new MessageDigestInputStream(bi, "XXXX");
			fail();
		} catch (MessageDigestException e) {
			assertNotNull(e.getMessage());
		}
	}

	@Test
	public void testSetMessageDigestAndGetDigest() throws Exception {
		ByteArrayInputStream bi = new ByteArrayInputStream("TEST".getBytes());
		MessageDigestInputStream in = new MessageDigestInputStream(bi);
		MessageDigest digest = MessageDigest.getInstance("SHA");
		in.setMessageDigest(digest);
		assertEquals("da39a3ee5e6b4b0d3255bfef95601890afd80709", in.getDigest());
	}
}
