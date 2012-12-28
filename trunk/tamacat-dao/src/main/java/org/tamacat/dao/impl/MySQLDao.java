/*
 * Copyright (c) 2007-2012 tamacat.org
 * All rights reserved.
 */
package org.tamacat.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.tamacat.dao.Dao;
import org.tamacat.dao.Query;
import org.tamacat.dao.Search;
import org.tamacat.dao.meta.Column;
import org.tamacat.dao.orm.ORMappingSupport;

public class MySQLDao<T extends ORMappingSupport> extends Dao<T> {
	
    public MySQLDao() {}

    @Override
    public Search createSearch() {
        return new MySQLSearch();
    }
    
    @Override
    public Collection<T> searchList(Query<T> query, int start, int max) {
        Collection<Column>columns = query.getSelectColumns();
        String sql = query.getSelectSQL();
        sql = sql.replaceFirst("SELECT ", "SELECT SQL_CALC_FOUND_ROWS ");
        
        ResultSet rs = executeQuery(sql);
        ArrayList<T> list = new ArrayList<T>();
        try {
        	if (start > 0) {
        		sql = sql + " limit " + start + "," + max;
        		//for (int i=1; i<start; i++) rs.next();
        	}
            int add = 0;
            while (rs.next()) {
                T o = mapping(columns, rs);
                list.add(o);
                add ++;
                if (max > 0 && add >= max) break;
            }
            rs = executeQuery("SELECT FOUND_ROWS()");
            if (rs.next()) {
            	long hit = rs.getLong(1);
            	System.out.println(hit);
            }
        } catch (SQLException e) {
            handleException(e);
        }
        return list;
    }
}
