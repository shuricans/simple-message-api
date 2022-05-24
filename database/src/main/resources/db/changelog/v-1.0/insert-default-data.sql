INSERT INTO users (username, password)
VALUES ('user', '$2a$12$hqfFS9uY9PZiukO0U84fUeGZQithhej6WmdztEKfm1H7MrldmoInK');

INSERT INTO roles (name)
VALUES ('ROLE_USER'), ('ROLE_MODERATOR'), ('ROLE_ADMIN');

INSERT INTO users_roles (user_id, role_id)
SELECT (SELECT user_id FROM users WHERE username = 'user'), (SELECT role_id FROM roles WHERE name = 'ROLE_USER');

INSERT INTO messages (text, created_time, user_id)
VALUES ('1. My first message.',  '2022-05-23 15:44:48', 1),
       ('2. My second message.', '2022-05-23 15:45:48', 1),
       ('3. My third message.',  '2022-05-23 15:46:48', 1),
       ('4. My fourth message.', '2022-05-23 15:47:48', 1),
       ('5. My fifth message.',  '2022-05-23 15:50:48', 1),
       ('6. My sixth message.',  '2022-05-23 15:51:48', 1),
       ('7. My seventh message.','2022-05-23 15:55:48', 1);