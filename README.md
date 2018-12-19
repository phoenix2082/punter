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
 

| **Bitly Users Usage Analysis.** | **Movielens - Most rated movies.** |
| ------------------------------ | --------------------------------  |
|  ![alt "Data: Bitly Usage by Timezone for Windows vs Other Operating Systems"](https://github.com/phoenix2082/punter/blob/master/images/tzvsos.png) | ![alt "Data: Movieslens Movies Most Rated Movies"](https://github.com/phoenix2082/punter/blob/master/images/top10.png) |


## DataSource - US Baby Names 1880-2017.

| Birth Trends by Gender | **Number of children with name John, Harry & Marry since 1880 to 2017. Interestingly Harry was not very common name compared to John and Mary.** |
|----------------------- | ------------------- |
| ![alt "Data: United States Baby Names"](https://github.com/phoenix2082/punter/blob/master/images/birth-trends.png) | ![alt "Data: People with name Harry, John & Mary"](https://github.com/phoenix2082/punter/blob/master/images/harry-john-mary.png) |


## DataSource - US California Housing Prices Dataset



| Some Trends  | Some More |
| ------------- | ------------- |
| ![alt "Houshold Histograms "](https://github.com/phoenix2082/punter/blob/master/images/housing/histograms/Households.png)  |  ![alt "Housing Median Age"](https://github.com/phoenix2082/punter/blob/master/images/housing/histograms/Housing_median_age.png)  |
| ![alt "Latitude Histogram"](https://github.com/phoenix2082/punter/blob/master/images/housing/histograms/Latitude.png)| ![alt "Longitude Histogram"](https://github.com/phoenix2082/punter/blob/master/images/housing/histograms/Longitude.png)|
| ![alt "Median_house_value Histograms"](https://github.com/phoenix2082/punter/blob/master/images/housing/histograms/Median_house_value.png)  |  ![alt "Median_income Histogram"](https://github.com/phoenix2082/punter/blob/master/images/housing/histograms/Median_income.png)  |
| ![alt "Population Histogram"](https://github.com/phoenix2082/punter/blob/master/images/housing/histograms/Population.png)| ![alt "Total Bedrooms Histogram"](https://github.com/phoenix2082/punter/blob/master/images/housing/histograms/Total_bedrooms.png)|
