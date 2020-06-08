-- MariaDB dump 10.17  Distrib 10.4.13-MariaDB, for Linux (x86_64)
--
-- Host: 127.0.0.1    Database: db_esame_tiw
-- ------------------------------------------------------
-- Server version	10.4.13-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `invitations`
--

DROP TABLE IF EXISTS `invitations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invitations` (
  `meetingid` int(11) NOT NULL,
  `userid` int(11) NOT NULL,
  PRIMARY KEY (`meetingid`,`userid`),
  KEY `invitations_user_id_fk` (`userid`),
  CONSTRAINT `invitations_meetings_id_fk` FOREIGN KEY (`meetingid`) REFERENCES `meetings` (`id`),
  CONSTRAINT `invitations_user_id_fk` FOREIGN KEY (`userid`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invitations`
--

LOCK TABLES `invitations` WRITE;
/*!40000 ALTER TABLE `invitations` DISABLE KEYS */;
INSERT INTO `invitations` VALUES (1,2),(1,3),(7,2),(7,3),(9,2);
/*!40000 ALTER TABLE `invitations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `meetings`
--

DROP TABLE IF EXISTS `meetings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `meetings` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `maxparticipants` int(11) NOT NULL,
  `timestamp` datetime NOT NULL,
  `duration` int(11) NOT NULL,
  `creatorid` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `meetings_pk` (`maxparticipants`,`creatorid`,`duration`,`title`),
  KEY `meetings_user_id_fk` (`creatorid`),
  CONSTRAINT `meetings_user_id_fk` FOREIGN KEY (`creatorid`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `meetings`
--

LOCK TABLES `meetings` WRITE;
/*!40000 ALTER TABLE `meetings` DISABLE KEYS */;
INSERT INTO `meetings` VALUES (1,'asd',5,'5555-05-05 05:05:00',55,1),(7,'asd',4,'5200-04-04 04:04:00',44,1),(9,'asd',2,'4444-04-04 04:04:00',55,1);
/*!40000 ALTER TABLE `meetings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(400) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `salt` char(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  UNIQUE KEY `user_id_uindex` (`id`),
  UNIQUE KEY `user_username_uindex` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'ale','$argon2id$v=19$m=65536,t=10,p=2$ZE1Qb0xqWmNEYm4wWjlFUmkzTm1temVnRVJVZXh1bWdiWmpORHgzNDV3a3lGOW01dnZ3a0liNWU1ZmFUZjQ4OTZZVHpGd0NiejlrVGZCQ1dkQkM3OEtJemtoR3lBbWIyb1Q0ZQ$QAJ9sKNthMGfY6bXluMlTKrxq25Q+QZHUtK/Y/PNbOw','dMPoLjZcDbn0Z9ERi3NmmzegERUexumgbZjNDx345wkyF9m5vvwkIb5e5faTf4896YTzFwCbz9kTfBCWdBC78KIzkhGyAmb2oT4e'),(2,'ale1','$argon2id$v=19$m=65536,t=10,p=2$a3BRT1pXcTF4b2lZcWpiSzhPRFdlRVJpM0Q4bHozaG9pbU5KcFdRa2lLMktKZ0JaQTBWanM2YzNnMENldTJGRXd5a0JWWXNFaWJJMHdsTHdKRGk3RlB2cWp6dnBjcnRCTktQaA$0kKHfe8geb+OVW1dMwHQMuz+4E4zGISeAJfl5wIE2qU','kpQOZWq1xoiYqjbK8ODWeERi3D8lz3hoimNJpWQkiK2KJgBZA0Vjs6c3g0Ceu2FEwykBVYsEibI0wlLwJDi7FPvqjzvpcrtBNKPh'),(3,'ale2','$argon2id$v=19$m=65536,t=10,p=2$N0F1YlZ4cTdPNjR3RXprdlU0N2ZvdTFYUGg5OUk4Z0NGQlM5eERra0VlNlhUaHlmSjdBY25hbFJXdWdJMnZjN2JtZlZ6ZGJFdkhmVDZWUlRHdm5JR1lCV3BzVEV2ckJvWXBRNg$MlWFAfHxgtVWI0DJJ/dHe1HvDmzVVv+odhPqoXY/COw','7AubVxq7O64wEzkvU47fou1XPh99I8gCFBS9xDkkEe6XThyfJ7AcnalRWugI2vc7bmfVzdbEvHfT6VRTGvnIGYBWpsTEvrBoYpQ6');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-06-08 13:15:34
