(ns travelmule.handler
  (:use [ring.middleware.json :only [wrap-json-response wrap-json-body]])
  (:require [compojure.core :refer [GET POST defroutes]]
            [compojure.route :refer [not-found resources]]
            [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
            [buddy.auth.backends.token :refer [token-backend]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [selmer.parser :refer [render-file]]
            [prone.middleware :refer [wrap-exceptions]]
            [environ.core :refer [env]]
            [ring.util.response :as response]
            [clojure.data.json :as json]
            [ragtime.sql.files :as files]
            [ragtime.core :as rt]
            [clojure.data.json :as json]
            [travelmule.users :refer :all]
            [travelmule.helpers :refer :all]))

(def db-url "jdbc:postgresql://localhost/travelmule?user=postgres")

(def users (->Users db-url))

(defn authenticate-token [req token]
  (.authenticate users token))

(def auth-backend (token-backend {:authfn authenticate-token}))

(defn init []
  (let [conn (rt/connection db-url)
        migrations (files/migrations "resources/migrations")]
    (println (rt/migrate-all conn migrations))))

(defroutes public-routes
  (POST "/register" req
        (.register users (:body req)))
  (POST "/login" req
        (.login users (:body req)))
  (GET "/" [] (render-file "templates/index.html" {:dev (env :dev?)}))

  (resources "/"))

(defn wrap-auth [handler]
  (fn [req]
    (if (authenticated? req)
      (handler req)
      (unauthorized))))

(defroutes secure-routes
  (GET "/requests" req (success {})))

(defroutes routes
  public-routes
  (-> secure-routes wrap-auth)
  (not-found "Not Found"))

(def app
  (let [handler (-> routes
                    (wrap-authentication auth-backend)
                    (wrap-authorization auth-backend)
                    (wrap-defaults api-defaults)
                    (wrap-json-body {:keywords? true :bigdecimals? true})
                    wrap-json-response)]
    (if (env :dev?) (wrap-exceptions handler) handler)))
