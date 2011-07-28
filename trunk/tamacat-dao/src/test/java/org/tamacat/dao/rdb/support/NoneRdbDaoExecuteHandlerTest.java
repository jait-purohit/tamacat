package org.tamacat.dao.rdb.support;

import org.junit.Before;
import org.junit.Test;
import org.tamacat.dao.rdb.RdbDaoEvent;

public class NoneRdbDaoExecuteHandlerTest {

	NoneRdbDaoExecuteHandler handler;
	
	@Before
	public void setUp() throws Exception {
		handler = new NoneRdbDaoExecuteHandler();
	}

	@Test
	public void testHandleAfterExecuteQuery() {
		handler.handleAfterExecuteQuery(null);
	}

	@Test
	public void testHandleAfterExecuteUpdate() {
		handler.handleAfterExecuteUpdate(null);
		handler.handleAfterExecuteUpdate(new RdbDaoEvent() {
			
			@Override
			public void setResult(int result) {				
			}
			@Override
			public void setQuery(String query) {				
			}
			@Override
			public int getResult() {
				return 0;
			}
			@Override
			public String getQuery() {
				return null;
			}
			@Override
			public Class<?> getCallerDao() {
				return null;
			}
		});
	}

	@Test
	public void testHandleBeforeExecuteQuery() {
		handler.handleBeforeExecuteQuery(null);
	}

	@Test
	public void testHandleBeforeExecuteUpdate() {
		handler.handleBeforeExecuteUpdate(null);
	}
}
