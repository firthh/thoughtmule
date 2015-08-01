(ns validations.users
  (:require
   #?(:cljs [validateur.validation :refer :all :include-macros true]
      :clj  [validateur.validation :refer :all])))

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

(def user-validation
  (validation-set
   (format-of :email
              :format #"^.*@thoughtworks.com$"
              :message "Must be a valid email address")
   (length-of :password :within (range 6 100))
   (presence-of :confirm-password)
   (equal :password :confirm-password)))

(defn invalid-user? [user]
  (valid? user-validation user))

(def valid-user? (complement invalid-user?))
