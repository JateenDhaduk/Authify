#!/bin/bash
# Insert a verified user into the MySQL database
mysql -u root -e "CREATE DATABASE IF NOT EXISTS authify_db;"
mysql -u root -e "USE authify_db; INSERT INTO users (username, email, phone, password, role, is_verified, created_at, updated_at) VALUES ('verifieduser', 'verified@test.com', '1234567890', '\$2a\$10\$K/B.J9... (hashed password needed) ...', 'USER', true, NOW(), NOW()) ON DUPLICATE KEY UPDATE is_verified=true;"
