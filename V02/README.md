# Module: V02
# Goals: 
* Create Maven structures for our static website
* Put some content in that structure
* Generate a deployment user and group in IAM
* Learn the basics of the seedy tool
* Upload new content using seedy and make sure it works

## Introduction to V02
It should be obvious that you could, at this point, create an entire static website served from S3 by just creating and
uploading files into that bucket.  It should also be obvious that actually doing that would be incredibly painful; So
now I'm going to introduce you to Seedy, an open source project of mine designed to simplify this step of the project.  
You don't HAVE to use it (feel free to try other things, I won't hate you) but I find it really useful to manage my 
static sites locally.  This step assumes you already know Maven pretty well, and we are just creating the right structures.

## Create Maven structures for our static website
While I know I originally told you to try doing the changes yourself before bringing in **my** changes, in this case I'm going
to break that rule and tell you to just go ahead and do **cp -R V02/static-site MINE** and **cp V02/pom.xml MINE** now.  
This is because this step is about setting up a bunch of directories and POM files for maven that you'll need for later work.  
Don't worry - we'll step through what we are doing and what changes we make.

### Information about the just-copied structure

After you do the copy, you should see that we created a directory static-site, and that it has the Maven-standard layout
**src/main/config** and **src/main/webapp**, along with a file pom.xml in the root directory.

Why the sub-directory for the static site if it has all the content?  Because in later stages we'll be adding an API directory
with our API functions.  For the moment notice that the root pom file really just contains so overall information (like the
developer name) and a list of sub-modules (at the moment, just static-site).

Now take a look in the static-site subdirectory.  At first the POM file in this module may seem a little weird since
its all going to be static content.  We place it here because we are going to use Maven for 2 things: 
* To run a webserver that serves the static content locally so we can do rapid development
* To push the content to our S3 bucket
Both of these functions are implemented as Maven plugins, making it easy both for you to do them manually, and for you
to plug them into your build server (like Jenkins or CircleCI) later if you so choose.

