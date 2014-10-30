-- MySQL dump 10.13  Distrib 5.1.46, for Win32 (ia32)
--
-- Host: localhost    Database: WeblinksInterconnectDB
-- ------------------------------------------------------
-- Server version	5.1.46-community

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `WeblinksInterconnectDB`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `WeblinksInterconnectDB` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `WeblinksInterconnectDB`;

--
-- Table structure for table `AGG_FILTER_MAP`
--

DROP TABLE IF EXISTS `AGG_FILTER_MAP`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `AGG_FILTER_MAP` (
  `MAP_GROUP` varchar(24) DEFAULT NULL,
  `CALL_CASE` varchar(24) DEFAULT NULL,
  `AGGREGATION_KEY` varchar(24) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `AGG_FILTER_MAP`
--

LOCK TABLES `AGG_FILTER_MAP` WRITE;
/*!40000 ALTER TABLE `AGG_FILTER_MAP` DISABLE KEYS */;
INSERT INTO `AGG_FILTER_MAP` VALUES ('Default','POC','InterconnectBilling');
/*!40000 ALTER TABLE `AGG_FILTER_MAP` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cdrFromMSC`
--

DROP TABLE IF EXISTS `cdrFromMSC`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cdrFromMSC` (
  `SCENARIO` varchar(24) DEFAULT NULL,
  `PARTNER` varchar(24) DEFAULT NULL,
  `BILL_MONTH` varchar(24) DEFAULT NULL,
  `DURATION` int(11) DEFAULT NULL,
  `COST` double DEFAULT NULL,
  `dummy` varchar(10) DEFAULT NULL,
  `KEY_SYS_ID` varchar(200) DEFAULT NULL,
  `INSTANCE` varchar(200) DEFAULT NULL,
  `RECORD_TYPE` varchar(200) DEFAULT NULL,
  `SERVICE_CENTRE` varchar(200) DEFAULT NULL,
  `SERVED_IMSI` varchar(200) DEFAULT NULL,
  `SERVED_IMEI` varchar(200) DEFAULT NULL,
  `SERVED_MSISDN` varchar(200) DEFAULT NULL,
  `CALLING_NUMBER` varchar(200) DEFAULT NULL,
  `CALLED_NUMBER` varchar(200) DEFAULT NULL,
  `RECORDING_ENTITY` varchar(200) DEFAULT NULL,
  `INCOMING_TRUNK` varchar(200) DEFAULT NULL,
  `OUTGOING_TRUNK` varchar(200) DEFAULT NULL,
  `LOCATION_MSC_NUMBER` varchar(200) DEFAULT NULL,
  `SUPS_USED` varchar(200) DEFAULT NULL,
  `MS_CLASSMARK` varchar(200) DEFAULT NULL,
  `SEIZURE_TIME` varchar(200) DEFAULT NULL,
  `ANSWER_TIME` varchar(200) DEFAULT NULL,
  `RELEASE_TIME` varchar(200) DEFAULT NULL,
  `CALL_DURATION` varchar(200) DEFAULT NULL,
  `CAUSE_FOR_TERM` varchar(200) DEFAULT NULL,
  `DIAGNOSTICS` varchar(200) DEFAULT NULL,
  `CALL_REFERNCE` varchar(200) DEFAULT NULL,
  `SEQUENCE_NUMBER` varchar(200) DEFAULT NULL,
  `RECORD_EXTENSIONS` varchar(200) DEFAULT NULL,
  `SRC_CODE` varchar(200) DEFAULT NULL,
  `LAST_FIELD` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cdrFromMSC`
--

LOCK TABLES `cdrFromMSC` WRITE;
/*!40000 ALTER TABLE `cdrFromMSC` DISABLE KEYS */;
/*!40000 ALTER TABLE `cdrFromMSC` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `duplicate_check`
--

DROP TABLE IF EXISTS `DUPLICATE_CHECK`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `DUPLICATE_CHECK` (
  `CDR_KEY` varchar(64) DEFAULT NULL,
  `CDR_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `DUPLICATE_CHECK`
--

LOCK TABLES `DUPLICATE_CHECK` WRITE;
/*!40000 ALTER TABLE `DUPLICATE_CHECK` DISABLE KEYS */;
/*!40000 ALTER TABLE `DUPLICATE_CHECK` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `INTERCONNECT_BILLING_RESULTS`
--

DROP TABLE IF EXISTS `INTERCONNECT_BILLING_RESULTS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `INTERCONNECT_BILLING_RESULTS` (
  `PARTNER` varchar(24) DEFAULT NULL,
  `SCENARIO` varchar(24) DEFAULT NULL,
  `BILLING_MONTH` varchar(24) DEFAULT NULL,
  `CDR_COUNT` int(11) DEFAULT NULL,
  `SECOND_COUNT` int(11) DEFAULT NULL,
  `COST` double DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `INTERCONNECT_BILLING_RESULTS`
--

LOCK TABLES `INTERCONNECT_BILLING_RESULTS` WRITE;
/*!40000 ALTER TABLE `INTERCONNECT_BILLING_RESULTS` DISABLE KEYS */;
/*!40000 ALTER TABLE `INTERCONNECT_BILLING_RESULTS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `NORMALISATION_MAP`
--

DROP TABLE IF EXISTS `NORMALISATION_MAP`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `NORMALISATION_MAP` (
  `MAP_GROUP` varchar(24) DEFAULT NULL,
  `NUMBER_MATCH` varchar(100) DEFAULT NULL,
  `REMOVE_PREFIX` varchar(24) DEFAULT NULL,
  `NEW_PREFIX` varchar(24) DEFAULT NULL,
  `RANK` int(11) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `NORMALISATION_MAP`
--

LOCK TABLES `NORMALISATION_MAP` WRITE;
/*!40000 ALTER TABLE `NORMALISATION_MAP` DISABLE KEYS */;
INSERT INTO `NORMALISATION_MAP` VALUES ('Default','^//+.*','//+','011',1),('Default','^011.*','','',2),('Default','^1264.*','','011',3),('Default','^536.*','','0111264',4),('Default','^537.*','','0111264',5),('Default','^538.*','','0111264',6),('Default','^539.*','','0111264',7),('Default','^543.*','','0111264',8),('Default','.*','','011',9);
/*!40000 ALTER TABLE `NORMALISATION_MAP` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PRICE_MODEL`
--

DROP TABLE IF EXISTS `PRICE_MODEL`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PRICE_MODEL` (
  `PRICE_MODEL` varchar(24) DEFAULT NULL,
  `STEP` varchar(24) DEFAULT NULL,
  `TIER_FROM` varchar(24) DEFAULT NULL,
  `TIER_TO` varchar(24) DEFAULT NULL,
  `BEAT` varchar(24) DEFAULT NULL,
  `FACTOR` varchar(24) DEFAULT NULL,
  `CHARGE_BASE` varchar(24) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PRICE_MODEL`
--

LOCK TABLES `PRICE_MODEL` WRITE;
/*!40000 ALTER TABLE `PRICE_MODEL` DISABLE KEYS */;
INSERT INTO `PRICE_MODEL` VALUES ('PM_0_800000','1','0','999999','1','0.8','60'),('PM_0_433333','1','0','999999','1','0.433333','60'),('PM_0_533333','1','0','999999','1','0.533333','60'),('PM_0_416667','1','0','999999','1','0.416667','60'),('PM_0_233333','1','0','999999','1','0.233333','60'),('PM_0_200000','1','0','999999','1','0.2','60'),('PM_1_850000','1','0','999999','1','1.85','60'),('PM_0_733333','1','0','999999','1','0.733333','60'),('PM_0_610000','1','0','999999','1','0.61','60'),('PM_15_0','1','0','999999','1','15','60'),('PM_0_130597','1','0','999999','1','0.130597','60'),('PM_0_149253','1','0','999999','1','0.149253','60'),('PM_0_0','1','0','999999','1','0','60');
/*!40000 ALTER TABLE `PRICE_MODEL` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `RUM_MAP`
--

DROP TABLE IF EXISTS `RUM_MAP`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `RUM_MAP` (
  `PRICE_GROUP` varchar(24) DEFAULT NULL,
  `PRICE_MODEL` varchar(24) DEFAULT NULL,
  `RUM` varchar(24) DEFAULT NULL,
  `RESOURCE` varchar(24) DEFAULT NULL,
  `RESOURCE_ID` int(11) DEFAULT NULL,
  `RUM_TYPE` varchar(24) DEFAULT NULL,
  `CONSUME_FLAG` int(11) DEFAULT NULL,
  `STEP` int(11) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `RUM_MAP`
--

LOCK TABLES `RUM_MAP` WRITE;
/*!40000 ALTER TABLE `RUM_MAP` DISABLE KEYS */;
/*!40000 ALTER TABLE `RUM_MAP` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SPLITTING_MAP`
--

DROP TABLE IF EXISTS `SPLITTING_MAP`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SPLITTING_MAP` (
  `MAP_GROUP` varchar(24) DEFAULT NULL,
  `CALL_CASE` varchar(24) DEFAULT NULL,
  `ZONE_MODEL` varchar(24) DEFAULT NULL,
  `ERR_DESC` varchar(64) DEFAULT NULL,
  `OUTPUT_LIST` varchar(64) DEFAULT NULL,
  `RANK` smallint(6) NOT NULL DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SPLITTING_MAP`
--

LOCK TABLES `SPLITTING_MAP` WRITE;
/*!40000 ALTER TABLE `SPLITTING_MAP` DISABLE KEYS */;
INSERT INTO `SPLITTING_MAP` VALUES ('Splitting','.*','.*','ERR_DUPLICATE_RECORD','Duplicate',1),('Splitting','.*','.*','ERR_CAUSE_FOR_TERM_NO_CALL','Filter',2),('Splitting','.*','.*','ERR_.*','Error',3),('Splitting','.*','A1/A2','','Weblinks',4),('Splitting','POC','.*','','InterconnectBilling,InterconnectReconciliation',5),('Splitting','.*','.*','','InterconnectReconciliation',6);
/*!40000 ALTER TABLE `SPLITTING_MAP` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TRUNK_IC_MAP`
--

DROP TABLE IF EXISTS `TRUNK_IC_MAP`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TRUNK_IC_MAP` (
  `MAP_GROUP` varchar(24) DEFAULT NULL,
  `TRUNK_IN` varchar(24) DEFAULT NULL,
  `TRUNK_OUT` varchar(24) DEFAULT NULL,
  `RATING_SCENARIO` varchar(24) DEFAULT NULL,
  `RANK` int(11) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TRUNK_IC_MAP`
--

LOCK TABLES `TRUNK_IC_MAP` WRITE;
/*!40000 ALTER TABLE `TRUNK_IC_MAP` DISABLE KEYS */;
INSERT INTO `TRUNK_IC_MAP` VALUES ('TrunkICMap','30','30','A1/A2',1),('TrunkICMap','30','5','A3/A6/A7',2),('TrunkICMap','30','2','A4/A5',3),('TrunkICMap','30','21','B1',4),('TrunkICMap','30','25','B2',5),('TrunkICMap','30','23','B3',6),('TrunkICMap','20','30','B4',7),('TrunkICMap','24','30','B5',8),('TrunkICMap','22','30','B6',9),('TrunkICMap','30','10','C1/C2',10),('TrunkICMap','10','30','C3/C4',11),('TrunkICMap','22','5','B6',9),('TrunkICMap','24','5','B5',8),('TrunkICMap','20','5','B4',7);
/*!40000 ALTER TABLE `TRUNK_IC_MAP` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TRUNK_MAP`
--

DROP TABLE IF EXISTS `TRUNK_MAP`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TRUNK_MAP` (
  `MAP_GROUP` varchar(24) DEFAULT NULL,
  `TRUNK_ID` varchar(24) DEFAULT NULL,
  `TRUNK_NAME` varchar(24) DEFAULT NULL,
  `TRUNK_OPERATOR` varchar(24) DEFAULT NULL,
  `RANK` int(11) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TRUNK_MAP`
--

LOCK TABLES `TRUNK_MAP` WRITE;
/*!40000 ALTER TABLE `TRUNK_MAP` DISABLE KEYS */;
INSERT INTO `TRUNK_MAP` VALUES ('TrunkMap','2','Trunk_AAS','Weblinks',1),('TrunkMap','30','BSC_0','Weblinks',11),('TrunkMap','25','Trunk_CW_MOBTRANSIT_OG','CW',10),('TrunkMap','24','Trunk_CW_MOBTRANSIT_IC','CW',9),('TrunkMap','23','Trunk_CW_INTL_OG','CW',8),('TrunkMap','22','Trunk_CW_INTL_IC','CW',7),('TrunkMap','21','Trunk_CW_PSTN_OG','CW',6),('TrunkMap','20','Trunk_CW_PSTN_IC','CW',5),('TrunkMap','10','Trunk_UTS','UTS',4),('TrunkMap','5','VoiceMail','Weblinks',3),('TrunkMap','3','Trunk_AAS','Weblinks',2),('TrunkMap','.*','Trunk_Default','Unknown',12);
/*!40000 ALTER TABLE `TRUNK_MAP` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ZONE_MODEL`
--

DROP TABLE IF EXISTS `ZONE_MODEL`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ZONE_MODEL` (
  `MAP_GROUP` varchar(24) DEFAULT NULL,
  `PREFIX_IN` varchar(24) DEFAULT NULL,
  `RESULT_OUT` varchar(24) DEFAULT NULL,
  `DESCRIPTION_OUT` varchar(100) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ZONE_MODEL`
--

LOCK TABLES `ZONE_MODEL` WRITE;
/*!40000 ALTER TABLE `ZONE_MODEL` DISABLE KEYS */;
INSERT INTO `ZONE_MODEL` VALUES ('B3','011','PM_0_800000','Rest of the world'),('B3','0111','PM_0_433333','USA & Canada'),('B3','0111242','PM_0_533333','Bahamas'),('B3','0111246','PM_0_416667','Barbados'),('B3','0111264222','PM_0_233333','Anguilla - Other'),('B3','0111264235','PM_0_416667','Anguilla- Mobile'),('B3','0111264297','PM_0_233333','Anguilla - Other'),('B3','0111264461','PM_0_233333','Anguilla - Other'),('B3','0111264462','PM_0_233333','Anguilla - Other'),('B3','0111264476','PM_0_416667','Anguilla- Mobile'),('B3','0111264497','PM_0_233333','Anguilla - Other'),('B3','0111264498','PM_0_233333','Anguilla - Other'),('B3','0111264536','PM_0_200000','Anguilla - Weblinks'),('B3','0111264537','PM_0_200000','Anguilla - Weblinks'),('B3','0111264538','PM_0_200000','Anguilla - Weblinks'),('B3','0111264539','PM_0_200000','Anguilla - Weblinks'),('B3','0111264543','PM_0_200000','Anguilla - Weblinks'),('B3','0111264581','PM_0_416667','Anguilla- Mobile'),('B3','0111264582','PM_0_416667','Anguilla- Mobile'),('B3','0111264583','PM_0_416667','Anguilla- Mobile'),('B3','0111264584','PM_0_416667','Anguilla- Mobile'),('B3','0111264729','PM_0_416667','Anguilla- Mobile'),('B3','0111264772','PM_0_416667','Anguilla- Mobile'),('B3','0111268','PM_0_416667','Antigua & Barbuda'),('B3','0111284','PM_0_416667','Britsh Virgin Islands'),('B3','0111340','PM_0_533333','U.S. Virgin Islands'),('B3','0111345','PM_0_533333','Cayman Islands'),('B3','0111441','PM_0_533333','Bermuda'),('B3','0111649','PM_0_533333','Turks & Caicos Islands'),('B3','0111664','PM_0_416667','Montserrat'),('B3','0111758','PM_0_416667','St. Lucia'),('B3','0111767','PM_0_416667','Dominica'),('B3','0111784','PM_0_533333','St. Vincent & Grenadines'),('B3','0111809','PM_0_416667','Dominican Republic'),('B3','0111868','PM_0_533333','Trinidad & Tobago'),('B3','0111869','PM_0_416667','St. Kitts & Nevis'),('B3','0111876','PM_0_533333','Jamaica'),('B3','011224','PM_1_850000','Guinea Bissau'),('B3','011239','PM_1_850000','Principe & Sao Tome'),('B3','011246','PM_1_850000','Diego Garcia'),('B3','011252','PM_1_850000','Somalia'),('B3','011264','PM_0_233333','Anguilla - Other'),('B3','011290','PM_1_850000','St Helena'),('B3','011297','PM_0_533333','Aruba'),('B3','011298','PM_0_733333','Faroe Islands'),('B3','01130','PM_0_733333','Greece'),('B3','01131','PM_0_733333','Netherlands'),('B3','01132','PM_0_733333','Belgium'),('B3','01133','PM_0_733333','France'),('B3','01134','PM_0_733333','Spain'),('B3','011351','PM_0_733333','Portugal'),('B3','011352','PM_0_733333','Luxembourg'),('B3','011353','PM_0_733333','Ireland'),('B3','011355','PM_0_733333','Albania'),('B3','011356','PM_0_733333','Malta'),('B3','011358','PM_0_733333','Finland'),('B3','011359','PM_0_733333','Bulgaria'),('B3','01136','PM_0_733333','Hungary'),('B3','011370','PM_0_733333','Lithuania'),('B3','011371','PM_0_733333','Latvia'),('B3','011372','PM_0_733333','Estonia'),('B3','011373','PM_0_733333','Moldova'),('B3','011374','PM_0_733333','Armenia'),('B3','011375','PM_0_733333','Belarus'),('B3','011376','PM_0_733333','Andorra'),('B3','011377','PM_0_733333','Monaco'),('B3','011378','PM_0_733333','San Marino'),('B3','011380','PM_0_733333','Ukraine'),('B3','011381','PM_0_733333','Yugoslavia'),('B3','011385','PM_0_733333','Croatia'),('B3','011386','PM_0_733333','Slovenia'),('B3','011387','PM_0_733333','Bosnia Herzegovina'),('B3','011389','PM_0_733333','Macedonia'),('B3','01139','PM_0_733333','Italy'),('B3','01140','PM_0_733333','Romania'),('B3','01141','PM_0_733333','Switzerland'),('B3','011420','PM_0_733333','Czech Republic'),('B3','011421','PM_0_733333','Slovakia'),('B3','011423','PM_1_850000','Liechtenstein'),('B3','01143','PM_0_733333','Austria'),('B3','01144','PM_0_433333','United Kingdom'),('B3','01145','PM_0_733333','Denmark'),('B3','01146','PM_0_733333','Sweden'),('B3','01147','PM_0_733333','Norway'),('B3','01148','PM_0_733333','Poland'),('B3','01149','PM_0_733333','Germany'),('B3','011500','PM_1_850000','Falkland Islands'),('B3','011506','PM_0_533333','Costa Rica'),('B3','011509','PM_0_533333','Haiti'),('B3','01153','PM_1_850000','Cuba'),('B3','011590','PM_0_533333','Guadeloupe'),('B3','011596','PM_0_533333','French Ant - Martinique'),('B3','011599','PM_0_416667','Netherlands Antilles'),('B3','01161','PM_0_433333','Australia'),('B3','011670','PM_1_850000','East Timor'),('B3','011674','PM_1_850000','Nauru'),('B3','011675','PM_1_850000','Papua New Guinea'),('B3','011677','PM_1_850000','Solomon Islands'),('B3','011678','PM_1_850000','Vanuatu'),('B3','011681','PM_1_850000','Wallis & Futuna'),('B3','011683','PM_1_850000','Niue Island'),('B3','011686','PM_1_850000','Kiribati'),('B3','011688','PM_1_850000','Tuvalu'),('B3','011690','PM_1_850000','Tokelau'),('B3','0117','PM_0_733333','Russia'),('B3','011850','PM_1_850000','Korea - North'),('B3','01186','PM_0_610000','China'),('B3','01187','PM_15_0','Satellite Phone'),('B3','011882','PM_15_0','Thyria'),('B3','01191','PM_0_610000','India'),('B3','01','PM_0_433333','USA & Canada'),('B3','01242','PM_0_533333','Bahamas'),('B3','01246','PM_0_416667','Barbados'),('B3','01264','PM_0_233333','Anguilla - Other'),('B3','01264222','PM_0_233333','Anguilla - Other'),('B3','01264235','PM_0_416667','Anguilla- Mobile'),('B3','01264297','PM_0_233333','Anguilla - Other'),('B3','01264461','PM_0_233333','Anguilla - Other'),('B3','01264462','PM_0_233333','Anguilla - Other'),('B3','01264476','PM_0_416667','Anguilla- Mobile'),('B3','01264497','PM_0_233333','Anguilla - Other'),('B3','01264498','PM_0_233333','Anguilla - Other'),('B3','01264536','PM_0_200000','Anguilla - Weblinks'),('B3','01264537','PM_0_200000','Anguilla - Weblinks'),('B3','01264538','PM_0_200000','Anguilla - Weblinks'),('B3','01264539','PM_0_200000','Anguilla - Weblinks'),('B3','01264543','PM_0_200000','Anguilla - Weblinks'),('B3','01264581','PM_0_416667','Anguilla- Mobile'),('B3','01264582','PM_0_416667','Anguilla- Mobile'),('B3','01264583','PM_0_416667','Anguilla- Mobile'),('B3','01264584','PM_0_416667','Anguilla- Mobile'),('B3','01264729','PM_0_416667','Anguilla- Mobile'),('B3','01264772','PM_0_416667','Anguilla- Mobile'),('B3','01268','PM_0_416667','Antigua & Barbuda'),('B3','01284','PM_0_416667','Britsh Virgin Islands'),('B3','01340','PM_0_533333','U.S. Virgin Islands'),('B3','01345','PM_0_533333','Cayman Islands'),('B3','01441','PM_0_533333','Bermuda'),('B3','01649','PM_0_533333','Turks & Caicos Islands'),('B3','01664','PM_0_416667','Montserrat'),('B3','01758','PM_0_416667','St. Lucia'),('B3','01767','PM_0_416667','Dominica'),('B3','01784','PM_0_533333','St. Vincent & Grenadines'),('B3','01809','PM_0_416667','Dominican Republic'),('B3','01868','PM_0_533333','Trinidad & Tobago'),('B3','01869','PM_0_416667','St. Kitts & Nevis'),('B3','01876','PM_0_533333','Jamaica'),('B3','0222','PM_0_233333','Anguilla - Other'),('B3','0235','PM_0_416667','Anguilla- Mobile'),('B3','0264222','PM_0_233333','Anguilla - Other'),('B3','0264235','PM_0_416667','Anguilla- Mobile'),('B3','0264297','PM_0_233333','Anguilla - Other'),('B3','0264461','PM_0_233333','Anguilla - Other'),('B3','0264462','PM_0_233333','Anguilla - Other'),('B3','0264476','PM_0_416667','Anguilla- Mobile'),('B3','0264497','PM_0_233333','Anguilla - Other'),('B3','0264498','PM_0_233333','Anguilla - Other'),('B3','0264536','PM_0_200000','Anguilla - Weblinks'),('B3','0264537','PM_0_200000','Anguilla - Weblinks'),('B3','0264538','PM_0_200000','Anguilla - Weblinks'),('B3','0264539','PM_0_200000','Anguilla - Weblinks'),('B3','0264543','PM_0_200000','Anguilla - Weblinks'),('B3','0264581','PM_0_416667','Anguilla- Mobile'),('B3','0264582','PM_0_416667','Anguilla- Mobile'),('B3','0264583','PM_0_416667','Anguilla- Mobile'),('B3','0264584','PM_0_416667','Anguilla- Mobile'),('B3','0264729','PM_0_416667','Anguilla- Mobile'),('B3','0264772','PM_0_416667','Anguilla- Mobile'),('B3','0297','PM_0_233333','Anguilla - Other'),('B3','0461','PM_0_233333','Anguilla - Other'),('B3','0462','PM_0_233333','Anguilla - Other'),('B3','0476','PM_0_416667','Anguilla- Mobile'),('B3','0497','PM_0_233333','Anguilla - Other'),('B3','0498','PM_0_233333','Anguilla - Other'),('B3','0536','PM_0_200000','Anguilla - Weblinks'),('B3','0537','PM_0_200000','Anguilla - Weblinks'),('B3','0538','PM_0_200000','Anguilla - Weblinks'),('B3','0539','PM_0_200000','Anguilla - Weblinks'),('B3','0543','PM_0_200000','Anguilla - Weblinks'),('B3','0581','PM_0_416667','Anguilla- Mobile'),('B3','0582','PM_0_416667','Anguilla- Mobile'),('B3','0583','PM_0_416667','Anguilla- Mobile'),('B3','0584','PM_0_416667','Anguilla- Mobile'),('B3','0729','PM_0_416667','Anguilla- Mobile'),('B3','0772','PM_0_416667','Anguilla- Mobile'),('B1','0','PM_0_130597','To C&W For PSTN'),('B2','0','PM_0_130597','To C&W For Mobile'),('B4','0','PM_0_130597','From PSTN For Weblinks Termination'),('B5','0','PM_0_130597','From Mobile For Weblinks Termination'),('B6','0','PM_0_130597','Call Transit Via C&W For Weblinks Termination'),('B4_OLD','0','PM_0_149253','From PSTN For Weblinks Termination'),('B5_OLD','0','PM_0_149253','From Mobile For Weblinks Termination'),('B6_OLD','0','PM_0_149253','Call Transit Via C&W For Weblinks Termination'),('A1/A2','0','PM_0_0','Weblinks Mobile'),('A3/A6/A7','0','PM_0_0','Weblinks Mobile'),('A4/A5','0','PM_0_0','Weblinks Mobile'),('C1/C2','0','PM_0_0','To UTS'),('C3/C4','0','PM_0_0','From UTS');
/*!40000 ALTER TABLE `ZONE_MODEL` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ZONE_MODEL_VALIDITY`
--

DROP TABLE IF EXISTS `ZONE_MODEL_VALIDITY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ZONE_MODEL_VALIDITY` (
  `MAP_GROUP` varchar(24) DEFAULT NULL,
  `ZONE_MODEL_IN` varchar(24) DEFAULT NULL,
  `VALIDITY_FROM` varchar(24) DEFAULT NULL,
  `VALIDITY_TO` varchar(24) DEFAULT NULL,
  `NEW_ZONE_MODEL_OUT` varchar(24) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ZONE_MODEL_VALIDITY`
--

LOCK TABLES `ZONE_MODEL_VALIDITY` WRITE;
/*!40000 ALTER TABLE `ZONE_MODEL_VALIDITY` DISABLE KEYS */;
INSERT INTO `ZONE_MODEL_VALIDITY` VALUES ('Default','B4','0','1207008000','B4_OLD'),('Default','B5','0','1207008000','B5_OLD'),('Default','B6','0','1207008000','B6_OLD');
/*!40000 ALTER TABLE `ZONE_MODEL_VALIDITY` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2012-07-29  9:43:47
