CREATE TABLE `user`
(
  user_id        BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'userEntity id',
  username       VARCHAR(250) NOT NULL COMMENT 'username',
  email          VARCHAR(250) COMMENT 'email',
  password       VARCHAR(100) NOT NULL COMMENT 'password',
  first_name     VARCHAR(100) COMMENT 'first name',
  last_name      VARCHAR(100) COMMENT 'last name',
  identity       BIGINT       NOT NULL COMMENT 'identity number',
  email_settings VARCHAR(128) COMMENT 'email settings',
  created_date   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'date and time this row was created',
  updated_date   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'date and time this row was last updated',
  PRIMARY KEY (user_id),
  UNIQUE KEY user_idx1 (username),
  UNIQUE KEY user_idx2 (`identity`),
  UNIQUE KEY user_idx3 (email),
  INDEX          user_idx4 (first_name),
  INDEX          user_idx5 (last_name)
) COMMENT 'Holds all information related with portalasig users';

CREATE TABLE role
(
  role_id      BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'role id',
  role         VARCHAR(100) NOT NULL COMMENT 'role name',
  description  VARCHAR(250) NOT NULL COMMENT 'role description',
  created_date datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'date and time this row was created',
  updated_date datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'date and time this row was last updated',
  PRIMARY KEY (role_id),
  KEY          role_idx1 (role)
) COMMENT 'Handle access roles for the application';

CREATE TABLE user_role_link
(
  user_role_link_id BIGINT    NOT NULL AUTO_INCREMENT COMMENT 'userEntity roleEntity id',
  user_id           BIGINT    NOT NULL COMMENT 'fk userEntity id',
  role_id           BIGINT    NOT NULL COMMENT 'fk roleEntity id',
  created_date      datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'date and time this row was created',
  updated_date      datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'date and time this row was last updated',
  PRIMARY KEY (user_role_link_id),
  KEY               user_role_link_idx1 (user_id),
  KEY               user_role_link_idx2 (role_id)
) COMMENT 'relationship table between user and roles';

CREATE TABLE client
(
  id                     BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'client id',
  client_id              VARCHAR(255) NOT NULL COMMENT 'string client identifier',
  name                   VARCHAR(100) NOT NULL COMMENT 'client name',
  secret                 VARCHAR(250) NOT NULL COMMENT 'client secret',
  scopes                 VARCHAR(250) NOT NULL COMMENT 'client scope',
  grant_types            VARCHAR(250) NOT NULL COMMENT 'client grant type',
  authentication_methods VARCHAR(250) NOT NULL COMMENT 'client auth methods',
  redirect_uri           VARCHAR(250) NOT NULL COMMENT 'redirect uri for client',
  redirect_uri_logout    VARCHAR(250) NOT NULL COMMENT 'redirect uri logout for client',
  created_date           datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'date and time this row was created',
  updated_date           datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'date and time this row was last updated',
  PRIMARY KEY (id),
  KEY                    client_idx1 (name)
) COMMENT 'Handles oauth2 clients for the login process';

-- BCrypt hash for 'p0rt4l4s1g'
-- (strength 10)
INSERT INTO client (client_id, name, secret, scopes, grant_types, authentication_methods, redirect_uri, redirect_uri_logout)
VALUES
('portalasig_engine', 'portalasig_engine', '$2a$10$7QJQ1dVYQxY8QJv1YzF7QeY8k9vHq7ZC2Qe9l8QwFz3Yk5m1Z7n2K', '', 'client_credentials', '', '', ''),
('portalasig_client', 'portalasig_client', '$2a$10$7QJQ1dVYQxY8QJv1YzF7QeY8k9vHq7ZC2Qe9l8QwFz3Yk5m1Z7n2K', 'openid,profile,email,phone,address', 'password', '', '', '');
