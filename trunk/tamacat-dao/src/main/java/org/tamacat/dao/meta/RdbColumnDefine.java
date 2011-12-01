/*
 * Copyright (c) 2008, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao.meta;

import java.io.Serializable;

public class RdbColumnDefine implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final RdbColumnDefine PRIMARY_KEY = new RdbColumnDefine("primary key");
    public static final RdbColumnDefine FOREIGN_KEY = new RdbColumnDefine("foreign key");
    public static final RdbColumnDefine AUTO_GENERATE_ID = new RdbColumnDefine("AutoGenerateId");
    public static final RdbColumnDefine AUTO_TIMESTAMP = new RdbColumnDefine("AutoTimestamp");
    public static final RdbColumnDefine NOT_NULL = new RdbColumnDefine("not null");

    private String defineName;

    public RdbColumnDefine(String defineName) {
        this.defineName = defineName;
    }

    public String getDefineName() {
        return defineName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((defineName == null) ? 0 : defineName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof RdbColumnDefine))
            return false;
        final RdbColumnDefine other = (RdbColumnDefine) obj;
        if (defineName == null) {
            if (other.defineName != null)
                return false;
        } else if (!defineName.equals(other.defineName))
            return false;
        return true;
    }
}
