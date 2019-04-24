(ns hello-bitly.core
  (:gen-class)
  (:import (java.lang.Double)
           (java.text.DecimalFormat))
  (:require [cheshire.core :refer :all]
            [clojure.walk]
            [clojure.string :as cstr]
            [hello-bitly.bitly_usage :as bu]
            [hello-bitly.movie_lens :as mvl]
            [hello-bitly.baby_names :as usbn]
            [hello-bitly.us_housing :as ush]
            [clojure.math.numeric-tower :as math]
            [aleph.http :as http]
            [byte-streams :as bs]
            [ring.middleware.params :as params]
            ;;[ring.middleware.content-type]
            [ring.util.response :refer [response content-type resource-response file-response redirect]]
            [compojure.core :as compojure :refer [defroutes GET POST]]
            [compojure.response :refer [Renderable]]
            [compojure.route :as route]))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
 " (prn args)"
  )

(defn handler-index [request]
  (prn "hello index")
  (file-response "index.html" {:root "resources/public"}))

(defn handler-bitly [request]
  (content-type (response (bu/get-usage-normalized)) "image/png"))
  
(defn handler-movielens [request]
  (content-type (response (mvl/get-naming-trends)) "image/png"))

(defn handler-babynames [request]
  (content-type (response (usbn/get-gender-trends)) "image/png"))

(defn handler-ushfeature [request]
  (content-type (response (ush/get-feature-histogram)) "image/png"))


(compojure/defroutes handler
  (params/wrap-params
   (compojure/routes
    (GET "/" [] handler-index)
    (GET "/bitly" [] handler-bitly)
    (GET "/movielens" [] handler-movielens)
    (GET "/usbnames" [] handler-babynames)
    (GET "/uscalhousing" [] handler-ushfeature))))

