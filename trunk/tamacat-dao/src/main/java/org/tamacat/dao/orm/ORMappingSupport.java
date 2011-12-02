/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.orm;

import org.tamacat.dao.meta.ColumnMetaData;


public interface ORMappingSupport {

	ORMappingSupport mapping(Object name, Object value);

    String getValue(ColumnMetaData column);
    
    ORMappingSupport setValue(ColumnMetaData column, String value);
    
    boolean isUpdate(Object name);
}
