(ns thoughtmule.users
  (:require [validateur.validation :refer :all]
            [thoughtmule.db :as db]
            [thoughtmule.helpers :refer :all]
            [ring.util.response :as response]
            [buddy.hashers :as hashers]))

(def user-validation
  (validation-set
   (format-of :email
              :format #"^.*@thoughtworks.com$"
              :message "Must be a valid email address")
   (length-of :password :within (range 6 100))
   (presence-of :confirm-password)))

(defprotocol UserProtocol
  (exists? [this email-address])
  (register [this user]))

(defrecord Users [db-url]
  UserProtocol
  (exists? [_ email-address]
    (:exist (first (db/user-exists? db-url email-address))))
  (register [this user]
    (if (or (invalid? user-validation user) (not (= (:password user) (:confirm-password user))))
      (invalid {:message "not a valid user"})
      (if (.exists? this (:email user))
        (invalid {:message "user already exists"})
        (do (db/insert-user! db-url (:email user) (hashers/encrypt (:password user)))
            (success {:message "success"}))))))
