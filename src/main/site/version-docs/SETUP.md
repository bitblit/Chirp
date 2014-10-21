Branch: Master
Goals: Verify that you have all your prerequisites set up, and clone the main repository

Prerequisites
=============

Before you get started, you'll want to make sure you have these things:

* An AWS account.  The free version is fine.  If you already have one, that's great, but if not, stop reading this page
long enough to head on over to http://aws.amazon.com/ and hit the "Sign Up" button, working through the steps to get an 
account set up.  This exercise works on the assumption that you have an empty AWS account ready to go.

* Java.  Type *java -version* at your command prompt and make sure you get something back.  Specifically, something that
is at least version 1.7 (we won't be using anything TOO new, but 1.6 isn't even a supported version any more so get with
the times).  If you don't have this, head over to http://www.oracle.com/technetwork/java/javase/downloads/index.html 
and download a *JDK NOT A JRE*

* Maven.  We'll use this for both build and deployment.  Type *mvn -version* at the command prompt and make sure you
get back at least version 3.2.  If you don't, go to http://maven.apache.org/download.cgi and get yourself a copy and 
install it.

* Git.  If you are reading this on your own machine then you must already have cloned it.  If you are reading it online
though you'll need git for everything.  Try *git --version* at the command line, and if you don't have it, you'll need
to install it.  Every major package manager for Linux (e.g. yum, aptget, etc) has git in it.  For OSX you may wanna try
http://git-scm.com/download/mac.

* A DNS domain you own.  This can be done on a variety of services (like GoDaddy), including Route 53 (AWS's own offering).  
You'll need this because you'll want to create a *CNAME* for a domain you own to the name of the bucket AWS creates.  How you
actually create a CNAME varies depending on your provider.  I'll give a general overview for how to do it on GoDaddy, but
 how to do this on a specific provider is outside the scope of a document.  If you don't know what a CNAME is, I suggest you
 read http://en.wikipedia.org/wiki/CNAME_record .
 
* Sorta optional: An HTTPS certificate for the domain name you create above.  You don't HAVE to do the HTTPS portion of
this exercise (especially since HTTPS certs aren't cheap), but if you already have a yourdomain.com cert, or just have
 money to burn, this piece is very useful.


* Sorta optional: A Github account.  If you don't already have one, they are free.  Go to https://github.com/ and get one.
This project works on the assumption that you won't really want to save the results, so if you are just working through
the system you can skip this.  But if you wanna keep and develop the app then you really should start by "forking" this
into your repo on github so that you can push back (you won't have push access to my Chirp, sorry).  Just remember that 
if you fork, and later I make changes, you'll need to pull and merge, so you may wanna wait to fork until the end.  And
if you *DO* fork, remember that the clone commands should be for your repo, not mine.


Getting started
===============

Now that you have all of that out of the way, you're ready to get started!  

Clone this repo
---------------
CD into a local directory where you store your work and:

*git clone git@github.com:bitblit/Chirp.git*

This should create a directory "Chirp" with all the files in it.