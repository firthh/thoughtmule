(ns thoughtmule.helpers
  (:require [ring.util.response :as response]))


(defn success [body]
  (response/content-type (response/response body) "application/json"))

(defn invalid [body]
  (response/content-type (response/status (response/response body) 400) "application/json"))

(defn Unauthorized []
  (response/content-type (response/status (response/response {}) 403) "application/json"))
