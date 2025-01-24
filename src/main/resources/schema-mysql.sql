CREATE TABLE IF NOT EXISTS `legal_case` (
  `group_id` varchar(100) NOT NULL,
  `id` varchar(100) NOT NULL,
  `court` varchar(25) NOT NULL,
  `verdict_date` date DEFAULT NULL,
  `url` varchar(500) DEFAULT NULL,
  `charge` varchar(500) DEFAULT NULL,
  `judge_name` varchar(50) DEFAULT NULL,
  `defendant_name` varchar(50) DEFAULT NULL,
  `content` longtext,
  `content2` longtext,
  `law` text,
  `case_type` varchar(25) DEFAULT NULL,
  `doc_type` varchar(25) DEFAULT NULL,
  PRIMARY KEY (`group_id`,`id`,`court`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `account_system` (
  `email` varchar(100) NOT NULL,
  `name` varchar(25) NOT NULL DEFAULT 'guest',
  `password` varchar(100) NOT NULL,
  `email_verified` tinyint(1) NOT NULL DEFAULT '0',
  `role` varchar(25) NOT NULL DEFAULT 'guest',
  `phone` varchar(25) NOT NULL DEFAULT '0',
  `email_verification_token` varchar(255) DEFAULT NULL,
  `token_expiry` datetime DEFAULT NULL,
  `profile_picture` longblob,
  `license_number` varchar(25) DEFAULT NULL,
  `law_firm_number` varchar(25) DEFAULT NULL,
  `city` varchar(25) DEFAULT NULL,
  `address` varchar(25) DEFAULT NULL,
  `law_firm` varchar(25) DEFAULT NULL,
  `available` tinyint DEFAULT '0',
  PRIMARY KEY (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `highlighters` (
  `email` varchar(100) NOT NULL,
  `group_id` varchar(100) NOT NULL,
  `id` varchar(100) NOT NULL,
  `court` varchar(45) NOT NULL,
  `highlighter_color` varchar(45) DEFAULT NULL,
  `select_text` text,
  `start_offset` int NOT NULL,
  `end_offset` int DEFAULT NULL,
  PRIMARY KEY (`email`,`court`,`id`,`group_id`,`start_offset`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `bookmarks` (
  `email` varchar(100) NOT NULL,
  `group_id` varchar(100) NOT NULL,
  `id` varchar(100) NOT NULL,
  `court` varchar(45) NOT NULL,
  `verdict_date` date DEFAULT NULL,
  `charge` varchar(500) DEFAULT NULL,
  `judge_name` varchar(50) DEFAULT NULL,
  `defendant_name` varchar(50) DEFAULT NULL,
  `case_type` varchar(25) DEFAULT NULL,
  `doc_type` varchar(25) DEFAULT NULL,
  PRIMARY KEY (`email`,`group_id`,`id`,`court`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;