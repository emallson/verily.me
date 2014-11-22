(ns identify-me.apis.generics
  (:require [clj-http.client :as http]
            [clojure.set :refer [rename-keys]]))

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

(defn identify
  [user]
  (let [identity (select-keys user [:email :name :screen_name :login :username])]
    (rename-keys identity {:login :screen_name, :username :screen_name})))

(defn combine
  [maps]
  (reduce
   (fn [combined next]
     (reduce
      (fn [c [k v]]
        (assoc c k (conj (get combined k) v)))
      combined next))
   {} maps))
