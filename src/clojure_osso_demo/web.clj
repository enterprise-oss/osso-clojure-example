(ns clojure-osso-demo.web
  (:require [compojure.core :refer [defroutes routes GET ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [clojure.data.json :as json]
            [clojure.pprint :as pprint]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.defaults :refer :all]
            [ring.middleware.oauth2 :refer [wrap-oauth2]]
            [environ.core :refer [env]]
            [clj-http.client :as client]))

  (defn get-osso-profile 
    "Osso profile info API call"
    [token]
    (-> (client/get "https://demo.ossoapp.com/oauth/me" {:oauth-token token, :as :json})
        :body))
          
  (defn login []
    (slurp (io/resource "login.html")))

  (defn callback []
    (fn [request]
      (let [token (get-in request [:session :ring.middleware.oauth2/access-tokens :osso :token])]
      (let [profile (get-osso-profile token)]
      {
        :status 200
        :headers {"Content-Type" "application/json"}
        :body (json/write-str profile)}))))

  (defroutes routes 
    (GET "/" [] (login))
    (GET "/welcome" [] (callback))
    (ANY "*" []
    (route/not-found (slurp (io/resource "404.html")))))
            
 (def handler
   (-> routes
       (wrap-params)
       (wrap-oauth2 {:osso
          {:authorize-uri   "https://demo.ossoapp.com/oauth/authorize"
          :access-token-uri "https://demo.ossoapp.com/oauth/token"
          :client-id        "demo-client-id"
          :client-secret    "demo-client-secret"
          :launch-uri       "/auth/osso"
          :redirect-uri     "/auth/osso/callback"
          :landing-uri      "/welcome" }})
       (wrap-defaults (-> site-defaults (assoc-in [:session :cookie-attrs :same-site] :lax)))))

  (def app 
      (-> handler))

  (defn -main []
    (let [port (Integer/parseInt (get (System/getenv) "PORT" "8000"))]
        (jetty/run-jetty app {:port port})))
