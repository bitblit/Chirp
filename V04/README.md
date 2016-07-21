# Module: V04
# Goals: 
* Setup AWS API Gateway to call our Lambda Functions
* Download the generated library and add it to our static deploy
* Call the library from our static page

## Introduction to V04
Much like the last module, most of this module will be spent working in the AWS console
rather than on the local software.  Here we are going to start up our API Gateway instance,
and configure it to call our Lambda function.  Then we'll deploy it, and generate a client
library to make calls into it.  Finally, we'll drop that library into our static page and 
make the call.

# Setup AWS API Gateway to call our Lambda Functions
Go to the AWS Console, and hit **API Gateway**.  If this is your first time you'll have to hit
the button to get past the introductory page, and then hit **Create API**.  Select **New API**,
and name it **Chirp**, then hit **Create API**.
 
You'll now have a new palette on which to construct your API.  API Gateway is rich with
features, and we'll be using several over the course of this program!  For now, lets just
create a single resource, the server status resource.  Following general REST practice,
we will mount this under the path /server/status, and we'll respond to the GET verb.  So hit
the **action** menu, and hit **Create resource**.  Enter **server** in the name box (it'll
autofill in the path box) and hit **Create resource**.  Then, with **server** selected, repeat the
process and add **status**, which should create a path, **server/status**.

Finally, hit **actions** once more while **status** is selected, but this time hit **Create Method**,
and select **GET**.

API Gateway will show a page allowing you to configure the new endpoint.  Let's do so.  First, select
**Lambda Function** as the integration type, **us-east-1** as the region, and then select **chirp_server_status**
as the lambda function and hit **save**.  AWS will ask you to authorize it to call that function, and you should 
hit **OK** - this gives API Gateway the right to call your Lambda function.

Now, lets configure how API gateway interacts with Lambda.  We'll
configure how the incoming HTTP request is converted to the event like what 
we saw in test/event/server_status.json.  Hit **Integration Request**, and
then expand **Body Mapping Templates** at the bottom.  Set the passthru to
** when there are no templates defined **, and then hit ** add mapping template **.

Set the content-type to **application/json** (you have to type it in) and hit the checkmark, then
select **method request passthrough** under *generate template* and hit **save**.
 
Now lets configure the other half - how Lambda responses get configured back into responses.  When
everything goes well this is automatic - the response code 200 is sent, with a content type of 
application/json and the returned object converted to JSON.  But for error codes we need to set it
up.  Fortunately, the lambda library we are using for API construction makes this easy.

Hit **GET** to go back to the previous page, and hit **Method Response** - this is where we'll define
all the valid response codes.  For Chirp we will support 200, 400, 500, 403, and 404 (Roughly,
these mean OK, Bad Request - user error, Server Error, Unauthorized, and Missing).  Hit 
** Add Response ** and add each of these for this endpoint.

Once that's done, lets go back to the previous page and hit **Integration Response**, and then hit
**Add Integration Response**.  We'll do this once for each status code:

1) Set the lambda error regex to : .*\"httpStatusCode\": 400.*
2) Set method response status to 400
3) Hit save
4) Expand **Body Mapping Templates**
5) Hit **Add mapping template**
6) Enter type **application/json**
7) Enter the value of  $input.path('$.errorMessage')
8) Hit BOTH save buttons (otherwise sometimes it doesnt save!)
(Repeat for each error code, changing the number)

This finds the given regular expression in the body, and then maps it to the appropriate error code.  The
body mapping makes sure that we only output our body, not the entire string conversion of 
the exception, which would include the stack trace.  Easy!

One more thing - this endpoint accepts a query parameter named "error".  Hit **GET** again,
then **Method Request**, then **URL Query String Parameters**.  Hit **Add query string**, and 
then enter **error** and hit save.

We are ready to test!  Hit the **Test** button on the left.  You should get the response:

```json
 {
   "notes": null,
   "code": 200,
   "data": {
     "status": "ok",
     "serverTimeFormatted": "2016-07-21 00:02:26",
     "serverTime": 1469059346,
     "serverStartTime": 1469059346,
     "version": "4",
     "serverStartTimeFormatted": "2016-07-21 00:02:26"
   }
 }
```


Now try it again, but enter "400" in the error parameter.  You should get:
```json
 {
   "code": 400100,
   "data": {
     "developerMessage": "Forced 400",
     "detailCode": 100,
     "httpStatusCode": 400,
     "message": "Forced 400",
     "moreInfoUrl": "https://my-server/api-errors/400-100"
   }
 }
```

Just a few more things!  First, we need to enable CORS on our endpoint since
we'll be calling it from a different URL.  Select the **status** resource
in the tree, and then hit **Enable CORS** in the actions menu, then hit
**Enable CORS and Replace Existing CORS headers**, and then hit
 ** Yes, replace existing values ** on the box that pops up.  This adds several
headers to the GET endpoint, and also creates a matching OPTIONS endpoint needed
for the CORS preflight call.

Finally, we need to deploy our API so it is reachable from the internet.  Select
the Chirp API in the far left panel, and then hit **Deploy API** from the actions menu.

