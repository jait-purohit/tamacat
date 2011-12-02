/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.test;

import org.tamacat.dao.meta.DefaultColumnMetaData;
import org.tamacat.dao.meta.DefaultTableMetaData;
import org.tamacat.dao.meta.DataType;
import org.tamacat.dao.orm.MapBasedORMappingBean;

public class User extends MapBasedORMappingBean {

    private static final long serialVersionUID = 1L;

    public static final DefaultTableMetaData TABLE = new DefaultTableMetaData("users");
    public static final DefaultColumnMetaData USER_ID = new DefaultColumnMetaData();
    public static final DefaultColumnMetaData PASSWORD = new DefaultColumnMetaData();
    public static final DefaultColumnMetaData DEPT_ID = new DefaultColumnMetaData();

    static {
        USER_ID.setType(DataType.STRING).setPrimaryKey(true).setColumnName("user_id");
        PASSWORD.setType(DataType.STRING).setColumnName("password");
    	DEPT_ID.setType(DataType.STRING).setColumnName("dept_id");
        TABLE.registerColumn(USER_ID, PASSWORD, DEPT_ID);
    }
}
