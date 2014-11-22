(ns identify-me.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-response]]
            [identify-me.apis.facebook :as facebook]
            [identify-me.apis.github :as github]
            [identify-me.apis.twitter :as twitter]
            [identify-me.apis.pgp :as pgp]))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (GET "/api/user/:name" [name]
    {:body {:facebook (facebook/get-user name),
            :github (github/get-user name),
            :twitter (twitter/get-user name),
            :pgp (pgp/get-user name)}})
  (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
      wrap-json-response))
