-- Create test database and assign 'admin' user permissions
CREATE DATABASE IF NOT EXISTS taskboard_test
    CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

GRANT ALL PRIVILEGES ON taskboard_test.* TO 'admin'@'%';
FLUSH PRIVILEGES;