(ns identify-me.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-response]]
            [identify-me.apis.facebook :as facebook]
            [identify-me.apis.github :as github]
            [identify-me.apis.twitter :as twitter]
            [identify-me.apis.hkp :as hkp]
            [identify-me.apis.generics :refer [identify combine]]
            [identify-me.signatures :refer [list-signatures list-identities]]
            [identify-me.templates :as templates]))

(defn identity-by-name
  [name]
  (let [[facebook github twitter :as others]
        [(identify (facebook/get-user name))
         (identify (github/get-user name))
         (identify (twitter/get-user name))]
        hkp (hkp/derive-user (combine others))]
    {:body {:facebook facebook,
            :github github,
            :twitter twitter,
            :pgp hkp}}))

(defroutes api-routes
  (GET "/" [] "Hello World")
  (GET "/api/user/:name/infer" [name] (identity-by-name name))
  (GET "/api/user/:service/:name/signatures"
      [service name]
    {:body (list-signatures service name)})
  (GET "/api/key/:key-id/identities"
      [key-id]
    {:body (list-identities key-id)})
  (POST "/api/user/:service/:name/signatures"
      {{service :service, name :name} :params, body :body}
    {:body (hkp/sign-identities service name body)}))

(defroutes web-routes
  (GET "/key/:key-id/identities"
      [key-id]
    (templates/identities (list-identities key-id)))
  (GET "/user/:service/:name/signatures"
      [service name]
    (templates/identities (list-signatures service name))))

(defroutes allroutes
  (wrap-json-response api-routes)
  web-routes
  (route/not-found "Not found"))

(def app
  (handler/api allroutes))
