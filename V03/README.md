# Module: V03
# Goals: 
* Create a very basic API Lambda function and test it locally
** Run chirp_service test locally
** Run server status locally
* Create a IAM Role for our Lambda function to use when it executes
* Deploy the API to Lambda
* Test server status on lambda


## Introduction to V03
At this point you have a fully functional, easy to deploy static website serving off of S3 and Cloudfront.  This website
should have a latency of about 250ms in the US, with about three to four nines of availability and costing pennies per
month unless you are taking a lot of traffic.  Not too bad, but lets face it, you could do the same thing with about any
static website hosting service.  We want to make things real - add dynamic content.  In years past I would have told you
to use Elastic Beanstalk to create an API layer (in fact, earlier versions of this tutorial did exactly that!) but now 
I'm going to go even cheaper - we are going to build our site on API Gateway and AWS Lambda.

(That's all you're going to hear about API gateway for now.  This module is all about Lambda)

AWS Lambda (https://aws.amazon.com/lambda/) is what's called a "serverless architecture".  That doesn't mean that there isn't
a server (its not magic!) but it does mean that YOU don't run a server.  You aren't responsible for scaling up or down in 
response to load, and if no one is using your website you aren't paying for idle hardware!  We will set things up so that
when someone requests something from your site the server will run for JUST THAT REQUEST, and then stop.

Before we can do that through the web though, we are going to do it locally.  Actually, first we will run the function locally
(on your own machine).  Then we will deploy it to Lambda, and run it there with a "fake request".  Only after both these work
will we move on to the next section where we will make it work in response to actual web requests.

### Why Python?
I'm a Java guy myself, but for reasons having to do with how the JVM is started and classes are loaded, Java is just too
slow to be useful behind API gateway at the moment.  Your other options are Python and NodeJS (javascript).  I've done both, and
Javascript is marginally faster, but I just happen to like Python.  So here we are.  If you really like Node you should try it
out after finishing up here, the Lambda tooling for node is quite nice (at least when paired with Grunt).

## Create a very basic API Lambda function and test it locally
Ok, I know I sound like a total liar now, but I'm going to break the "try it first rule" one more time, and have you
**cp -R V03/api MINE** now.  That will bring in the API layer sample.  Like in V02, we'll step through what just happened.

After you did the copy you should see that the static-site directory didn't change at all, but you have a new directory,
**api** in the root directory.  It is important to understand that you have **2** modules here, one a set of functions that
will become your API layer, and a static server that serves HTML and everything else.  When we reach the point of development, we
will start the Tomcat server and our local content will be served from Tomcat, but the API services will be running on AWS.  If 
this were a production system you'd need a way to separate the current production version of the API from the "new development"
version of the API, but thats out of scope for this discussion.

Browsing the api directory, you'll see a **src** directory.  Feel free to browse around in there.  You'll see two subdirectories,
_api and _lib.  These start with underscores because, thanks to how Lambda works, if we want to use third party libraries they will
show up in the src directory too... and the underscores makes sure that your personal stuff lists first alphabetically.

The _api directory will hold your "API Endpoints"... basically, one file (and Lambda function) for each url that consumers of your
API can use.  You'll see that right now it has 1 file in there, server_status.py, a simple function for us to learn how to do this.

The _lib directory will hold anything that gets reused acrossed multiple API endpoints.  You'll see it has these contents to start with:
* chirp_service : This is where we will put all the logic for actually interacting with the chirp database (we'll do that later)
* illegal_access_error : This is the error we'll throw when a user is unauthorized (403)
* invalid_request_error : This is the error we'll throw when a user sends an bad request (400)
* no_such_resource_error : This is the error we'll throw when a user asks for something that doesnt exist (404)
* lambda_api : This is a helper library to make sure our API responses are consistent, following good API design principles
like those laid out in the Apigee API design book

The server_status endpoint can be used to test the normal operation of Lambda, and also test the error endpoints.  You'll see how in 
later modules.

Testing non-lambda functions (like the functionality in chirp_service) is pretty easy - we just add a main function at the bottom and 
run the script.  There are much nicer test frameworks available in Python than this, but it'll get us through this tutorial.  We'll try
this in just a moment.

Now take a look at the **test** directory.  You'll see a directory named **event** and in that, a file named **server_status.json**.  This
matches the server_status api function.  In general, when we add new functions we will also add event files in here for testing.  Lambda works 
by running your function in response to "events".  In the case of API gateway, the incoming HTTP request is converted to an event that
looks like the contents of this file.  Your function is then called with that event object and a context object that represents the 
general processing environment (that we'll ignore for these purposes).  You'll see how this is read in below, and then passed to the
lambda function for local testing, making it easy to simulate web requests locally.  Pretty cool, eh?  To test other variables, you just
change this file and call it again.

## Run Chirp_service test locally

If you look in the chirp_service file you'll see it describes a Python object that already has several functions we'll need for
Chirp - getting counts of all chirps, saving new chirps, etc.  They are currently *stubbed* out, which means we have defined the
interface but not actually made them work yet.  Note that the stub for chirp_count just returns 0.  You'll also notice the test
at the bottom of the script just creates an instance of the script and calls get_chirp_count.  Lets try that now, just to make sure
our Python is setup correctly.

* ** cd api/src **
* ** python _lib/chirp_service.py **

This should give you the output:

 Running test
 Count: 0

Let's try it slightly differently though, like this:

* ** cd api/src **
* ** python -m _lib.chirp_service **

It should give the same result.  This runs chirp service as a module instead of a script, which will be much more useful once we start
depending on third party libraries.

## Run server_status locally

Now we'll do the same thing, but we want to run server status lambda function:

* ** cd api/src **
* ** python -m _api.server_status **

Should give the results:

  -----------------------------------
  {'notes': None, 'code': 200, 'data': {'status': 'ok', 'serverTimeFormatted': '2016-07-19 16:57:40', 'serverTime': 1468972660, 'serverStartTime': 1468972660, 'version': '4', 'serverStartTimeFormatted': '2016-07-19 16:57:40'}}

Of course, your time values will be different.  If you look inside the server_status file you'll see in the main function it is calling
a helper function within lambda_api, and passing __file__ (a reference to itself) and lambda_handler (a reference to its lambda function).  These
helper functions use the naming convention to find the appropriate event file, read it from json into an object, and pass it to the lambda
function for execution.


## Create a IAM Role for our Lambda function to use when it executes

Now we are going to create some security infrastructure for our API layer.  First off, an **IAM Role**.  Understanding
roles completely is outside of the scope of this document, but for the moment assume a role is like the user account you 
created for yourself, except it is an account used by EC2 instances instead of people.  We will create one, and every
EC2 instance created by Elastic Beanstalk will have that role, allowing it to do whatever privileges we grant to the 
role.  

So head into AWS console, and hit IAM, then Roles.  Hit **Create new Role** and name it **chirp-prod**.  Select the 
service role of **Amazon EC2**, and then grant it **Administrator Access** (Yes, all the things I said about Admin
access for you also apply to roles here.  Don't do this on a real production server).

Next, we are going to create a keypair.  Keypairs are basically passwords that allow you to login directly (using SSH) to
EC2 instances.  Generally, logging into your Elastic Beanstalk instances is a bad idea, because it leads you to think
about them as if they are constant servers, instead of ephemeral things that can disappear at any minute - but when you
are debugging an app, the ability to login and watch whats going on sometimes comes in handy.  Go to the console and 
click **EC2**, then under **Network and Security** select **Key Pairs**.  Hit **Create Key Pair** and use the name
**chirp-key**.  It should download you a file called **chirp-key.pem**.  Store this file in a safe place, and
**NEVER** check it into source control.

## Deploy the API to Elastic Beanstalk

Now we are going to make this thing live on a public server.  By the end of this step we'll be automating the proces, but
for the first release we'll do it manually.  Start out by, in the **api** directory, running the command 
**mvn clean package**.  Then, look in the **target** directory, where you should see a file named
**chirp-api-1.0.0-SNAPSHOT.war**.

Now go to the AWS console in your 


Now go to your AWS Console in your browser, and select **Elastic Beanstalk**, and then select **Create new application**.
Going through the wizard it then runs, you'll want to use the values (anything that isn't specified below can be left as 
the defaults):
* Application Name: Chirp
* Environment Tier:  Web Server
* Predefined Configuration: Tomcat
* Environment Type: Load Balancing, Autoscaling
* Source: Select **Upload your own**, then **Choose File** and select the **chirp-api-1.0.0-SNAPSHOT.war** from the target
directory you found in the last step
* Environment Name: Chirp-prod
* Environment URL: Here you'll have to do some exploring.  The URL's have to be unique; they don't really matter since you'll
be using your CNAME, but remember what you put here because you'll need it later.  I'll use "api.elasticbeanstalk.com"
in this document, replace it with your own url as necessary
* We won't create an RDS instance, and not in a VPC this time (leave both unchecked)
* EC2-Keypair: Select chirp-key
* Email address: Enter your address.  You'll be emailed when important things (like server crashes) happen on your EB application
* Application health check URL: Leave blank for now.  This will cause the Load balancer to use a TCP-ping to tell if the 
instance has crashed.  Later we'll wanna replace that with a better check, but this is ok for the moment
* Instance profile: Select **chirp-prod**
* Environment tags: leave blank

At that point you can go ahead and launch!  Wait watching the little circle until we get a green check, meaning the application
is up and ready to serve traffic.  Once it is green, you should be able to click on the link next to **chirp-prod**, and 
open your environment in a new window, which will give you a 404 because chirp doesn't serve anything at the root level.  Go
instead to **http://api.elasticbeanstalk.com/api/v1/info/server** to get the familiar endpoint we saw when we were running 
locally.




-- replace your bucket in updateserver.sh

