(defproject identify.me "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.2.0"]
                 [ring/ring-defaults "0.1.2"]
                 [ring/ring-json "0.3.1"]
                 [org.clojure/data.json "0.2.5"]
                 [clj-http "1.0.1"]
                 [clj-time "0.8.0"]
                 [korma "0.3.0"]
                 [org.postgresql/postgresql "9.2-1002-jdbc4"]
                 [mvxcvi/clj-pgp "0.5.4"]
                 [enlive "1.1.5"]]
  :plugins [[lein-ring "0.8.13"]]
  :main identify-me.handler
  :ring {:handler identify-me.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}})
