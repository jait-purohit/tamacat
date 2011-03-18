/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.rdb;

public class User extends MapBasedORMappingBean {

    private static final long serialVersionUID = 1L;

    public static final DefaultRdbTableMetaData TABLE = new DefaultRdbTableMetaData("users");
    public static final DefaultRdbColumnMetaData USER_ID = new DefaultRdbColumnMetaData();
    public static final DefaultRdbColumnMetaData PASSWORD = new DefaultRdbColumnMetaData();
    public static final DefaultRdbColumnMetaData DEPT_ID = new DefaultRdbColumnMetaData();

    static {
        USER_ID.setType(RdbDataType.STRING).setPrimaryKey(true).setColumnName("user_id");
        PASSWORD.setType(RdbDataType.STRING).setColumnName("password");
    	DEPT_ID.setType(RdbDataType.STRING).setColumnName("dept_id");
        TABLE.registerColumn(USER_ID, PASSWORD, DEPT_ID);
    }
}
