(ns identify-me.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-response]]
            [identify-me.apis.facebook :as facebook]
            [identify-me.apis.github :as github]
            [identify-me.apis.twitter :as twitter]
            [identify-me.apis.pgp :as pgp]
            [identify-me.apis.generics :refer [identify combine]]))

(defn identity-by-name
  [name]
  (let [[facebook github twitter :as others]
        [(identify (facebook/get-user name))
         (identify (github/get-user name))
         (identify (twitter/get-user name))]
        pgp (pgp/derive-user (combine others))]
    {:body {:facebook facebook,
            :github github,
            :twitter twitter,
            :pgp pgp}}))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (GET "/api/user/:name/infer" [name] (identity-by-name name))
  (GET "/api/user/:name/signatures" [name] (identity name))
  (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
      wrap-json-response))
