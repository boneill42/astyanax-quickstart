package com.github.boneill42.counter;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.serializers.AnnotatedCompositeSerializer;
import com.netflix.astyanax.serializers.CompositeRangeBuilder;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;

public class FishMonthStatisticsDao {
    private static final Logger LOG = LoggerFactory.getLogger(FishMonthStatisticsDao.class);
    private Keyspace keyspace;
    private AstyanaxContext<Keyspace> astyanaxContext;

    public FishMonthStatisticsDao(String host, String keyspace) {
        try {
            this.astyanaxContext = new AstyanaxContext.Builder()
                    .forCluster("ClusterName")
                    .forKeyspace(keyspace)
                    .withAstyanaxConfiguration(new AstyanaxConfigurationImpl().setDiscoveryType(NodeDiscoveryType.NONE))
                    .withConnectionPoolConfiguration(
                            new ConnectionPoolConfigurationImpl("MyConnectionPool").setMaxConnsPerHost(1)
                                    .setSeeds(host)).withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
                    .buildKeyspace(ThriftFamilyFactory.getInstance());

            this.astyanaxContext.start();
            this.keyspace = this.astyanaxContext.getClient();
            // test the connection
            this.keyspace.describeKeyspace();
        } catch (Throwable e) {
            LOG.warn("Preparation failed.", e);
            throw new RuntimeException("Failed to prepare CassandraBolt", e);
        }
    }

    public void cleanup() {
        this.astyanaxContext.shutdown();
    }
    
    /**
     * Writes compound/composite columns.
     * dao.writeStatistics("fishblogs", fishMonthStatistics);
     */
    public void writeStatistics(String columnFamilyName, FishMonthStatistics fishMonthStatistics) throws ConnectionException {
        AnnotatedCompositeSerializer<FishMonthStatistics> entitySerializer = new AnnotatedCompositeSerializer<FishMonthStatistics>(FishMonthStatistics.class);
        MutationBatch mutation = keyspace.prepareMutationBatch();
        ColumnFamily<String, FishMonthStatistics> columnFamily = new ColumnFamily<String, FishMonthStatistics>(columnFamilyName, StringSerializer.get(), entitySerializer);
        /*
            RowKey: bigcat
                    => (counter=2012:01, value=1)
                    => (counter=2013:07, value=2)
                    => (counter=2013:08, value=1)
        */
        mutation.withRow(columnFamily, fishMonthStatistics.getUserId()).incrementCounterColumn(fishMonthStatistics, 1);
        mutation.execute();
    }
    
    /**
     * Fetches an entire row using composite keys
     */
    public List<FishMonthStatistics> readStatistics(String columnFamilyName, String rowKey) throws ConnectionException {
        AnnotatedCompositeSerializer<FishMonthStatistics> entitySerializer = new AnnotatedCompositeSerializer<FishMonthStatistics>(FishMonthStatistics.class);
        ColumnFamily<String, FishMonthStatistics> columnFamily = new ColumnFamily<String, FishMonthStatistics>(columnFamilyName,
                StringSerializer.get(), entitySerializer);
        OperationResult<ColumnList<FishMonthStatistics>> result = this.keyspace.prepareQuery(columnFamily).getKey(rowKey).execute();
        
        List<FishMonthStatistics> statisticsList = new ArrayList<FishMonthStatistics>();
        for(Column<FishMonthStatistics> column : result.getResult()) {
            FishMonthStatistics statistics = column.getName();
            statistics.setUserId(rowKey);
            statistics.setFishCatch(column.getLongValue());
            statisticsList.add(statistics);
        }
        
        return statisticsList;
    }
    
    public List<FishMonthStatistics> readStatistics(String columnFamilyName, String rowKey, String year) throws ConnectionException {
        AnnotatedCompositeSerializer<FishMonthStatistics> entitySerializer = new AnnotatedCompositeSerializer<FishMonthStatistics>(FishMonthStatistics.class);
        ColumnFamily<String, FishMonthStatistics> columnFamily = new ColumnFamily<String, FishMonthStatistics>(columnFamilyName,
                StringSerializer.get(), entitySerializer);
        CompositeRangeBuilder rangeBuilder = entitySerializer.buildRange();
        rangeBuilder.lessThanEquals(year);
        rangeBuilder.greaterThanEquals(year);
        
        OperationResult<ColumnList<FishMonthStatistics>> result = this.keyspace.prepareQuery(columnFamily).getKey(rowKey).withColumnRange(rangeBuilder.build()).execute();
        
        List<FishMonthStatistics> statisticsList = new ArrayList<FishMonthStatistics>();
        for(Column<FishMonthStatistics> column : result.getResult()) {
            FishMonthStatistics statistics = column.getName();
            statistics.setUserId(rowKey);
            statistics.setFishCatch(column.getLongValue());
            statisticsList.add(statistics);
        }
        
        return statisticsList;
    }
    
    public FishMonthStatistics readStatistics(String columnFamilyName, String rowKey, String year, String month) throws ConnectionException {
        AnnotatedCompositeSerializer<FishMonthStatistics> entitySerializer = new AnnotatedCompositeSerializer<FishMonthStatistics>(FishMonthStatistics.class);
        ColumnFamily<String, FishMonthStatistics> columnFamily = new ColumnFamily<String, FishMonthStatistics>(columnFamilyName,
                StringSerializer.get(), entitySerializer);
        FishMonthStatistics statistics = new FishMonthStatistics(rowKey, year, month);
        OperationResult<Column<FishMonthStatistics>> result = this.keyspace.prepareQuery(columnFamily).getKey(rowKey).getColumn(statistics).execute();
        Column<FishMonthStatistics> column = result.getResult();
        statistics.setFishCatch(column.getLongValue());

        return statistics;
    }
}
