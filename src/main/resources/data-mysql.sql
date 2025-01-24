-- 密碼預設均為: 1234

INSERT INTO `account_system` (`email`, `name`, `password`, `email_verified`, `role`, `phone`, `email_verification_token`, `token_expiry`, `profile_picture`, `license_number`, `law_firm_number`, `city`, `address`, `law_firm`,`available`)
SELECT 'abc111@gmail.com', 'guest', '$2a$10$6SZapUNtdDWr6fRK0wJ/U.Qf3WApbvguBhZLEPxz1MCEmdKk/CwJW', 1, 'guest', '0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1
WHERE NOT EXISTS (SELECT 1 FROM `account_system` WHERE `email` = 'abc111@gmail.com');

INSERT INTO `account_system` (`email`, `name`, `password`, `email_verified`, `role`, `phone`, `email_verification_token`, `token_expiry`, `profile_picture`, `license_number`, `law_firm_number`, `city`, `address`, `law_firm`,`available`)
SELECT 'abc123@gmail.com', '王小白', '$2a$10$6SZapUNtdDWr6fRK0wJ/U.Qf3WApbvguBhZLEPxz1MCEmdKk/CwJW', 1, 'user', '0912345671', NULL, NULL, NULL, NULL, NULL, '台南市', NULL, NULL, 1
WHERE NOT EXISTS (SELECT 1 FROM `account_system` WHERE `email` = 'abc123@gmail.com');

INSERT INTO `account_system` (`email`, `name`, `password`, `email_verified`, `role`, `phone`, `email_verification_token`, `token_expiry`, `profile_picture`, `license_number`, `law_firm_number`, `city`, `address`, `law_firm`,`available`)
SELECT 'abc456@gmail.com', '陳小黑', '$2a$10$6SZapUNtdDWr6fRK0wJ/U.Qf3WApbvguBhZLEPxz1MCEmdKk/CwJW', 1, 'lawyer', '0912345672', NULL, NULL, NULL, '112臺檢證字第17687號', NULL, NULL, NULL, '陳大律師事務所', 1
WHERE NOT EXISTS (SELECT 1 FROM `account_system` WHERE `email` = 'abc456@gmail.com');

INSERT INTO `account_system` (`email`, `name`, `password`, `email_verified`, `role`, `phone`, `email_verification_token`, `token_expiry`, `profile_picture`, `license_number`, `law_firm_number`, `city`, `address`, `law_firm`,`available`)
SELECT 'abc789@gmail.com', '林小綠', '$2a$10$6SZapUNtdDWr6fRK0wJ/U.Qf3WApbvguBhZLEPxz1MCEmdKk/CwJW', 1, 'lawFirm', '0912345673', NULL, NULL, NULL, NULL, '01234567', NULL, '台南市高發二路320號', NULL, 1
WHERE NOT EXISTS (SELECT 1 FROM `account_system` WHERE `email` = 'abc789@gmail.com');
