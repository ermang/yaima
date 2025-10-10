INSERT INTO app_user (username, password, version) VALUES ('alice', 'alice', 0);
INSERT INTO app_user (username, password, version) VALUES ('bob', 'bob',0);
INSERT INTO app_user (username, password, version) VALUES ('eve', 'eve', 0);


INSERT INTO app_friend (app_user_id, app_friend_id, version) VALUES ((SELECT id FROM app_user WHERE username = 'alice'), (SELECT id FROM app_user WHERE username = 'bob'), 0);
INSERT INTO app_friend (app_user_id, app_friend_id, version) VALUES ((SELECT id FROM app_user WHERE username = 'alice'), (SELECT id FROM app_user WHERE username = 'eve'), 0);

INSERT INTO app_friend (app_user_id, app_friend_id, version) VALUES ((SELECT id FROM app_user WHERE username = 'bob'), (SELECT id FROM app_user WHERE username = 'alice'), 0);
INSERT INTO app_friend (app_user_id, app_friend_id, version) VALUES ((SELECT id FROM app_user WHERE username = 'bob'), (SELECT id FROM app_user WHERE username = 'eve'), 0);

INSERT INTO app_friend (app_user_id, app_friend_id, version) VALUES ((SELECT id FROM app_user WHERE username = 'eve'), (SELECT id FROM app_user WHERE username = 'alice'), 0);
INSERT INTO app_friend (app_user_id, app_friend_id, version) VALUES ((SELECT id FROM app_user WHERE username = 'eve'), (SELECT id FROM app_user WHERE username = 'bob'), 0);
