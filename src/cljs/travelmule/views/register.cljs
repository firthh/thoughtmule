(ns travelmule.views.register
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [cljsjs.react :as react]
            [ajax.core :refer [GET POST]]
            [travelmule.views.common :as c]
            [validations.users :as v]))

(def error (atom ""))

(defn handler [response]
  (.log js/console (str response))
  (.log js/console (secretary/dispatch! "/"))
  (session/put! :message {:message "User successfully registered"
                          :class "success"}))

(defn error-handler [response]
  (.log js/console (str "something bad happened: " (:status response) " " (:status-text response) ". " response))
  (session/put! :message {:message (:message (:response response))
                          :class "error"}))

(defn user [email-address password confirm-password]
  {:email email-address
   :password password
   :confirm-password confirm-password})

(defn send-request [user]
  (if (v/valid-user? user)
    (POST "/register" {:params user
                       :response-format :json
                       :format :json
                       :keywords? true
                       :handler handler
                       :error-handler error-handler})))

(defn error-message [errors]
  (if errors
    [:div {:class "error"} (map (fn [e] [:span e ", "]) errors)]))

(defn register-form []
  (let [form (atom {})
        validation-errors (atom {})]
    (fn []
      (add-watch form :validation (fn [key at old new] (reset! validation-errors (User new))))
      [:div {:id "regiseter-form"}
       [:div [:label "Email address"] [atom-input :email form "text"]]
       [error-message (:email @validation-errors)]
       [:div [:label "Password"] [atom-input :password form "password"]]
       [error-message (:password @validation-errors)]
       [:div [:label "Confirm Password"] [atom-input :confirm-password form "password"]]
       [error-message (:confirm-password @validation-errors)]
       [:input {:type "button" :value "Register"
                :on-click #(send-request @form)}]])))

(defn register-page []
  [:div [:h2 "Register"]
   [register-form]
   [:div [:a {:href "#/"} "go to the home page"]]])
