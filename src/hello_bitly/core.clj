(ns hello-bitly.core
  (:gen-class)
  (:require [cheshire.core :refer :all])
  (:require [clojure.walk]))
  ;; (:require [incanter :refer core charts stats datasets :as incanter]))
  ;;(use '(incanter core stats charts)))

(use '(incanter core charts datasets))

(def path "usagov_bitly_data2012-03-16-1331923249.txt")

(defn read-file
  "Read the file which has JSON text at each line.
  Return lines as collection."
  [path]
  (with-open [rdr (clojure.java.io/reader path)]
    (reduce conj [] (line-seq rdr))))

(defn convert-to-map
  "Parse each record and convert to JSON"
  [record]
  (parse-string record true))

(defn get-json [items]
  (map convert-to-map items))

(defn get-timezones [records]
  (map :tz records))

(defn get-timezones-sorted [tzs]
  (sort-by val > tzs))

(defn remove-nil [ii]
  (remove clojure.string/blank? ii))

(defn get-bar-chart [oo]
  (bar-chart (vec (keys oo)) (vec (vals oo))
                 :title "Usage by Timezones"
                 :y-label "Count"
                 :x-label "Timezones"
                 :legend true))

(defn convert-keys-to-string [items]
  (map (fn [[k v]] [(name k) v]) items))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (prn args)
  (-> (read-file path)
      (get-json)
      (get-timezones)
      (remove-nil)
      (frequencies)
      (clojure.walk/keywordize-keys)
      (get-timezones-sorted)
     ;; (convert-keys-to-string)
      (vec)
      (subvec 0 5)
      (get-bar-chart)
      (view)))
