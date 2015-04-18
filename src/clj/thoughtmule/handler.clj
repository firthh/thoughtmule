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
            [yesql.core :refer [defqueries]]
            [clojure.data.json :as json]
            [buddy.hashers :as hashers]
            [validateur.validation :refer :all]))

(def db-url "jdbc:postgresql://localhost/thoughtmule")

(defqueries "dao/users.sql")

(def User (validation-set
           (format-of :email
                      :format #"^.*@.*$"
                      :message "Must be a valid email address")
           (length-of :password :within (range 6 100))
           (presence-of :confirm-password)))


(defn register [user]
  (if (or (invalid? User user) (not (= (:password user) (:confirm-password user))))
    (response/response "not a valid user")
    (do (insert-user! db-url (:email user) (hashers/encrypt (:password user)))
        (response/response "success"))))

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
                    )]
    (if (env :dev?) (wrap-exceptions handler) handler)))
