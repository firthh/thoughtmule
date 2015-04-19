(ns thoughtmule.views.regiser
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [cljsjs.react :as react]
            [ajax.core :refer [GET POST]]
            [thoughtmule.views.common :as c]
            [validateur.validation :as v :include-macros true]))

(def User (v/validation-set
           (v/format-of :email
                        :format #"^.*@thoughtworks.com"
                        :message "Must be a valid email address")
           (v/length-of :password :within (range 6 100))
           (v/presence-of :confirm-password)))

(defn handler [response]
  (.log js/console (str response)))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console (str "something bad happened: " status " " status-text)))

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

(defn validate-user [user validation-errors]
  (reset! validation-errors (User user))
  user)

(defn register-user [validation-errors email-address password confirm-password]
  (-> (user email-address password confirm-password)
      (validate-user validation-errors)
      send-request))

(defn error-message [errors]
  (if errors
    [:div {:class "error"} (map (fn [e] [:span e ", "]) errors)]))

(defn register-form []
  (let [email-address (atom "")
        password (atom "")
        confirm-password (atom "")
        validation-errors (atom {})]
    (fn []
      [:div {:id "regiseter-form"}
       [:div [:label "Email address"] [c/atom-input email-address "text"]]
       [error-message (:email @validation-errors)]
       [:div [:label "Password"] [c/atom-input password "password"]]
       [error-message (:password @validation-errors)]
       [:div [:label "Confirm Password"] [c/atom-input confirm-password "password"]]
       [error-message (:confirm-password @validation-errors)]
       [:input {:type "button" :value "Register"
                :on-click #(register-user validation-errors @email-address @password @confirm-password)}]])))

(defn register-page []
  [:div [:h2 "Register"]
   [register-form]
   [:div [:a {:href "#/"} "go to the home page"]]])
