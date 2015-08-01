(ns clj.handler
  (:use midje.sweet)
  (:require [travelmule.handler :refer :all]
            [travelmule.db :as db]
            [ring.mock.request :as mock]
            [ragtime.core :as rt]
            [clojure.java.jdbc :as jdbc]
            [yesql.core :refer [defqueries]]))

(against-background [(before :facts (do (init)
                                        (let [user (db/insert-user<! db-url "test@test.com" "password")]
                                          (db/add-user-token! db-url "token" (:id user)))))
                     (after :facts (do (db/delete-auth-tokens! db-url "test@test.com")
                                       (db/delete-user! db-url "test@test.com")))]

                    (fact
                      (app (mock/request :get "/requests")) => {:status  401
                                                                :headers {"Content-Type" "application/json"}
                                                                :body    "{}"})
                    (fact
                      (app (mock/header (mock/request :get "/requests") "authorization" "Token token")) => {:status  200
                                                                                                            :headers {"Content-Type" "application/json"}
                                                                                                            :body    "{}"}))
