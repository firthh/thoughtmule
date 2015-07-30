(ns travelmule.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [cljsjs.react :as react]
            [ajax.core :refer [GET POST]]
            [travelmule.views.home :as home]
            [travelmule.views.register :as register])
  (:import goog.History))

;; -------------------------
;; Views

(defn message-component [message]
  [:div {:class (:class message)} (:message message)])

(defn current-page []
  [:div
   [:div (message-component (session/get :message))]
   [:div [(session/get :current-page)]]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :current-page #'home/home-page))

(secretary/defroute "/register" []
  (session/put! :current-page #'register/register-page))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (hook-browser-navigation!)
  (mount-root))
