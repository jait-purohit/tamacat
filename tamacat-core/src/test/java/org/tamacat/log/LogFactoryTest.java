/*
 * Copyright 2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tamacat.log;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.tamacat.log.impl.Log4jDiagnosticContext;
import org.tamacat.log.impl.Log4jLogger;
import org.tamacat.log.impl.NoneDiagnosticContext;

public class LogFactoryTest {

	Log logger;

	@Before
	public void setUp() throws Exception {
		logger = LogFactory.getLog(LogFactoryTest.class);
	}

	@Test
	public void testDebugString() {
		logger.debug("test");
	}

	@Test
	public void testDebugStringStringArray() {
		logger.debug("arg0=${0}, arg1=${1}", "one", "two");
	}

	@Test
	public void testIsEnabled() {
		assertTrue(logger.isFatalEnabled());
		assertTrue(logger.isErrorEnabled());
		assertTrue(logger.isWarnEnabled());
		assertTrue(logger.isInfoEnabled());
		assertTrue(logger.isDebugEnabled());
		assertTrue(logger.isTraceEnabled());
	}

	@Test
	public void testGetLog() {
		Log l1 = LogFactory.getLog("test");
		Log l2 = LogFactory.getLog("test");
		assertSame(l1, l2);
	}

	@Test
	public void testLoadLogger() {
		assertTrue(LogFactory.SELF.loadLogger("test") instanceof Log4jLogger);

		Log l1 = LogFactory.SELF.loadLogger("test");
		Log l2 = LogFactory.SELF.loadLogger("test");
		assertNotSame(l1, l2);
	}


	@Test
	public void testGetDiagnosticContext() {
		assertTrue(LogFactory.getDiagnosticContext(new Log4jLogger("test")) instanceof Log4jDiagnosticContext);
		assertTrue(LogFactory.getDiagnosticContext(null) instanceof NoneDiagnosticContext);
	}
}
