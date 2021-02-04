(ns clojure-osso-demo.web
  (:require [compojure.core :refer [routes GET ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [clojure.pprint :as pprint]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.defaults :refer :all]
            [ring.middleware.oauth2 :refer [wrap-oauth2]]
            [environ.core :refer [env]]
            [cheshire.core :refer [parse-string]]))

  (defn login []
    {:status 200
    :headers {"Content-Type" "text/plain"}
    :body "Please Login (TODO: add template?)"})

  (defn callback []
    {:status 200
    :headers {"Content-Type" "text/plain"}
    :body "Hello from Heroku"})

  (defn wrap-print-request [handler]
    (fn [request]
      (println request)
      (handler request)))

  (def handler
    (wrap-params
      (routes
          (GET "/" [] (login))
          (GET "/welcome" [] (callback))
          (wrap-oauth2
            routes
            {:osso
              {:authorize-uri   "https://demo.ossoapp.com/oauth/authorize"
              :access-token-uri "https://demo.ossoapp.com/oauth/token"
              :client-id        "demo-client-id"
              :client-secret    "demo-client-secret"
              :launch-uri       "/auth/osso"
              :redirect-uri     "/auth/osso/callback"
              :landing-uri      "/welcome",
              :redirect_handler wrap-print-request}})
          (ANY "*" []
            (route/not-found (slurp (io/resource "404.html")))))))
          

  (def app 
      (wrap-defaults handler (-> site-defaults (assoc-in [:session :cookie-attrs :same-site] :lax))))

  (defn -main []
    (let [port (Integer/parseInt (get (System/getenv) "PORT" "8000"))]
        (jetty/run-jetty app {:port port})))
