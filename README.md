# punter

Demo project for using Clojure for Data Processing and Incanter for Charting.


## Web Application Preview - 

Shwoing Housing Tab. Switch Tab to view different dataset.

| ** Housing Tab ** |
| ------------------------------ | 
|  <a href="url"><img src="https://github.com/phoenix2082/punter/blob/master/images/WebPreview.png" align="left" height="384" width="512" alt= "Data: Bitly Usage by Timezone for Windows vs Other Operating Systems"/> | 
 
## Usage


 1. Clone the project - 
     
     $ git clone https://github.com/phoenix2082/punter.git
 
 2. cd in directory - 
    
     $ cd punter
 
 3. Execute following command to launch as web application:
    
    $ lein fig -- -b dev -r
 
 It will automatically launch default browser and will open following URL: http://localhost:9500. 
 
 ## Below command still works
 
 Run following command to view a normalized map of timetzone vs Windows vs Others OSes.
 
 hello-bitly.core=> (view-os-by-timezones-normalized)
 

 
