package org.tamacat.dao.test;

import org.tamacat.dao.meta.DefaultColumn;
import org.tamacat.dao.meta.DefaultTable;
import org.tamacat.dao.meta.DataType;
import org.tamacat.dao.orm.MapBasedORMappingBean;

public class FileData extends MapBasedORMappingBean {

	private static final long serialVersionUID = 1L;

    public static final DefaultTable TABLE = new DefaultTable("file");
    public static final DefaultColumn FILE_ID = new DefaultColumn();
    public static final DefaultColumn FILE_NAME = new DefaultColumn();
    public static final DefaultColumn SIZE = new DefaultColumn();
    public static final DefaultColumn CONTENT_TYPE = new DefaultColumn();
    public static final DefaultColumn DATA = new DefaultColumn();
    public static final DefaultColumn UPDATE_DATE = new DefaultColumn();
    
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
