(ns travelmule.views.common
  (:require [reagent.core :as reagent :refer [atom]]))

(defn atom-input [value type]
  [:input {:type type
           :value @value
           :on-change #(reset! value (-> % .-target .-value))}])
