(ns thoughtmule.users
  (:require [validateur.validation :refer :all]
            [thoughtmule.db :as db]
            [thoughtmule.helpers :refer :all]
            [ring.util.response :as response]
            [buddy.hashers :as hashers]
            [buddy.core.nonce :as nonce]))

(defn- attributes-equal
  [attribute1 attribute2]
  (fn [m]
    (let [value1   (attribute1 m)
          value2   (attribute2 m)
          invalid (not (= value1 value2))]
      (if invalid
        {attribute1 #{(str " must equal " (name attribute2))}
         attribute2 #{(str " must equal " (name attribute1))}}
        nil))))

(defn equal [field1 field2]
  (let [validator (attributes-equal field1 field2)]
    (fn [m]
      (let [result (validator m)]
        [(nil? result) result]))))

(def user-validation
  (validation-set
   (format-of :email
              :format #"^.*@thoughtworks.com$"
              :message "Must be a valid email address")
   (length-of :password :within (range 6 100))
   (presence-of :confirm-password)
   (equal :password :confirm-password)))

(defprotocol UserProtocol
  (exists? [this email-address])
  (register [this user])
  (login [this user]))

(defrecord Users [db-url]
  UserProtocol
  (exists? [_ email-address]
    (:exist (first (db/user-exists? db-url email-address))))

  (register [this user]
    (if (invalid? user-validation user)
      (invalid {:message "not a valid user"})
      (if (.exists? this (:email user))
        (invalid {:message "user already exists"})
        (do (db/insert-user! db-url (:email user) (hashers/encrypt (:password user)))
            (success {:message "success"})))))

  (login [this user]
    (let [db-user (first (db/get-user db-url (:email user)))]
      (println db-user)
      (if (hashers/check (:password user) (:password db-user))
        (let [token (hashers/encrypt (str (clj-time.core/now)))]
          (db/add-user-token! db-url token (:id db-user))
          (success {:token token}))
        ()
        ))))
