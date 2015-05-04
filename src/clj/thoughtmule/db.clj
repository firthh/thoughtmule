(ns thoughtmule.db
  (:require [yesql.core :refer [defqueries]]))

(defqueries "dao/users.sql")
