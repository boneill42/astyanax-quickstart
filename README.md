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

		userid | when                     | fishtype | blog            | image
		--------+--------------------------+----------+-----------------+----------------------
		bigcat | 2012-10-08 12:08:10-0400 |  CATFISH | this is myblog. | 01000000000000000000
