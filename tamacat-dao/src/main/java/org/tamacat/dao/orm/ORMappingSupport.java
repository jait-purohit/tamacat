/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.orm;

import org.tamacat.dao.meta.RdbColumnMetaData;


public interface ORMappingSupport {

	ORMappingSupport mapping(Object name, Object value);

    String getValue(RdbColumnMetaData column);
    
    ORMappingSupport setValue(RdbColumnMetaData column, String value);
    
    boolean isUpdate(Object name);
}
