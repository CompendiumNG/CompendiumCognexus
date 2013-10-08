-- MySQL dump 10.13  Distrib 5.1.49, for debian-linux-gnu (i486)
--
-- Host: localhost    Database: mysql_test_01_1296668217246
-- ------------------------------------------------------
-- Server version	5.1.49-3-log

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
-- Table structure for table `Audit`
--

DROP TABLE IF EXISTS `Audit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Audit` (
  `AuditID` varchar(50) NOT NULL,
  `Author` varchar(50) NOT NULL,
  `ItemID` varchar(50) NOT NULL,
  `AuditDate` double NOT NULL,
  `Category` varchar(50) NOT NULL,
  `Action` int(11) NOT NULL,
  `Data` longtext,
  PRIMARY KEY (`AuditID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Audit`
--

LOCK TABLES `Audit` WRITE;
/*!40000 ALTER TABLE `Audit` DISABLE KEYS */;
/*!40000 ALTER TABLE `Audit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Clone`
--

DROP TABLE IF EXISTS `Clone`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Clone` (
  `ParentNodeID` varchar(50) NOT NULL,
  `ChildNodeID` varchar(50) NOT NULL,
  PRIMARY KEY (`ParentNodeID`,`ChildNodeID`),
  KEY `Clone_ChildNodeID_Ind` (`ChildNodeID`),
  CONSTRAINT `FK_Clone_1` FOREIGN KEY (`ChildNodeID`) REFERENCES `Node` (`NodeID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Clone`
--

LOCK TABLES `Clone` WRITE;
/*!40000 ALTER TABLE `Clone` DISABLE KEYS */;
/*!40000 ALTER TABLE `Clone` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Code`
--

DROP TABLE IF EXISTS `Code`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Code` (
  `CodeID` varchar(50) NOT NULL,
  `Author` varchar(50) NOT NULL,
  `CreationDate` double NOT NULL,
  `ModificationDate` double NOT NULL,
  `Name` varchar(50) NOT NULL,
  `Description` varchar(100) DEFAULT NULL,
  `Behavior` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`CodeID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Code`
--

LOCK TABLES `Code` WRITE;
/*!40000 ALTER TABLE `Code` DISABLE KEYS */;
INSERT INTO `Code` VALUES ('1282093118927906450859','Administrator',927906450859,927906450859,'openissue','No Description','No Behavior'),('1282093118927906458109','Administrator',927906458109,927906458109,'actionitem','No Description','No Behavior'),('1282093118927906463484','Administrator',927906463484,927906463484,'open','No Description','No Behavior'),('1282093118927906467468','Administrator',927906467468,927906467468,'closed','No Description','No Behavior'),('1282093118927906472156','Administrator',927906472156,927906472156,'task','No Description','No Behavior'),('1282093118927906476515','Administrator',927906476515,927906476515,'role','No Description','No Behavior'),('1282093118927906481859','Administrator',927906481859,927906481859,'object','No Description','No Behavior'),('1282093118927906488593','Administrator',927906488593,927906488593,'group','No Description','No Behavior'),('1282093118927906497046','Administrator',927906497046,927906497046,'communication','No Description','No Behavior'),('1282093118927906501421','Administrator',927906501421,927906501421,'resource','No Description','No Behavior'),('1282093118927906526218','Administrator',927906526218,927906526218,'knowledge','No Description','No Behavior'),('1282093118927906531500','Administrator',927906531500,927906531500,'location','No Description','No Behavior'),('1282093118927906547921','Administrator',927906547921,927906547921,'requirement','No Description','No Behavior'),('1282093118927906554437','Administrator',927906554437,927906554437,'problem','No Description','No Behavior'),('1282093118927906567250','Administrator',927906567250,927906567250,'opportunity','No Description','No Behavior'),('19216811001221456237376','Administrator',1221456237376,1221456237376,'Jeff Conklin','No Description','No Behavior'),('19216811001221457175564','Jeff Conklin',1221457175564,1221457175564,'Mark Trexler','No Description','No Behavior'),('19216811001221457224157','Jeff Conklin',1221457224157,1221457224157,'Kyle Silon','No Description','No Behavior'),('19216811001221457267392','Jeff Conklin',1221457267392,1221457267392,'Kate Thomsen','No Description','No Behavior'),('19216811001221457295735','Jeff Conklin',1221457295735,1221457295735,'Kate Elliott','No Description','No Behavior'),('19216811001221457324532','Jeff Conklin',1221457324532,1221457324532,'Yuri Poudayel','No Description','No Behavior'),('19216811001221457371782','Jeff Conklin',1221457371782,1221457371782,'Rebecca Smith','No Description','No Behavior'),('19216811001221457395407','Jeff Conklin',1221457395407,1221457395407,'Sarah Nunnery','No Description','No Behavior'),('19216811001221457428142','Jeff Conklin',1221457428142,1221457428142,'Matt Ehrman','No Description','No Behavior'),('19216811001221457451282','Jeff Conklin',1221457451282,1221457451282,'Molly Hatfield','No Description','No Behavior'),('19216811001221457478001','Jeff Conklin',1221457478001,1221457478001,'Sam Stevenson','No Description','No Behavior'),('19216811001221457500735','Jeff Conklin',1221457500735,1221457500735,'Peter Kelly','No Description','No Behavior'),('19216811001261426627975','Jeff Conklin',1261426627975,1261426627975,'Michael Knowles','No Description','No Behavior'),('19216811011262027443924','Michael Knowles',1262027443924,1262027443924,'AR Rafiq','No Description','No Behavior'),('19216811011262036486603','Jeff Conklin',1259705518434,1259705518434,'Concern','No Description','No Behavior'),('19216811051290214227050','Jeff Conklin',1290214227050,1290214227050,'Matt Stucky','No Description','No Behavior'),('19216811051290215635644','Jeff Conklin',1290215635644,1290215635644,'KC Burgess Yakemovic','No Description','No Behavior'),('19216811051290407861915','Jeff Conklin',1289609171123,1289609171123,'Sync-map','No Description','No Behavior'),('19216811151295665915166','Jeff Conklin',1295665915166,1295665915166,'JC2','No Description','No Behavior');
/*!40000 ALTER TABLE `Code` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `CodeGroup`
--

DROP TABLE IF EXISTS `CodeGroup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CodeGroup` (
  `CodeGroupID` varchar(50) NOT NULL,
  `Author` varchar(50) NOT NULL,
  `Name` varchar(100) NOT NULL,
  `Description` varchar(255) DEFAULT NULL,
  `CreationDate` double NOT NULL,
  `ModificationDate` double NOT NULL,
  PRIMARY KEY (`CodeGroupID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `CodeGroup`
--

LOCK TABLES `CodeGroup` WRITE;
/*!40000 ALTER TABLE `CodeGroup` DISABLE KEYS */;
/*!40000 ALTER TABLE `CodeGroup` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Connections`
--

DROP TABLE IF EXISTS `Connections`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Connections` (
  `UserID` varchar(50) NOT NULL,
  `Profile` varchar(255) NOT NULL,
  `Type` int(11) NOT NULL,
  `Server` varchar(255) NOT NULL,
  `Login` varchar(255) NOT NULL,
  `Password` varchar(255) NOT NULL,
  `Name` varchar(255) DEFAULT NULL,
  `Port` int(11) DEFAULT NULL,
  `Resource` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`UserID`,`Profile`,`Type`),
  CONSTRAINT `FK_Connection_1` FOREIGN KEY (`UserID`) REFERENCES `Users` (`UserID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Connections`
--

LOCK TABLES `Connections` WRITE;
/*!40000 ALTER TABLE `Connections` DISABLE KEYS */;
/*!40000 ALTER TABLE `Connections` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ExtendedNodeType`
--

DROP TABLE IF EXISTS `ExtendedNodeType`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ExtendedNodeType` (
  `ExtendedNodeTypeID` varchar(50) NOT NULL,
  `Author` varchar(50) NOT NULL,
  `CreationDate` double NOT NULL,
  `ModificationDate` double NOT NULL,
  `Name` varchar(50) DEFAULT NULL,
  `Description` varchar(100) DEFAULT NULL,
  `BaseNodeType` int(11) NOT NULL,
  `Icon` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`ExtendedNodeTypeID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ExtendedNodeType`
--

LOCK TABLES `ExtendedNodeType` WRITE;
/*!40000 ALTER TABLE `ExtendedNodeType` DISABLE KEYS */;
/*!40000 ALTER TABLE `ExtendedNodeType` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ExtendedTypeCode`
--

DROP TABLE IF EXISTS `ExtendedTypeCode`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ExtendedTypeCode` (
  `ExtendedNodeTypeID` varchar(50) NOT NULL,
  `CodeID` varchar(50) NOT NULL,
  PRIMARY KEY (`ExtendedNodeTypeID`,`CodeID`),
  KEY `ExtendedCode_CodeID_Ind` (`CodeID`),
  CONSTRAINT `FK_ExtendedCode_1` FOREIGN KEY (`ExtendedNodeTypeID`) REFERENCES `ExtendedNodeType` (`ExtendedNodeTypeID`) ON DELETE CASCADE,
  CONSTRAINT `FK_ExtendedCode_2` FOREIGN KEY (`CodeID`) REFERENCES `Code` (`CodeID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ExtendedTypeCode`
--

LOCK TABLES `ExtendedTypeCode` WRITE;
/*!40000 ALTER TABLE `ExtendedTypeCode` DISABLE KEYS */;
/*!40000 ALTER TABLE `ExtendedTypeCode` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Favorite`
--

DROP TABLE IF EXISTS `Favorite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Favorite` (
  `UserID` varchar(50) NOT NULL,
  `NodeID` varchar(50) NOT NULL,
  `Label` longtext NOT NULL,
  `NodeType` int(11) NOT NULL,
  `CreationDate` double NOT NULL,
  `ModificationDate` double NOT NULL,
  `ViewID` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`UserID`,`NodeID`),
  KEY `Favorite_NodeID_Ind` (`NodeID`),
  KEY `FK_Favorite_3` (`ViewID`),
  CONSTRAINT `FK_Favorite_1` FOREIGN KEY (`UserID`) REFERENCES `Users` (`UserID`) ON DELETE CASCADE,
  CONSTRAINT `FK_Favorite_2` FOREIGN KEY (`NodeID`) REFERENCES `Node` (`NodeID`) ON DELETE CASCADE,
  CONSTRAINT `FK_Favorite_3` FOREIGN KEY (`ViewID`) REFERENCES `Node` (`NodeID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Favorite`
--

LOCK TABLES `Favorite` WRITE;
/*!40000 ALTER TABLE `Favorite` DISABLE KEYS */;
/*!40000 ALTER TABLE `Favorite` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `GroupCode`
--

DROP TABLE IF EXISTS `GroupCode`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `GroupCode` (
  `CodeID` varchar(50) NOT NULL,
  `CodeGroupID` varchar(50) NOT NULL,
  `Author` varchar(50) NOT NULL,
  `CreationDate` double NOT NULL,
  `ModificationDate` double NOT NULL,
  PRIMARY KEY (`CodeID`,`CodeGroupID`),
  KEY `GroupCode_CodeGroupID_Ind` (`CodeGroupID`),
  CONSTRAINT `FK_GroupCode_1` FOREIGN KEY (`CodeID`) REFERENCES `Code` (`CodeID`) ON DELETE CASCADE,
  CONSTRAINT `FK_GroupCode_2` FOREIGN KEY (`CodeGroupID`) REFERENCES `CodeGroup` (`CodeGroupID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `GroupCode`
--

LOCK TABLES `GroupCode` WRITE;
/*!40000 ALTER TABLE `GroupCode` DISABLE KEYS */;
/*!40000 ALTER TABLE `GroupCode` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `GroupUser`
--

DROP TABLE IF EXISTS `GroupUser`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `GroupUser` (
  `UserID` varchar(50) NOT NULL,
  `GroupID` varchar(50) NOT NULL,
  PRIMARY KEY (`UserID`,`GroupID`),
  KEY `GroupUser_GroupID_Ind` (`GroupID`),
  KEY `UserGroup_UserID_Ind` (`UserID`),
  CONSTRAINT `FK_GroupUser_1` FOREIGN KEY (`UserID`) REFERENCES `Users` (`UserID`) ON DELETE CASCADE,
  CONSTRAINT `FK_GroupUser_2` FOREIGN KEY (`GroupID`) REFERENCES `UserGroup` (`GroupID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `GroupUser`
--

LOCK TABLES `GroupUser` WRITE;
/*!40000 ALTER TABLE `GroupUser` DISABLE KEYS */;
/*!40000 ALTER TABLE `GroupUser` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Link`
--

DROP TABLE IF EXISTS `Link`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Link` (
  `LinkID` varchar(50) NOT NULL,
  `Author` varchar(50) NOT NULL,
  `CreationDate` double NOT NULL,
  `ModificationDate` double NOT NULL,
  `LinkType` varchar(50) NOT NULL,
  `OriginalID` varchar(50) DEFAULT NULL,
  `FromNode` varchar(50) NOT NULL,
  `ToNode` varchar(50) NOT NULL,
  `ViewID` varchar(50) NOT NULL DEFAULT '0',
  `Label` text,
  `Arrow` int(11) NOT NULL,
  `CurrentStatus` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`LinkID`),
  KEY `Link_FromNode_Ind` (`FromNode`),
  KEY `Link_ToNode_Ind` (`ToNode`),
  CONSTRAINT `FK_Link_1` FOREIGN KEY (`FromNode`) REFERENCES `Node` (`NodeID`) ON DELETE CASCADE,
  CONSTRAINT `FK_Link_2` FOREIGN KEY (`ToNode`) REFERENCES `Node` (`NodeID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Link`
--

LOCK TABLES `Link` WRITE;
/*!40000 ALTER TABLE `Link` DISABLE KEYS */;
INSERT INTO `Link` VALUES ('19216801021293659489048','Matt Stucky',1293659489048,1293659494726,'39','','19216801021293659473760','19216801021293659482293','0','link one',1,0),('19216801021293660301980','Matt Stucky',1293660301980,1293660301980,'40','','19216811051290449861277','19216801021293659482293','0','',1,0),('19216801021293723978463','Matt Stucky',1293723978463,1293723986702,'39','','19216801021293723117230','19216801021293723151417','0','created in mysql',1,0),('19216801021293816566557','Matt Stucky',1293816566557,1293816566557,'39','','19216801021293815887822','19216801021293815897428','0','',1,0),('19216801021293816592873','Matt Stucky',1293816592873,1293816592873,'39','','19216801021293816174110','19216801021293816166833','0','',1,0),('19216801021294083931502','Matt Stucky',1294083931502,1294083931502,'39','','19216801021294083923754','19216811041294080746686','0','',1,0),('19216801021294175722119','Matt Stucky',1294175722119,1294175722119,'39','','19216801021294175716261','19216801021294175304243','0','',1,0),('19216801021294175758501','Matt Stucky',1294175758501,1294175758501,'39','','19216801021294175561047','19216801021294175750994','0','',1,0),('19216811041294080767358','Jeff Conklin',1294080767358,1294080846139,'39','','19216811041294080746686','19216811041294080692999','0',' link ',1,0),('19216811041294084697014','Jeff Conklin',1294084697014,1294084697014,'39','','19216811041294084677280','19216811041294080746686','0','',1,0),('19216811041294084750342','Jeff Conklin',1294084750342,1294084750342,'39','','19216811041294084708467','19216801021293816166833','0','',1,0),('19216811041294084771217','Jeff Conklin',1294084771217,1294084771217,'39','','19216811041294084708467','19216801021293816174110','0','',1,0),('19216811041295376523179','Jeff Conklin',1295376523194,1295376523194,'40','','19216811051290449861277','19216811051290449840824','0','',1,0),('19216811041295552611651','Jeff Conklin',1295552611651,1295552611651,'39','','19216811041295552606761','19216811041295552592730','0','',1,0),('19216811041295637758149','Jeff Conklin',1295637758149,1295637758149,'39','','19216811041295637746742','19216811041295552606761','0','',1,0),('19216811041295638292242','Jeff Conklin',1295638292242,1295638292242,'39','','19216811041295638284524','19216811041295638183383','0','',1,0),('19216811041295638310930','Jeff Conklin',1295638310930,1295638310930,'39','','19216811041295638307617','19216811041295638284524','0','',1,0),('19216811041295638312180','Jeff Conklin',1295638312180,1295638312180,'39','','19216811041295638237664','19216811041295638307617','0','',1,0),('19216811041295638313477','Jeff Conklin',1295638313477,1295638313477,'39','','19216811041295638248086','19216811041295638307617','0','',1,0),('19216811041296067703442','Jeff Conklin',1296067703442,1296067703442,'39','','19216811041296067689223','19216811041296067593583','0','',1,0),('19216811041296068276458','Jeff Conklin',1296068276458,1296068276458,'39','','19216811041296068260911','19216811041296067689223','0','',1,0),('19216811051290449963418','Jeff Conklin',1290449963418,1290449963418,'39','','19216811051290449859277','19216811051290449825449','0','',1,0),('19216811051290449964293','Jeff Conklin',1290449964293,1290449964293,'41','','19216811051290449836746','19216811051290449840824','0','',1,0),('19216811051290449964574','Jeff Conklin',1290449964574,1290449964574,'41','','19216811051290449832261','19216811051290449840824','0','',1,0),('19216811051290449965168','Jeff Conklin',1290449965168,1290449965168,'39','','19216811051290449840824','19216811051290449852589','0','',1,0),('19216811051290449966355','Jeff Conklin',1290449966355,1290449966355,'39','','19216811051290449844699','19216811051290449840824','0','',1,0),('19216811051290449967246','Jeff Conklin',1290449967246,1290449967246,'39','','19216811051290449837996','19216811051290449845980','0','',1,0),('19216811051290449968214','Jeff Conklin',1290449968214,1290449968214,'39','','19216811051290449825449','19216811051290449844699','0','',1,0),('19216811051290449968793','Jeff Conklin',1290449968793,1290449968793,'41','','19216811051290449829793','19216811051290449840824','0','',1,0),('19216811051290449969996','Jeff Conklin',1290449969996,1290449969996,'39','','19216811051290449834261','19216811051290449844699','0','',1,0),('19216811051290449970871','Jeff Conklin',1290449970871,1290449970871,'41','','19216811051290449845980','19216811051290449829793','0','',1,0),('19216811051290449972027','Jeff Conklin',1290449972027,1290449972027,'41','','19216811051290449871839','19216811051290449870339','0','',1,0),('19216811051290449972308','Jeff Conklin',1290449972308,1290449972308,'39','','19216811051290449876355','19216811051290449875105','0','',1,0),('19216811051290449972605','Jeff Conklin',1290449972605,1290449972605,'39','','19216811051290449870339','19216811051290449852589','0','',1,0),('19216811051290449972886','Jeff Conklin',1290449972886,1290449972886,'40','','19216811051290449875105','19216811051290449870339','0','',1,0),('19216811051290449973183','Jeff Conklin',1290449973183,1290449973183,'40','','19216811051290449873136','19216811051290449870339','0','',1,0),('19216811091296068463118','JC2',1296068463118,1296068463118,'39','','19216811091296068450602','19216811041296067689223','0','',1,0),('192168121296520469218','Jeff Conklin',1296520469218,1296520469218,'39','','192168121296520359109','192168121296520321296','0','',1,0);
/*!40000 ALTER TABLE `Link` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MediaIndex`
--

DROP TABLE IF EXISTS `MediaIndex`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MediaIndex` (
  `ViewID` varchar(50) NOT NULL,
  `NodeID` varchar(50) NOT NULL,
  `MeetingID` varchar(255) NOT NULL,
  `MediaIndex` double NOT NULL,
  `CreationDate` double NOT NULL,
  `ModificationDate` double NOT NULL,
  PRIMARY KEY (`ViewID`,`NodeID`,`MeetingID`),
  KEY `MediaIndex_NodeID_Ind` (`NodeID`),
  KEY `MediaIndex_MeetingID_Ind` (`MeetingID`),
  CONSTRAINT `FK_MediaIndex_1` FOREIGN KEY (`ViewID`) REFERENCES `Node` (`NodeID`) ON DELETE CASCADE,
  CONSTRAINT `FK_MediaIndex_2` FOREIGN KEY (`NodeID`) REFERENCES `Node` (`NodeID`) ON DELETE CASCADE,
  CONSTRAINT `FK_MediaIndex_3` FOREIGN KEY (`MeetingID`) REFERENCES `Meeting` (`MeetingID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MediaIndex`
--

LOCK TABLES `MediaIndex` WRITE;
/*!40000 ALTER TABLE `MediaIndex` DISABLE KEYS */;
/*!40000 ALTER TABLE `MediaIndex` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Meeting`
--

DROP TABLE IF EXISTS `Meeting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Meeting` (
  `MeetingID` varchar(255) NOT NULL,
  `MeetingMapID` varchar(50) NOT NULL,
  `MeetingName` varchar(255) DEFAULT NULL,
  `MeetingDate` double DEFAULT NULL,
  `CurrentStatus` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`MeetingID`),
  KEY `Meeting_MeetingMapID_Ind` (`MeetingMapID`),
  CONSTRAINT `FK_Meeting_1` FOREIGN KEY (`MeetingMapID`) REFERENCES `Node` (`NodeID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Meeting`
--

LOCK TABLES `Meeting` WRITE;
/*!40000 ALTER TABLE `Meeting` DISABLE KEYS */;
/*!40000 ALTER TABLE `Meeting` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Node`
--

DROP TABLE IF EXISTS `Node`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Node` (
  `NodeID` varchar(50) NOT NULL,
  `Author` varchar(50) NOT NULL,
  `CreationDate` double NOT NULL,
  `ModificationDate` double NOT NULL,
  `NodeType` int(11) NOT NULL,
  `OriginalID` varchar(255) DEFAULT NULL,
  `ExtendedNodeType` varchar(50) DEFAULT NULL,
  `Label` longtext,
  `Detail` longtext,
  `CurrentStatus` int(11) NOT NULL DEFAULT '0',
  `LastModAuthor` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`NodeID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Node`
--

LOCK TABLES `Node` WRITE;
/*!40000 ALTER TABLE `Node` DISABLE KEYS */;
INSERT INTO `Node` VALUES ('19216801021293659473760','Matt Stucky',1293659473760,1293722977765,4,'','','this one was on derby','',3,'Matt Stucky'),('19216801021293659482293','Matt Stucky',1293659482293,1293723008142,4,'','','this one was on mysql','',3,'Matt Stucky'),('19216801021293723117230','Matt Stucky',1293723117230,1293723123752,3,'','','new on Derby?','',3,'Matt Stucky'),('19216801021293723151417','Matt Stucky',1293723151417,1293723158981,4,'','','new on mysql!','',3,'Matt Stucky'),('19216801021293815887822','Matt Stucky',1293815887822,1293815894177,3,'','','created on mysql','',3,'Matt Stucky'),('19216801021293815897428','Matt Stucky',1293815897428,1293815903966,4,'','','yes, created on mysql','',3,'Matt Stucky'),('19216801021293816166833','Matt Stucky',1293816166833,1293816172002,3,'','','created on Derby','',3,'Matt Stucky'),('19216801021293816174110','Matt Stucky',1293816174110,1293816180248,4,'','','yes, created on derby','',3,'Matt Stucky'),('19216801021293816223977','Matt Stucky',1293816223977,1293816233326,4,'','','also created on mysq','',3,'Matt Stucky'),('19216801021294083923754','Matt Stucky',1294083923754,1294083929735,4,'','','i see you!  --matt','',3,'Matt Stucky'),('19216801021294175304243','Matt Stucky',1294175304243,1294175322273,3,'','','does this one show up?','created in derby, should xfer to mysql\n',3,'Matt Stucky'),('19216801021294175561047','Matt Stucky',1294175561047,1294175570604,4,'','','this is a mysql Node','',3,'Matt Stucky'),('19216801021294175716261','Matt Stucky',1294175716261,1294175723120,4,'','','link this one','',3,'Matt Stucky'),('19216801021294175750994','Matt Stucky',1294175750994,1294175755814,4,'','','another mysql node!','',3,'Matt Stucky'),('19216801021296417117782','Matt Stucky',1296417117782,1296668767721,3,'','','Matt Test Beta','pizza pizza',0,'Matt Stucky'),('19216811001221456236485','Jeff Conklin',1221456236470,1221456236470,2,'','','Home Window','Home Window of Jeff Conklin',0,''),('19216811001221456237423','Jeff Conklin',1221456236470,1221456237829,1,'','','Inbox','Inbox of Jeff Conklin',0,'Jeff Conklin'),('19216811001221456251329','Administrator',1221456251329,1221456259689,2,'','','The Commons','',0,'Administrator'),('19216811001261426627068','Michael Knowles',1261426627053,1261426627053,2,'','','Home Window','Home Window of Michael Knowles',0,''),('19216811001261426628037','Michael Knowles',1261426627053,1261426628459,1,'','','Inbox','Inbox of Michael Knowles',0,'Michael Knowles'),('19216811011262027443745','AR Rafiq',1262027443744,1262027443744,2,'','','Home Window','Home Window of AR Rafiq',0,''),('19216811011262027443984','AR Rafiq',1262027443744,1262027444404,1,'','','Inbox','Inbox of AR Rafiq',0,'AR Rafiq'),('19216811041294080692999','Jeff Conklin',1294080692999,1294080713155,3,'','','Created by Jeff on MySQL','',3,'Jeff Conklin'),('19216811041294080722561','Jeff Conklin',1294080722561,1294080745217,10,'','','This no','',3,'Jeff Conklin'),('19216811041294080746686','Jeff Conklin',1294080746686,1294084519811,4,'','','Also by Jeff on M','This is very exciting!\nAnd this is a second comment that Jeff added to this node at noon pacific on 1/3.',3,'Jeff Conklin'),('19216811041294084677280','Jeff Conklin',1294084677280,1294084698092,10,'','','Jeff moved this node','',3,'Jeff Conklin'),('19216811041294084708467','Jeff Conklin',1294084708467,1294084767577,10,'','','Jeff deleted Matt s  yes, created on derby  Idea node here','that was attached to this question and the idea.',3,'Jeff Conklin'),('19216811041295376527523','Jeff Conklin',1295376527523,1295376561226,10,'','','Changes made by Jeff on 1/18','',3,'Jeff Conklin'),('19216811041295552592730','Jeff Conklin',1295552592730,1295552604058,10,'','','New node','',3,'Jeff Conklin'),('19216811041295552606761','Jeff Conklin',1295552606761,1295552611980,2,'','','New Map','',3,'Jeff Conklin'),('19216811041295552951745','Jeff Conklin',1295552951745,1295552975714,10,'','','Home Window','',3,'Jeff Conklin'),('19216811041295637746742','Jeff Conklin',1295637746742,1295637758992,10,'','','Team meeting test','',3,'Jeff Conklin'),('19216811041295637787008','Jeff Conklin',1295637787008,1295637787024,9,'','','GO TO: Team meeting test','From: Jeff Conklin\nMessage: body of Cmail message\n-------\nLink to node in ( New Map )\n\n',0,'Jeff Conklin'),('19216811041295638183383','Jeff Conklin',1295638183383,1295638200617,3,'','','What would be an adequate error report for Alpha testing?','',3,'Jeff Conklin'),('19216811041295638237664','Jeff Conklin',1295638237664,1295638247227,4,'','','Text of the console','',3,'Jeff Conklin'),('19216811041295638248086','Jeff Conklin',1295638248086,1295638262930,4,'','','Sync log','',3,'Jeff Conklin'),('19216811041295638284524','Jeff Conklin',1295638284524,1295638292883,4,'','','Screen cast ','',3,'Jeff Conklin'),('19216811041295638307617','Jeff Conklin',1295638307617,1295638314961,3,'','','Include?','',3,'Jeff Conklin'),('19216811041296067593583','Jeff Conklin',1296067593583,1296067609395,10,'','','Test: time = 10:46am','',0,'Jeff Conklin'),('19216811041296067679614','Jeff Conklin',1296067679614,1296067679614,4,'','','','',3,'Jeff Conklin'),('19216811041296067689223','Jeff Conklin',1296067689223,1296067706145,3,'','','How do we know this node got synced?','',0,'Jeff Conklin'),('19216811041296068260911','Jeff Conklin',1296068260911,1296068279145,4,'','','Because it is now in MySQL','',0,'Jeff Conklin'),('19216811051289608833764','Jeff Conklin',1289608833764,1289608876404,4,'','','User Cases and test scripts derived from them','',0,'Jeff Conklin'),('19216811051290214225612','Matt Stucky',1290214225597,1290214225597,2,'','','Home Window','Home Window of Matt Stucky',0,''),('19216811051290214227175','Matt Stucky',1290214225597,1290214227753,1,'','','Inbox','Inbox of Matt Stucky',0,'Matt Stucky'),('19216811051290215634785','KC Burgess Yakemovic',1290215634769,1290215634769,2,'','','Home Window','Home Window of KC Burgess Yakemovic',0,''),('19216811051290215635738','KC Burgess Yakemovic',1290215634769,1290215636144,1,'','','Inbox','Inbox of KC Burgess Yakemovic',0,'KC Burgess Yakemovic'),('19216811051290449823105','Jeff Conklin',1289522034051,1290449825011,2,'','','Start with the existing XML export/import implementation and scale it up','',3,'Jeff Conklin'),('19216811051290449825449','Jeff Conklin',1289591748482,1290449826308,4,'','','Management of multiple automatic XML exports and imports','',3,'Jeff Conklin'),('19216811051290449829793','Jeff Conklin',1289522530942,1295376568929,7,'','','It s not clear how the XML mechanism would work in a slow connection situation XXX','',3,'Jeff Conklin'),('19216811051290449832261','Jeff Conklin',1289722016919,1290449833871,7,'','','Requires major extensions to the XML file format','We don t need to expand the XML inport/export system to handle every node and view property, because we already have a complete representation of those object properties, in the form of C s internal data structures for nodes and views.',3,'Jeff Conklin'),('19216811051290449834261','Jeff Conklin',1289522196364,1290449835152,4,'','','Detection of changed maps and nodes','',3,'Jeff Conklin'),('19216811051290449835543','Jeff Conklin',1289785616223,1290449836386,4,'','','[ms] i think svn would handle this problem fine','',3,'Jeff Conklin'),('19216811051290449836746','Jeff Conklin',1289785298841,1290449837621,7,'','','[ms] great fear of massive rework for very little return','',3,'Jeff Conklin'),('19216811051290449837996','Jeff Conklin',1289521719504,1290449840418,2,'','','Approach: Design for disconnected users and slow-connection users separately','',3,'Jeff Conklin'),('19216811051290449840824','Jeff Conklin',1289521943864,1290449841855,4,'','','Start with the existing XML export/import implementation and scale it up','',3,'Jeff Conklin'),('19216811051290449843449','Jeff Conklin',1289721420685,1290449844324,3,'','','What are node changes and view changes?','',3,'Jeff Conklin'),('19216811051290449844699','Jeff Conklin',1289522159458,1290449845589,3,'','','How would the XML mechanism need to be extended?','',3,'Jeff Conklin'),('19216811051290449845980','Jeff Conklin',1289719328248,1290449846839,7,'','','The slow connection scenario may not be crucial','',3,'Jeff Conklin'),('19216811051290449847230','Jeff Conklin',1289720524794,1290449848964,6,'','','Every possible change a user can make in a map must be captured in the export/import format','in order to assure that synchronization is complete.',3,'Jeff Conklin'),('19216811051290449852589','Jeff Conklin',1289520464114,1290449853605,3,'','','What broad approach should we take to addressing the problem?','Responses to this question are not necessarily mutually exclusive.',3,'Jeff Conklin'),('19216811051290449859277','Jeff Conklin',1289595121123,1290449860918,3,'','','How?','',3,'Jeff Conklin'),('19216811051290449861277','Jeff Conklin',1289522232958,1290449862136,6,'','','The existing  preseve node ID s  merge process is reasonable approach to merging maps, even though it does not detect or resolve conflicts','',3,'Jeff Conklin'),('19216811051290449870339','Jeff Conklin',1289520581004,1290449871464,4,'','','Design for disconnected users primarily, and try to accomodate slow-connection users if practical','',3,'Jeff Conklin'),('19216811051290449871839','Jeff Conklin',1289520835942,1290449872761,7,'','','Likely to force users to make a choice between on-line and off-line modes','',3,'Jeff Conklin'),('19216811051290449873136','Jeff Conklin',1289520739364,1290449874730,6,'','','Remote users can choose between using the slow connection and going into off-line mode','',3,'Jeff Conklin'),('19216811051290449875105','Jeff Conklin',1289520653864,1290449875980,6,'','','A design that works equally well for both may be much more complicated','',3,'Jeff Conklin'),('19216811051290449876355','Jeff Conklin',1289520719614,1290449877214,3,'','','Why?','',3,'Jeff Conklin'),('19216811091296068450602','JC2',1296068450618,1296068480399,4,'','','And this proves it','',0,'JC2'),('19216811091296520833803','JC2',1296520833803,1296521175421,2,'','','new stuff','19216811091296520833803',0,'Jeff Conklin'),('19216811091296522729178','Jeff Conklin',1296522729178,1296522747740,10,'','','test','',0,'Jeff Conklin'),('19216811091296522806990','Jeff Conklin',1296522806990,1296522806990,10,'','','','',0,'Jeff Conklin'),('19216811091296522981834','JC2',1296522981834,1296522992943,10,'','','JC2 test node','',0,'JC2'),('19216811151295665914588','Jeff Conklin',1295665914572,1295665914572,2,'','','Home Window','Home Window of JC2',0,''),('19216811151295665915228','Jeff Conklin',1295665914572,1295665916041,1,'','','Inbox','Inbox of JC2',0,'JC2'),('19216811151296004159587','JC2',1296004159587,1296004159587,10,'','','','',3,'JC2'),('192168121296520321296','Jeff Conklin',1296520321296,1296520352359,3,'','','A test on Monday, 1/31','192168121296520321296',0,'Jeff Conklin'),('192168121296520359109','Jeff Conklin',1296520359109,1296520466046,4,'','','For ease of finding nodes from the log files I am putting the node ID in the nodes detail','192168121296520359109\n\n(Note to self: add to Search ability to search on node ID.)',0,'Jeff Conklin'),('1921681641221420656546','Administrator',1221420656546,1221420686671,10,'','','Hi Jeff!','This is a node created by MLB in the HG01 database.',0,'Administrator'),('id_administrator_home','Administrator',916762292406,916762292406,2,'','0','Home Window','Home Window of Administrator',0,'Administrator'),('id_administrator_inbox','Administrator',1158582336554,1158582336616,1,'','','Inbox','Inbox of Administrator',0,'Administrator');
/*!40000 ALTER TABLE `Node` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `NodeCode`
--

DROP TABLE IF EXISTS `NodeCode`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `NodeCode` (
  `NodeID` varchar(50) NOT NULL,
  `CodeID` varchar(50) NOT NULL,
  PRIMARY KEY (`NodeID`,`CodeID`),
  KEY `NodeCode_CodeID_Ind` (`CodeID`),
  CONSTRAINT `FK_NodeCode_1` FOREIGN KEY (`NodeID`) REFERENCES `Node` (`NodeID`) ON DELETE CASCADE,
  CONSTRAINT `FK_NodeCode_2` FOREIGN KEY (`CodeID`) REFERENCES `Code` (`CodeID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `NodeCode`
--

LOCK TABLES `NodeCode` WRITE;
/*!40000 ALTER TABLE `NodeCode` DISABLE KEYS */;
INSERT INTO `NodeCode` VALUES ('19216811051290449823105','19216811051290407861915'),('19216811051290449837996','19216811051290407861915'),('192168121296520359109','19216811151295665915166');
/*!40000 ALTER TABLE `NodeCode` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `NodeDetail`
--

DROP TABLE IF EXISTS `NodeDetail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `NodeDetail` (
  `NodeID` varchar(50) NOT NULL,
  `Author` varchar(50) NOT NULL,
  `PageNo` int(11) NOT NULL,
  `CreationDate` double NOT NULL,
  `ModificationDate` double NOT NULL,
  `Detail` longtext,
  PRIMARY KEY (`NodeID`,`PageNo`),
  CONSTRAINT `FK_NodeDetail_1` FOREIGN KEY (`NodeID`) REFERENCES `Node` (`NodeID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `NodeDetail`
--

LOCK TABLES `NodeDetail` WRITE;
/*!40000 ALTER TABLE `NodeDetail` DISABLE KEYS */;
INSERT INTO `NodeDetail` VALUES ('19216801021296417117782','Matt Stucky',1,1296668767721,1296668767721,'pizza pizza'),('19216811041294080746686','Jeff Conklin',1,1294080746686,1294084519811,'This is very exciting!\nAnd this is a second comment that Jeff added to this node at noon pacific on 1/3.'),('19216811041294084708467','Jeff Conklin',1,1294084767577,1294084767577,'that was attached to this question and the idea.'),('19216811051290449823105','Jeff Conklin',1,1290449825011,1290449825011,''),('19216811051290449825449','Jeff Conklin',1,1290449826308,1290449826308,''),('19216811051290449829793','Jeff Conklin',1,1290449830668,1290449830668,''),('19216811051290449832261','Jeff Conklin',1,1290449833871,1290449833871,'We don\'t need to expand the XML inport/export system to handle every node and view property, because we already have a complete representation of those object properties, in the form of C\'s internal data structures for nodes and views.'),('19216811051290449834261','Jeff Conklin',1,1290449835152,1290449835152,''),('19216811051290449835543','Jeff Conklin',1,1290449836386,1290449836386,''),('19216811051290449836746','Jeff Conklin',1,1290449837621,1290449837621,''),('19216811051290449837996','Jeff Conklin',1,1290449840418,1290449840418,''),('19216811051290449840824','Jeff Conklin',1,1290449841855,1290449841855,''),('19216811051290449843449','Jeff Conklin',1,1290449844324,1290449844324,''),('19216811051290449844699','Jeff Conklin',1,1290449845589,1290449845589,''),('19216811051290449845980','Jeff Conklin',1,1290449846839,1290449846839,''),('19216811051290449847230','Jeff Conklin',1,1290449848964,1290449848964,'in order to assure that synchronization is complete.'),('19216811051290449852589','Jeff Conklin',1,1290449853605,1290449853605,'Responses to this question are not necessarily mutually exclusive.'),('19216811051290449859277','Jeff Conklin',1,1290449860918,1290449860918,''),('19216811051290449861277','Jeff Conklin',1,1290449862136,1290449862136,''),('19216811051290449870339','Jeff Conklin',1,1290449871464,1290449871464,''),('19216811051290449871839','Jeff Conklin',1,1290449872761,1290449872761,''),('19216811051290449873136','Jeff Conklin',1,1290449874730,1290449874730,''),('19216811051290449875105','Jeff Conklin',1,1290449875980,1290449875980,''),('19216811051290449876355','Jeff Conklin',1,1290449877214,1290449877214,''),('19216811091296520833803','Jeff Conklin',1,1296521175421,1296521175421,'19216811091296520833803'),('192168121296520321296','Jeff Conklin',1,1296520352359,1296520352359,'192168121296520321296'),('192168121296520359109','Jeff Conklin',1,1296520466046,1296520466046,'192168121296520359109\n\n(Note to self: add to Search ability to search on node ID.)'),('1921681641221420656546','Administrator',1,1221420686671,1221420686671,'This is a node created by MLB in the HG01 database.');
/*!40000 ALTER TABLE `NodeDetail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `NodeUserState`
--

DROP TABLE IF EXISTS `NodeUserState`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `NodeUserState` (
  `NodeID` varchar(50) NOT NULL,
  `UserID` varchar(50) NOT NULL,
  `State` int(11) NOT NULL,
  PRIMARY KEY (`NodeID`,`UserID`),
  KEY `NodeUserState_UserID_Ind` (`UserID`),
  CONSTRAINT `FK_NodeUaerState_2` FOREIGN KEY (`UserID`) REFERENCES `Users` (`UserID`) ON DELETE CASCADE,
  CONSTRAINT `FK_NodeUserState_1` FOREIGN KEY (`NodeID`) REFERENCES `Node` (`NodeID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `NodeUserState`
--

LOCK TABLES `NodeUserState` WRITE;
/*!40000 ALTER TABLE `NodeUserState` DISABLE KEYS */;
INSERT INTO `NodeUserState` VALUES ('19216801021293659473760','19216811051290214225597',2),('19216801021293659482293','19216811051290214225597',2),('19216801021293723151417','19216811051290214225597',2),('19216801021293815887822','19216811001221456236470',2),('19216801021293815887822','19216811051290214225597',2),('19216801021293815897428','19216811001221456236470',2),('19216801021293815897428','19216811051290214225597',2),('19216801021293816166833','19216811001221456236470',2),('19216801021293816174110','19216811001221456236470',2),('19216801021293816223977','19216811001221456236470',2),('19216801021293816223977','19216811051290214225597',2),('19216801021294083923754','19216811001221456236470',2),('19216801021294175304243','19216811001221456236470',2),('19216801021294175304243','19216811051290214225597',2),('19216801021294175304243','19216811151295665914572',2),('19216801021294175561047','19216811001221456236470',2),('19216801021294175561047','19216811051290214225597',2),('19216801021294175561047','19216811151295665914572',2),('19216801021294175716261','19216811001221456236470',2),('19216801021294175716261','19216811051290214225597',2),('19216801021294175716261','19216811151295665914572',2),('19216801021294175750994','19216811001221456236470',2),('19216801021294175750994','19216811051290214225597',2),('19216801021294175750994','19216811151295665914572',2),('19216801021296417117782','19216811051290214225597',2),('19216801021296417117782','19216811151295665914572',3),('19216811001221456236485','1234567890',2),('19216811001221456236485','19216811001221456236470',2),('19216811001221456237423','1234567890',2),('19216811001221456237423','19216811001221456236470',2),('19216811001221456251329','1234567890',2),('19216811001221456251329','19216811001221456236470',2),('19216811001221456251329','19216811001261426627053',2),('19216811001221456251329','19216811051290214225597',2),('19216811001221456251329','19216811151295665914572',2),('19216811001261426627068','19216811001221456236470',2),('19216811001261426627068','19216811001261426627053',2),('19216811001261426628037','19216811001221456236470',2),('19216811001261426628037','19216811001261426627053',2),('19216811011262027443745','19216811001261426627053',2),('19216811011262027443984','19216811001261426627053',2),('19216811041294080692999','19216811001221456236470',2),('19216811041294080722561','19216811001221456236470',3),('19216811041294080746686','19216811001221456236470',2),('19216811041294084677280','19216811001221456236470',2),('19216811041294084708467','19216811001221456236470',2),('19216811041295376527523','19216811001221456236470',2),('19216811041295376527523','19216811151295665914572',2),('19216811041295552592730','19216811001221456236470',2),('19216811041295552592730','19216811151295665914572',2),('19216811041295552606761','19216811001221456236470',2),('19216811041295552606761','19216811151295665914572',2),('19216811041295552951745','19216811001221456236470',2),('19216811041295637746742','19216811001221456236470',2),('19216811041295637746742','19216811151295665914572',2),('19216811041295637787008','19216811001221456236470',2),('19216811041295638183383','19216811001221456236470',2),('19216811041295638183383','19216811151295665914572',2),('19216811041295638237664','19216811001221456236470',2),('19216811041295638237664','19216811151295665914572',2),('19216811041295638248086','19216811001221456236470',2),('19216811041295638248086','19216811151295665914572',2),('19216811041295638284524','19216811001221456236470',2),('19216811041295638284524','19216811151295665914572',2),('19216811041295638307617','19216811001221456236470',2),('19216811041295638307617','19216811151295665914572',2),('19216811041296067593583','19216811001221456236470',2),('19216811041296067593583','19216811151295665914572',2),('19216811041296067679614','19216811001221456236470',2),('19216811041296067689223','19216811001221456236470',2),('19216811041296067689223','19216811151295665914572',2),('19216811041296068260911','19216811001221456236470',2),('19216811041296068260911','19216811151295665914572',2),('19216811051289608833764','19216811001221456236470',2),('19216811051290214225612','19216811001221456236470',2),('19216811051290214225612','19216811051290214225597',2),('19216811051290214227175','19216811001221456236470',2),('19216811051290214227175','19216811051290214225597',2),('19216811051290215634785','19216811001221456236470',2),('19216811051290215635738','19216811001221456236470',2),('19216811051290449823105','19216811001221456236470',2),('19216811051290449823105','19216811051290214225597',2),('19216811051290449823105','19216811151295665914572',2),('19216811051290449825449','19216811001221456236470',2),('19216811051290449825449','19216811151295665914572',2),('19216811051290449829793','19216811001221456236470',2),('19216811051290449829793','19216811151295665914572',2),('19216811051290449832261','19216811001221456236470',2),('19216811051290449832261','19216811151295665914572',2),('19216811051290449834261','19216811001221456236470',2),('19216811051290449834261','19216811151295665914572',2),('19216811051290449835543','19216811001221456236470',2),('19216811051290449835543','19216811151295665914572',2),('19216811051290449836746','19216811001221456236470',2),('19216811051290449836746','19216811151295665914572',2),('19216811051290449837996','19216811001221456236470',2),('19216811051290449837996','19216811151295665914572',2),('19216811051290449840824','19216811001221456236470',2),('19216811051290449840824','19216811151295665914572',2),('19216811051290449843449','19216811001221456236470',2),('19216811051290449843449','19216811151295665914572',2),('19216811051290449844699','19216811001221456236470',2),('19216811051290449844699','19216811151295665914572',2),('19216811051290449845980','19216811001221456236470',2),('19216811051290449845980','19216811151295665914572',2),('19216811051290449847230','19216811001221456236470',2),('19216811051290449847230','19216811151295665914572',2),('19216811051290449852589','19216811001221456236470',2),('19216811051290449852589','19216811151295665914572',2),('19216811051290449859277','19216811001221456236470',2),('19216811051290449859277','19216811151295665914572',2),('19216811051290449861277','19216811001221456236470',2),('19216811051290449861277','19216811151295665914572',2),('19216811051290449870339','19216811001221456236470',2),('19216811051290449871839','19216811001221456236470',2),('19216811051290449873136','19216811001221456236470',2),('19216811051290449875105','19216811001221456236470',2),('19216811051290449876355','19216811001221456236470',2),('19216811091296068450602','19216811151295665914572',2),('19216811091296520833803','19216811051290214225597',3),('19216811091296520833803','19216811151295665914572',2),('19216811091296522729178','19216811001221456236470',2),('19216811091296522806990','19216811001221456236470',2),('19216811091296522981834','19216811151295665914572',2),('19216811151295665914588','19216811001221456236470',2),('19216811151295665914588','19216811151295665914572',2),('19216811151295665915228','19216811001221456236470',2),('19216811151295665915228','19216811151295665914572',2),('19216811151296004159587','19216811151295665914572',2),('192168121296520321296','19216811001221456236470',2),('192168121296520321296','19216811151295665914572',2),('192168121296520359109','19216811001221456236470',2),('192168121296520359109','19216811151295665914572',2),('1921681641221420656546','1234567890',2),('id_administrator_home','1234567890',2),('id_administrator_inbox','1234567890',2);
/*!40000 ALTER TABLE `NodeUserState` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Permission`
--

DROP TABLE IF EXISTS `Permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Permission` (
  `ItemID` varchar(50) NOT NULL,
  `GroupID` varchar(50) NOT NULL,
  `Permission` int(11) NOT NULL,
  PRIMARY KEY (`ItemID`,`GroupID`),
  KEY `Permission_GroupID_Ind` (`GroupID`),
  CONSTRAINT `FK_Permission_1` FOREIGN KEY (`GroupID`) REFERENCES `UserGroup` (`GroupID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Permission`
--

LOCK TABLES `Permission` WRITE;
/*!40000 ALTER TABLE `Permission` DISABLE KEYS */;
/*!40000 ALTER TABLE `Permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Preference`
--

DROP TABLE IF EXISTS `Preference`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Preference` (
  `UserID` varchar(50) NOT NULL,
  `Property` varchar(100) NOT NULL,
  `Contents` varchar(255) NOT NULL,
  PRIMARY KEY (`UserID`,`Property`),
  CONSTRAINT `FK_Preference_1` FOREIGN KEY (`UserID`) REFERENCES `Users` (`UserID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Preference`
--

LOCK TABLES `Preference` WRITE;
/*!40000 ALTER TABLE `Preference` DISABLE KEYS */;
/*!40000 ALTER TABLE `Preference` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ReferenceNode`
--

DROP TABLE IF EXISTS `ReferenceNode`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ReferenceNode` (
  `NodeID` varchar(50) NOT NULL,
  `Source` text,
  `ImageSource` varchar(255) DEFAULT NULL,
  `ImageWidth` int(11) DEFAULT NULL,
  `ImageHeight` int(11) DEFAULT NULL,
  PRIMARY KEY (`NodeID`),
  CONSTRAINT `FK_ReferenceNode_1` FOREIGN KEY (`NodeID`) REFERENCES `Node` (`NodeID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ReferenceNode`
--

LOCK TABLES `ReferenceNode` WRITE;
/*!40000 ALTER TABLE `ReferenceNode` DISABLE KEYS */;
INSERT INTO `ReferenceNode` VALUES ('19216811001221456237423','','System/resources/Images/inbox.png',0,0),('19216811001261426628037','','System/resources/Images/inbox.png',0,0),('19216811011262027443984','','System/resources/Images/inbox.png',0,0),('19216811051290214227175','','System/resources/Images/inbox.png',0,0),('19216811051290215635738','','System/resources/Images/inbox.png',0,0),('19216811051290449823105','','',0,0),('19216811051290449837996','','',0,0),('19216811151295665915228','','System/resources/Images/inbox.png',0,0),('id_administrator_inbox','','System/resources/Images/inbox.png',0,0);
/*!40000 ALTER TABLE `ReferenceNode` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ShortCutNode`
--

DROP TABLE IF EXISTS `ShortCutNode`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ShortCutNode` (
  `NodeID` varchar(50) NOT NULL,
  `ReferenceID` varchar(50) NOT NULL,
  PRIMARY KEY (`NodeID`,`ReferenceID`),
  CONSTRAINT `FK_ShortcutNode_1` FOREIGN KEY (`NodeID`) REFERENCES `Node` (`NodeID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ShortCutNode`
--

LOCK TABLES `ShortCutNode` WRITE;
/*!40000 ALTER TABLE `ShortCutNode` DISABLE KEYS */;
/*!40000 ALTER TABLE `ShortCutNode` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `System`
--

DROP TABLE IF EXISTS `System`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `System` (
  `Property` varchar(100) NOT NULL,
  `Contents` varchar(255) NOT NULL,
  PRIMARY KEY (`Property`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `System`
--

LOCK TABLES `System` WRITE;
/*!40000 ALTER TABLE `System` DISABLE KEYS */;
INSERT INTO `System` VALUES ('codegroup','0'),('fontsize','14'),('fontstyle','1'),('linkedFilesFlat','false'),('linkedFilesPath','Linked Files'),('linkgroup','1'),('version','1.5.0');
/*!40000 ALTER TABLE `System` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `UserGroup`
--

DROP TABLE IF EXISTS `UserGroup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `UserGroup` (
  `GroupID` varchar(50) NOT NULL,
  `UserID` varchar(50) NOT NULL,
  `CreationDate` double NOT NULL,
  `ModificationDate` double NOT NULL,
  `Name` varchar(100) NOT NULL,
  `Description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`GroupID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `UserGroup`
--

LOCK TABLES `UserGroup` WRITE;
/*!40000 ALTER TABLE `UserGroup` DISABLE KEYS */;
/*!40000 ALTER TABLE `UserGroup` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Users`
--

DROP TABLE IF EXISTS `Users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Users` (
  `UserID` varchar(50) NOT NULL,
  `Author` varchar(50) NOT NULL,
  `CreationDate` double NOT NULL,
  `ModificationDate` double NOT NULL,
  `Login` varchar(20) NOT NULL,
  `Name` varchar(50) DEFAULT NULL,
  `Password` varchar(50) NOT NULL,
  `Description` varchar(255) DEFAULT NULL,
  `HomeView` varchar(50) NOT NULL,
  `IsAdministrator` enum('N','Y') NOT NULL,
  `CurrentStatus` int(11) NOT NULL DEFAULT '0',
  `LinkView` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`UserID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Users`
--

LOCK TABLES `Users` WRITE;
/*!40000 ALTER TABLE `Users` DISABLE KEYS */;
INSERT INTO `Users` VALUES ('1234567890','Administrator',1221456209860,1221456209860,'administrator','Administrator','1sysadmin1','Compendium administrator','id_administrator_home','Y',0,'id_administrator_inbox'),('19216811001221456236470','Jeff Conklin',1290213519393,1290213519393,'jconklin','Jeff Conklin','granola','Email: jeff@cognexus.org','19216811001221456236485','Y',0,'19216811001221456237423'),('19216811001261426627053','Jeff Conklin',1290321961257,1290321961257,'mkknowles','Michael Knowles','nhy6','Email: mk@mwknowles.com','19216811001261426627068','Y',0,'19216811001261426628037'),('19216811011262027443744','Jeff Conklin',1290213500689,1290213500689,'rafiq','AR Rafiq','2b8ten0','','19216811011262027443745','N',1,'19216811011262027443984'),('19216811051290214225597','Jeff Conklin',1290214225597,1290214225597,'mstucky','Matt Stucky','vfr4','email:stuckym44@yahoo.com','19216811051290214225612','Y',0,'19216811051290214227175'),('19216811051290215634769','Jeff Conklin',1290215634769,1290215634769,'kcby','KC Burgess Yakemovic','5tgb','email:kcby@cognexus.org','19216811051290215634785','Y',0,'19216811051290215635738'),('19216811151295665914572','Jeff Conklin',1295665914572,1295665914572,'jconklin2','JC2','granola','email:jeff@cognexus.org','19216811151295665914588','Y',0,'19216811151295665915228');
/*!40000 ALTER TABLE `Users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ViewLayer`
--

DROP TABLE IF EXISTS `ViewLayer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ViewLayer` (
  `UserID` varchar(50) NOT NULL,
  `ViewID` varchar(50) NOT NULL,
  `Scribble` longtext,
  `Background` varchar(255) DEFAULT NULL,
  `Grid` varchar(255) DEFAULT NULL,
  `Shapes` longtext,
  PRIMARY KEY (`UserID`,`ViewID`),
  KEY `ViewLayer_ViewID_Ind` (`ViewID`),
  CONSTRAINT `FK_ViewLayer_1` FOREIGN KEY (`UserID`) REFERENCES `Users` (`UserID`) ON DELETE CASCADE,
  CONSTRAINT `FK_ViewLayer_2` FOREIGN KEY (`ViewID`) REFERENCES `Node` (`NodeID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ViewLayer`
--

LOCK TABLES `ViewLayer` WRITE;
/*!40000 ALTER TABLE `ViewLayer` DISABLE KEYS */;
INSERT INTO `ViewLayer` VALUES ('1234567890','19216811001221456251329','','','',''),('1234567890','id_administrator_home','','','',''),('1234567890','id_administrator_inbox','','','',''),('19216811001221456236470','19216811001221456236485','','','',''),('19216811001221456236470','19216811001221456237423','','','',''),('19216811001221456236470','19216811001221456251329','','','',''),('19216811001221456236470','19216811041295552606761','','','',''),('19216811001221456236470','19216811051290449823105','','','',''),('19216811001261426627053','19216811001221456251329','','','',''),('19216811001261426627053','19216811001261426627068','','','',''),('19216811001261426627053','19216811001261426628037','','','',''),('19216811051290214225597','19216811001221456251329','','','',''),('19216811051290214225597','19216811051290214225612','','','',''),('19216811051290214225597','19216811051290214227175','','','',''),('19216811051290214225597','19216811051290449823105','','','',''),('19216811051290214225597','19216811091296520833803','','','',''),('19216811151295665914572','19216811001221456251329','','','',''),('19216811151295665914572','19216811041295552606761','','','',''),('19216811151295665914572','19216811051290449823105','','','',''),('19216811151295665914572','19216811051290449837996','','','',''),('19216811151295665914572','19216811091296520833803','','','',''),('19216811151295665914572','19216811151295665914588','','','',''),('19216811151295665914572','19216811151295665915228','','','','');
/*!40000 ALTER TABLE `ViewLayer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ViewLink`
--

DROP TABLE IF EXISTS `ViewLink`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ViewLink` (
  `ViewID` varchar(50) NOT NULL,
  `LinkID` varchar(50) NOT NULL,
  `CreationDate` double NOT NULL,
  `ModificationDate` double NOT NULL,
  `CurrentStatus` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ViewID`,`LinkID`),
  KEY `ViewLink_LinkID_Ind` (`LinkID`),
  CONSTRAINT `FK_ViewLink_2` FOREIGN KEY (`LinkID`) REFERENCES `Link` (`LinkID`) ON DELETE CASCADE,
  CONSTRAINT `viewlink_ibfk_1` FOREIGN KEY (`ViewID`) REFERENCES `Node` (`NodeID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ViewLink`
--

LOCK TABLES `ViewLink` WRITE;
/*!40000 ALTER TABLE `ViewLink` DISABLE KEYS */;
INSERT INTO `ViewLink` VALUES ('19216811001221456251329','19216801021294083931502',1294083931561,1294083931561,3),('19216811001221456251329','19216811041295552611651',1295552611714,1295552611714,3),('19216811001221456251329','19216811041295637758149',1295637758196,1295637758196,3),('19216811001221456251329','19216811041295638292242',1295638292414,1295638292414,3),('19216811001221456251329','19216811041295638310930',1295638311117,1295638311117,3),('19216811001221456251329','19216811041295638312180',1295638312383,1295638312383,3),('19216811001221456251329','19216811041295638313477',1295638313649,1295638313649,3),('19216811001221456251329','19216811041296067703442',1296067703614,1296067703614,0),('19216811001221456251329','19216811041296068276458',1296068276505,1296068276505,0),('19216811001221456251329','19216811091296068463118',1296068463306,1296068463306,0),('19216811001221456251329','192168121296520469218',1296520469265,1296520469265,0),('19216811041295552606761','19216811041295638292242',1295930011384,1295930011384,3),('19216811041295552606761','19216811041295638310930',1295930011603,1295930011603,3),('19216811041295552606761','19216811041295638312180',1295930012118,1295930012118,3),('19216811041295552606761','19216811041295638313477',1295930011821,1295930011821,3),('19216811051290449823105','19216801021293659489048',1293659489251,1293659489251,3),('19216811051290449823105','19216801021293660301980',1293660302199,1293660302199,3),('19216811051290449823105','19216801021293723978463',1293723978594,1293723978594,3),('19216811051290449823105','19216801021293816566557',1293816566654,1293816566654,3),('19216811051290449823105','19216801021293816592873',1293816592974,1293816592974,3),('19216811051290449823105','19216801021294175722119',1294175722160,1294175722160,3),('19216811051290449823105','19216801021294175758501',1294175758547,1294175758547,3),('19216811051290449823105','19216811041294080767358',1294080767514,1294080767514,3),('19216811051290449823105','19216811041294084697014',1294084697170,1294084697170,3),('19216811051290449823105','19216811041294084750342',1294084750514,1294084750514,3),('19216811051290449823105','19216811041294084771217',1294084771389,1294084771389,3),('19216811051290449823105','19216811041295376523179',1295376523460,1295376523460,3),('19216811051290449823105','19216811051290449963418',1290449963652,1290449963652,3),('19216811051290449823105','19216811051290449964293',1290449964511,1290449964511,3),('19216811051290449823105','19216811051290449964574',1290449964824,1290449964824,3),('19216811051290449823105','19216811051290449965168',1290449965402,1290449965402,3),('19216811051290449823105','19216811051290449966355',1290449966589,1290449966589,3),('19216811051290449823105','19216811051290449967246',1290449968152,1290449968152,3),('19216811051290449823105','19216811051290449968214',1290449968433,1290449968433,3),('19216811051290449823105','19216811051290449968793',1290449969027,1290449969027,3),('19216811051290449823105','19216811051290449969996',1290449970230,1290449970230,3),('19216811051290449823105','19216811051290449970871',1290449971089,1290449971089,3),('19216811051290449837996','19216811051290449972027',1290449972261,1290449972261,3),('19216811051290449837996','19216811051290449972308',1290449972543,1290449972543,3),('19216811051290449837996','19216811051290449972605',1290449972839,1290449972839,3),('19216811051290449837996','19216811051290449972886',1290449973136,1290449973136,3),('19216811051290449837996','19216811051290449973183',1290449974089,1290449974089,3),('19216811091296520833803','192168121296520469218',1296520871631,1296520871631,0);
/*!40000 ALTER TABLE `ViewLink` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ViewNode`
--

DROP TABLE IF EXISTS `ViewNode`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ViewNode` (
  `ViewID` varchar(50) NOT NULL,
  `NodeID` varchar(50) NOT NULL,
  `XPos` int(11) NOT NULL DEFAULT '0',
  `YPos` int(11) NOT NULL DEFAULT '0',
  `CreationDate` double DEFAULT NULL,
  `ModificationDate` double DEFAULT NULL,
  `CurrentStatus` int(11) NOT NULL DEFAULT '0',
  `ShowTags` varchar(1) NOT NULL DEFAULT 'Y',
  `ShowText` varchar(1) NOT NULL DEFAULT 'Y',
  `ShowTrans` varchar(1) NOT NULL DEFAULT 'Y',
  `ShowWeight` varchar(1) NOT NULL DEFAULT 'Y',
  `SmallIcon` varchar(1) NOT NULL DEFAULT 'N',
  `HideIcon` varchar(1) NOT NULL DEFAULT 'N',
  `LabelWrapWidth` int(11) NOT NULL DEFAULT '25',
  `FontSize` int(11) NOT NULL DEFAULT '12',
  `FontFace` varchar(100) NOT NULL DEFAULT 'Arial',
  `FontStyle` int(11) NOT NULL DEFAULT '0',
  `Foreground` int(11) NOT NULL DEFAULT '0',
  `Background` int(11) NOT NULL DEFAULT '-1',
  PRIMARY KEY (`ViewID`,`NodeID`),
  KEY `ViewNode_NodeID_Ind` (`NodeID`),
  CONSTRAINT `FK_ViewNode_1` FOREIGN KEY (`ViewID`) REFERENCES `Node` (`NodeID`) ON DELETE CASCADE,
  CONSTRAINT `FK_ViewNode_2` FOREIGN KEY (`NodeID`) REFERENCES `Node` (`NodeID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ViewNode`
--

LOCK TABLES `ViewNode` WRITE;
/*!40000 ALTER TABLE `ViewNode` DISABLE KEYS */;
INSERT INTO `ViewNode` VALUES ('19216811001221456236485','19216811001221456237423',0,75,1221456236470,1221456236470,0,'N','N','N','Y','N','N',25,12,'Dialog',0,-16777216,-1),('19216811001221456236485','19216811001221456251329',117,5,1221456962579,1295552588730,0,'Y','Y','N','Y','N','N',25,12,'Dialog',0,-16777216,-1),('19216811001221456236485','19216811041295552951745',221,173,1295552951745,1295552976683,3,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811001221456236485','19216811091296522729178',359,357,1296522729178,1296522748068,0,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811001221456237423','19216811041295637787008',0,10,1295637787008,1295638102836,0,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811001221456251329','19216801021294083923754',359,324,1294083923754,1294083929816,3,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811001221456251329','19216801021296417117782',760,404,1296417117782,1296668836605,0,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-26164,-1),('19216811001221456251329','19216811041294080746686',424,189,1294080865889,1294080869905,3,'Y','Y','Y','Y','N','N',25,14,'Dialog',0,-16777216,-1),('19216811001221456251329','19216811041295552592730',389,148,1295552592730,1295929638587,3,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811001221456251329','19216811041295552606761',558,177,1295552606761,1295552893573,3,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811001221456251329','19216811041295637746742',365,242,1295637746742,1295638212055,3,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811001221456251329','19216811041295638183383',517,401,1295638183383,1295666177744,3,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811001221456251329','19216811041295638237664',1041,387,1295638237664,1295638305992,3,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811001221456251329','19216811041295638248086',1079,447,1295638248086,1295638306055,3,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811001221456251329','19216811041295638284524',790,419,1295638284524,1295638300805,3,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811001221456251329','19216811041295638307617',924,416,1295638307617,1295638315289,3,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811001221456251329','19216811041296067593583',284,193,1296067593583,1296067609473,0,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811001221456251329','19216811041296067679614',553,189,1296067679614,1296067679614,3,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811001221456251329','19216811041296067689223',513,184,1296067689223,1296067706473,0,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811001221456251329','19216811041296068260911',763,144,1296068260911,1296068279223,0,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811001221456251329','19216811051290449823105',79,178,1289522034051,1295371381882,3,'Y','Y','Y','Y','N','N',35,12,'Dialog',0,-16777216,-1),('19216811001221456251329','19216811091296068450602',798,226,1296068450618,1296068480915,0,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811001221456251329','19216811091296520833803',351,285,1296520833803,1296520856865,0,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811001221456251329','19216811091296522806990',330,68,1296522806990,1296522806990,0,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811001221456251329','192168121296520321296',204,385,1296520321296,1296520339671,0,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811001221456251329','192168121296520359109',424,358,1296520359109,1296520471843,0,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811001261426627068','19216811001221456251329',111,4,1261502470287,1261502474662,0,'Y','Y','N','Y','N','N',25,12,'Dialog',0,-16777216,-1),('19216811001261426627068','19216811001261426628037',0,75,1261426627053,1261426627053,0,'N','N','N','Y','N','N',25,12,'Dialog',0,-16777216,-1),('19216811011262027443745','19216811011262027443984',0,75,1262027443744,1262027443744,0,'N','N','N','Y','N','N',25,12,'Dialog',0,-16777216,-1),('19216811041295552606761','19216811041295637746742',111,90,1295637764805,1295637766992,3,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811041295552606761','19216811041295638183383',142,247,1295930010993,1295930015228,3,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811041295552606761','19216811041295638237664',673,226,1295930010212,1295930019337,3,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811041295552606761','19216811041295638248086',704,293,1295930009806,1295930015056,3,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811041295552606761','19216811041295638284524',415,265,1295930009399,1295930014993,3,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811041295552606761','19216811041295638307617',549,262,1295930010587,1295930015165,3,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811051290214225612','19216811001221456251329',114,1,1290320665285,1293816157190,0,'Y','Y','N','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811051290214225612','19216811051290214227175',0,75,1290214225597,1290214225597,0,'N','N','N','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811051290215634785','19216811051290215635738',0,75,1290215634769,1290215634769,0,'N','N','N','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811051290449823105','19216801021293659473760',116,90,1293659473760,1293660296239,3,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811051290449823105','19216801021293659482293',355,77,1293659482293,1293723008437,3,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811051290449823105','19216801021293723117230',104,431,1293723117230,1293723977438,3,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811051290449823105','19216801021293723151417',334,440,1293723151417,1293724043154,3,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811051290449823105','19216801021293815887822',105,78,1293815887822,1293816220670,3,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811051290449823105','19216801021293815897428',298,78,1293815897428,1293816219655,3,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811051290449823105','19216801021293816166833',103,396,1293816166833,1293816770647,3,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811051290449823105','19216801021293816174110',280,396,1293816174110,1294080624920,3,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811051290449823105','19216801021293816223977',160,188,1293816223977,1293816233474,3,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811051290449823105','19216801021294175304243',72,103,1294175304243,1294175712159,3,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811051290449823105','19216801021294175561047',86,477,1294175561047,1294175874314,3,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811051290449823105','19216801021294175716261',327,102,1294175716261,1294175725135,3,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811051290449823105','19216801021294175750994',295,488,1294175750994,1294175874853,3,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811051290449823105','19216811041294080692999',38,528,1294080692999,1294080713483,3,'Y','Y','Y','Y','N','N',15,14,'Dialog',1,-16777216,-1),('19216811051290449823105','19216811041294080722561',153,661,1294080722561,1294080722561,3,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811051290449823105','19216811041294080746686',33,680,1294080746686,1294084692342,3,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-65536,-1),('19216811051290449823105','19216811041294084677280',18,750,1294084677280,1294084698420,3,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811051290449823105','19216811041294084708467',270,470,1294084708467,1294084770061,3,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811051290449823105','19216811041295376527523',878,11,1295376527523,1295376561569,3,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811051290449823105','19216811051290449825449',839,362,1289591748482,1289720134248,3,'Y','Y','Y','Y','N','N',35,12,'Dialog',0,-16777216,-1),('19216811051290449823105','19216811051290449829793',557,153,1289522530942,1295376569273,3,'Y','Y','Y','Y','N','N',35,12,'Dialog',0,-16777216,-1),('19216811051290449823105','19216811051290449832261',555,248,1289722016919,1289722332357,3,'Y','Y','Y','Y','N','N',35,12,'Dialog',0,-16777216,-1),('19216811051290449823105','19216811051290449834261',834,284,1289522196364,1289785690011,3,'Y','Y','Y','Y','N','N',35,12,'Dialog',0,-16777216,-1),('19216811051290449823105','19216811051290449835543',1083,767,1289785616223,1289800043513,3,'Y','Y','Y','Y','N','N',35,12,'Dialog',0,-16777216,-1),('19216811051290449823105','19216811051290449836746',558,330,1289842023196,1289842023196,3,'Y','Y','Y','Y','N','N',35,12,'Dialog',0,-16777216,-1),('19216811051290449823105','19216811051290449837996',1072,157,1289722765404,1289722775123,3,'Y','Y','Y','Y','N','N',35,12,'Dialog',0,-16777216,-1),('19216811051290449823105','19216811051290449840824',297,262,1289522036176,1289522150504,3,'Y','Y','Y','Y','N','N',35,12,'Dialog',0,-16777216,-1),('19216811051290449823105','19216811051290449843449',1112,670,1289721420685,1289800035560,3,'Y','Y','Y','Y','N','N',35,12,'Dialog',0,-16777216,-1),('19216811051290449823105','19216811051290449844699',545,452,1289522159458,1289722824123,3,'Y','Y','Y','Y','N','N',35,12,'Dialog',0,-16777216,-1),('19216811051290449823105','19216811051290449845980',801,162,1289719328248,1289722011763,3,'Y','Y','Y','Y','N','N',35,12,'Dialog',0,-16777216,-1),('19216811051290449823105','19216811051290449847230',1104,583,1289720524794,1289800035966,3,'Y','Y','Y','Y','N','N',35,12,'Dialog',0,-16777216,-1),('19216811051290449823105','19216811051290449852589',9,275,1289522104348,1289522110786,3,'Y','Y','Y','Y','Y','N',35,12,'Dialog',0,-6710887,-1),('19216811051290449823105','19216811051290449859277',1092,375,1289595121123,1289720134451,3,'Y','Y','Y','Y','N','N',35,12,'Dialog',0,-16777216,-1),('19216811051290449823105','19216811051290449861277',557,29,1289522232958,1289722011998,3,'Y','Y','Y','Y','N','N',35,12,'Dialog',0,-16777216,-1),('19216811051290449837996','19216811051290449852589',16,249,1289521771411,1289521794145,3,'Y','Y','Y','Y','Y','N',35,12,'Dialog',0,-6710887,-1),('19216811051290449837996','19216811051290449870339',283,241,1289521722973,1289722632529,3,'Y','Y','Y','Y','N','N',35,12,'Dialog',0,-16777216,-1),('19216811051290449837996','19216811051290449871839',556,342,1289521737614,1289722668123,3,'Y','Y','Y','Y','N','N',35,12,'Dialog',0,-16777216,-1),('19216811051290449837996','19216811051290449873136',553,257,1289521738395,1289521769598,3,'Y','Y','Y','Y','N','N',35,12,'Dialog',0,-16777216,-1),('19216811051290449837996','19216811051290449875105',561,171,1289521739958,1289521769708,3,'Y','Y','Y','Y','N','N',35,12,'Dialog',0,-16777216,-1),('19216811051290449837996','19216811051290449876355',796,186,1289521739176,1289521769489,3,'Y','Y','Y','Y','N','N',35,12,'Dialog',0,-16777216,-1),('19216811091296520833803','192168121296520321296',149,235,1296520871240,1296520871240,0,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811091296520833803','192168121296520359109',369,208,1296520870834,1296520870834,0,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811151295665914588','19216811001221456251329',125,0,1295665946791,1295665950322,0,'Y','Y','N','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811151295665914588','19216811091296522981834',217,191,1296522981834,1296522993287,0,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811151295665914588','19216811151295665915228',0,75,1295665914572,1295665914572,0,'N','N','N','Y','N','N',25,14,'Dialog',1,-16777216,-1),('19216811151295665914588','19216811151296004159587',207,151,1296004159587,1296004159587,3,'Y','Y','Y','Y','N','N',25,14,'Dialog',1,-16777216,-1),('id_administrator_home','19216811001221456251329',112,4,1221456251329,1221456260126,0,'Y','Y','Y','Y','N','N',25,12,'Dialog',0,-16777216,-1),('id_administrator_home','1921681641221420656546',225,68,1221420656546,1221420664578,0,'Y','Y','Y','Y','N','N',25,12,'Dialog',0,-16777216,-1),('id_administrator_home','id_administrator_inbox',0,75,1158582336554,1158582336554,0,'N','N','N','Y','N','N',25,12,'Dialog',0,-16777216,-1);
/*!40000 ALTER TABLE `ViewNode` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ViewProperty`
--

DROP TABLE IF EXISTS `ViewProperty`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ViewProperty` (
  `UserID` varchar(50) NOT NULL,
  `ViewID` varchar(50) NOT NULL,
  `HorizontalScroll` int(11) NOT NULL,
  `VerticalScroll` int(11) NOT NULL,
  `Width` int(11) NOT NULL,
  `Height` int(11) NOT NULL,
  `XPosition` int(11) NOT NULL,
  `YPosition` int(11) NOT NULL,
  `IsIcon` enum('N','Y') NOT NULL DEFAULT 'N',
  `IsMaximum` enum('N','Y') NOT NULL DEFAULT 'N',
  `ShowTags` enum('N','Y') NOT NULL DEFAULT 'N',
  `ShowText` enum('N','Y') NOT NULL DEFAULT 'N',
  `ShowTrans` enum('N','Y') NOT NULL DEFAULT 'N',
  `ShowWeight` enum('N','Y') NOT NULL DEFAULT 'N',
  `SmallIcons` enum('N','Y') NOT NULL DEFAULT 'N',
  `HideIcons` enum('N','Y') NOT NULL DEFAULT 'N',
  `LabelLength` int(11) NOT NULL DEFAULT '100',
  `LabelWidth` int(11) NOT NULL DEFAULT '15',
  `FontSize` int(11) NOT NULL DEFAULT '12',
  `FontFace` varchar(100) NOT NULL DEFAULT 'Arial',
  `FontStyle` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`UserID`,`ViewID`),
  KEY `ViewProperty_ViewID_Ind` (`ViewID`),
  CONSTRAINT `FK_ViewProperty_1` FOREIGN KEY (`UserID`) REFERENCES `Users` (`UserID`) ON DELETE CASCADE,
  CONSTRAINT `FK_ViewProperty_2` FOREIGN KEY (`ViewID`) REFERENCES `Node` (`NodeID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ViewProperty`
--

LOCK TABLES `ViewProperty` WRITE;
/*!40000 ALTER TABLE `ViewProperty` DISABLE KEYS */;
INSERT INTO `ViewProperty` VALUES ('1234567890','19216811001221456251329',0,0,500,500,400,26,'N','N','N','N','N','N','N','N',100,15,12,'Arial',0),('1234567890','id_administrator_home',0,0,500,500,0,0,'N','N','N','N','N','N','N','N',100,15,12,'Arial',0),('19216811001221456236470','19216811001221456236485',0,0,1020,608,0,0,'N','N','N','N','N','N','N','N',100,15,12,'Arial',0),('19216811001221456236470','19216811001221456237423',0,0,932,250,24,24,'N','N','N','N','N','N','N','N',100,15,12,'Arial',0),('19216811001221456236470','19216811001221456251329',0,6,500,500,24,24,'N','Y','N','N','N','N','N','N',100,15,12,'Arial',0),('19216811001221456236470','19216811041295552606761',0,0,500,500,48,48,'N','Y','N','N','N','N','N','N',100,15,12,'Arial',0),('19216811001221456236470','19216811051290449823105',0,0,500,500,48,48,'N','Y','N','N','N','N','N','N',100,15,12,'Arial',0),('19216811001261426627053','19216811001221456251329',0,0,823,500,24,24,'N','N','N','N','N','N','N','N',100,15,12,'Arial',0),('19216811001261426627053','19216811001261426627068',0,0,838,500,0,0,'N','N','N','N','N','N','N','N',100,15,12,'Arial',0),('19216811051290214225597','19216811001221456251329',0,9,1148,642,11,43,'N','N','N','N','N','N','N','N',100,15,12,'Arial',0),('19216811051290214225597','19216811051290214225612',0,0,1006,576,89,132,'N','N','N','N','N','N','N','N',100,15,12,'Arial',0),('19216811051290214225597','19216811051290214227175',0,0,500,250,48,48,'N','N','N','N','N','N','N','N',100,15,12,'Arial',0),('19216811051290214225597','19216811051290449823105',0,0,1058,782,81,71,'N','N','N','N','N','N','N','N',100,15,12,'Arial',0),('19216811051290214225597','19216811091296520833803',0,0,873,532,48,48,'N','N','N','N','N','N','N','N',100,15,12,'Arial',0),('19216811151295665914572','19216811001221456251329',0,0,500,500,24,24,'N','Y','N','N','N','N','N','N',100,15,12,'Arial',0),('19216811151295665914572','19216811051290449823105',304,0,500,500,48,48,'N','Y','N','N','N','N','N','N',100,15,12,'Arial',0),('19216811151295665914572','19216811091296520833803',0,0,639,500,48,48,'N','N','N','N','N','N','N','N',100,15,12,'Arial',0),('19216811151295665914572','19216811151295665914588',0,0,500,500,0,0,'N','N','N','N','N','N','N','N',100,15,12,'Arial',0);
/*!40000 ALTER TABLE `ViewProperty` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Workspace`
--

DROP TABLE IF EXISTS `Workspace`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Workspace` (
  `WorkspaceID` varchar(50) NOT NULL,
  `UserID` varchar(50) NOT NULL,
  `Name` varchar(100) NOT NULL,
  `CreationDate` double NOT NULL,
  `ModificationDate` double NOT NULL,
  PRIMARY KEY (`WorkspaceID`),
  KEY `Workspace_UserID_Ind` (`UserID`),
  CONSTRAINT `FK_Workspace_1` FOREIGN KEY (`UserID`) REFERENCES `Users` (`UserID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Workspace`
--

LOCK TABLES `Workspace` WRITE;
/*!40000 ALTER TABLE `Workspace` DISABLE KEYS */;
/*!40000 ALTER TABLE `Workspace` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `WorkspaceView`
--

DROP TABLE IF EXISTS `WorkspaceView`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `WorkspaceView` (
  `WorkspaceID` varchar(50) NOT NULL,
  `ViewID` varchar(50) NOT NULL,
  `HorizontalScroll` int(11) NOT NULL,
  `VerticalScroll` int(11) NOT NULL,
  `Width` int(11) NOT NULL,
  `Height` int(11) NOT NULL,
  `XPosition` int(11) NOT NULL,
  `YPosition` int(11) NOT NULL,
  `IsIcon` enum('N','Y') NOT NULL,
  `IsMaximum` enum('N','Y') NOT NULL,
  PRIMARY KEY (`WorkspaceID`,`ViewID`),
  KEY `WorkspaceView_ViewID_Ind` (`ViewID`),
  CONSTRAINT `FK_WorkspaceView_1` FOREIGN KEY (`WorkspaceID`) REFERENCES `Workspace` (`WorkspaceID`) ON DELETE CASCADE,
  CONSTRAINT `FK_WorkspaceView_2` FOREIGN KEY (`ViewID`) REFERENCES `Node` (`NodeID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `WorkspaceView`
--

LOCK TABLES `WorkspaceView` WRITE;
/*!40000 ALTER TABLE `WorkspaceView` DISABLE KEYS */;
/*!40000 ALTER TABLE `WorkspaceView` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2011-02-02 10:18:21
