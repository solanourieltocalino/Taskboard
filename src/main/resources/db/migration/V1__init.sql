-- Users
CREATE TABLE app_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Projects
CREATE TABLE project (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    owner_id BIGINT NOT NULL,
    CONSTRAINT fk_project_owner
        FOREIGN KEY (owner_id) REFERENCES app_user(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_project_owner ON project(owner_id);

-- Tasks
CREATE TABLE task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description VARCHAR(1000),
    status ENUM('TODO','DOING','DONE') NOT NULL DEFAULT 'TODO',
    priority ENUM('LOW','MEDIUM','HIGH') NOT NULL DEFAULT 'MEDIUM',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    due_date DATE,
    project_id BIGINT NOT NULL,
    CONSTRAINT fk_task_project
        FOREIGN KEY (project_id) REFERENCES project(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_task_project    ON task(project_id);
CREATE INDEX idx_task_status     ON task(status);
CREATE INDEX idx_task_due_date   ON task(due_date);
