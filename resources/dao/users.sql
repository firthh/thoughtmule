-- name: insert-user!
-- inserts a single user
INSERT INTO users
(email_address, password)
VALUES
(:email_address, :password)
