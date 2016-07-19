# Module: V04
# Goals: 
* Setup AWS API Gateway to call our Lambda Functions
* Download the generated library and add it to our static deploy
* Call the library from our static page

## Introduction to V04




## Why not change our Cloudfront distribution to route a path (say, /api/** ?) to our API Gateway?

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

