(ns travelmule.views.register
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [cljsjs.react :as react]
            [ajax.core :refer [GET POST]]
            [travelmule.views.common :as c]
            [validateur.validation :as v :include-macros true]))

(def error (atom ""))

(defn- attributes-equal
  [attribute1 attribute2]
  (fn [m]
    (let [value1   (attribute1 m)
          value2   (attribute2 m)
          invalid (not (= value1 value2))]
      (if invalid
        {attribute1 #{(str " must equal " (name attribute2))}
         attribute2 #{(str " must equal " (name attribute1))}}
        nil))))

(defn equal [field1 field2]
  (let [validator (attributes-equal field1 field2)]
    (fn [m]
      (let [result (validator m)]
        [(nil? result) result]))))

(def User (v/validation-set
           (v/format-of :email
                        :format #"^.*@thoughtworks.com"
                        :message "Must be a valid email address")
           (v/length-of :password :within (range 6 100))
           (v/presence-of :confirm-password)
           (equal :password :confirm-password)
           ))

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
  (if (v/valid? User user)
    (POST "/register" {:params user
                       :response-format :json
                       :format :json
                       :keywords? true
                       :handler handler
                       :error-handler error-handler})))

(defn error-message [errors]
  (if errors
    [:div {:class "error"} (map (fn [e] [:span e ", "]) errors)]))

(defn atom-input [value key type]
  [:input {:type type
           :value (key @value)
           :on-change #(swap! value assoc key (.-target.value %))}])

(defn register-form []
  (let [form (atom {})
        validation-errors (atom {})]
    (fn []
      (add-watch form :validation (fn [key at old new] (reset! validation-errors (User new))))
      [:div {:id "regiseter-form"}
       [:div [:label "Email address"] [atom-input form :email "text"]]
       [error-message (:email @validation-errors)]
       [:div [:label "Password"] [atom-input form :password "password"]]
       [error-message (:password @validation-errors)]
       [:div [:label "Confirm Password"] [atom-input form :confirm-password "password"]]
       [error-message (:confirm-password @validation-errors)]
       [:input {:type "button" :value "Register"
                :on-click #(send-request @form)}]])))

(defn register-page []
  [:div [:h2 "Register"]
   [register-form]
   [:div [:a {:href "#/"} "go to the home page"]]])
