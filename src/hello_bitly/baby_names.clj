(ns hello-bitly.baby_names
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

(use '(incanter core charts datasets))

(def babynamefilepath "./datasets/names")
(def bheader [:name :sex :births])

(defn filter-out-files [filelist]
  (filter (fn [fitem] (and (.isFile fitem) (cstr/ends-with? (.getName fitem) ".txt"))) filelist))

(defn file-abs-path [filelist]
  (map #(.getAbsolutePath %) filelist))

(defn read-file
  "Read the file which has JSON text at each line.
  Return lines as collection."
  [path]
  ;(prn "readingfile")
  (with-open [rdr (clojure.java.io/reader path)]
    (reduce conj [] (line-seq rdr))))

(defn read-table [path seperator names]
  (as-> (read-file path) records
    (-> (map (fn [rec] (cstr/split rec (re-pattern seperator))) records)
        (into names))))

(defn read-directory 
  "This method read all the files from a directory and returns list
  of files.
  arg - folderpath - the path of directory"
  [folderpath]
  (-> (new java.io.File folderpath)
      (.listFiles)
      (filter-out-files)
      (file-abs-path)))
 ;;     (#(.getPath %))
 ;;     (read-file)))

(defn read-records [records seperator]
  ;(prn "reading records")
  (map (fn [rec] (cstr/split rec (re-pattern seperator))) records))
      
  
(defn prep-records [records]
  ;(prn "prep-records")
  (pmap (fn [rec] [(first rec) (second rec) (Integer/parseInt (last rec))]) records))

(defn add-extra-value [records val]
  ;(prn "adding year")
  (pmap (fn [rec] (conj rec val)) records))

(defn get-year-from-filename
  [filename]
  ;(prn filename)
  (-> (re-pattern "\\d+")
      (re-find filename)
      (Integer/parseInt)))

(defn get-yearly-records [filepath]
  (-> (read-file filepath)
      (read-records ",")
      (prep-records)
      (add-extra-value (get-year-from-filename filepath))))

(defn get-all-year-record [dirpath]
  (doseq [filepath (read-directory dirpath)]
    (if (cstr/ends-with? filepath ".txt")
      (get-yearly-records filepath))))

(defn all-year-records [dirpath]
  (let [filepaths (vec (read-directory dirpath))
        filecount (count filepaths)]
    (loop [i 0 v ()]
      (if (< i filecount)
        (recur (inc i) (into v (get-yearly-records (get filepaths i))))
        v))))



;; (def allrec (all-year-records babynamefilepath))
;; (def namedata (incanter.core/dataset [:name :sex :births] records))
;; (incanter.core/$rollup :sum :births :sex babynames)
;; (incanter.core/$rollup :sum :births [:year :sex] namedata)

;; years - (1880 1900 1920 1940 1960 1980 2000 2020)
;; (query-dataset cars {:speed {:$in #{17 14 19}}})
;; (query-dataset cars {:year {:$in #{ 1880 1900 1920 1940 1960 1980 2000 2020}}})

(defn get-years []
  #{1880 1900 1920 1940 1960 1980 2000 2017})

(defonce allrec (all-year-records babynamefilepath))
(defonce namedata (incanter.core/dataset [:name :sex :births :year] allrec))

(defn view-test-chart-2 []
  (with-data
    (->> (incanter.core/query-dataset namedata {:year {:$in (get-years)}})
         (incanter.core/$rollup :sum :births [:year :sex])
         (incanter.core/$order :year :asc))
    (view (incanter.charts/line-chart :year :births :group-by :sex
                                      :legend true
                                      :y-label "Births"
                                      :x-label "Year"
                                      :title  "Trends"))))

(defn save-test-chart-2 []
  (with-data
    (->> (incanter.core/query-dataset namedata {:year {:$in (get-years)}})
         (incanter.core/$rollup :sum :births [:year :sex])
         (incanter.core/$order :year :asc))
    (save (incanter.charts/line-chart :year :births :group-by :sex
                                      :legend true
                                      :y-label "Count by Gender"
                                      :x-label "Year"
                                      :title  "Trends") "birth-trends.png")))

;; Use this method to create chart object.
;; Input : cnames > Vector of names.
;; Output : returen a chart object.
;; Used by view-births-by-name/save-births-by-name methods.
(defn chart-by-birth-name [cnames]
  (with-data
    (->> (incanter.core/query-dataset namedata {:name {:$in (set cnames)}})
         (incanter.core/$rollup :sum :births [:year :name])
         (incanter.core/$order :year :asc))
    (incanter.charts/line-chart :year :births :group-by :name
                                      :legend true
                                      :y-label "Births"
                                      :x-label "Year"
                                      :title  "Trends")))


;; Use this to method to view child naming trends.
;; Will create  a chart on desktop
;;
;; Input : cnames > Vector of names.
;;         filename > Name of file in which result need to be saved.
;;
;; Example:
;;   To view trends for a single name  > (view-births-by-name ["Harry"]) ;
;;   To view trends for multiple names > (view-births-by-name ["Harry" "Mary"]) ;
;;
(defn view-births-by-name [cnames]
   (incanter.core/view (chart-by-birth-name cnames)))


;; Use this method to save child naming trends as png file.
;;
;; Input : cnames > Vector of names.
;;         filename > Name of file in which result need to be saved.
;;
;; Example: (save-births-by-name ["Harry" "John"] "harry-john.png")
;;
(defn save-births-by-name
  [cnames filename]
  (incanter.core/save (chart-by-birth-name cnames) filename))

;; Most Popular names in last 10 years 
(defn most-popular-name []
  (with-data
    (->> (incanter.core/query-dataset namedata {:year {:$in (set (range 2008 2018 1))}})
         (incanter.core/$rollup :sum :births [:year :name])
         (incanter.core/$order [:year :name] :asc)
         (#(incanter.core/sel % :rows (range 10))))
    (incanter.charts/line-chart :year :births :group-by :name
                                      :legend true
                                      :y-label "Births"
                                      :x-label "Year"
                                      :title  "Trends")))

(defn view-most-popular-name []
   (incanter.core/view (most-popular-name)))

;; (def f10data (incanter.core/query-dataset namedata {:year {:$in (set (range 2008 2018 1))}}))
;; (def f10grouped (incanter.core/$rollup :sum :births [:year :name]) f10data)
;; 
;; (def f10sorted (incanter.core/$order [:year :name] :asc f10grouped))


(defn check-and-update [resultmap year bname bncount]
  (let [kbname (keyword bname)]
   ;; (prn kyear)
    (assoc-in resultmap [year kbname] bncount)))

                   
(defn process-record [cnames cyear ccount]
  (let [totalrec (count cyear)]
  (loop [i 0  v {}]
    (if (< i totalrec)
      (recur (inc i) (check-and-update v (get cyear i) (get cnames i) (get ccount i)))
      v))))


