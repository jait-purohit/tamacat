package org.tamacat.dao.test;

import org.tamacat.dao.meta.DefaultColumnMetaData;
import org.tamacat.dao.meta.DefaultTableMetaData;
import org.tamacat.dao.meta.DataType;
import org.tamacat.dao.orm.MapBasedORMappingBean;

public class FileData extends MapBasedORMappingBean {

	private static final long serialVersionUID = 1L;

    public static final DefaultTableMetaData TABLE = new DefaultTableMetaData("file");
    public static final DefaultColumnMetaData FILE_ID = new DefaultColumnMetaData();
    public static final DefaultColumnMetaData FILE_NAME = new DefaultColumnMetaData();
    public static final DefaultColumnMetaData SIZE = new DefaultColumnMetaData();
    public static final DefaultColumnMetaData CONTENT_TYPE = new DefaultColumnMetaData();
    public static final DefaultColumnMetaData DATA = new DefaultColumnMetaData();
    public static final DefaultColumnMetaData UPDATE_DATE = new DefaultColumnMetaData();
    
    static {
        FILE_ID.setType(DataType.STRING).setColumnName("file_id")
        	.setPrimaryKey(true).setAutoGenerateId(true);
        FILE_NAME.setType(DataType.STRING).setColumnName("file_name");
        SIZE.setType(DataType.NUMERIC).setColumnName("size");
        CONTENT_TYPE.setType(DataType.STRING).setColumnName("content_type");
        DATA.setType(DataType.OBJECT).setColumnName("data");
        UPDATE_DATE.setType(DataType.DATE).setColumnName("update_date")
        	.setAutoTimestamp(true);
        TABLE.registerColumn(FILE_ID, FILE_NAME, SIZE, CONTENT_TYPE, DATA, UPDATE_DATE);
    }
}
