-- ---------- DROP TABLES -----------
drop table if exists `receipt`;
drop table if exists `transaction`;

drop table if exists `student_checking_account`;
drop table if exists `checking_account`;
drop table if exists `savings_account`;
drop table if exists `credit_card_account`;
drop table if exists `account`;

drop table if exists `account_holder`;
drop table if exists `third_party`;
drop table if exists `admin`;

drop table if exists `users_roles`;
drop table if exists `roles`;
drop table if exists `user`;

-- ---------- CREATE TABLES -----------

-- ---------- users and roles -----------
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY (`username`)
);

CREATE TABLE IF NOT EXISTS `roles` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY (`name`)
);

CREATE TABLE IF NOT EXISTS `users_roles` (
  `user_id` bigint NOT NULL,
  `role_id` int NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
);

CREATE TABLE IF NOT EXISTS `admin` (
  `id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`id`) REFERENCES `user` (`id`)
);

CREATE TABLE IF NOT EXISTS `third_party` (
  `hashed_key` varchar(255) NOT NULL,
  `id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`id`) REFERENCES `user` (`id`)
);

CREATE TABLE IF NOT EXISTS `account_holder` (
  `date_of_birth` date NOT NULL,
  `mailing_address_city` varchar(255) DEFAULT NULL,
  `mailing_address_country` varchar(255) DEFAULT NULL,
  `mailing_address_postal_code` varchar(255) DEFAULT NULL,
  `mailing_address_street` varchar(255) DEFAULT NULL,
  `primary_address_city` varchar(255) NOT NULL,
  `primary_address_country` varchar(255) NOT NULL,
  `primary_address_postal_code` varchar(255) NOT NULL,
  `primary_address_street` varchar(255) NOT NULL,
  `id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`id`) REFERENCES `user` (`id`)
);


-- ---------- accounts -----------

CREATE TABLE IF NOT EXISTS `account` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `account_type` varchar(255) NOT NULL,
  `balance_amount` decimal(19,2) NOT NULL,
  `balance_currency` varchar(255) NOT NULL,
  `creation_date` datetime(6) NOT NULL,
  `last_penalty_fee_check` date NOT NULL,
  `penalty_fee_amount` decimal(19,2) NOT NULL,
  `penalty_fee_currency` varchar(255) NOT NULL,
  `primary_owner_id` bigint NOT NULL,
  `secondary_owner_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`secondary_owner_id`) REFERENCES `account_holder` (`id`),
  FOREIGN KEY (`primary_owner_id`) REFERENCES `account_holder` (`id`)
);

CREATE TABLE IF NOT EXISTS `credit_card_account` (
  `credit_limit_amount` decimal(19,2) NOT NULL,
  `credit_limit_currency` varchar(255) NOT NULL,
  `interest_rate` decimal(16,4) NOT NULL,
  `last_interest_update` date NOT NULL,
  `id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`id`) REFERENCES `account` (`id`)
);

CREATE TABLE IF NOT EXISTS `savings_account` (
  `status` varchar(255) NOT NULL,
  `interest_rate` decimal(16,4) NOT NULL,
  `last_interest_update` date NOT NULL,
  `min_balance_amount` decimal(19,2) NOT NULL,
  `min_balance_currency` varchar(255) NOT NULL,
  `secret_key` varchar(255) NOT NULL,
  `id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`id`) REFERENCES `account` (`id`)
);

CREATE TABLE IF NOT EXISTS `checking_account` (
  `status` varchar(255) NOT NULL,
  `last_maintenance_fee` date NOT NULL,
  `min_balance_amount` decimal(19,2) NOT NULL,
  `min_balance_currency` varchar(255) NOT NULL,
  `monthly_maintenance_fee_amount` decimal(19,2) NOT NULL,
  `monthly_maintenance_fee_currency` varchar(255) NOT NULL,
  `secret_key` varchar(255) NOT NULL,
  `id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`id`) REFERENCES `account` (`id`)
);

CREATE TABLE IF NOT EXISTS `student_checking_account` (
  `id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`id`) REFERENCES `checking_account` (`id`)
);


-- ---------- transaction and receipt -----------

CREATE TABLE IF NOT EXISTS `transaction` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `base_amount` decimal(19,2) NOT NULL,
  `base_currency` varchar(255) NOT NULL,
  `converted_amount` decimal(19,2) NOT NULL,
  `converted_currency` varchar(255) NOT NULL,
  `operation_date` datetime(6) NOT NULL,
  `status` varchar(255) NOT NULL,
  `transaction_purpose` varchar(255) DEFAULT NULL,
  `base_account_id` bigint DEFAULT NULL,
  `target_account_id` bigint NOT NULL,
  `target_owner_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`base_account_id`) REFERENCES `account` (`id`),
  FOREIGN KEY (`target_owner_id`) REFERENCES `account_holder` (`id`),
  FOREIGN KEY (`target_account_id`) REFERENCES `account` (`id`)
);

CREATE TABLE IF NOT EXISTS `receipt` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `base_amount` decimal(19,2) NOT NULL,
  `base_currency` varchar(255) NOT NULL,
  `details` varchar(255) NOT NULL,
  `operation_date` datetime(6) NOT NULL,
  `status` varchar(255) NOT NULL,
  `transaction_type` varchar(255) NOT NULL,
  `external_account_id` bigint DEFAULT NULL,
  `account_id` bigint NOT NULL,
  `transaction_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`account_id`) REFERENCES `account` (`id`),
  FOREIGN KEY (`external_account_id`) REFERENCES `account` (`id`),
  FOREIGN KEY (`transaction_id`) REFERENCES `transaction` (`id`)
);


-- ---------- create admin with ADMIN role -----------

INSERT INTO `user`(`id`,`name`,`password`,`username`)
VALUES("1","Admin","$2a$12$/8T7G8Pv.9rl7HZRhqCy3O86U4II2VdmnAaJaIJsSvfxR7KYo7R6S","admin") -- password="admin"
ON DUPLICATE KEY update `id` = VALUES(`id`), `name` = VALUES(`name`), `password` = VALUES(`password`), `username` = VALUES(`username`);

INSERT INTO `admin`(`id`)
VALUES("1")
ON DUPLICATE KEY update `id` = VALUES(`id`);

INSERT INTO `roles`(`id`,`name`)
VALUES("1","ADMIN")
ON DUPLICATE KEY update `id` = VALUES(`id`), `name` = VALUES(`name`);

INSERT INTO `users_roles`(`user_id`,`role_id`)
VALUES(1,1)
ON DUPLICATE KEY update `user_id` = VALUES(`user_id`), `role_id` = VALUES(`role_id`);
