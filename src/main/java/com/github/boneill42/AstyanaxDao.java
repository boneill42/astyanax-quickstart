package com.github.boneill42;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.ColumnListMutation;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.serializers.AnnotatedCompositeSerializer;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;

public class AstyanaxDao {
    private static final Logger LOG = LoggerFactory.getLogger(AstyanaxDao.class);
    private Keyspace keyspace;
    private AstyanaxContext<Keyspace> astyanaxContext;

    public AstyanaxDao(String host, String keyspace) {
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
     * Writes columns.
     */
    public void write(String columnFamilyName, String rowKey, Map<String, String> columns) throws ConnectionException {
        MutationBatch mutation = keyspace.prepareMutationBatch();
        ColumnFamily<String, String> columnFamily = new ColumnFamily<String, String>(columnFamilyName,
                StringSerializer.get(), StringSerializer.get());
        for (Map.Entry<String, String> entry : columns.entrySet()) {
            mutation.withRow(columnFamily, rowKey).putColumn(entry.getKey(), entry.getValue(), null);
        }
        mutation.execute();
    }
    
    /**
     * Writes compound/composite columns.
     * dao.writeBlog("fishblogs", fishBlog);
     */
    public void writeBlog(String columnFamilyName, FishBlog fishBlog) throws ConnectionException {
        AnnotatedCompositeSerializer<FishBlogCompositeColumn> entitySerializer = new AnnotatedCompositeSerializer<FishBlogCompositeColumn>(FishBlogCompositeColumn.class);
        MutationBatch mutation = keyspace.prepareMutationBatch();
        ColumnFamily<String, FishBlogCompositeColumn> columnFamily = new ColumnFamily<String, FishBlogCompositeColumn>(columnFamilyName, StringSerializer.get(), entitySerializer);
        
        /*
            RowKey: <userId>
            => (column=<when>:<fishType>:blog, value=<blog>, timestamp=<>)
            => (column=<when>:<fishType>:image, value=<image>, timestamp=<>)
        */
        ColumnListMutation<FishBlogCompositeColumn> row = mutation.withRow(columnFamily, fishBlog.getUserId());
        FishBlogCompositeColumn blogColumn = new FishBlogCompositeColumn(fishBlog.getWhen(), fishBlog.getFishType(), FishBlogCompositeColumn.BLOG_FIELD);
        FishBlogCompositeColumn imageColumn = new FishBlogCompositeColumn(fishBlog.getWhen(), fishBlog.getFishType(), FishBlogCompositeColumn.IMAGE_FIELD);
        row.putColumn(blogColumn, fishBlog.getBlog());
        row.putColumn(imageColumn, fishBlog.getImage());
        
        mutation.execute();
    }
    
    /**
     * Fetches an entire row.
     */
    public ColumnList<String> read(String columnFamilyName, String rowKey) throws ConnectionException {
        ColumnFamily<String, String> columnFamily = new ColumnFamily<String, String>(columnFamilyName,
                StringSerializer.get(), StringSerializer.get());
        OperationResult<ColumnList<String>> result = this.keyspace.prepareQuery(columnFamily).getKey(rowKey).execute();
        return result.getResult();
    }

    /**
     * Fetches an entire row using composite keys
     * 
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public ColumnList<FishBlogCompositeColumn> readBlogs(String columnFamilyName, String rowKey) throws ConnectionException {
        AnnotatedCompositeSerializer<FishBlogCompositeColumn> entitySerializer = new AnnotatedCompositeSerializer<FishBlogCompositeColumn>(FishBlogCompositeColumn.class);
        ColumnFamily<String, FishBlogCompositeColumn> columnFamily = new ColumnFamily<String, FishBlogCompositeColumn>(columnFamilyName,
                StringSerializer.get(), entitySerializer);
        OperationResult<ColumnList<FishBlogCompositeColumn>> result = this.keyspace.prepareQuery(columnFamily).getKey(rowKey).execute();
        return result.getResult();
    }
}
