-- Required with JBoss EAP 6.1 as unfortunately it cannot determine H2 jdbc type...
-- 
-- 09:24:18,813 WARN  [org.hibernate.dialect.H2Dialect] (ServerService Thread Pool -- 62) HHH000431: 
--  .... Unable to determine H2 database version, certain features may not work
--
-- To make it work, we rely on sequence instead of Identity column, which is a shame.
--

DROP SEQUENCE IF EXISTS HIBERNATE_SEQUENCE;
CREATE SEQUENCE HIBERNATE_SEQUENCE START WITH 1000;
