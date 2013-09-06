Astyanax Quick Start
===========================

This is a quick demo/quickstart application that uses Astyanax to connect to Cassandra.  It shows how to read and write from Cassandra using Astyanax, and how that plays with CQL.

We'll use the following schema:

		CREATE KEYSPACE examples WITH strategy_class = 'NetworkTopologyStrategy' AND strategy_options:datacenter1 = '1';
  
		use examples;

		CREATE TABLE fishblogs (
			userid varchar,
			when timestamp,
			fishtype varchar,
			blog varchar,
			image blob,
			PRIMARY KEY (userid, when, fishtype)
		);

After running the unit tests, you should see:

CQL: select * from fishblogs;

		userid | when                     | fishtype | blog            | image
		--------+--------------------------+----------+-----------------+----------------------
		bigcat | 2013-08-30 10:56:31+0700 |  CATFISH | this is myblog. | 01000000000000000000

CLI: list fishblogs;

		RowKey: bigcat
		=> (column=2013-08-30 10\:56\:31+0700:CATFISH:blog, value=74686973206973206d79626c6f672e, timestamp=1377834991626000)
		=> (column=2013-08-30 10\:56\:31+0700:CATFISH:image, value=01000000000000000000, timestamp=1377834991626000)

Counter example is also added to com.github.boneill42.counter package, refer to db/schema.cql for details.