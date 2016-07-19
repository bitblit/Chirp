# Module: V01
# Goals: 
* Create an S3 bucket
* Create a Cloudfront distribution in front of that bucket
* Point your CNAME at cloudfront
* Put a file in the bucket, and edit some metadata
* See the file served from your domain

## Introduction to V01
I know you just can't wait to get to the coding, but before we do that we need to set up some of our stuff in AWS.  Now,
if you were a large company with an installed user base, I would tell you to automate a lot of the stuff we are about
to do using the AWS API calls and shell scripts, because it would save you a lot of screwing around in the console.  But
for today I want you to do it through the console as an introduction to how the console works, and also to learn how to
MVP things quickly on AWS.

Please be aware that these instructions were last updated July 2016.  AWS changes their stuff **ALL THE TIME**, so its 
likely you'll find things that this doesn't mention.  If this happens, leave them at their defaults.  If everything breaks,
let me know and I'll update the doc.

## Create an S3 bucket
* Login to your AWS account, go to the AWS console, and select "S3" (Red section)
* Create a new bucket (Hit the **Create Bucket**) button.  Doesn't really matter what you name it, but remember since 
we'll use it a lot.  I'm going to assume you called the bucket **my-bucket**, so any place you see **my-bucket** in here
replace that with what you used.  I can't tell you what to name it, exactly, since bucket names need to be globally unique.

## Create a HTTPS certificate
* First, make sure you can receive email as the administrator for your domain.  For example, if 
your domain is xyz.com, try sending an email to "administrator@xyz.com".  You'll need this to verify
your ownership of the domain for the certificate.
* Go to the AWS console, and select "Certificate Manager" (under Security and Identity)
* Hit "Getting Started" if you've never used this before
* Assuming your domain name is xyz.com, enter ** *.xyz.com ** in the box and hit "Review and Request"
* Validate and hit "Confirm and Request"
* Amazon will send you an email at the admin email account.  Click the "approval" link in the email they
send you.
* You'll get a webpage that lists the ARN of the newly created certificate.  You can always look this up in the
certificate manager later

## Create a Cloudfront distribution in front of that bucket
* Go to the AWS console, and select "Cloudfront" (Red Section)
* Hit "Create Distribution", and select a Web distribution
* The origin domain is the S3 bucket you just created.  It should be in the drop-list
* Origin path can stay blank 
* Origin ID can be left to what AMZN sets it
* Set "Restrict Bucket Access" to **Yes**
* For "Origin Access Identity" select **Create new identity**
* For "Grant Read Permissions on Bucket" select **Yes, update bucket policy**
* Set "Viewer Protocol Policy" to "Redirect HTTP to HTTPS" - more secure!
* Set "Allowed HTTP Methods" to **GET, HEAD, OPTIONS** (The rest S3 can't support)
* Leave "Forward Headers" to None
* Leave "Object Caching" to "Use Origin Cache Headers"
* Leave the Min/Max TTLs alone
* Set "Forward Cookies" to **ALL**
* Set "Forward Query Strings" to **Yes**
* Leave "Smooth Streaming" to **No**
* Leave "Restrict viewer access" to **No**
* Set "Compress Objects Automatically" to **Yes** (This will make Cloudfront GZIP your content and set the Content-Encoding header.  Maybe
one of the most important things you can do for a fast website)
* Set "Price Class" to **US and Europe**
* Leave "AWS WAF" set to **None**
* Decide what your domain name is going to be (I'll use www.yourdomain.com in the example) - put it in the "Alternate CNames" box
* Set "SSL Certificate" to **Custom SSL Certificate**, and then select the one you created above
* Leave "Custom SNL Client" set to **Only clients that support SNI** (This excludes only browsers older than IE6)
* Set the "Default Root Object" to **index.html**
* Leave Logging set to **Off** for now (In production you'd want ON)
* Set distribution state to **Enabled**
* Hit "Create Distribution"
* Once you see the "creating" page, you'll want to go to the distributions page.  You should see your new distribution being 
created.  It'll be in status "Updating" or something similar.  You won't be able to hit your content until it switches to 
status "Deployed", which takes about 15-25 minutes.  You can do the next step (Setup CNAMEs) while this is still running.

## Point your CNAME at cloudfront
* In the list of CloudFront distributions, click the ID of the one you just created
* Note the value under "Domain Name", it should be something like DXXXXXXXX.cloudfront.net.  Copy this value
* Login to GoDaddy in another window (or your DNS provider)
* Go to the editor for the DNS zone file for your domain, and create a CNAME, **www.yourdomain.com**  = **DXXXXX.cloudfront.net** 
    (the value on the clipboard)
** How you do this varies greatly depending on your DNS provider    
* Save your changes/Write the DNS zone file
* At a command line, run **ping www.yourdomain.com**.  The first line that comes back should be something like
  **PING dxxxxxxxxx.cloudfront.net (##.##.##.##):** - if it says **ping: cannot resolve www.yourdomain.com: Unknown host**,
  then your changes haven't propagated yet.  Wait a few minutes and try again.  It is ok if the ping doesn't respond, as
  long as it finds an IP address for it.  Depending on your DNS provider this can take a while.

## Put a file in the bucket, edit some metadata, and make sure it serves over HTTP
* Create a file, index.html, in a scratch directory.  Its contents can be a simple **Hello World**
* In the AWS console, select S3, then select **my-bucket**
* Hit **Actions**, **Upload**, and then upload your file into the bucket
* Once its there, select your file and hit **Properties** on the right hand side
* Expand the **Metadata** section, you should see a **Content Type = text/html** metadata in there.  That's because
 S3 will serve this metadata as headers when the objects are requested over HTTP
* Add a metadata entry, **Cache-Control** with a value **max-age=30**.  We do this because we are currently developing, and
 we don't want Cloudfront caching our stuff for its default length (which is one day)
* Hit the save button


## See the file served from your domain
* Check that your ping returns successfully
* Go to the Cloudfront panel, and look at the status of your distribution.  If it isn't **Deployed**, wait until it is
* Once these two things are true, use your web browser to hit https://www.yourdomain.com/index.html
* Hello, world!
* You can also try out the redirect, if you hit http://www.yourdomain.com/index.html it should redirect you to
https://www.yourdomain.com/index.html and serve that
* Try a **curl -i http://www.yourdomain.com/index.html**, you should see the Max-Age: 30 header in there.  You should
also see a Content-Encoding: GZIP in there
* Congrats, you have a working static website!  Let's add some content and simplify adding changes.  Move on to the V02
 branch...  If you don't have a working "Hello World", stop here until you do.

## Extra Credit
* Browse over to S3, select your bucket and hit Properties.  Click "Edit Bucket Policy" and "Add CORS Configuration" - see
how CloudFront automatically created entries to allow it to read from your bucket, and set appropriate CORS headers when
doing so.


Congrats!  If you reached here, you are ready to move on to <a href="../V02/README.md">Version 02</a>