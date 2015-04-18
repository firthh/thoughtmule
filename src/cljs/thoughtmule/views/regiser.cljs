(ns thoughtmule.views.regiser
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [cljsjs.react :as react]
            [ajax.core :refer [GET POST]]
            [thoughtmule.views.common :as c]))


(defn handler [response]
  (.log js/console (str response)))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console (str "something bad happened: " status " " status-text)))

(defn register-form []
  (let [email-address (atom "")
        password (atom "")
        confirm-password (atom "")]
    (fn []
      [:div {:id "regiseter-form"}
       [:div [:label "Email address"] [c/atom-input email-address "text"]]
       [:div [:label "Password"] [c/atom-input password "password"]]
       [:div [:label "Confirm Password"] [c/atom-input confirm-password "password"]]
       [:input {:type "button" :value "Register"
                :on-click #(POST "/register" {:params {:email @email-address
                                                       :password @password
                                                       :confirm-password @confirm-password}
                                              :response-format :json
                                              :format :json
                                              :keywords? true
                                              :handler handler
                                              :error-handler error-handler})}]])))

(defn register-page []
  [:div [:h2 "Register"]
   [register-form]
   [:div [:a {:href "#/"} "go to the home page"]]])
