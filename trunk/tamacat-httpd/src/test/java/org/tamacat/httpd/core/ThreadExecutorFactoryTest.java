/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import static org.junit.Assert.*;


import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.config.ServerConfig;

public class ThreadExecutorFactoryTest {

	ServerConfig config;
	
	@Before
	public void setUp() throws Exception {
		config = new ServerConfig();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetExecutorService() {
		ThreadExecutorFactory factory = new ThreadExecutorFactory(config);
		ExecutorService executor = factory.getExecutorService();
		assertNotNull(executor);
//		executor.execute(new Thread(){
//			public void run() {
//				//System.out.println("run");
//			}
//		});
		for (int i=0; i<10; i++) {
			Future<Long> future = executor.submit(new CallbackImpl());
			try {
				System.out.println("No." + i + "=" + future.get());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		executor.shutdown();
	}
	
	class CallbackImpl implements Callable<Long> {

		private long time;
		public CallbackImpl() {
			this.time = System.currentTimeMillis();
		}
		
		@Override
		public Long call() throws Exception {
			return System.currentTimeMillis() - time;
		}		
	}
}
