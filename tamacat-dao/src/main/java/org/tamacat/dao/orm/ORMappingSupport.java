/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.orm;

import org.tamacat.dao.meta.Column;


public interface ORMappingSupport {

	ORMappingSupport mapping(Object name, Object value);

    String getValue(Column column);
    
    ORMappingSupport setValue(Column column, String value);
    
    boolean isUpdate(Object name);
}