In the **src/main/config** directory you'll see a keystore file - this is just so that we can run 
HTTPS locally (useful since you are going to run HTTPS in production - you'll want to see how things run HTTPS).  Note that
this is a generic self-signed certificate, so your browser will complain when you hit the HTTPS port, but thats ok, it'll
still be running HTTPS.

In **src/main/webapp** you'll see the directory that we will be mirroring into our website down the road.  In there you 
will see a file (index.html) and a directory (js) which contains a file **chirp.js**, that is currently just a JQuery
placeholder for stuff to do on document load.

You'll notice that the index.html file is already configured to load JQuery and Twitter bootstrap, and the chirp.js file.  This
doesn't mean you MUST use JQuery or Twitter bootstrap, but for this exercise we will be using both.  Also notice the form
these usages take, something like this:

**<script src="//cdnjs.cloudflare.com/ajax/libs/jquery/3.1.0/jquery.min.js" type="text/javascript"></script>**

Note 2 things: first, the protocol relative url (a url that starts with //) - this means load from that server, but using
the protocol the current page was loaded with.  This prevents those annoying mixed-mode warnings when the page was loaded
https and the script is loaded http - the script will be loaded with whatever protocol the page was loaded with.

Second, notice we are using a public CDN for our JQuery.  The upside of this is that we get fast downloads, and if the customer
has visited some other page that used this same CDN they may already have that file in their browser cache!  The downsides
are that you can't develop offline (your page wont work) and, more importantly, this is a really easy way to introduce a 
security hole if you don't trust the CDN provider (you are allowing them to put whatever they want on your webpage, and
change it without notice).

## Generate a deployment user and group in IAM
Interacting with AWS always requires an Access Key and a Secret Key.  When you are on a build server, these really should
be granted via an IAM role that is granted to the EC2 box the build server is running on, but for today we are going to
use a user created just for deployments.
* Login to the AWS console
* Go to the IAM page, and hit the **Users** button
* Hit **Create new users** button, and add a user named **deployer**.
* Hit the **Download credentials** button and save the file in a secure place **NOT ANY PLACE YOU'D CHECK IN TO GITHUB!**
 If you lose this file you'll have to do this step all over again because AWS won't give them to you again
* Hit the **Groups** button, then **Create New Group**.  Name it **chirp**, and on the next page grant it
**Administrator Access**

** BIG IMPORTANT NOTE:  Granting pretty much ANYTHING administrator access is a bad idea in general.  We are doing it here
because I don't want you getting into the weeds of why your app doesn't work because the security is misconfigured.  In
real life though you will need to remove this access and replace it with the specific permissions your application needs
for deployment and operation **

* Hit the **Next Step** and **Create group** buttons.  Then, select your newly created group, and hit **Add users to group**, 
and select the user named **deployer**
* Congrats, the key/secret pair you just downloaded now has adminstrator access.
* Lets go ahead and setup that key for your local work.  At the command line, enter **aws configure** , enter
the access key and secret you just got.  Leave everything else as the defaults.  If you are on OSX, if you do a
**ls ~/.aws** you'll see the file it created.

## Learn the basics of the tomcat plugin
Take a look in the POM file in the static-site subdirectory, looking for this piece:

    <plugin>
        <groupId>org.apache.tomcat.maven</groupId>
        <artifactId>tomcat7-maven-plugin</artifactId>
        <version>2.2</version>
        <configuration>
            <path>/</path>
            <port>${tomcat.port.number}</port>
            <httpsPort>${tomcat.ssl.port.number}</httpsPort>
            <keystoreFile>${basedir}/src/main/config/tomcat-ssl.keystore</keystoreFile>
            <keystorePass>jetty8</keystorePass>
        </configuration>
    </plugin>
    
This block allows you to run the contents of the src/main/webapp directory as a static website.  On the command
line, cd into the **static-site** directory and run **mvn tomcat7:run**.  Once you see the line 
**INFO: Starting ProtocolHandler ["http-bio-8080"]**, direct your web browser to **http://localhost:8080/index.html**, 
where you should see the rendered index.html page, reporting version V02.  Go into the index.html file and change
this to be V02-a, then hit refresh on your web browser.  If you see V02-a show up, you are golden!  Go ahead and **Ctrl-C** the
Tomcat webserver.

## Learn the basics of the seedy s3-upload plugin
Take a look in the POM again, looking for this piece:

    <plugin>
        <groupId>com.erigir</groupId>
        <artifactId>seedy-maven-plugin</artifactId>
        <version>${seedy.version}</version>
        <configuration>
            <s3Bucket>${BUCKET_NAME}</s3Bucket>
            <source>${project.basedir}/src/main/webapp</source>
            <recursive>true</recursive>
    
            <fileCompression>
                <includeRegex>.*</includeRegex>
            </fileCompression>
    
            <objectMetadataSettings>
                <objectMetadataSetting>
                    <includeRegex>.*</includeRegex>
                    <cacheControl>max-age=30</cacheControl>
                    <userMetaData>
                        <uploadTime>${maven.build.timestamp}</uploadTime>
                    </userMetaData>
                </objectMetadataSetting>
                <objectMetadataSetting>
                    <includeRegex>.*\.html</includeRegex>
                    <content-type>text/html; charset=utf-8</content-type>
                </objectMetadataSetting>
            </objectMetadataSettings>
    
        </configuration>
    </plugin>

A brief summary of what this plugin is doing in its configuration:
* s3Bucket : the name of the bucket you will upload the content to.  This is a variable, to allow you to upload to different
 buckets based on configuration.  For this exercise you will be using the same bucket every time, so feel free to replace 
 **${BUCKET_NAME}** with the name of your bucket.  If you don't, every time you upload you'll need to add a
 -DBUCKET_NAME=my-bucket to the maven command.
* source: the directory you'll be uploading
* recursive: recursively descend directories and upload subdirectories
* fileCompression: Cloudfront and S3 don't support native GZIP compression, but there is a hack to make them do so by
 gzipping the file yourself, and setting the appropriate Content-Encoding header as metadata.  This line enables GZIP
 compression for every file.  If you wanted to exclude certain files (for example, video files) you would need to toy 
 with the REGEX here.
* ObjectMetadataSettings allows you to set various other pieces of metadata on your uploaded files.  The first block
 applies to ALL files (**note the regex**) and has these settings:
** cacheControl: Remember how we set the Content-Disposition header manually in V01?  This does it automatically
** userMetaData: This is an arbitrary piece of user data, in this case the timestamp of when we last uploaded
* The second block applies only to html files, and sets the content type to **text/html; charset=utf-8**.  S3 would
 have set this content type anyway, but wouldn't have set the charset, which is a standard best practice.

## Upload new content using seedy and make sure it works
Lets try the upload now.  At the command line in the static-site directory, execute **mvn seedy:s3-upload**.  It should
fail, complaining of a missing s3Bucket parameter if you forget to make the edit above, or if not a likely
NullPointerException.  This is a poorly-written exception caused by not having any credentials available!  Let's go ahead
and setup the credentials you created in the earlier step, like so:

**mvn -Daws.accessKeyId=XXX -Daws.secretKey=YYY seedy:s3-upload**

If all went well, you should see a bunch of output ending with **Completed transfering xxx bytes...**

Let's see if it actually happened!  Take your browser to http://www.yourdomain.com/index.html and see if you get the 
 new page.  You did?  Excellent!
 

## Change some content and push it live again

Making changes should be really easy at this point.  Go into the index.html page and change the version number to 1.2,
then run the upload command again.  Check it out on the live site - just like that you can roll new static website 
changes!
  
Congrats!  If you reached here, you are ready to move on to <a href="V03.md">Version 03</a>