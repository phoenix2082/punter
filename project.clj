(defproject hello-bitly "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
               [incanter/incanter-core "1.9.3"]
                 [incanter/incanter-charts "1.9.3"]
                 [cheshire "5.8.1"]
                 [org.clojure/math.numeric-tower "0.0.4"]
                 [criterium "0.4.4"]
                 [org.clojure/tools.trace "0.7.10"]
                 [aleph "0.4.6"]
                 [ring/ring-core "1.6.3"]
                 [ring/ring-jetty-adapter "1.6.3"]
                 [ring/ring-devel "1.6.3"]
                 [compojure "1.6.1"]]

  :plugins [[lein-ring "0.12.1"]]

  :profiles {:uberjar {:aot :all}
             :dev     {:resource-paths ["target"]
                       :clean-targets ^{:protect false} ["target"]
                       :dependencies [[org.clojure/clojurescript "1.10.339"]
                                      [com.bhauman/figwheel-main "0.2.0"]
                                      ;; optional but recommended
                                      [com.bhauman/rebel-readline-cljs "0.1.4"]]}
             }

  :aliases {"fig" ["trampoline" "run" "-m" "figwheel.main"]}
  
  :main ^:skip-aot hello-bitly.core
  :target-path "target/%s"

  
  :jvm-opts ["-Xmx2g"])
