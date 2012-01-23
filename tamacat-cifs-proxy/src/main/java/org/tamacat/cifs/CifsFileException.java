package org.tamacat.cifs;

public class CifsFileException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CifsFileException() {}

	public CifsFileException(String message) {
		super(message);
	}

	public CifsFileException(Throwable cause) {
		super(cause);
	}

	public CifsFileException(String message, Throwable cause) {
		super(message, cause);
	}

}
