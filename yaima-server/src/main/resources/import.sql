INSERT INTO app_user (username, version) VALUES ('alice', 0);
INSERT INTO app_user (username, version) VALUES ('bob', 0);
INSERT INTO app_user (username, version) VALUES ('eve', 0);


INSERT INTO app_friend (app_user_id, app_friend_id, version) VALUES ((SELECT id FROM app_user WHERE username = 'alice'), (SELECT id FROM app_user WHERE username = 'bob'), 0);
INSERT INTO app_friend (app_user_id, app_friend_id, version) VALUES ((SELECT id FROM app_user WHERE username = 'alice'), (SELECT id FROM app_user WHERE username = 'eve'), 0);

INSERT INTO app_friend (app_user_id, app_friend_id, version) VALUES ((SELECT id FROM app_user WHERE username = 'bob'), (SELECT id FROM app_user WHERE username = 'alice'), 0);
INSERT INTO app_friend (app_user_id, app_friend_id, version) VALUES ((SELECT id FROM app_user WHERE username = 'bob'), (SELECT id FROM app_user WHERE username = 'eve'), 0);

INSERT INTO app_friend (app_user_id, app_friend_id, version) VALUES ((SELECT id FROM app_user WHERE username = 'eve'), (SELECT id FROM app_user WHERE username = 'alice'), 0);
INSERT INTO app_friend (app_user_id, app_friend_id, version) VALUES ((SELECT id FROM app_user WHERE username = 'eve'), (SELECT id FROM app_user WHERE username = 'bob'), 0);
