/*
 * Copyright (c) 2008 Tamacat.org
 * All rights reserved.
 */
package org.tamacat.dao.orm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.tamacat.dao.exception.DaoException;
import org.tamacat.dao.meta.Column;
import org.tamacat.dao.meta.DataType;
import org.tamacat.dao.util.MappingUtils;
import org.tamacat.di.DI;
import org.tamacat.util.ClassUtils;

public class ORMapper<T extends ORMappingSupport> {

    private String xml = "orm.xml";

    private String name;

    private T data;
    private Class<T> type;

    public ORMapper() {}

    public ORMapper(T data) {
        this.data = data;
    }

    public void setPrototype(String name) {
        this.name = name;
    }

    public void setPrototype(T prototype) {
        this.data = prototype;
    }

    public void setPrototype(Class<T> type) {
        this.type = type;
    }

    public void setMappingXml(String xml) {
        this.xml = xml;
    }

    private T createPrototype() {
        T o = null;
        if (xml != null && name != null) {
            T obj = DI.configure(xml).getBean(name, type);
            if (obj != null) {
            	o = obj;
            } else {
            	//throw new DIContainerException(name + " is not found.[" + xml + "]");
            }
        }
        if (o == null && type != null) {
            o = ClassUtils.newInstance(type);
        }
        return o;
    }

    public T getMappedObject() {
        if (data != null) {
            return data;
        } else {
            return createPrototype();
        }
    }

    public ORMapper<T> mapping(Collection<Column> columns, ResultSet rs) {
        data = createPrototype();
        try {
            int index = 1;
            for (Column column : columns) {
                DataType type = column.getType();
                data.mapping(column, MappingUtils.mapping(type, rs, index));
                index++;
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return this;
    }
}
