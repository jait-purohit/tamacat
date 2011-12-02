/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.test;

import org.tamacat.dao.meta.DefaultColumnMetaData;
import org.tamacat.dao.meta.DefaultTableMetaData;
import org.tamacat.dao.meta.DataType;
import org.tamacat.dao.orm.MapBasedORMappingBean;

public class Dept extends MapBasedORMappingBean {
	private static final long serialVersionUID = 1L;

    public static final DefaultTableMetaData TABLE = new DefaultTableMetaData("dept");
    public static final DefaultColumnMetaData DEPT_ID = new DefaultColumnMetaData();
    public static final DefaultColumnMetaData DEPT_NAME = new DefaultColumnMetaData();
    
    static {
    	DEPT_ID.setType(DataType.STRING).setPrimaryKey(true).setColumnName("dept_id");
        DEPT_NAME.setType(DataType.STRING).setColumnName("dept_name");
        TABLE.registerColumn(DEPT_ID, DEPT_NAME);
    }
}
