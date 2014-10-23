Chirp
=====

An application for learning Java by reimplimenting Twitter (sorta).  Shows how to:
1) Create a static site on S3 and Cloudfront
2) Add HTTPS through Cloudfront
3) Add an API layer using EB and Cloudfront
4) Add image uploads through S3


Introduction
============

Congrats on getting started with Chirp!  In the course of working through this project you'll start by setting up an AWS
account and end with a miniture Twitter clone.  On the way you should learn some things about HTTP, HTML, Javascript, 
Java, databases, AWS, cloud computing, HTTPS, load balancers, etc, etc.  This isn't meant to be a full-fledged 
tutorial - especially with AWS, the web interfaces change over time, so rather than give you pictures that will
likely become broken with age, I'll give you general directions and rely on you being able to "read between the lines"
on how to do that on the particular page.  If you just can't figure out how from the directions feel free to contact me,
but make a good try at it first - one of the lessons you should learn from this project is that good software developers
learn how to find solutions to problems themselves using the internet and other resources before bugging other 
software developers.

While you can do this exercise on a Windows box without too many modifications, its written from the standpoint of someone
coding on a unix variant like Linux or OSX...  Mainly because most of the commands are done at the command line.  Windows
is fine, just expect to have to do some digging to find equivalent commands.

How this works
==============

Each step of *Chirp* is meant to leave you with a functioning system, while we add features one at a time.  If at any point
your system STOPS working, then *STOP* and fix it.  I promise you it won't get better if you keep adding features to a 
broken system.

Each step in Chirp is added as a new branch labelled V## (version 1 is branch V01, master branch just has these instructions).
Each branch is designed to be able to be merged into the branch before it, so, for example, when you cloned Chirp you got
the "master" branch.  The first step is branch V01, so you can "git merge V01" and it will bring all the changes into your current
changeset.

You should see the directory src/main/site/version-docs (this file is in it) at the root of your checkout.  For each version
there is a file in there describing what the goals and changes needed for that version are.  Given that, there are two ways to
approach this; the way I'd do it (I recommend this way, of course) or the way some other people might do it.

Either way you choose to proceed, be sure to read the version document when you get started, since it will often include
various non-code steps you need to do (e.g., create this S3 bucket) in addition to describing how the code needs to change.

The Way I'd Do It
=================

For each step, I would start by reading the goals of that step, and then seeing if I could make those goals happen on my
own.  Only after I had gotten the thing working (or given up in utter despair) would I then compare my changes to the changes
in the branch with that label (I would do this with *git diff master..V01*, for instance.  That way I'd know that I had actually learned the material.


The Way Other People Might Do It
================================

For each step, I would look at the changes between what I currently have and what is coming (*git diff master..V01*, for instance),
then I would go ahead and merge (*git merge V01*), make sure it deploys and runs, and make sure I understand why it runs.


Getting Started
===============
Follow the <a href="src/main/site/version-docs/SETUP.md">initial setup instructions</a>.

Links to each step's documentation
==================================
* <a href="src/main/site/version-docs/SETUP.md">Getting started</a>
* <a href="src/main/site/version-docs/V01.md">Version 01</a>
* <a href="src/main/site/version-docs/V02.md">Version 02</a>
* <a href="src/main/site/version-docs/V03.md">Version 03</a>
* <a href="src/main/site/version-docs/V04.md">Version 04</a>
* <a href="src/main/site/version-docs/V05.md">Version 05</a>
* <a href="src/main/site/version-docs/V06.md">Version 06</a>
* <a href="src/main/site/version-docs/V07.md">Version 07</a>


