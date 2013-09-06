package com.github.boneill42;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.boneill42.counter.FishMonthStatistics;
import com.github.boneill42.counter.FishMonthStatisticsDao;

public class FishMonthStatisticsDaoTest extends TestEnv {
    private static Logger LOG = LoggerFactory.getLogger(FishMonthStatisticsDaoTest.class);
    private static final String COLUMN_FAMILY_NAME = "fish_month_statistics";

    @Test
    public void testDao() throws Exception {
        FishMonthStatisticsDao dao = new FishMonthStatisticsDao(HOST, KEYSPACE);
        System.out.println(dao);
    }

    @Test
    public void testWriteFishStatistics() throws Exception {
        FishMonthStatisticsDao dao = new FishMonthStatisticsDao(HOST, KEYSPACE);
        String userId = "bigcat";
        String year = "2012";
        String month = "11";
        FishMonthStatistics fishStatistics = new FishMonthStatistics(userId, year, month);
        dao.writeStatistics(COLUMN_FAMILY_NAME, fishStatistics);
    }

    @Test
    public void testGetFishMonthStatistics() throws Exception {
        FishMonthStatisticsDao dao = new FishMonthStatisticsDao(HOST, KEYSPACE);
        List<FishMonthStatistics> listStatistics = dao.readStatistics(COLUMN_FAMILY_NAME, "bigcat");
        for (FishMonthStatistics statistics : listStatistics) {
            LOG.info(statistics.toString());
        }
    }

    @Test
    public void testGetFishMonthStatisticsByYear() throws Exception {
        FishMonthStatisticsDao dao = new FishMonthStatisticsDao(HOST, KEYSPACE);
        List<FishMonthStatistics> listStatistics = dao.readStatistics(COLUMN_FAMILY_NAME, "bigcat", "2012");
        for (FishMonthStatistics statistics : listStatistics) {
            LOG.info(statistics.toString());
        }
    }

    @Test
    public void testGetSingleFishMonthStatistics() throws Exception {
        FishMonthStatisticsDao dao = new FishMonthStatisticsDao(HOST, KEYSPACE);
        FishMonthStatistics statistics = dao.readStatistics(COLUMN_FAMILY_NAME, "bigcat", "2012", "11");
        LOG.info(statistics.toString());
    }
}
