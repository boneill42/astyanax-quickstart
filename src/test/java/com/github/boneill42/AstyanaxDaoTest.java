package com.github.boneill42;

import java.util.Date;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnList;

public class AstyanaxDaoTest {
    private static Logger LOG = LoggerFactory.getLogger(AstyanaxDaoTest.class);

    @Test
    public void testDao() throws Exception {
       AstyanaxDao dao = new AstyanaxDao("localhost:9160", "example");
       dumpStrings(dao.read("fishblogs", "boneill42"));
    }
    
    @Test
    public void testFishBlogDao() throws Exception {
       AstyanaxDao dao = new AstyanaxDao("localhost:9160", "example");
       dumpFishBlog(dao.readEntity("fishblogs", "boneill42"));
    }
    
    public void dumpStrings(ColumnList<String> columns){
        for (Column<String> column : columns){
            LOG.debug("[" + column.getName() + "]->[" + column.getStringValue() + "]");
        }        
    }

    public void dumpFishBlog(ColumnList<FishBlog> columns){
        for (Column<FishBlog> column : columns){
            FishBlog fishBlog = column.getName();
            LOG.debug("fishBlog.when=>[" + new Date(fishBlog.when) + "]");
            LOG.debug("fishBlog.type=>[" + fishBlog.type + "]");
            LOG.debug("fishBlog.field=>[" + fishBlog.field + "]");
            LOG.debug("fishBlog.value=>[" + column.getStringValue() + "]");
        }        
    }
}
