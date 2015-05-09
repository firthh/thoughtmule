-- name: insert-user<!
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


-- name: get-authorised-user
-- gets a user based on an authorisation token
SELECT * FROM users
WHERE id in
      (SELECT user_id
       FROM user_tokens
       WHERE token=:token
       AND last_access > current_timestamp - interval '1 hour')

-- name: delete-user!
-- deletes a user
DELETE FROM users
where email_address=:email_address

-- name: delete-auth-tokens!
-- delete all auth tokens for a user
DELETE from user_tokens
WHERE user_id in (SELECT id from users WHERE email_address=:email_address)
