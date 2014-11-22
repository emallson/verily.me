(ns identify-me.apis.pgp
  (:require [clj-http.client :as http]
 [clojure.string :refer [split split-lines]]
            [clj-time.core :as time]
            [clj-time.coerce :as coerce]
            [clj-time.format :as tformat]
            [clojure.set :as set]))

(defn- parse-info-line
  [line]
  (zipmap [:version :count] (rest line)))

(defn- parse-flags
  [flagstring]
  {:revoked? (contains? flagstring \r),
   :expired? (contains? flagstring \e),
   :disabled? (contains? flagstring \d)})

(def pgp-algorithms
  {"1" {:name "RSA",
         :capabilities [:encrypt, :sign]},
    "2" {:name "RSA",
         :capabilities [:encrypt]},
    "3" {:name "RSA",
         :capabilities [:sign]},
    "16" {:name "Elgamal",
          :capabilities [:encrypt]},
    "17" {:name "DSA",
          :capabilities [:encrypt, :sign]},
    "18" {:name "Reserved for Elliptic Curve",
          :capabilities []},
    "19" {:name "Reserved for ECDSA",
          :capabilities []},
    "20" {:name "Elgamal",
          :capabilities [:encrypt, :sign]},
    "21" {:name "Reserved for Diffie-Hellman",
          :capabilties []}})

(defn- parse-time
  [string]
  (when-not (empty? string)
    string))

(defn- parse-pub-line
  [[_, id, algorithm, length, creation-date, expiration-date, flags]]
  {:id id,
   :algorithm (pgp-algorithms algorithm),
   :length (Integer/parseInt length),
   :creation-date (parse-time creation-date),
   :expiration-date (parse-time expiration-date),
   :flags (parse-flags (into [] flags))})

(defn- parse-uid-line
  [[_, uid, creation-date, expiration-date, flags]]
  {:uid uid,
   :creation-date (parse-time creation-date),
   :expiration-date (parse-time expiration-date),
   :flags (parse-flags (into [] flags))})

(defn- parse-uids
  [src-lines]
  (loop [uids [],
         lines src-lines]
    (if (= (ffirst lines) "uid")
      (recur (conj uids (parse-uid-line (first lines)))
             (rest lines))
      uids)))

(defn- parse-key
  [src-lines]
  {:key (parse-pub-line (first src-lines)),
   :uids (parse-uids (rest src-lines))})

(defn- parse-body
  [body]
  (let [dataz (->> body
                   split-lines
                   (map #(split % #":")))]
    (loop [keys #{}, lines dataz]
      (if (= (ffirst lines) "pub")
        (recur (conj keys (parse-key lines))
               (rest lines))
        (if-not (empty? lines)
          (recur keys (rest lines))
          keys)))))

(defn get-user
  [search]
  (let [response (http/get "http://hkps.pool.sks-keyservers.net/pks/lookup"
                           {:insecure? true,        ; TODO: fix CA path
                            :query-params {:options "mr",
                                           :op "index",
                                           :search search},
                            :throw-exceptions false})]
    (when (= (:status response) 200)
      (parse-body (:body response)))))

(defn derive-user
  [{emails :email, names :name, screen-names :screen_name}]
  (let [keys (apply set/union (map get-user emails))]
    (if (empty? keys)
      (let [keys (apply set/union (map get-user screen-names))]
        (if (empty? keys)
          (apply set/union (map get-user names))
          keys))
      keys)))
