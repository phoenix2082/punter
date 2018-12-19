(ns hello-bitly.util
  (:gen-class)
  (:import (java.lang.Double)
           (java.text.DecimalFormat)
           (org.jfree.chart StandardChartTheme)
           (org.jfree.chart.plot DefaultDrawingSupplier)
           (java.awt Color)
           (java.nio.file.Files))
  (:require [clojure.walk]
            [clojure.pprint :as pp]
            [clojure.string :as cstr]
            [criterium.core :refer :all]))


(defn read-file
  "Read the file which has JSON text at each line.
  Return lines as collection."
  [path]
  (with-open [rdr (clojure.java.io/reader path)]
    (reduce conj [] (line-seq rdr))))

(defn read-table [path seperator names]
  (as-> (read-file path) records
    (-> (map (fn [rec] (cstr/split rec (re-pattern seperator))) records)
        (into names))))

(defonce decimalformatter (atom {}))

(defn create-decimal-formatter [n]
  (let [df (new java.text.DecimalFormat (str "##." (apply str (take n (repeat 0)))))]
    (prn (str "creating decimal formatter for " n))
     ;; (new java.text.DecimalFormat (str "##.00" (apply str (take n (repeat 0))))))
     (.setMinimumFractionDigits df n)
     (.setDecimalSeparatorAlwaysShown df true)
     df))

(defn get-formatter
  "Get formatter by number of decimal places required.

   n - Number of decimal places upto which formatting is required.
  "
  [n]
  (let [dfmap decimalformatter
        numkey (keyword (str n))]
    (if (contains? @dfmap numkey)
      (-> @dfmap
           (numkey))
      (->> (create-decimal-formatter n)
          (swap! dfmap assoc-in [numkey]) ;;  two > means last argument in current line
          (numkey)))))

(defn round-n
  "Round the value to two decimal places"
  [num n]
  (Double/valueOf (.format (get-formatter n) num)))

(defn calculate-mean [avector]
  " Calculate arithmatic mean of all the values in vector.
    Input: 
      avector: vector of numebrs e.x. [1 2 3 4]

    Output:
      Arithmatic mean > e.x. (1 + 2 + 3 + 4)/ 4 = 2.5 
  "
  (round-n (/ (reduce + avector) (double (count avector))) 2))

(defn calculate-variance
  [meanv rvec]
  (/ (reduce + (vec (for [x rvec]
                 (let [xi (- x meanv)]
                   (* xi xi))))) (count rvec)))

(defn square-root [x]
  (Math/sqrt x))

(defn standard-deviation
  [ivector]
  (-> (calculate-mean ivector)
      (calculate-variance ivector)
      (square-root)
      (round-n 6)))
