# Module: V05
# Goals: 
* Add a database to our API
* Deploy the new API features
* Use those features on our static page

## Introduction to V05
You now have a functioning website with both dynamic and static content being served through Cloudfront.  We have the
basic plumbing done and its time to start adding features.  We're going to create a DynamoDB Table for storing our "chirps",
and add functions to our api to add new chirps and fetch the existing ones.

## Add a database to our API

First, we'll need to create our table.  From the AWS Console, go to **Dynamo DB**.  Pass the introductory page (if you haven't before)
and hit **Create Table**.  There are lots of details on how to correctly build a Dynamo DB table for rapid access... We are going to skip
all of them here and build a very simplistic table.  Just realize, as with the security constraints, that you wouldn't do it
exactly this way in a production site.

Set the table name to **chirp**, the primary key to **userId** (type string) and check the **add sort key** box, then
enter the name **timestamp** for the sort key (set the type to Number).

Uncheck *use default settings* and change the read and write capacity to 2 each - we won't be using 5 TPS at this time.

While that table is being created, lets take a look at the new and improved chirp service under **src/api/_lib**.  It's still
pretty simple, but you'll notice that we have included a new library, boto3, that is used to interact with AWS services.  In this
case we'll be using it to talk to dynamodb; using the get_item, put_item, and query functions to get and save new chirps.  Then
take a look at _api... 2 new api endpoints in there, fetch_chirps and save_chirp.  You'll also notice that in the UpdateServer
script they are now wired in to be uploaded as chirp_save_chirp and chirp_fetch_chirps.

## Deploy the new API features

Here's where we leave a big exercise for the reader - for both of the new api functions you'll need to follow the instructions from section
4 to create Lambda functions with the correct names... then run the UpdateServer script to populate them (you need to create the functions
manually - UpdateServer will only update functions, not create them).  Once they are created, you'll need to create a new endpoint in
your API /chirp (at the same level as /server).  On /chirp we will create two methods, POST and GET.  The POST will call the save_chirp
function and GET will call the fetch_chirps function.  Be sure to add CORS (it'll only need to be done once, since the OPTIONS will apply to
both the POST and GET at that url) and then deploy the API (deploy it over the top of V1).  Finally, you'll want to download the new 
javascript library and install it in the *static-site/src/main/webapp/js* directory.  Sound like a lot?  It is, but nows the time to 
get your feet wet.  Some errors will do you good. And when you're finished, come back because in the next step we'll start calling 
those functions to implement Chirp!

## Use those features on our static page

TBD: Explain the changes to the index.html page


