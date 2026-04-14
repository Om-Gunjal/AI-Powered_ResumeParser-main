BEGIN TRANSACTION;

-- *************************************************************************************************
-- Drop all db objects in the proper order
-- *************************************************************************************************
DROP TABLE IF EXISTS users;

-- *************************************************************************************************
-- Create the tables and constraints
-- *************************************************************************************************

--users (name is pluralized because 'user' is a SQL keyword)
CREATE TABLE users (
	user_id SERIAL,
	username varchar(50) NOT NULL UNIQUE,
	password_hash varchar(200) NOT NULL,
	role varchar(50) NOT NULL,
	CONSTRAINT PK_user PRIMARY KEY (user_id)
);

-- *************************************************************************************************
-- Insert some sample starting data
-- *************************************************************************************************

-- Users
-- Password for all users is password
INSERT INTO
    users (username, password_hash, role)
VALUES
    ('user', '$2a$10$tmxuYYg1f5T0eXsTPlq/V.DJUKmRHyFbJ.o.liI1T35TFbjs2xiem','ROLE_USER'),
    ('admin','$2a$10$tmxuYYg1f5T0eXsTPlq/V.DJUKmRHyFbJ.o.liI1T35TFbjs2xiem','ROLE_ADMIN');


DROP TABLE IF EXISTS applicant_role;
DROP TABLE IF EXISTS applicant;
DROP TABLE IF EXISTS roles;


-- Applicant table
CREATE TABLE applicant (
  id SERIAL PRIMARY KEY,
  firstname VARCHAR(100),
  lastname VARCHAR(100),
  email VARCHAR(200) UNIQUE,
  created_at timestamp DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp DEFAULT CURRENT_TIMESTAMP  
);


-- Roles table
CREATE TABLE roles (
  id SERIAL PRIMARY KEY,
  role_name VARCHAR(100) NOT NULL UNIQUE
);


-- applicant_role join table (stores the fitness rating)
CREATE TABLE applicant_role (
  applicant_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  fitness INT NOT NULL,
  PRIMARY KEY (applicant_id, role_id),
  FOREIGN KEY (applicant_id) REFERENCES applicant(id),
  FOREIGN KEY (role_id) REFERENCES roles(id)
);

COMMIT TRANSACTION;
