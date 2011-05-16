package org.tamacat.dao.rdb.util;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.tamacat.dao.DaoException;

public class BlobUtils {
	
	public static int executeUpdate(PreparedStatement stmt,
			int index, InputStream in) throws DaoException {
		try {
			stmt.setBinaryStream(index, in);
			return stmt.executeUpdate();
		} catch (SQLException e) {
			throw new DaoException(e);
		}
	}
}
