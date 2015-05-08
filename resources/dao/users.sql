-- name: insert-user!
-- inserts a single user
INSERT INTO users
(email_address, password)
VALUES
(:email_address, :password)


-- name: user-exists?
-- checks if a user exists
SELECT count(*) > 0 as exist
FROM users
WHERE email_address=:email_address

-- name: get-user
-- gets user by email address
SELECT *
FROM users
WHERE email_address=:email_address

-- name: add-user-token!
-- adds a new authentication token into the db for a user
INSERT INTO user_tokens
(token, user_id)
VALUES
(:token, :user_id)
