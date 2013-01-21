package org.tamacat.io;

public class MessageDigestException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MessageDigestException() {
	}

	public MessageDigestException(String message) {
		super(message);
	}

	public MessageDigestException(Throwable cause) {
		super(cause);
	}

	public MessageDigestException(String message, Throwable cause) {
		super(message, cause);
	}
}
