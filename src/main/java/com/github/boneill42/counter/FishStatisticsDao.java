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
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;

public class FishStatisticsDao {
    private static final Logger LOG = LoggerFactory.getLogger(FishStatisticsDao.class);
    private Keyspace keyspace;
    private AstyanaxContext<Keyspace> astyanaxContext;

    public FishStatisticsDao(String host, String keyspace) {
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
     * dao.writeStatistics("fishblogs", fishStatistics);
     */
    public void writeStatistics(String columnFamilyName, FishStatistics fishStatistics) throws ConnectionException {
        AnnotatedCompositeSerializer<FishStatistics> entitySerializer = new AnnotatedCompositeSerializer<FishStatistics>(FishStatistics.class);
        MutationBatch mutation = keyspace.prepareMutationBatch();
        ColumnFamily<String, FishStatistics> columnFamily = new ColumnFamily<String, FishStatistics>(columnFamilyName, StringSerializer.get(), entitySerializer);
        /*
            RowKey: bigcat
                => (counter=2012, value=6)
                => (counter=2013, value=5)
        */
        mutation.withRow(columnFamily, fishStatistics.getUserId()).incrementCounterColumn(fishStatistics, 1);
        mutation.execute();
    }
    
    /**
     * Fetches an entire row using composite keys
     */
    public List<FishStatistics> readStatistics(String columnFamilyName, String rowKey) throws ConnectionException {
        AnnotatedCompositeSerializer<FishStatistics> entitySerializer = new AnnotatedCompositeSerializer<FishStatistics>(FishStatistics.class);
        ColumnFamily<String, FishStatistics> columnFamily = new ColumnFamily<String, FishStatistics>(columnFamilyName,
                StringSerializer.get(), entitySerializer);
        OperationResult<ColumnList<FishStatistics>> result = this.keyspace.prepareQuery(columnFamily).getKey(rowKey).execute();
        
        List<FishStatistics> statisticsList = new ArrayList<FishStatistics>();
        for(Column<FishStatistics> column : result.getResult()) {
            FishStatistics statistics = column.getName();
            statistics.setUserId(rowKey);
            statistics.setFishCatch(column.getLongValue());
            statisticsList.add(statistics);
        }
        
        return statisticsList;
    }
    
    public FishStatistics readStatistics(String columnFamilyName, String rowKey, String year) throws ConnectionException {
        AnnotatedCompositeSerializer<FishStatistics> entitySerializer = new AnnotatedCompositeSerializer<FishStatistics>(FishStatistics.class);
        ColumnFamily<String, FishStatistics> columnFamily = new ColumnFamily<String, FishStatistics>(columnFamilyName,
                StringSerializer.get(), entitySerializer);
        FishStatistics statistics = new FishStatistics(rowKey, year);
        OperationResult<Column<FishStatistics>> result = this.keyspace.prepareQuery(columnFamily).getKey(rowKey).getColumn(statistics).execute();
        Column<FishStatistics> column = result.getResult();
        statistics.setFishCatch(column.getLongValue());
            
        return statistics;
    }
}
