CREATE TABLE user
(
  user_id      BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT 'userEntity id',
  username     VARCHAR(250) NOT NULL COMMENT 'username',
  password     VARCHAR(100) NOT NULL COMMENT 'password',
  first_name   VARCHAR(100) NOT NULL COMMENT 'first name',
  last_name    VARCHAR(100) NOT NULL COMMENT 'last name',
  created_date datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'date and time this row was created',
  updated_date timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'date and time this row was last updated',
  PRIMARY KEY (user_id),
  KEY user_idx1 (username)
)
  COMMENT 'Test userEntity table to handle authentication and authorization for the application';

CREATE TABLE role
(
  role_id      BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT 'role id',
  name         VARCHAR(100) NOT NULL COMMENT 'role name',
  description  VARCHAR(250) NOT NULL COMMENT 'role description',
  created_date datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'date and time this row was created',
  updated_date timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'date and time this row was last updated',
  PRIMARY KEY (role_id),
  KEY role_idx1 (name)
)
  COMMENT 'Test roleEntity table to handle access roles for the application';

CREATE TABLE user_role
(
  user_role_id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'userEntity roleEntity id',
  user_id      BIGINT(20) NOT NULL COMMENT 'fk userEntity id',
  role_id      BIGINT(20) NOT NULL COMMENT 'fk roleEntity id',
  created_date datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'date and time this row was created',
  updated_date timestamp  NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'date and time this row was last updated',
  PRIMARY KEY (user_role_id),
  KEY user_role_idx1 (user_id),
  KEY user_role_idx2 (role_id)
)
  COMMENT 'Test userEntity roleEntity table to handle userEntity roles for the application';
