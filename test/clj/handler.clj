(ns clj.handler
  (:require [thoughtmule.handler :refer :all]
            [thoughtmule.db :as db]
            [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [ragtime.sql.files :as files]
            [ragtime.core :as rt]
            [clojure.java.jdbc :as jdbc]
            [yesql.core :refer [defqueries]]))



(init)
(let [user (db/insert-user<! db-url "test@test.com" "password")]
  (db/add-user-token! db-url "token" (:id user)))

(deftest handler-test
  (is (= {:status  401
          :headers {"Content-Type" "application/json"}
          :body    "{}"}
         (app (mock/request :get "/requests"))))
  (is (= {:status  200
          :headers {"Content-Type" "application/json"}
          :body    "{}"}
         (app (mock/header (mock/request :get "/requests") "authorization" "Token token")))))

(db/delete-auth-tokens! db-url "test@test.com")
(db/delete-user! db-url "test@test.com")
