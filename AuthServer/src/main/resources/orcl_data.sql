INSERT INTO oauth_client_details (client_id, client_secret, scope, authorized_grant_types, access_token_validity, additional_information)
VALUES ('client1', 'client1', 'USER_COMPLIANCE', 'password,client_credentials', '900', '{}');

INSERT INTO oauth_client_details (client_id, client_secret, scope, authorized_grant_types, access_token_validity, additional_information)
VALUES ('client2', 'client2', 'USER_FINANCIAL', 'password,client_credentials', '900', '{}');

INSERT INTO oauth_client_details (client_id, client_secret, scope, authorized_grant_types, access_token_validity, additional_information)
VALUES ('external1', 'external1', 'EXTERNAL_COMPLIANCE1', 'authorization_code', '900', '{}');

INSERT INTO oauth_client_details (client_id, client_secret, scope, authorized_grant_types, access_token_validity, additional_information)
VALUES ('external2', 'external2', 'EXTERNAL_COMPLIANCE2', 'authorization_code', '900', '{}');

INSERT INTO oauth_client_details (client_id, client_secret, scope, authorized_grant_types, access_token_validity, additional_information)
VALUES ('external3', 'external3', 'EXTERNAL_FINANCIAL', 'authorization_code', '900', '{}');

INSERT INTO oauth_client_details (client_id, client_secret, scope, authorized_grant_types, access_token_validity, additional_information)
VALUES ('demo', 'demo', 'TRUSTED', 'client_credentials', '900', '{}');

INSERT INTO oauth_client_details (client_id, client_secret, scope, authorized_grant_types, access_token_validity, additional_information)
VALUES ('resource', 'resource', 'TRUSTED', 'authorization_code,password,refresh_token,implicit', '900', '{}');

