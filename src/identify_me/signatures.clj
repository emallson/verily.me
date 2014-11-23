(ns identify-me.signatures
  (:require [korma.core :refer :all]
            [korma.db :refer :all]))

(def ^:private db (postgres {:db "identifyme"
                             :user "identifyme-writer"
                             :password ""}))

(defdb local db)

(declare identities proof)

(defentity proofs
  (pk :id)
  (entity-fields :proof)
  (has-many identities))

(defentity identities
  (belongs-to proofs {:fk :proof_id})
  (entity-fields :key_id :service :identifier))

(defn list-signatures
  [service name]
  (select identities
          (with proofs)
          (where {:service service, :identifier name})))

(defn list-identities
  [key-id]
  (select identities
          (with proofs)
          (where {:key_id key-id})))

(defn insert-signature
  [service name key_id proof]
  (try
    (let [result (insert proofs
                         (values {:proof proof}))]
      (insert identities
              (values {:service service, :identifier name, :key_id key_id, :proof_id (:id result)})))
    (catch Exception e (str "Error: duplicate signature\n"))))
