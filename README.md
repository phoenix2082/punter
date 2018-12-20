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

| Some Trends   | Some More     | Some More     |
| ------------- | ------------- | ------------- |
| <a href="url"><img src="https://github.com/phoenix2082/punter/blob/master/images/housing/histograms/Households.png" align="left" height="230" width="230"/> |  <a href="url"><img src="https://github.com/phoenix2082/punter/blob/master/images/housing/histograms/Housing_median_age.png" align="left" height="230" width="230"/> | <a href="url"><img src="https://github.com/phoenix2082/punter/blob/master/images/housing/histograms/Latitude.png" align="left" height="230" width="230"/> |
| <a href="url"><img src="https://github.com/phoenix2082/punter/blob/master/images/housing/histograms/Longitude.png" align="left" height="230" width="230"/> | <a href="url"><img src="https://github.com/phoenix2082/punter/blob/master/images/housing/histograms/Median_house_value.png" align="left" height="230" width="230"/> | <a href="url"><img src="https://github.com/phoenix2082/punter/blob/master/images/housing/histograms/Median_income.png" align="left" height="230" width="230"/>  |
| <a href="url"><img src="https://github.com/phoenix2082/punter/blob/master/images/housing/histograms/Population.png" align="left" height="230" width="230"/>   |  <a href="url"><img src="https://github.com/phoenix2082/punter/blob/master/images/housing/histograms/Total_bedrooms.png" align="left" height="230" width="230"/>  | <a href="url"><img src="https://github.com/phoenix2082/punter/blob/master/images/housing/histograms/Total_rooms.png" align="left" height="230" width="230"/> |


The dataset we have longitude and latitude information. It is always pleasure to have location based dataset as it allows us to draw map and do further analysis. So see the scatter plot below which tell about distribution of dataset.

| Actual Map | Data Distribution | Data Distribution with alpha 0.1 |
| ---------- | ----------------- | -------------------------------- |
| <a href="url"><img src="https://github.com/phoenix2082/punter/blob/master/images/housing/locationmap/california-map.jpg" align="left" height="250" width="250"/>  | <a href="url"><img src="https://github.com/phoenix2082/punter/blob/master/images/housing/locationmap/california-orange.png" align="left" height="250" width="250"/>  | <a href="url"><img src="https://github.com/phoenix2082/punter/blob/master/images/housing/locationmap/california-orange-alpha-01.png" align="left" height="250" width="250"/>  |
 
 If you compare first image with second one, you can see almost all of california is covered. However second image shows all datapoint from which it is difficult to understand where the high density area. In third image we create by setting alpha 0.1 which means draw low density area lightly. From this we can infer that high density area are around bay area, Sacramento, Fresno, Log Angeles and San Diego. This seems like a good deduction for further analysis.

Alright next thing: **Median Income vs Median House Value Comparision**

<table>
  <tr>
   <th> **Median Income vs Median House Value** </th>
   <th> **Description** </th>
  </tr>
  <tr> 
   <td width="50%" align="center"> 
       <img src="https://github.com/phoenix2082/punter/blob/master/images/housing/median-income-vs-median-house-value.png" 
            align="left" height="250" width="250"/> 
   </td>
   <td width="50%"> 
    Well What is going on here ? First you can see there is a straight line around $500,000, because our dataset has upper cap on that value. You can also notice same for some other value. We need to get rid of those value from our dataset, otherwise we might get unexpected result during further analysis. 
   </td>
  </tr>
 </table>
 
