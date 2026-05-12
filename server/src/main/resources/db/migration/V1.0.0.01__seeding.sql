-- Seeding inicial de prueba
INSERT INTO `user` (username, email, password, first_name, last_name, identity, email_settings)
VALUES
('admin', 'admin@portalasig.ucv.ve', '$2a$10$YJCEI8dDtdCIavcqrgC3K.f09Cy5OQtuICola6FwGWUxUAy5cd5d6', 'Administrador', 'Sistema', 12345678, 'ALL'),
('profesor', 'profesor@portalasig.ucv.ve', '$2a$10$YJCEI8dDtdCIavcqrgC3K.f09Cy5OQtuICola6FwGWUxUAy5cd5d6', 'Profesor', 'Prueba', 22345678, 'ALL'),
('estudiante', 'estudiante@portalasig.ucv.ve', '$2a$10$YJCEI8dDtdCIavcqrgC3K.f09Cy5OQtuICola6FwGWUxUAy5cd5d6', 'Estudiante', 'Prueba', 32345678, 'ALL');

-- Asignar roles a los usuarios creados
-- admin -> ADMIN (role_id=1)
INSERT INTO user_role_link (user_id, role_id) 
SELECT user_id, 1 FROM `user` WHERE username = 'admin';

-- profesor -> PROFESSOR (role_id=4)
INSERT INTO user_role_link (user_id, role_id) 
SELECT user_id, 4 FROM `user` WHERE username = 'profesor';

-- estudiante -> STUDENT (role_id=3)
INSERT INTO user_role_link (user_id, role_id) 
SELECT user_id, 3 FROM `user` WHERE username = 'estudiante';
