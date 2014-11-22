(ns identify-me.apis.twitter
  (:require [clj-http.client :as http]))

(def credentials
  {:key ""
   :secret ""})

(defn- get-token
  [{key :key, secret :secret}]
  (-> (http/post "https://api.twitter.com/oauth2/token"
                 {:basic-auth [key secret],
                  :form-params {:grant_type "client_credentials"}
                  :as :json})
      :body :access_token))

(defn get-user
  ([name]
     (let [token (get-token credentials)]
       (get-user token name)))
  ([token name]
     (let [response (http/get "https://api.twitter.com/1.1/users/show.json"
                              {:oauth-token token,
                               :query-params {:screen_name name},
                               :as :json,
                               :throw-exceptions false})]
       (when (= (:status response) 200)
         (:body response)))))
