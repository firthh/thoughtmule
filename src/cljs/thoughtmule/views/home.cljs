(ns thoughtmule.views.home
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [cljsjs.react :as react]
            [ajax.core :refer [GET POST]]
            [thoughtmule.views.common :as c]))

(defn- sign-in-form []
  (let [email-address (atom "")
        password (atom "")]
    (fn []
      [:div
       [:div [:label "Email address"] [c/atom-input email-address "text"]]
       [:div [:label "Password"] [c/atom-input password "password"]]
       [:input {:type "button" :value "sign in"
                :on-click #(println "test")}]])))

(defn home-page []
  [:div [:h2 "Welcome to thoughtmule"]
   [:div "Sign In"
    [sign-in-form]]
   [:div [:a {:href "#/register"} "Register"]]])
