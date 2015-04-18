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
            [clojure.data.json :as json]))

(def db-url "jdbc:postgresql://localhost/thoughtmule")

(defqueries "dao/users.sql")

(defn register [user]
  (println user))

(defroutes routes
  (GET "/test" [] (response/content-type
                   (response/response "{'test': 'hello'}")
                   "application/json"))
  (POST "/register" req (do
                          (register (:body req))
                          (response/response "hello")))
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
