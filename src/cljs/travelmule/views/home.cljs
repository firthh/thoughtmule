(ns travelmule.views.home
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [cljsjs.react :as react]
            [ajax.core :refer [GET POST]]
            [travelmule.views.common :as c]))

(defn error-handler [response]
  (.log js/console (str "something bad happened: " (:status response) " " (:status-text response) ". " response))
  (session/put! :message {:message (:message (:response response))
                          :class "error"}))

(defn handler [response]
  (.log js/console (str response))
  (.log js/console (secretary/dispatch! "/requests"))
  (session/put! :message {:message "User successfully registered"
                          :class "success"}))

(defn send-request [creds]
  (POST "/login" {:params creds
                  :response-format :json
                  :format :json
                  :keywords? true
                  :handler handler
                  :error-handler error-handler}))

(defn- sign-in-form []
  (let [form (atom {})]
    (fn []
      [:div
       [:div [:label "Email address"] [c/atom-input :email form "text"]]
       [:div [:label "Password"] [c/atom-input :password form "password"]]
       [:input {:type "button" :value "sign in"
                :on-click #(send-request @form)}]])))

(defn home-page []
  [:div [:h2 "Welcome to Travel Mule"]
   [:div "Sign In"
    [sign-in-form]]
   [:div [:a {:href "#/register"} "Register"]]])
