(ns thoughtmule.handler
  (:use [ring.middleware.json :only [wrap-json-response wrap-json-body]])
  (:require [compojure.core :refer [GET POST defroutes]]
            [compojure.route :refer [not-found resources]]
            [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
            [selmer.parser :refer [render-file]]
            [prone.middleware :refer [wrap-exceptions]]
            [environ.core :refer [env]]
            [ring.util.response :as response]
            [clojure.data.json :as json]
            [ragtime.sql.files :as files]
            [ragtime.core :as rt]
            [clojure.data.json :as json]
            [buddy.hashers :as hashers]
            [thoughtmule.db :as db]
            [thoughtmule.users :as users]
            [validateur.validation :refer :all]))

(def db-url "jdbc:postgresql://localhost/thoughtmule")

(defn success [body]
  (response/content-type (response/response body) "application/json"))

(defn invalid [body]
  (response/content-type (response/status (response/response body) 400) "application/json"))

(defn exists? [email-address]
  (:exist (first (db/user-exists? db-url email-address))))

(defn register [user]
  (if (or (invalid? users/User user) (not (= (:password user) (:confirm-password user))))
    (invalid {:message "not a valid user"})
    (if (exists? (:email user))
      (invalid {:message "user already exists"})
      (do (db/insert-user! db-url (:email user) (hashers/encrypt (:password user)))
          (success {:message "success"})))))

(defroutes routes
  (POST "/register" req
        (register (:body req)))
  (GET "/" [] (render-file "templates/index.html" {:dev (env :dev?)}))

  (resources "/")
  (not-found "Not Found"))

(defn init []
  (let [conn (rt/connection db-url)
        migrations (files/migrations "resources/migrations")]
    (println (rt/migrate-all conn migrations))))

(def app
  (let [handler (-> routes
                    (wrap-defaults api-defaults)
                    (wrap-json-body {:keywords? true :bigdecimals? true})
                    wrap-json-response)]
    (if (env :dev?) (wrap-exceptions handler) handler)))
