(ns travelmule.users
  (:require [travelmule.db :as db]
            [travelmule.helpers :refer :all]
            [ring.util.response :as response]
            [buddy.hashers :as hashers]
            [buddy.core.nonce :as nonce]
            [validations.users :refer :all]))

(defprotocol UserProtocol
  (exists? [this email-address])
  (register [this user])
  (login [this user])
  (authenticate [this token]))

(defrecord Users [db-url]
  UserProtocol
  (exists? [_ email-address]
    (:exist (first (db/user-exists? db-url email-address))))

  (register [this user]
    (if (invalid-user? user)
      (invalid {:message "not a valid user"})
      (if (.exists? this (:email user))
        (invalid {:message "user already exists"})
        (do (db/insert-user<! db-url (:email user) (hashers/encrypt (:password user)))
            (success {:message "success"})))))

  (login [this user]
    (let [db-user (first (db/get-user db-url (:email user)))]
      (if (hashers/check (:password user) (:password db-user))
        (let [token (str (java.util.UUID/randomUUID))]
          (db/add-user-token! db-url token (:id db-user))
          (success {:token token}))
        (unauthorized))))
  (authenticate [this token]
    (first (db/get-authorised-user db-url token))))
