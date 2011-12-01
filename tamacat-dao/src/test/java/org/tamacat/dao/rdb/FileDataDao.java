package org.tamacat.dao.rdb;

import org.tamacat.dao.Condition;
import org.tamacat.dao.RdbDaoAdapter;
import org.tamacat.dao.RdbQuery;

public class FileDataDao extends RdbDaoAdapter<FileData> {
    
	public FileData search(FileData data) {
        RdbQuery<FileData> query = createQuery()
            .addSelectColumns(FileData.TABLE.getColumns())
            .andWhere(param(FileData.FILE_ID, Condition.EQUAL,
            			data.getValue(FileData.FILE_ID)));
        return super.search(query);
    }

    @Override
    protected String getInsertSQL(FileData data) {
        RdbQuery<FileData> query = createQuery()
        	.addUpdateColumns(FileData.TABLE.getColumns());
        return query.getInsertSQL(data);
    }

    @Override
    protected String getUpdateSQL(FileData data) {
        RdbQuery<FileData> query = createQuery()
        	.addUpdateColumn(FileData.UPDATE_DATE)
        	.addUpdateColumn(FileData.FILE_NAME)
        	.andWhere(
        		param(FileData.FILE_ID, Condition.EQUAL,
        				data.getValue(FileData.FILE_ID))
        	);
        return query.getUpdateSQL(data);
    }

    @Override
    protected String getDeleteSQL(FileData data) {
        RdbQuery<FileData> query = createQuery()
        	.addUpdateColumn(FileData.FILE_ID);
        return query.getDeleteSQL(data);
    }
}
