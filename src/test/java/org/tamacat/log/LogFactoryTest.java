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
}
