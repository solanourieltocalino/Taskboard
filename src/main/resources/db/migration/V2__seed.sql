-- Seed: minimal but useful demo data

-- Users
INSERT INTO app_user (name, email) VALUES
    ('Solano Tocalino', 'solanotocalino@example.com'),
    ('Nicolas Ansich', 'nicolas.ansich@example.com');

-- Projects (tie by email to avoid guessing IDs)
INSERT INTO project (name, description, owner_id) 
SELECT 'Spring Boot', 'Personal project tasks', u.id FROM app_user u WHERE u.email='solanotocalino@example.com';
INSERT INTO project (name, description, owner_id) 
SELECT 'React Project', 'Basic react project', u.id FROM app_user u WHERE u.email='nicolas.ansich@example.com';

-- Tasks for Spring Boot
INSERT INTO task (title, description, status, priority, due_date, project_id)
SELECT 'Build Environment', 'Build basic environment', 'DONE', 'HIGH', CURRENT_DATE, p.id
FROM project p WHERE p.name='Spring Boot';

INSERT INTO task (title, description, status, priority, due_date, project_id)
SELECT 'Quick Set Up', 'Initialize Spring Boot + Flyway', 'DONE', 'HIGH', CURRENT_DATE, p.id
FROM project p WHERE p.name='Spring Boot';

INSERT INTO task (title, description, status, priority, due_date, project_id)
SELECT 'Create CRUD endpoints', 'Users/Projects/Tasks basic CRUD', 'DOING', 'HIGH', CURRENT_DATE + INTERVAL 3 DAY, p.id
FROM project p WHERE p.name='Spring Boot';

INSERT INTO task (title, description, status, priority, due_date, project_id)
SELECT 'Write README', 'Add run instructions', 'TODO', 'MEDIUM', CURRENT_DATE + INTERVAL 7 DAY, p.id
FROM project p WHERE p.name='Spring Boot';

-- Tasks for React Project
INSERT INTO task (title, description, status, priority, due_date, project_id)
SELECT 'Build Environment', 'Build basic environment', 'TODO', 'MEDIUM', CURRENT_DATE - INTERVAL 1 DAY, p.id
FROM project p WHERE p.name='React Project';

INSERT INTO task (title, description, status, priority, due_date, project_id)
SELECT 'Quick Set Up', 'Initial basic set up', 'TODO', 'MEDIUM', NULL, p.id
FROM project p WHERE p.name='React Project';
