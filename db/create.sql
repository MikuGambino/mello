drop table user_role;
drop table users;
drop table roles;
drop table subtask;
drop table task;
drop table list;
drop table project;


create table users(
	id int PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
	password text NOT NULL,
	username text NOT NULL UNIQUE,
	email text NOT NULL UNIQUE,
	image text DEFAULT 'default.png'
);


create table roles (
	id int PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
	title text NOT NULL UNIQUE
);

create table user_role (
	user_id int NOT NULL,
	role_id int NOT NULL,
	PRIMARY KEY (user_id, role_id),
	FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
	FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

create table project (
	id int PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
	owner_id int NOT NULL,
	title text NOT NULL,
	color text NOT NULL DEFAULT '#D9D9D9',
	created_at timestamp,
	is_active bool,
	FOREIGN KEY (owner_id) REFERENCES users (id)
);

create table list (
	id int PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
	project_id int NOT NULL,
	title text NOT NULL,
	FOREIGN KEY (project_id) REFERENCES project (id) ON DELETE CASCADE
);

create table task (
	id int PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
	list_id int NOT NULL,
	positional int NOT NULL,
	title text NOT NULL,
	description text DEFAULT '',
	FOREIGN KEY (list_id) REFERENCES list (id) ON DELETE CASCADE
);

create table subtask (
	id int PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
	task_id int NOT NULL,
	title text NOT NULL,
	is_complete bool DEFAULT false,
	FOREIGN KEY (task_id) REFERENCES task (id) ON DELETE CASCADE
);

create table user_project(
	user_id int NOT NULL,
	project_id int NOT NULL,
	PRIMARY KEY (user_id, project_id),
	FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
	FOREIGN KEY (project_id) REFERENCES project (id) ON DELETE CASCADE
);

create table invitations(
	user_id int NOT NULL,
	project_id int NOT NULL,
	PRIMARY KEY (user_id, project_id),
	FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
	FOREIGN KEY (project_id) REFERENCES project (id) ON DELETE CASCADE
);

INSERT INTO roles(id, title) 
VALUES
(1, 'ROLE_ADMIN'),
(2, 'ROLE_USER');

select * from users;
select * from subtask;
select * from task;
select * from invitations;
select * from user_project;
