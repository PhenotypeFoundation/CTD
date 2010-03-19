-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version	5.0.51a-3ubuntu5.5


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
CREATE TABLE  `ctd`.`chip` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(200) character set latin1 default NULL,
  `time_stamp` varchar(45) default NULL,
  `dbname` varchar(45) character set latin1 default NULL,
  `tax_id` int(11) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
CREATE TABLE  `ctd`.`chip_annotation` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `chip_id` int(10) unsigned default NULL,
  `probeset` varchar(50) character set latin1 default NULL,
  `gene_accession` varchar(45) character set latin1 default NULL,
  `gene_symbol` varchar(45) default NULL,
  `gene_description` varchar(200) default NULL,
  `LIST_POS` int(11) default NULL,
  PRIMARY KEY  (`id`),
  KEY `new_fk_constraint` (`chip_id`),
  KEY `new_index` (`probeset`)
) ENGINE=InnoDB AUTO_INCREMENT=16396 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
CREATE TABLE  `ctd`.`expression` (
  `study_sample_assay_id` int(10) unsigned default NULL,
  `chip_annotation_id` int(10) unsigned default NULL,
  `expression` double(7,2) default NULL,
  KEY `new_index` (`study_sample_assay_id`,`chip_annotation_id`),
  CONSTRAINT `new_fk_constraint` FOREIGN KEY (`study_sample_assay_id`) REFERENCES `study_sample_assay` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
CREATE TABLE  `ctd`.`study_sample_assay` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `X_REF` varchar(45) character set latin1 default NULL,
  `chip_time` varchar(45) character set latin1 default NULL,
  `name_RAWFILE` varchar(45) character set latin1 default NULL,
  `average` double default NULL,
  `std` double default NULL,
  `ticket_id` int(11) default NULL,
  `LIST_POS` int(11) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=115 DEFAULT CHARSET=utf8;
CREATE TABLE  `ctd`.`ticket` (
  `id` int(11) NOT NULL auto_increment,
  `ctd_REF` varchar(45) NOT NULL,
  `folder` varchar(45) default NULL,
  `password` varchar(45) default NULL,
  `closed` varchar(3) default 'no',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=20 DEFAULT CHARSET=latin1;



/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
