(ns travelmule.db
  (:require [yesql.core :refer [defqueries]]))

(defqueries "dao/users.sql")
