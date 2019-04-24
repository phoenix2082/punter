(ns hello-bitly.core)

(enable-console-print!)

(defn hello [] "hello There")

;; uncomment this to alter the provided "app" DOM element
;;(set! (.-innerHTML (js/document.getElementById "app")) (hello))

(println (hello))
