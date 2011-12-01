/*
 * Copyright (c) 2007, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.dao;


public enum Condition {

    LIKE_HEAD(" like ", SQLParser.VALUE1 + "%"),
    LIKE_PART(" like ", "%" + SQLParser.VALUE1 + "%"),
    LIKE_TAIL(" like ", "%" + SQLParser.VALUE1),
    EQUAL("=", SQLParser.VALUE1),
    NOT_EQUAL("<>", SQLParser.VALUE1),
    BETWEEN(" between ", SQLParser.VALUE1 + " and " + SQLParser.VALUE2),
    LESS("<", SQLParser.VALUE1),
    GREATER(">", SQLParser.VALUE1),
    LESS_OR_EQUAL("<=", SQLParser.VALUE1),
    GREATER_OR_EQUAL(">=", SQLParser.VALUE1),
    IS_NULL(" is null", null),
    NOT_NULL(" not null", null),
    IN(" in ", "(" + SQLParser.MULTI_VALUE + ")")
    ;

    private final String replaceHolder;
    private final String condition;

    private Condition(String condition, String replaceHolder) {
        this.condition = condition;
        this.replaceHolder = replaceHolder;
    }

    public String getReplaceHolder() {
        return replaceHolder;
    }

    public String getCondition() {
        return condition;
    }
}
