INSERT INTO `usuario` (`id`, `administrador`, `apellidos`, `dni`, `enabled`, `fecha_nacimiento`, `nombre`, `password`, `telefono`) VALUES (NULL, b'1', 'Curras', '45158726C', b'001', '2019-11-01', 'Alex', '$2a$04$JG59zE940cTgfuWODJa3de.hxdWpkV5FH.HONdwOEbP6f93yGiZvi', '697217053');
INSERT INTO `usuario` (`id`, `administrador`, `apellidos`, `dni`, `enabled`, `fecha_nacimiento`, `nombre`, `password`, `telefono`) VALUES (NULL, b'1', 'Duque', '12345678A', b'001', '2019-11-02', 'Dani', '$2a$04$JG59zE940cTgfuWODJa3de.hxdWpkV5FH.HONdwOEbP6f93yGiZvi', '123456789')

INSERT INTO `authority` (`id`, `authority`) VALUES ('1', 'ROLE_ADMIN'), ('2', 'ROLE_USER');
INSERT INTO `authorities_users` (`usuario_id`, `authority_id`) VALUES ('1', '1'), ('1', '2'), ('2','2');