(ns identify-me.apis.github
  (:require [identify-me.apis.generics :refer [defcall]]))

(defcall get-user
  "Gets a user by name from Github"
  [name]
  #(str "https://api.github.com/users/" %)
  {})
