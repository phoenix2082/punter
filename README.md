# punter

Demo project for using Clojure for Data Processing and Incanter for Charting.

- [ ] - Be less lazy.
- [ ] - Add detailed documenation
- [ ] - Refactor wherever possible.

## Usage

First Method: 

 1. Clone the project
 2. cd in directory
 3. Execute following command:
    
    $ lein repl
 
 And wait for repl to show up.
 
 Run followin command to view a normalized map of timetzone vs Windows vs Others OSes.
 
 hello-bitly.core=> (view-os-by-timezones-normalized)
 
 EASY WAY - Open project in Cider and read & FOLLOW the comments to view some work in action.
 
 ## Bitly Users Usage Analysis.
 
 ![alt "Data: Bitly Usage by Timezone for Windows vs Other Operating Systems"](https://github.com/phoenix2082/punter/blob/master/images/tzvsos.png) 

## Movielens - Most rated movies.

![alt "Data: Movieslens Movies Most Rated Movies"](https://github.com/phoenix2082/punter/blob/master/images/top10.png)

## DataSource - US Baby Names 1880-2017.

![alt "Data: United States Baby Names"](https://github.com/phoenix2082/punter/blob/master/images/birth-trends.png)

**Number of children with name John, Harry & Marry since 1880 to 2017. Interestingly Harry was not very common name compared to John and Mary.**

![alt "Data: People with name Harry, John & Mary"](https://github.com/phoenix2082/punter/blob/master/images/harry-john-mary.png)

## DataSource - US California Housing Prices Dataset

Run "lien repl" command from project root directory and switch to namespace using below command:


    hello-bitly.core=> (in-ns 'hello-bitly.us_housing)
    #object[clojure.lang.Namespace 0x7fe87037 "hello-bitly.us_housing"]

Run following command to print data for various features in tabular method.

hello-bitly.us_housing=> (describe-nicely housingdatasets)
"creating decimal formatter for 2"
"creating decimal formatter for 6"

```
| :fname | :median_income | :total_bedrooms | :longitude | :population | :housing_median_age | :latitude | :median_house_value | :total_rooms | :households |
|--------+----------------+-----------------+------------+-------------+---------------------+-----------+---------------------+--------------+-------------|
|  count |          20640 |           20640 |      20640 |       20640 |               20640 |     20640 |               20640 |        20640 |       20640 |
|   mean |           3.87 |          532.48 |    -119.57 |     1425.48 |               28.64 |     35.63 |           206855.82 |      2635.76 |      499.54 |
|     sd |       1.899776 |      422.668093 |   2.003483 | 1132.434688 |           12.585253 |  2.135901 |       115392.820404 |  2181.562402 |  382.320491 |
|    max |        15.0001 |          6445.0 |    -114.31 |     35682.0 |                52.0 |     41.95 |            500001.0 |      39320.0 |      6082.0 |
|    min |         0.4999 |             0.0 |    -124.35 |         3.0 |                 1.0 |     32.54 |             14999.0 |          2.0 |         1.0 |

```
