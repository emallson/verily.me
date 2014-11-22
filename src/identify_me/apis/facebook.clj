(ns identify-me.apis.facebook
  (:require [identify-me.apis.generics :refer [defcall]]))

(defcall get-user
  "Get a user from FB. FB is pretty flexible in how the user is identified."
  [name]
  #(str "https://graph.facebook.com/" %)
  {})
