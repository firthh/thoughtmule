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
            [thoughtmule.users :refer :all]))

(def db-url "jdbc:postgresql://localhost/thoughtmule")

(def users (atom {}))

(defn init []
  (let [conn (rt/connection db-url)
        migrations (files/migrations "resources/migrations")]
    (println (rt/migrate-all conn migrations))
    (reset! users (->Users db-url))))

(defroutes routes
  (POST "/register" req
        (.register @users (:body req)))
  (POST "/login" req
        (.login @users (:body req)))
  (GET "/" [] (render-file "templates/index.html" {:dev (env :dev?)}))

  (resources "/")
  (not-found "Not Found"))

(def app
  (let [handler (-> routes
                    (wrap-defaults api-defaults)
                    (wrap-json-body {:keywords? true :bigdecimals? true})
                    wrap-json-response)]
    (if (env :dev?) (wrap-exceptions handler) handler)))
