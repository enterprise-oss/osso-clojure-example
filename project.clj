(defproject clojure-getting-started "1.0.0-SNAPSHOT"
  :description "Demo Clojure web app"
  :url "http://clojure-getting-started.herokuapp.com"
  :license {:name "Eclipse Public License v1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [compojure "1.6.1"]
                 [ring "1.9.0"]
                 [ring/ring-defaults "0.3.2"]
                 [ring-oauth2 "0.1.5"]
                 [clj-http "3.12.0"]
                 [org.clojure/data.json "1.0.0"]
                 [environ "1.1.0"]
                 [stencil "0.5.0"]]
  :min-lein-version "2.0.0"
  :plugins [[environ/environ.lein "0.3.1"]]
  :hooks [environ.leiningen.hooks]
  :uberjar-name "clojure-osso-demo-standalone.jar"
  :profiles {:production {:env {:production true}}})
