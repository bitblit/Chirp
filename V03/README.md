# Module: V03
# Goals: 
* Create a very basic API Lambda function and test it locally
* Create a IAM Role for our Lambda function to use when it executes
* Deploy the API to Lambda and test it there


## Introduction to V03
At this point you have a fully functional, easy to deploy static website serving off of S3 and Cloudfront.  This website
should have a latency of about 250ms in the US, with about three to four nines of availability and costing pennies per
month unless you are taking a lot of traffic.  Not too bad, but lets face it, you could do the same thing with about any
static website hosting service.  Time to make things real.  Time to add dynamic content, served from Elastic Beanstalk.  If
you don't know what Elastic Beanstalk is or why it is cool for deploying your API layer, start out by heading 
[here](http://aws.amazon.com/elasticbeanstalk) to get a general idea of why you would use it.

For this version we are going to start with a VERY simple 
[Spring MVC](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html) app for your API layer
- it has only one endpoint, **/api/v1/info/server** that returns a JSON body containing a single item, **serverTime** with 
the current server timestamp.  It uses Spring's newer Java configuration instead of XML.

## Create a very basic API layer server and test it locally
Ok, I know I sound like a total liar now, but I'm going to break the "try it first rule" one more time, and have you
**git merge V03** now.  That will bring in the API layer sample, and update the top level POM to also reference it as
a submodule.  Like in V02, we'll step through what just happened.

After you did the merge you should see that the static-site directory didn't change at all, but you have a new directory,
**api** in the root directory.  It is important to understand that you have **2** websites here, one an API server that 
serves JSON, and another static server that serves HTML and everything else.  When we reach the point of development, we
will need to start **BOTH** tomcat servers (on different ports) if we want to do a complete test (although ideal integration
tests wouldn't **depend** on the presence of the other piece to run - they should be independent of that.  Of course,
back here in the real world they often are somewhat interdependent).

Browsing the api directory, you'll see a **src/main/java** directory.  Feel free to browse around in there.  Aside from
a boilerplate SpringMVC config found in **com.erigir.chirp.config**, you should see the file
 **com.erigir.chirp.ctrl.v1.InformationCtrl**.  Looking in there you will see the server information endpoint, and a
 **force-500** endpoint that can come in handy when testing client-side error handling.  You'll also see the file
 **com.erigir.chirp.ChirpFilter**, which if you look in it you will see that we are setting the max age header (for
 the same reason we do on the static content - we'll tweak it later) and we are setting CORS headers wide open.  If
 you don't know what CORS is, [read this](http://en.wikipedia.org/wiki/Cross-origin_resource_sharing).  We'll need 
 CORS locally at least since API will be running on a different port than the static content.  Ironically, since 
 Cloudfront is going to mount our API server on /api/** in production CORS isn't needed there!  Still,
  once you want to allow business partners to use your API you'll be glad you understand CORS.

Let's start this up and run it locally, shall we?  CD into the api directory and run **mvn tomcat7:run**.  Notice
that when it starts up it runs on ports 8081 and 8444, (http and https) so that it can run at the same time as the
static site.  Once it starts, hit **http://localhost:8081/api/v1/info/server** and see that you get a appropriate
JSON block.  If you hit refresh you should see the time advance as well.  If this is working, go ahead and kill the
Tomcat server - you have a good local test!

## Create a IAM Role for our API servers and a EC2-Key for login

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

