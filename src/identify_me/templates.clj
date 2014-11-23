(ns identify-me.templates
  (:require [net.cgrand.enlive-html :refer :all]))

(defsnippet an-identity "public/identity.html" [:tr]  [id]
  [:.name] (content (:identifier id))
  [:.service] (content (:service id))
  [:.key-id] (content (:key_id id)))

(deftemplate identities "public/identities.html" [identities]
  [:table.content :tbody] (clone-for [id identities] (content (an-identity id))))
