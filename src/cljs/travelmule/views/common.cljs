(ns travelmule.views.common
  (:require [reagent.core :as reagent :refer [atom]]))

(defn atom-input
  ([value type]
   [:input {:type type
            :value @value
            :on-change #(reset! value (-> % .-target .-value))}])
  ([key value type]
   [:input {:type type
            :value (key @value)
            :on-change #(swap! value assoc key (.-target.value %))}]))
