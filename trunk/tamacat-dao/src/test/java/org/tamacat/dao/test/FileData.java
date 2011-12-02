package org.tamacat.dao.test;

import org.tamacat.dao.meta.DefaultRdbColumnMetaData;
import org.tamacat.dao.meta.DefaultTableMetaData;
import org.tamacat.dao.meta.DataType;
import org.tamacat.dao.orm.MapBasedORMappingBean;

public class FileData extends MapBasedORMappingBean {

	private static final long serialVersionUID = 1L;

    public static final DefaultTableMetaData TABLE = new DefaultTableMetaData("file");
    public static final DefaultRdbColumnMetaData FILE_ID = new DefaultRdbColumnMetaData();
    public static final DefaultRdbColumnMetaData FILE_NAME = new DefaultRdbColumnMetaData();
    public static final DefaultRdbColumnMetaData SIZE = new DefaultRdbColumnMetaData();
    public static final DefaultRdbColumnMetaData CONTENT_TYPE = new DefaultRdbColumnMetaData();
    public static final DefaultRdbColumnMetaData DATA = new DefaultRdbColumnMetaData();
    public static final DefaultRdbColumnMetaData UPDATE_DATE = new DefaultRdbColumnMetaData();
    
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