Under Deployment stage hit **New Stage** and then enter **v1** in the "Stage Name" field.  You can
enter whatever you like in the other 2 fields, they are there for notes only.  Hit **deploy**.

And congrats!  You have a deployed API.  Take a look at the top of the screen; you should see
a url that looks kinda like this:

 Invoke URL: https://dye6xunt40.execute-api.us-east-1.amazonaws.com/v1

This is the URL where your API lives.  Eventually we will add a CNAME to use one of our own
domain names, but for the moment this one is fine.  Lets hit it!  From the command line, try:

 > curl https://dye6xunt40.execute-api.us-east-1.amazonaws.com/v1/server/status
 
Did you get the right response?  You're golden!
 
# Download the generated library and add it to our static deploy

One of the best things about API Gateway (and all that config we just went through) is that it
creates a fully valid OpenAPI/Swagger configuration (see https://openapis.org/ for details about
the spec).  Hit **Export** under the invoke URL, and hit the "Swagger + API Gateway Extensions" button
if you'd like to see exactly what such a definition looks like (in the future we'll do so to simplify
some things like adding all of those error code mappings!)

Now, hit **SDK Generation** - check that out, we can auto-generate a client for calling our API, both for
mobile platforms like iOS and for websites through Javascript!  We are going to download the javascript 
client (it'll come down as a zip file) and unzip it into our directory **static-site/src/main/webapp/js**,
creating a directory named **apiGateway-js-sdk** under there.  We'll leave it untouched - in future modules
we'll delete this directory entirely and replace it as we add more functions to the API.

# Call the library from our static page

How do we use it?  The library comes with a very nice README file for now we will add a call
our **chirp.js** file, located inside the **js** directory as well.

First, in index.html we will add a reference to the library, just before the inclusion of chirp.js
(it needs to come before that since chirp.js will use these)

```html
 <!-- Bring in the api stuff -->
 <script type="text/javascript" src="/js/apiGateway-js-sdk/lib/axios/dist/axios.standalone.js"></script>
 <script type="text/javascript" src="/js/apiGateway-js-sdk/lib/CryptoJS/rollups/hmac-sha256.js"></script>
 <script type="text/javascript" src="/js/apiGateway-js-sdk/lib/CryptoJS/rollups/sha256.js"></script>
 <script type="text/javascript" src="/js/apiGateway-js-sdk/lib/CryptoJS/components/hmac.js"></script>
 <script type="text/javascript" src="/js/apiGateway-js-sdk/lib/CryptoJS/components/enc-base64.js"></script>
 <script type="text/javascript" src="/js/apiGateway-js-sdk/lib/url-template/url-template.js"></script>
 <script type="text/javascript" src="/js/apiGateway-js-sdk/lib/apiGatewayCore/sigV4Client.js"></script>
 <script type="text/javascript" src="/js/apiGateway-js-sdk/lib/apiGatewayCore/apiGatewayClient.js"></script>
 <script type="text/javascript" src="/js/apiGateway-js-sdk/lib/apiGatewayCore/simpleHttpClient.js"></script>
 <script type="text/javascript" src="/js/apiGateway-js-sdk/lib/apiGatewayCore/utils.js"></script>
 <script type="text/javascript" src="/js/apiGateway-js-sdk/apigClient.js"></script>
```

And then in chirp.js we'll replace its contents with:

```javascript
 $(document).ready(function () {
     console.log("Chirp page loaded");
     var api = apigClientFactory.newClient();
     console.log("Api interface created : "+api);

     api.serverStatusGet({'error':''}, {}, {}).then(function (data) {
         $("#output").html(JSON.stringify(data));
     }, function (err) {
         alert("There was an error!\n\n" + JSON.stringify(err));
     });

});

```

You'll see that it now makes a call out to the api, and if it succeeds (the client library uses
promises as its default method of making calls) it replaces the contents of the "output" div
with a JSON version of the response, which will look familiar to you by now!  You'll also notice
that we are sending an empty string for the 'error' parameter - this is how we could send the error
code we were testing with above.

Go ahead and deploy that to your bucket, and refresh your page.  Look at that!  You have a functioning
website with both static and API layers, and you aren't running any servers!  If you've got it down you
could stop here - but if you'd like some more examples, we add using DynamoDB as our database in
 <a href="../V05/README.md">Version 05</a>

### Footnote : Why not change our Cloudfront distribution to route a path (say, /api/** ?) to our API Gateway?

I used to recommend doing exactly this, since it meant not having to do all the CORS stuff (which
also simplified supporting old browsers).  The reason I don't any more is twofold:

1) Once you do that, you have to make super-sure that you have your cache headers set correctly, or you'll
end up accidentally double-caching API results in Cloudfront and you'll have a hard time figuring out why
you don't get the results you expect.
2) Cloudfront allows you to set custom error pages (instead of the default ugly cloudfront ones).  On any
production site you'll want to do this... but you'll want your API to return more useful data on its error
pages.  Sadly, the Cloudfront ones will replace your nice API ones if you route your API through Cloudfront.
2.5) Doing it this way forces you to learn CORS, which you should learn anyway.


Congrats!  If you reached here, you are ready to move on to <a href="../V05/README.md">Version 05</a>

