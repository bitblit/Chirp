# Branch: V05
# Goals: 
* Create a table in DynamoDB to store the chirps
* Add functions in ChirpService to store and list chirps
* Add HTTPS endpoints for store and list
* Add functionality to the HTML page for store and list

## Introduction to V05
In this section we make the website truly dynamic by adding the ability to store and read Chirps.  We will be using
AWS's DynamoDB (A NoSQL database) to store our Chirps, which makes scaling up a breeze (and cost effective).  Learning
DynamoDB is outside of the scope of this document, but  [you can read up on it.](http://aws.amazon.com/dynamodb/)

## Create a table in DynamoDB to store the chirps

* Go into AWS console, and select DynamoDB
* Hit **Create Table**
* Name the table **chirps**
* Use just a hash key, of type string, with the name uid
* 

* Create a table named **chirps** with the hash key **uid** of type String (hash key only)
** global secondary index on created (type string)
** read and write capacity of 2


## Add functions in ChirpService to store and list chirps
## Add HTTPS endpoints for store and list
## Add functionality to the HTML page for store and list




Congrats!  If you reached here, you are ready to move on to <a href="V06.md">Version 06</a>

