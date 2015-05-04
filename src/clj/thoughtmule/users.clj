(ns thoughtmule.users
  (:require [validateur.validation :refer :all]))

(def User (validation-set
           (format-of :email
                      :format #"^.*@thoughtworks.com$"
                      :message "Must be a valid email address")
           (length-of :password :within (range 6 100))
           (presence-of :confirm-password)))
