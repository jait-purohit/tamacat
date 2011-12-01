/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.rdb;

import org.tamacat.dao.meta.DefaultRdbColumnMetaData;
import org.tamacat.dao.meta.DefaultRdbTableMetaData;
import org.tamacat.dao.meta.RdbDataType;
import org.tamacat.dao.orm.MapBasedORMappingBean;

public class Dept extends MapBasedORMappingBean {
	private static final long serialVersionUID = 1L;

    public static final DefaultRdbTableMetaData TABLE = new DefaultRdbTableMetaData("dept");
    public static final DefaultRdbColumnMetaData DEPT_ID = new DefaultRdbColumnMetaData();
    public static final DefaultRdbColumnMetaData DEPT_NAME = new DefaultRdbColumnMetaData();
    
    static {
    	DEPT_ID.setType(RdbDataType.STRING).setPrimaryKey(true).setColumnName("dept_id");
        DEPT_NAME.setType(RdbDataType.STRING).setColumnName("dept_name");
        TABLE.registerColumn(DEPT_ID, DEPT_NAME);
    }
}
