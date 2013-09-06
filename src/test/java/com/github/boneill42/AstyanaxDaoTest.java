package com.github.boneill42;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnList;

public class AstyanaxDaoTest extends TestEnv {
    private static Logger LOG = LoggerFactory.getLogger(AstyanaxDaoTest.class);
    private static final String COLUMN_FAMILY_NAME = "fishblogs";
    
    @Test
    public void testDao() throws Exception {
       AstyanaxDao dao = new AstyanaxDao(HOST, KEYSPACE);
       dumpStrings(dao.read("fishblogs", "bigcat"));
    }
    
    /*  CQL: select * from fishblogs;
            userid | when                     | fishtype | blog            | image
            --------+--------------------------+----------+-----------------+----------------------
            bigcat | 2013-08-30 10:56:31+0700 |  CATFISH | this is myblog. | 01000000000000000000
        
        CLI: list fishblogs;
            RowKey: bigcat
                => (column=2013-08-30 10\:56\:31+0700:CATFISH:blog, value=74686973206973206d79626c6f672e, timestamp=1377834991626000)
                => (column=2013-08-30 10\:56\:31+0700:CATFISH:image, value=01000000000000000000, timestamp=1377834991626000)
    */
    @Test
    public void testFishBlogDao() throws Exception {
        AstyanaxDao dao = new AstyanaxDao(HOST, KEYSPACE);
        String userId = "bigcat";
        long now = System.currentTimeMillis();
        byte[] image = new byte[10];
        image[0] = 1;
        FishBlog fishBlog = new FishBlog(userId, now, "CATFISH", "this is myblog.", image);
        dao.writeBlog(COLUMN_FAMILY_NAME, fishBlog);
        dumpFishBlog(dao.readBlogs(COLUMN_FAMILY_NAME, userId));
    }
    
    public void dumpStrings(ColumnList<String> columns){
        for (Column<String> column : columns){
            LOG.debug("[" + column.getName() + "]->[" + column.getStringValue() + "]");
        }        
    }

    public void dumpFishBlog(ColumnList<FishBlogCompositeColumn> columns) {
        for (Column<FishBlogCompositeColumn> column : columns) {
            FishBlogCompositeColumn fishBlogColumn = column.getName();
            if(fishBlogColumn.getField().equals(FishBlogCompositeColumn.BLOG_FIELD)) {
                LOG.debug(fishBlogColumn.toString() + " => value: " + column.getStringValue());
            } else {
                LOG.debug(fishBlogColumn.toString() + " => value: " + column.getByteValue());
            }
        }
    }
}
