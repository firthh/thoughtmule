-- name: insert-user!
-- inserts a single user
INSERT INTO users
(email_address, password)
VALUES
(:email_address, :password)


-- name: user-exists?
-- checks if a user exists
SELECT count(*) > 0 as exist FROM users
WHERE email_address=:email_address
