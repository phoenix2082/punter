(ns hello-bitly.core
  (:gen-class)
  (:import (java.lang.Double)
           (java.text.DecimalFormat))
  (:require [cheshire.core :refer :all]
            [clojure.walk]
            [clojure.string :as cstr]
            [hello-bitly.movie_lens :as mvl]
            [hello-bitly.baby_names :as usbn]
            [clojure.math.numeric-tower :as math]))

(use '(incanter core charts datasets))

(def path "usagov_bitly_data2012-03-16-1331923249.txt")

(def df (new java.text.DecimalFormat "##.00"))

(defn round-2
  "Round the value to two decimal places"
  [num]
  (Double/valueOf (.format df num)))

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
  (mapv convert-to-map items))

(def my-records (get-json (read-file path)))

(defn check-and-update [rec v]
  (if (cstr/includes? (:a rec) "Windows")
    (update-in v [(keyword (:tz rec)) :Windows] (fnil inc 0))
    (update-in v [(keyword (:tz rec)) :Others] (fnil inc 0))))
    
(defn tz-os-map-2
  [records]
  (let [total-records (count records)]
    (loop [i 0 v {}]
      (if (< i total-records)
        (recur (inc i) (check-and-update (get records i) v))
        (into {} v)))))

(defn tzs-os-map-2 [records]
  (sort-by #(get-in (val %) [:Windows]) records))

(defn get-timezones [records]
  (map :tz records))

(defn get-timezones-sorted [tzs]
  (sort-by val > tzs))

(defn remove-nil [ii]
  (remove clojure.string/blank? ii))

(defn remove-nil-by-prop
  [prop items]
  (remove (fn [x] (clojure.string/blank? (prop x))) items))

(def tz-clean (remove-nil-by-prop :tz my-records))
(def tzoes (tz-os-map-2 (vec tz-clean)))
(def tzoes-sorted-reversed (vec (reverse (tzs-os-map-2 tzoes))))
(def top-10 (subvec tzoes-sorted-reversed 0 10))
(def keyitems (flatten (map (fn [ii] (keys (second ii))) top-10)))
(def valitems (flatten (map (fn [ii] (vals (second ii))) top-10)))
(def mytzmi (flatten (map (fn [ii] [(first ii) (first ii)]) top-10)))

(defn build-for-plot
  [records]
  (let [rcount (count records)]
    (loop [i 0 tzv [] kv [] vv []]
    (if (< i rcount)
      (recur (inc i)
             (conj tzv (take 2 (repeat (first (get records i)))))
             (conj kv "Windows" "Others")
             (conj vv
                   (if-let [a (:Windows (second (get records i)))] a 0)
                   (if-let [a (:Others (second (get records i)))] a 0)))
      [(flatten tzv) (flatten kv) (flatten vv)]))))

(defn normalized-vals
  [items]
  (flatten
   (for [x (map (fn [ii] (vals (second ii))) items)
         :let [msum (sum x)]]
     [(round-2 (/ (double (first x)) msum))
      (round-2 (/ (double (if-let [a (second x)] a 0)) msum))])))

(def norma-val (normalized-vals top-10))

   
(defn get-sum
  [coll]
  (let [t-sum (sum coll)]
    (for [x coll]
      (/ x sum))))

(defn get-bar-chart [oo]
  (bar-chart (vec (keys oo)) (vec (vals oo))
                 :title "Usage by Timezones"
                 :y-label "Timezones"
                 :x-label "Count"
                 :legend true))

(defn convert-keys-to-string [items]
  (map (fn [[k v]] [(name k) v]) items))

(defn get-stacked-chart [xItem yItem gItem]
  (stacked-bar-chart
    xItem
    yItem
    :group-by gItem
    :legend true
    :vertical false
    :y-label "Users (In %)"
    :x-label "Timezones"))

(defn transform-x [xItem]
  (map #(subs (str %) 1) xItem))

(defn transform-y [yItem]
  (map #(* % 100 ) yItem))

(defn transform-g [gItem]
  (map #(name %) gItem))

(defn draw-plot
  "Renders a Stacked Bar Chart.
  xItem - Vector of Item for X-Axis
  yItem - Vector of Item for Y-Axis
  gItem - Vector of Item to group by"
  [xItem yItem gItem]
  (view
   (get-stacked-chart
    (transform-x xItem)
    (transform-y yItem)
    (transform-g gItem))))

(defn save-plot
  "Renders a Stacked Bar Chart.
  xItem - Vector of Item for X-Axis
  yItem - Vector of Item for Y-Axis
  gItem - Vector of Item to group by"
  [xItem yItem gItem filename]
  (save
   (get-stacked-chart
    (transform-x xItem)
    (transform-y yItem)
    (transform-g gItem)) filename))

(defn view-os-by-timezones []
  (draw-plot mytzmi valitems keyitems))
    
(defn view-os-by-timezones-2 []
  (let [items (build-for-plot top-10)]
    ;; arg1 - tz repeated, arg1 - values , arg2 - [Windows Others].....
    (draw-plot (get items 0) (get items 2) (get items 1))))

(defn view-os-by-timezones-normalized []
  (draw-plot mytzmi norma-val (conj (vec keyitems) :Others)))

(defn save-os-by-timezones-normalized []
  (save-plot mytzmi norma-val (conj (vec keyitems) :Others) "tzvsos.png"))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
 " (prn args)  (-> (read-file path)
      (get-json)
      (get-timezones)
      (remove-nil)
      (frequencies)
      (clojure.walk/keywordize-keys)
      (get-timezones-sorted)
      (vec)
      (subvec 0 5)
      (get-bar-chart)
      (view))"
;  (view-os-by-timezones)
;  (view-os-by-timezones-normalized))
)
