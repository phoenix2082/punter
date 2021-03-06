(ns hello-bitly.movie_lens
  (:gen-class)
  (:import (java.lang.Double)
           (java.text.DecimalFormat)
           (org.jfree.chart StandardChartTheme)
           (org.jfree.chart.plot DefaultDrawingSupplier)
           (java.awt Color)
           (java.io ByteArrayOutputStream 
                    ByteArrayInputStream))
  (:require [cheshire.core :refer :all]
            [clojure.walk]
            [clojure.pprint :as pp]
            [clojure.string :as cstr]
            [criterium.core :refer :all]
            [hello-bitly.util :as hbu]))

(use '(incanter core charts datasets))

(def mlusers "ml-1m/users.dat")       ; path to movielens user data file
(def mlratings "ml-1m/ratings.dat")   ; path to movielens user ratings data file.
(def mlmovies  "ml-1m/movies.dat")    ; path to movielens movie data file. 

(def df (new java.text.DecimalFormat "##.000000"))


(defn round-6
  "Round the value to two decimal places"
  [num]
  (Double/valueOf (.format df num)))

(defn read-table [path seperator names]
  (as-> (hbu/read-file path) records
    (-> (pmap (fn [rec] (cstr/split rec (re-pattern seperator))) records)
        (into names))))

(defn print-nice
  "Print data in pretty tabular format. Useful when you have large dataset
   and want to view data from range combined with headers. See next line for expected data.

   Input:  
     headers > [\"user_id\" \"movie_id\" \"rating\" \"timestamp\"]
     records > [[\"1\" \"1\" \"5\" \"978824268\"] [\"1\" \"1961\" \"5\" \"978301590\"]
   
   Output: 
     | user_id | movie_id | rating | timestamp |
     |---------+----------+--------+-----------|
     |       1 |     1961 |      5 | 978301590 |
     |       1 |     1962 |      4 | 978301753 |
    "
    [headers records]
    (-> (map (fn [x] (zipmap headers x)) (rest records))
        (pp/print-table)))

(defn convert-to-map [record]
  (pmap (fn [x] (zipmap (pmap keyword (first record)) x)) (rest record)))

(defn new-convert [headers movie]
  (let [mmovie (zipmap (map keyword headers) movie)]
    (assoc-in {} (keyword (:movie_id mmovie)) mmovie)))

(defn view-table-data 
  "Print data in nice tabular format.
   mrecords should be vector of vectors where first record is headers.
   e.x. [[\"movie_id\" \"title\" \"genres\"]
         [\"1\" \"Toy Story (1995)\" \"Animation|Children's|Comedy\"]"
  [mrecords]
  (clojure.pprint/print-table (convert-to-map mrecords)))

;; TODO: Is this still required ? Remove if not.
(defn my-merge [x y]
  (let [my-count (count x)]
    (loop [i 0 m [] x1 x y1 y]
      (if (< i my-count)
         (recur (inc i) (conj m (into (first x1) (first y1))) (rest x1) (rest y1))
        m))))
        
(def names [["user_id" "gender" "age" "occupation" "zip"]])
(def rnames [["user_id" "movie_id" "rating" "timestamp"]])
(def mnames [["movie_id" "title" "genres"]])

(defn merge-user-movies-ratings
  " Merges all movies, ratings and users into a single record."
  [usermap ratingmap moviesmap]
  (sort-by :user_id (clojure.set/join (clojure.set/join usermap ratingmap) moviesmap)))

(defn check-and-update
  "Process an individual record and add to map, almost like a pivot.

   INPUT: Individual movie record.
    rec - {:movie_id 1234, :rating 3, :gender M, :title \"12 Angry Men\"}

   Output: A continuosly updated map.
   e.x.
     [
     {:1234 {:title \"12 Angry Men\",
             :F [2 4 5 3]              ; Ratings by all female users
             :M [1 3 4 5]}}            ; Ratings by all male users
     .....]
  "
  [rec v]
  (let [movie_id (keyword (:movie_id rec))
        ratingval (:rating rec)
        rating (Integer/parseInt (if-let [rv ratingval] rv 0)) ]
  (if (contains? v movie_id)
    ;;(-> (keys (movie_id v))
    ;;    ((fn [x]
    (if (= (:gender rec) "F")
      (update-in v [movie_id :F] conj rating)
      (update-in v [movie_id :M] conj rating))
    (->
     (if (= (:gender rec) "F")
      (assoc-in v [movie_id :F] [rating])
      (assoc-in v [movie_id :M] [rating]))
     (assoc-in [movie_id :title] (:title rec))))))

(defn check-and-update-ratings
  "Process an individual record and add to map, almost like a pivot.

   INPUT: Individual movie record.
    rec - {:movie_id 1234, :rating 3, :gender M, :title \"12 Angry Men\"}

   Output: A continuosly updated map.
   e.x.
     [
     {:1234 {:title \"12 Angry Men\",
             :F [2 4 5 3]              ; Ratings by all female users
             :M [1 3 4 5]}}            ; Ratings by all male users
     .....]
  "
  [rec v]
  (let [movie_id (keyword (:movie_id rec))
        rating (Integer/parseInt (:rating rec)) ]
  (if (contains? v movie_id)
    ;;(-> (keys (movie_id v))
    ;;    ((fn [x]
    (if (= (:gender rec) "F")
      (update-in v [movie_id :F] conj rating)
      (update-in v [movie_id :M] conj rating))
    (->
     (if (= (:gender rec) "F")
      (assoc-in v [movie_id :F] [rating])
      (assoc-in v [movie_id :M] [rating]))
     (assoc-in [movie_id :title] (:title rec))))))

(defn pivot-title-gender-rating-map
  " Create a almost pivot table like map of all ratings for all movies by gender.

    Output: A continuosly update map.
   e.x.
     [
     {:1234 {:title \"12 Angry Men\",
             :F [2 4 5 3]              ; Ratings by all female users
             :M [1 3 4 5]}}            ; Ratings by all male users
     .....]
  "
  [tgrm]
  (let [total-records (count tgrm)]
    (loop [i 0 v {}]
      (if (< i total-records)
        (recur (inc i) (check-and-update (get tgrm i) v))
        (into {} v)))))


(defn get-trmap-trg []
  (let [movies (read-table  mlmovies "::" mnames)
        ratings (read-table mlratings "::" rnames)
        musers (read-table mlusers "::" names)
        allmovies (convert-to-map movies)
        allrating (convert-to-map ratings)
        allusers (convert-to-map musers)
        all-records-merged (merge-user-movies-ratings allusers allrating allmovies)
        trmap (pmap (fn [x] (select-keys x [:movie_id :title :gender :rating])) all-records-merged)
        trmap-trg (pivot-title-gender-rating-map (vec trmap))]
    trmap-trg))

(defn calcualte-mean-by-gender-rating
  " Calculate the mean ratings for all movies.
    TODO: Fix NaN issue when there is no ratings by a gender.
  "
  [grvector]
  (map (fn [x]
         (let [mr (:M (second x)) fr (:F (second x))]
           { :title (:title (second x)),
            :mcount (count mr),
            :mmean (if-not (nil? mr) (hbu/calculate-mean mr) 0),
            :fcount (count fr),
            :fmean (if-not (nil? fr) (hbu/calculate-mean fr) 0)})) grvector))

;; calculate final means
;; (def final-means (calcualte-mean-by-gender-rating (vec trmap-trg)))

;; filter by movie title
;; (filter #(= (:title %) "12 Angry Men (1957)") final-means) 
;; ({:title "12 Angry Men (1957)", :mcount 615, :mmean 4.297560975609756, :fcount 1, :fmean 3.0})

(defn view-result-by-range
  " Total final means count is 3706.
    If you want to see result for a range, use this method.

    rmeans - all calcualted means
    sindex - start index
    eindex - end index

    For example call like below inside cider repl,

    hello-bitly.movie_lens> (view-result-by-range final-means 0 5)

    Will give output like this:

    |                                               :title | :mcount |             :mmean | :fcount | :fmean |
    |------------------------------------------------------+---------+--------------------+---------+--------|
    | Prisoner of the Mountains (Kavkazsky Plennik) (1996) |      14 | 3.7857142857142856 |       0 |    NaN |
    |      Best Man, The (Il Testimone dello sposo) (1997) |       8 |              3.625 |       1 |    5.0 |
    |                       Last Days of Disco, The (1998) |     134 |  3.298507462686567 |       1 |    5.0 |
    |        Jungle2Jungle (a.k.a. Jungle 2 Jungle) (1997) |     146 |  2.335616438356164 |       0 |    NaN |
    |                             
  "
  [rmeans sindex eindex]
  (clojure.pprint/print-table (subvec (vec rmeans) sindex eindex)))

(defn view-result-by-movie-name
  " View the means of movie rating in pretty format.

    Input:
       rmeans - all means vector.
       mname - name of the movie - \"12 Angry Men (1957)\"

    output will be something like this:

    |              :title | :mcount |            :mmean | :fcount | :fmean |
    |---------------------+---------+-------------------+---------+--------|
    | 12 Angry Men (1957) |     615 | 4.297560975609756 |       1 |    3.0 |

    where :title - name of the movie
          :mcount - number of ratings by male users.
          :mmean - mean value of all the ratings.
          :fcount - number of ratings by female users.
          :fmean - mean value of all the ratings by female users.
  "
  [rmeans mname]
  (clojure.pprint/print-table (filter #(= (:title %) mname) rmeans)))

(defn get-means-with-atlease-n-ratings
  "Gets the means for record where atlease n rating.
   mmeans - Vector of all means
   nratings - Integer. Total rating count greater than this number.

   Output: New Vector of map of mean ratings filter by criteria.
  "
  [mmeans nrating]
  (filter (fn [x] (> (+ (:mcount x) (:fcount x)) nrating)) mmeans))


(defn movies-with-gender-diff-250-ratings
  [omeans nratings]
  (as-> (get-means-with-atlease-n-ratings omeans nratings) omeansn
      (map (fn [x] {:F (:fmean x),
                    :M (:mmean x),
                    :diff (- (:mmean x) (:fmean x)),
                    :title (:title x)}) omeansn)))

(defn get-total-ratings [rec]
  (+ (count (:M rec)) (count (:F rec))))

(defn filter-rating-by-count [rec minrating]
  (> (get-total-ratings rec) minrating))

(defn filter-all-record-by-rating-count [records minrating]
  (filter #(filter-rating-by-count (second %) minrating) records))

(defn get-movies-by-most-ratings
  [records]
  (-> (pmap (fn [[k m]] {:title (:title m),
                        :totalratings (get-total-ratings m)}) records)
      (vec)
      (#(sort-by :totalratings %))
      (reverse)))

(defn set-custom-theme-colors
  [chart]
     (let [plot (.getPlot chart)
           renderer (.getRenderer plot)]
       (do
          (doto plot
           (.setBackgroundPaint (java.awt.Color.  230 247 255)) ;java.awt.Color/white)
           (.setRangeGridlinePaint (java.awt.Color. 235 235 235))
           (.setDomainGridlinePaint (java.awt.Color. 235 235 235)))
         (doto renderer
           (.setOutlinePaint java.awt.Color/white)
           (.setPaint (java.awt.Color.  0 153 230)));; 77 255 166)))
         chart)))

(def all-cyan-theme
      (doto
        (StandardChartTheme/createJFreeTheme)
        (.setDrawingSupplier
        (proxy [DefaultDrawingSupplier] []
          (getNextPaint [] Color/cyan)))))

(defn sort-by-ratings [ivec]
  (sort-by :totalratings ivec))

(defn prepare-bar-chart [tmap]
  (incanter.charts/bar-chart
         (map #(:xitems %) tmap)
         (map #(:yitems %) tmap)
         :title "Movies with Most Ratings"
         :y-label "Count"
         :x-label "Title"
         :legend false
         :vertical false
         :theme all-cyan-theme))

(defn gen-fav [records fprop sprop minc mcount]
   (let  [items (filter #(> (fprop %) minc) records)]
    (-> (sort-by sprop items)
        (reverse)
        (#(take mcount %))
        (#(pmap (fn [x] {:xitems (:title x), :yitems (sprop x)}) %)))))

(defn view-gen-fav 
   " Call this method to view top N movies to PNG images.
    e.x. (view-movies-with-most-ratings records 10). Here:

    records - Vector of map of movieid and gender and rating group.
    mcount - number of movies you want to see in chart.
  "
  [records fprop sprop minc mcount]
  (-> (gen-fav records fprop sprop minc mcount)
      (prepare-bar-chart)
      (set-custom-theme-colors)
      (incanter.core/view)))  

(defn female-favorites [records minratings mcount]
  (let  [items (filter #(> (:fcount %) minratings) records)]
    (-> (sort-by :fmean items)
        (reverse)
        (#(take mcount %))
        (#(pmap (fn [x] {:xitems (:title x), :yitems (:fmean x)}) %)))))
  
(defn view-female-favs 
   " Call this method to view top N movies to PNG images.
    e.x. (view-movies-with-most-ratings records 10). Here:

    records - Vector of map of movieid and gender and rating group.
    mcount - number of movies you want to see in chart.
  "
  [records minratings mcount]
  (-> (female-favorites records minratings mcount)
      (prepare-bar-chart)
      (incanter.core/view)))  

(defn movies-with-most-ratings [records mcount]
  (-> (get-movies-by-most-ratings (vec records))
      (vec)
      (sort-by-ratings)
      (reverse)
      (#(take mcount %))
      (#(pmap (fn [x] {:xitems (:title x), :yitems (:totalratings x)}) %))))

(defn view-movies-with-most-ratings
   " Call this method to view top N movies to PNG images.
    e.x. (view-movies-with-most-ratings records 10). Here:

    records - Vector of map of movieid and gender and rating group.
    mcount - number of movies you want to see in chart.
  "
  [records mcount]
  (-> (movies-with-most-ratings records mcount)
      (prepare-bar-chart)
      (incanter.core/view)))

(defn save-movies-with-most-ratings
  " Call this method to save top 10 movies to PNG images.
    e.x. (save-movies-with-most-ratings records 10). Here:

    records - Vector of map of movieid and gender and rating group.
    mcount - number of movies you want to see in chart.
  "
  [records mcount]
  (-> (movies-with-most-ratings records mcount)
      (prepare-bar-chart)
      (incanter.core/save "topmovies.png")))
      

(defn get-naming-trends []
  (let [mvdata (movies-with-most-ratings (get-trmap-trg)  10)
        mlchart (prepare-bar-chart mvdata)
        out-stream (ByteArrayOutputStream.)
        in-stream (do
                    (save mlchart out-stream)
                    (ByteArrayInputStream.
                     (.toByteArray out-stream)))]
    in-stream))

(defn user-gender-map [userlist]
  (into {} (map (fn [x] (assoc {} (keyword (:user_id x)) (:gender x))) userlist)))

(defn count-rating-by-gender
  [ratinglist ugenmap]
  (frequencies
   (for [x ratinglist]
     ((keyword (:user_id x)) ugenmap))))

(defn movies-by-rating-count
  [moviesratings]
  (into {}
        (for [x moviesratings]
          (assoc {} (first (keys x)) (count (first (vals x)))))))

(defn movie-names [movielist]
  (into {}
        (map (fn [x] [(keyword (:movie_id x)) (:title x)]) movielist)))

(defn sort-map-by-vals [umap]
  (into (sorted-map-by (fn [key1 key2]
                         (compare [(get umap key2) key2]
                                  [(get umap key1) key1])))
        umap))

(defn most-rated-movies [rating-map movie-map]
  (for [mkey (keys rating-map)]
    (assoc {} (mkey movie-map) (mkey rating-map))))

(defn get-results []
  (let [movies (read-table  mlmovies "::" mnames)
        ratings (read-table mlratings "::" rnames)
        musers (read-table mlusers "::" names)
        allmovies (convert-to-map movies)
        allrating (convert-to-map ratings)
        allusers (convert-to-map musers)]))
