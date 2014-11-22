(ns identify-me.apis.generics
  (:require [clj-http.client :as http]))

(defmacro defcall
  ([name docstring params url-fn opts]
     `(defn ~name
        ~docstring
        ~params
        (let [response# (http/get (~url-fn ~@params)
                                 (merge {:as :json,
                                         :throw-exceptions false}))]
          (when (= (:status response#) 200)
            (:body response#))))))
