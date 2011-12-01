package org.tamacat.dao.test;

import org.tamacat.dao.meta.DefaultRdbColumnMetaData;
import org.tamacat.dao.meta.DefaultRdbTableMetaData;
import org.tamacat.dao.meta.RdbDataType;
import org.tamacat.dao.orm.MapBasedORMappingBean;

public class FileData extends MapBasedORMappingBean {

	private static final long serialVersionUID = 1L;

    public static final DefaultRdbTableMetaData TABLE = new DefaultRdbTableMetaData("file");
    public static final DefaultRdbColumnMetaData FILE_ID = new DefaultRdbColumnMetaData();
    public static final DefaultRdbColumnMetaData FILE_NAME = new DefaultRdbColumnMetaData();
    public static final DefaultRdbColumnMetaData SIZE = new DefaultRdbColumnMetaData();
    public static final DefaultRdbColumnMetaData CONTENT_TYPE = new DefaultRdbColumnMetaData();
    public static final DefaultRdbColumnMetaData DATA = new DefaultRdbColumnMetaData();
    public static final DefaultRdbColumnMetaData UPDATE_DATE = new DefaultRdbColumnMetaData();
    
    static {
        FILE_ID.setType(RdbDataType.STRING).setColumnName("file_id")
        	.setPrimaryKey(true).setAutoGenerateId(true);
        FILE_NAME.setType(RdbDataType.STRING).setColumnName("file_name");
        SIZE.setType(RdbDataType.NUMERIC).setColumnName("size");
        CONTENT_TYPE.setType(RdbDataType.STRING).setColumnName("content_type");
        DATA.setType(RdbDataType.OBJECT).setColumnName("data");
        UPDATE_DATE.setType(RdbDataType.DATE).setColumnName("update_date")
        	.setAutoTimestamp(true);
        TABLE.registerColumn(FILE_ID, FILE_NAME, SIZE, CONTENT_TYPE, DATA, UPDATE_DATE);
    }
}
