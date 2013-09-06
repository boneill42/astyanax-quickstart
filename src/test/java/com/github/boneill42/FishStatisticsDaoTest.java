package com.github.boneill42;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.boneill42.counter.FishStatistics;
import com.github.boneill42.counter.FishStatisticsDao;

public class FishStatisticsDaoTest extends TestEnv {
    private static Logger LOG = LoggerFactory.getLogger(FishStatisticsDaoTest.class);
    private static final String COLUMN_FAMILY_NAME = "fish_statistics";
    
    @Test
    public void testDao() throws Exception {
        FishStatisticsDao dao = new FishStatisticsDao(HOST, KEYSPACE);
       System.out.println(dao);
    }

    @Test
    public void testWriteFishStatistics() throws Exception {
        FishStatisticsDao dao = new FishStatisticsDao(HOST, KEYSPACE);
        String userId = "bigcat";
        String year = "2012";
        FishStatistics fishStatistics = new FishStatistics(userId, year);
        dao.writeStatistics(COLUMN_FAMILY_NAME, fishStatistics);
    }
    
    @Test
    public void testGetFishStatistics() throws Exception {
        FishStatisticsDao dao = new FishStatisticsDao(HOST, KEYSPACE);
        List<FishStatistics> listStatistics = dao.readStatistics(COLUMN_FAMILY_NAME, "bigcat");
        for(FishStatistics statistics : listStatistics) {
            LOG.info(statistics.toString());
        }
    }
    
    @Test
    public void testGetSingleFishStatistics() throws Exception {
        FishStatisticsDao dao = new FishStatisticsDao(HOST, KEYSPACE);
        System.out.println(dao.readStatistics(COLUMN_FAMILY_NAME, "bigcat", "2013").toString());
    }
}
