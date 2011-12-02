/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.test;

import org.tamacat.dao.meta.DefaultColumn;
import org.tamacat.dao.meta.DefaultTable;
import org.tamacat.dao.meta.DataType;
import org.tamacat.dao.orm.MapBasedORMappingBean;

public class Dept extends MapBasedORMappingBean {
	private static final long serialVersionUID = 1L;

    public static final DefaultTable TABLE = new DefaultTable("dept");
    public static final DefaultColumn DEPT_ID = new DefaultColumn();
    public static final DefaultColumn DEPT_NAME = new DefaultColumn();
    
    static {
    	DEPT_ID.setType(DataType.STRING).setPrimaryKey(true).setColumnName("dept_id");
        DEPT_NAME.setType(DataType.STRING).setColumnName("dept_name");
        TABLE.registerColumn(DEPT_ID, DEPT_NAME);
    }
}
