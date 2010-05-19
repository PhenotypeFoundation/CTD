-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version	5.4.3-beta-community


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


--
-- Create schema ctd
--

CREATE DATABASE IF NOT EXISTS ctd;
USE ctd;

--
-- Definition of table `chip`
--

DROP TABLE IF EXISTS `chip`;
CREATE TABLE `chip` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(200) CHARACTER SET latin1 DEFAULT NULL,
  `time_stamp` varchar(45) DEFAULT NULL,
  `dbname` varchar(45) CHARACTER SET latin1 DEFAULT NULL,
  `tax_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `chip`
--

/*!40000 ALTER TABLE `chip` DISABLE KEYS */;
/*!40000 ALTER TABLE `chip` ENABLE KEYS */;


--
-- Definition of table `chip_annotation`
--

DROP TABLE IF EXISTS `chip_annotation`;
CREATE TABLE `chip_annotation` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `chip_id` int(10) unsigned DEFAULT NULL,
  `probeset` varchar(50) CHARACTER SET latin1 DEFAULT NULL,
  `gene_accession` varchar(45) CHARACTER SET latin1 DEFAULT NULL,
  `gene_symbol` varchar(45) DEFAULT NULL,
  `gene_description` varchar(200) DEFAULT NULL,
  `LIST_POS` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `new_fk_constraint` (`chip_id`),
  KEY `new_index` (`probeset`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Dumping data for table `chip_annotation`
--

/*!40000 ALTER TABLE `chip_annotation` DISABLE KEYS */;
/*!40000 ALTER TABLE `chip_annotation` ENABLE KEYS */;


--
-- Definition of table `expression`
--

DROP TABLE IF EXISTS `expression`;
CREATE TABLE `expression` (
  `study_sample_assay_id` int(10) unsigned DEFAULT NULL,
  `chip_annotation_id` int(10) unsigned DEFAULT NULL,
  `expression` double(7,5) DEFAULT NULL,
  KEY `new_index` (`study_sample_assay_id`,`chip_annotation_id`),
  CONSTRAINT `new_fk_constraint` FOREIGN KEY (`study_sample_assay_id`) REFERENCES `study_sample_assay` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `expression`
--

/*!40000 ALTER TABLE `expression` DISABLE KEYS */;
/*!40000 ALTER TABLE `expression` ENABLE KEYS */;


--
-- Definition of table `procedure`
--

DROP TABLE IF EXISTS `procedure`;
CREATE TABLE `procedure` (
  `ticket_id` int(10) unsigned NOT NULL,
  `protocol_id` int(10) unsigned NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `procedure`
--

/*!40000 ALTER TABLE `procedure` DISABLE KEYS */;
/*!40000 ALTER TABLE `procedure` ENABLE KEYS */;


--
-- Definition of table `protocol`
--

DROP TABLE IF EXISTS `protocol`;
CREATE TABLE `protocol` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET latin1 DEFAULT NULL,
  `description` varchar(1000) CHARACTER SET latin1 DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `protocol`
--

/*!40000 ALTER TABLE `protocol` DISABLE KEYS */;
/*!40000 ALTER TABLE `protocol` ENABLE KEYS */;


--
-- Definition of table `study_sample_assay`
--

DROP TABLE IF EXISTS `study_sample_assay`;
CREATE TABLE `study_sample_assay` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `X_REF` varchar(45) CHARACTER SET latin1 DEFAULT NULL,
  `chip_time` varchar(45) CHARACTER SET latin1 DEFAULT NULL,
  `name_RAWFILE` varchar(45) CHARACTER SET latin1 DEFAULT NULL,
  `average` double DEFAULT NULL,
  `std` double DEFAULT NULL,
  `ticket_id` int(11) DEFAULT NULL,
  `LIST_POS` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `study_sample_assay`
--

/*!40000 ALTER TABLE `study_sample_assay` DISABLE KEYS */;
/*!40000 ALTER TABLE `study_sample_assay` ENABLE KEYS */;


--
-- Definition of table `ticket`
--

DROP TABLE IF EXISTS `ticket`;
CREATE TABLE `ticket` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ctd_REF` varchar(45) NOT NULL,
  `folder` varchar(45) DEFAULT NULL,
  `password` varchar(45) DEFAULT NULL,
  `closed` varchar(3) DEFAULT 'no',
  `title` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=20 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `ticket`
--

/*!40000 ALTER TABLE `ticket` DISABLE KEYS */;
/*!40000 ALTER TABLE `ticket` ENABLE KEYS */;




/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
