(ns travelmule.views.requests.all
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [cljsjs.react :as react]
            [ajax.core :refer [GET]]
            [travelmule.views.common :as c]))

(defn view-all []
  [:div [:h2 "All current requests"]])
