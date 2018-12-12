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
                 [org.clojure/tools.trace "0.7.10"]]
  
  :main ^:skip-aot hello-bitly.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :java-cmd "/Library/Java/JavaVirtualMachines/jdk1.8.0_202.jdk/Contents/Home/bin/java"
  :jvm-opts ["-Xmx2g"])
