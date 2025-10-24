-- Seed: minimal but useful demo data

-- Users
INSERT INTO app_user (name, email) VALUES
    ('Solano Tocalino', 'solanotocalino@gmail.com'),
    ('Jane Doe', 'jane.doe@example.com');

-- Projects (tie by email to avoid guessing IDs)
INSERT INTO project (name, description, owner_id) 
SELECT 'Personal Board', 'Personal tasks', u.id FROM app_user u WHERE u.email='solanotocalino@gmail.com';
INSERT INTO project (name, description, owner_id) 
SELECT 'Demo Project', 'Sample backlog', u.id FROM app_user u WHERE u.email='jane.doe@example.com';

-- Tasks for Personal Board
INSERT INTO task (title, description, status, priority, due_date, project_id)
SELECT 'Set up project', 'Initialize Spring Boot + Flyway', 'DONE', 'HIGH', CURRENT_DATE, p.id
FROM project p WHERE p.name='Personal Board';

INSERT INTO task (title, description, status, priority, due_date, project_id)
SELECT 'Create CRUD endpoints', 'Users/Projects/Tasks basic CRUD', 'DOING', 'HIGH', CURRENT_DATE + INTERVAL 3 DAY, p.id
FROM project p WHERE p.name='Personal Board';

INSERT INTO task (title, description, status, priority, due_date, project_id)
SELECT 'Write README', 'Add run instructions', 'TODO', 'MEDIUM', CURRENT_DATE + INTERVAL 7 DAY, p.id
FROM project p WHERE p.name='Personal Board';

-- Tasks for Demo Project
INSERT INTO task (title, description, status, priority, due_date, project_id)
SELECT 'Design schema', 'Tables & constraints', 'DONE', 'MEDIUM', CURRENT_DATE - INTERVAL 1 DAY, p.id
FROM project p WHERE p.name='Demo Project';

INSERT INTO task (title, description, status, priority, due_date, project_id)
SELECT 'Implement pagination', 'Simple page + size', 'TODO', 'LOW', NULL, p.id
FROM project p WHERE p.name='Demo Project';
