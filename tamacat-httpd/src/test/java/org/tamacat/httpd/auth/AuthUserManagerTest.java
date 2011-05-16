package org.tamacat.httpd.auth;

import static org.junit.Assert.*;

import org.junit.Test;

public class AuthUserManagerTest {

	@Test
	public void testGet() {
		User user = new User();
		user.setAuthUsername("testuser");
		user.setAuthPassword("password");
		new AuthUserManager().set(user);
		
		AuthUser get = new AuthUserManager().get();
		assertSame(get, user);
		assertEquals(get.getAuthUsername(), "testuser");
		assertEquals(get.getAuthPassword(), "password");
	}

	
	static class User implements AuthUser {
		private String username;
		private String password;
		
		@Override
		public String getAuthUsername() {
			return username;
		}

		@Override
		public String getAuthPassword() {
			return password;
		}

		@Override
		public void setAuthUsername(String username) {
			this.username = username;
		}

		@Override
		public void setAuthPassword(String password) {
			this.password = password;
		}

		@Override
		public boolean isEncrypted() {
			return false;
		}
	}
}
