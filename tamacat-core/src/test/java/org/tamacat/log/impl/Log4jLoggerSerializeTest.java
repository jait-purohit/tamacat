/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.log.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.TestCase;

import org.junit.Test;
import org.tamacat.log.impl.Log4jLogger;
import org.tamacat.util.IOUtils;

public class Log4jLoggerSerializeTest extends TestCase {
	
	@Test
	public void testSerialize() {
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		try {
			Log4jLogger logger = new Log4jLogger("TEST");
			
			//FileOutputStream os = new FileOutputStream("logger.ser");
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			out = new ObjectOutputStream(os);
			out.writeObject(logger);
			byte[] serialize = os.toByteArray();
			assertNotNull(serialize);
			//System.out.println(new String(serialize));
			logger.debug("Serialize OK. length=" + serialize.length);
			
			//FileInputStream is = new FileInputStream("logger.ser");
			ByteArrayInputStream is = new ByteArrayInputStream(serialize);
			in = new ObjectInputStream(is);
			Object obj = in.readObject();
			assertNotNull(obj);
			assertEquals(Log4jLogger.class, obj.getClass());
			
			logger = (Log4jLogger)obj;
			logger.debug("Deserialize OK.");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			IOUtils.close(in);
			IOUtils.close(out);
		}
	}
}
