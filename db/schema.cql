CREATE KEYSPACE examples WITH strategy_class = 'NetworkTopologyStrategy' AND strategy_options:datacenter1 = '1';
use examples;

-- CF fishblogs with Composite Key
CREATE TABLE fishblogs (
	userid varchar,
	when timestamp,
	fishtype varchar,
	blog varchar,
	image blob,
	PRIMARY KEY (userid, when, fishtype)
);

-- CF fish_statistics with counter column
CREATE TABLE fish_statistics (
	userid varchar,
	year varchar,
	catch counter,
	PRIMARY KEY (userid, year)
);

-- CF fish_month_statistics with counter column
CREATE TABLE fish_month_statistics (
	userid varchar,
	year varchar,
	month varchar,
	catch counter,
	PRIMARY KEY (userid, year, month)
);