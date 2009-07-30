/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.di.define;

public class BeanConstructorParam implements Cloneable {

    private String refId;
    private String type;

    private String value;

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public boolean isRef() {
        return refId != null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    @Override
    public BeanConstructorParam clone() {
        try {
        	return (BeanConstructorParam) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e.getMessage());
        }
    }
}
