(ns hello-bitly.us_housing
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
            [criterium.core :refer :all]
            [hello-bitly.util :as hbu]))

(use '(incanter core charts datasets))

(def housingdata "./datasets/housing/housing.csv")
(def names [["longitude" "latitude" "housing_median_age" "total_rooms" "total_bedrooms"	"population" "households" "median_income" "median_house_value"	"ocean_proximity"]])
(def rectype ["double" "double" "double" "double" "double" "double" "double" "double" "double" "string"])

(def housingrecords (hbu/read-table housingdata "," []))

(defn dtype-parse [rtype rvalue]
  (case rtype
    "double" (if (clojure.string/blank? rvalue) 0.00 (Double/valueOf (Double/parseDouble rvalue)))
    "int" (Integer/parseInt  rvalue)
    "bool" (Boolean/parseBoolean rvalue)
    "string" rvalue
    :else rvalue))

(defn parse-rec [dtype drecv]
  (for [recount (range 0 (count drecv))]
    (dtype-parse (get dtype recount) (get drecv recount))))
    
(defn parser-records [dtypes records]
  (map #(parse-rec dtypes %) records))

(def all-record-parsed (parser-records rectype (rest housingrecords)))

(def headers [:longitude :latitude :housing_median_age :total_rooms :total_bedrooms :population :households :median_income :median_house_value :ocean_proximity])

(def housingdatasets (incanter.core/dataset headers all-record-parsed))

(comment "This are test function for individual mathemtical operation on a column in dataset.
           Refactor and move appropriately.

(defn count-by-property [hdataset propname]
 (incanter.core/$rollup :count propname propname hdataset))



(defn count-for-each [hdataset headers]
  (prn headers)
  ;(map (fn [hprop] {(incanter.core/sel hdataset )) headers))
  (-> (incanter.core/to-map hdataset)
      (as-> gprop (map (fn [x] {x (count (x gprop))}) (keys gprop)))
      ((fn [x] (apply merge x)))))


(defn mean-by-proeprty [hdataset]
  ;(prn (butlast (keys gprop)))
  (-> (incanter.core/to-map hdataset)
      (as-> gprop (map (fn [x] {x (hbu/calculate-mean (x gprop))}) (remove #{:ocean_proximity} (keys gprop))))
      ((fn [x] (apply merge x)))))

(defn variance-by-proeprty [hdataset]
  ;(prn (butlast (keys gprop)))
  (-> (incanter.core/to-map hdataset)
      (as-> gprop (map (fn [x] {x (hbu/standard-deviation (x gprop))}) (remove #{:ocean_proximity} (keys gprop))))
      ((fn [x] (apply merge x)))))

(defn max-by-proeprty [hdataset]
  ;(prn (butlast (keys gprop)))
  (-> (incanter.core/to-map hdataset)
      (as-> gprop (map (fn [x] {x (apply max (x gprop))}) (remove #{:ocean_proximity} (keys gprop))))
      ((fn [x] (apply merge x)))))

(defn min-by-proeprty [hdataset]
  ;(prn (butlast (keys gprop)))
  (-> (incanter.core/to-map hdataset)
      (as-> gprop (map (fn [x] {x (apply min (x gprop))}) (remove #{:ocean_proximity} (keys gprop))))
      ((fn [x] (apply merge x)))))
"
)

(defn apply-mathx [hdataset mathf]
   (-> (incanter.core/to-map hdataset)
      (as-> gprop (map (fn [x] {x (mathf (x gprop))}) (remove #{:ocean_proximity} (keys gprop))))
      ((fn [x] (apply merge x)))))


(defn describe-dataset
  "Apply count, mean, standard-deviation, max and min function to all columns of dataset.

   call it like below:
   hello-bitly.us_housing> (def dresult (describe-dataset housingdatasets))

  TODO: Error Handling and validation. Add Exclusion property which should be vector to skip column which are not numeric type
  "
  [hdataset]
  (->> (for [f [{:fname "count" :fval #(count %)}
                {:fname "mean" :fval hbu/calculate-mean}
                {:fname "sd" :fval hbu/standard-deviation}
                {:fname "max" :fval #(apply max %)}
                {:fname "min" :fval #(apply min %)}]]
         (let [fnamek (ffirst f)
               fnamev (first (vals f))]
           (->> (apply-mathx hdataset (:fval f))
                (merge {fnamek fnamev}))))))

(defn describe-nicely
  "Print the descibed dataset in nice tabular format.
   call it like below:
   hello-bitly.us_housing> (describe-nicely housingdatasets)

  > Output will be like below

  | :fname | :median_income | :total_bedrooms | :longitude | :population | :housing_median_age | :latitude | :median_house_value | :total_rooms | :households |
  |--------+----------------+-----------------+------------+-------------+---------------------+-----------+---------------------+--------------+-------------|
  |  count |          20640 |           20640 |      20640 |       20640 |               20640 |     20640 |               20640 |        20640 |       20640 |
  |   mean |           3.87 |          532.48 |    -119.57 |     1425.48 |               28.64 |     35.63 |           206855.82 |      2635.76 |      499.54 |
  |     sd |       1.899776 |      422.668093 |   2.003483 | 1132.434688 |           12.585253 |  2.135901 |       115392.820404 |  2181.562402 |  382.320491 |
  |    max |        15.0001 |          6445.0 |    -114.31 |     35682.0 |                52.0 |     41.95 |            500001.0 |      39320.0 |      6082.0 |
  |    min |         0.4999 |             0.0 |    -124.35 |         3.0 |                 1.0 |     32.54 |             14999.0 |          2.0 |         1.0 |
  "
  [hdataset]
  (clojure.pprint/print-table (describe-dataset hdataset)))


; TODO find better way to change color
(defn set-custom-theme-colors
  [chart]
     (let [plot (.getPlot chart)
           renderer (.getRenderer plot)]
       (do
          (doto plot
           (.setBackgroundPaint java.awt.Color/white)
           (.setRangeGridlinePaint (java.awt.Color. 235 235 235))
           (.setDomainGridlinePaint (java.awt.Color. 235 235 235)))
         (doto renderer
           (.setOutlinePaint java.awt.Color/white)
           (.setPaint (java.awt.Color. 153 230 255))) ; set histogram color to nice blue
         chart)))



(defn draw-histogram []
  (doto (histogram :households :data housingdatasets :nbins 50 :x-label "Households" :y-label "Count")
    (set-custom-theme-colors)
    view))

(defn create-histograms
  "Use this method to generate histograms for all features,
   which has numeric values.

   e.x.  The last valeue in header is of string type exclude it.
   hello-bitly.us_housing> (create-histograms housingdatasets (butlast headers))
  "
  [hdataset columnnames]
  (for [v columnnames]
    (let [xlabel (clojure.string/capitalize (name v))]
      (doto (histogram v :data hdataset :nbins 50
                       :x-label xlabel :y-label "Count")
        (set-custom-theme-colors)
        view))))

(defn save-histograms
  "Use this method to save histograms for all features,
   which has numeric values.

   e.x.  The last valeue in header is of string type exclude it.
   hello-bitly.us_housing> (save-histograms housingdatasets (butlast headers))
  "
  [hdataset columnnames]
  (for [v columnnames]
    (let [xlabel (clojure.string/capitalize (name v))]
      (doto (histogram v :data hdataset :nbins 50
                       :x-label xlabel :y-label "Count")
        (set-custom-theme-colors)
        (save (str xlabel ".png"))))))
