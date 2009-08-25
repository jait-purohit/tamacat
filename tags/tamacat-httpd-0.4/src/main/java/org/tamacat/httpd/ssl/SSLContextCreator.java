/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.ssl;

import java.io.IOException;

import java.net.URL;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.tamacat.httpd.config.ServerConfig;

/**
 * <p>The {@link SSLContext} create from {@link ServerConfig} or setter methods.
 */
public class SSLContextCreator {
	
	private String keyStoreFile;
	private char[] keyPassword;
	private KeyStoreType type = KeyStoreType.JKS;
	private SSLProtocol protocol = SSLProtocol.TLS;

	public void setKeyStoreFile(String keyStoreFile) {
		this.keyStoreFile = keyStoreFile;
	}

	public void setKeyPassword(String keyPassword) {
		this.keyPassword = keyPassword.toCharArray();
	}
	
	/**
	 * <p>Default constructor.
	 */
	public SSLContextCreator() {}
	
	/**
	 * <p>The constructor of setting values from {@code ServerConfig}.
	 */
	public SSLContextCreator(ServerConfig serverConfig) {
		setKeyStoreFile(serverConfig.getParam("https.keyStoreFile", ""));
		setKeyPassword(serverConfig.getParam("https.keyPassword", ""));
		setKeyStoreType(serverConfig.getParam("https.keyStoreType", "JKS"));
		setSSLProtocol(serverConfig.getParam("https.protocol", "TLS"));
	}

	public void setKeyStoreType(String type) {
		this.type = KeyStoreType.valueOf(type);
	}
	
	public void setKeyStoreType(KeyStoreType type) {
		this.type = type;
	}
	
	public void setSSLProtocol(String protocol) {
		this.protocol = SSLProtocol.valueOf(protocol);
	}
	
	public void setSSLProtocol(SSLProtocol protocol) {
		this.protocol = protocol;
	}
	
	public SSLContext getSSLContext() throws IOException {
		try {
			URL url = getClass().getClassLoader().getResource(keyStoreFile);
	        KeyStore keystore  = KeyStore.getInstance(type.name());
	        keystore.load(url.openStream(), keyPassword);
	        
	        KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(
	                KeyManagerFactory.getDefaultAlgorithm());
	        kmfactory.init(keystore, keyPassword);
	        KeyManager[] keymanagers = kmfactory.getKeyManagers(); 
	        SSLContext sslcontext = SSLContext.getInstance(protocol.name());
	        sslcontext.init(keymanagers, null, null);
	        return sslcontext;
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
}
