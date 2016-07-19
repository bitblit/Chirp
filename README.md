Chirp
=====

An application for learning serverless architectures by reimplimenting Twitter (sorta).  Shows how to:
1) Create a static site on S3 and Cloudfront
2) Add HTTPS through Cloudfront
3) Add an API layer using API Gateway and Lambda
4) Add dynamic content using DynamoDB
5) Add image uploads through S3


Introduction
============

Congrats on getting started with Chirp!  In the course of working through this project you'll start by setting up an AWS
account and end with a miniture Twitter clone.  On the way you should learn some things about HTTP, HTML, Javascript, 
Pythong, Lambda, databases, AWS, cloud computing, HTTPS, load balancers, etc, etc.  This isn't meant to be a full-fledged 
tutorial - especially with AWS, the web interfaces change over time, so rather than give you pictures that will
likely become broken with age, I'll give you general directions and rely on you being able to "read between the lines"
on how to do that on the particular page.  If you just can't figure out how from the directions feel free to contact me,
but make a good try at it first - one of the lessons you should learn from this project is that good software developers
learn how to find solutions to problems themselves using the internet and other resources before bugging other 
software developers.

While you can do this exercise on a Windows box without too many modifications, its written from the standpoint of someone
coding on a unix variant like Linux or OSX...  Mainly because most of the commands are done at the command line.  Windows
is fine, just expect to have to do some digging to find equivalent commands - and you'll have to rewrite at least one of the
scripts.  An alternative would be to install VirtualBox and run this in an inner Ubuntu.  I'll leave the details of that
one up to you.

How this works
==============

Each step of *Chirp* is meant to leave you with a functioning system, while we add features one at a time.  If at any point
your system STOPS working, then *STOP* and fix it.  I promise you it won't get better if you keep adding features to a 
broken system.

Each step in Chirp is added as a new directory labelled V##.
Each directory is designed to be able to be compared with the directory before it so you can see everything that changed.  To do
this, you'll run the command:

  diff -rq {dir1} {dir2}

This will give you a list of every file that changed.  If you want to see the actual changes for each file, remove the q flag.  So,
to see what's changed between versions 1 and 2 you would enter:

 diff -r V01 V02
 
You can even compare versions further apart like 1 and 5 if you like. 

You should see the a file named README.MD in the root of each directory.  It contains the instructions for that step - when you're finished
 reading this file, you should start with V00/README.md.  You'll create a new directory (call it MINE or something) where you'll put your
 stuff.  Then you can always see the differences between what you have and what you are trying to accomplish.
 
Given all that, there are two ways to approach this; the way I'd do it (I recommend this way, of course) or the way some other people might do it.

Either way you choose to proceed, be sure to read the version document when you get started, since it will often include
various non-code steps you need to do (e.g., create this S3 bucket) in addition to describing how the code needs to change.

The Way I'd Do It
=================

For each step, I would start by reading the goals of that step, and then seeing if I could make those goals happen on my
own.  Only after I had gotten the thing working (or given up in utter despair) would I then compare my changes to the changes
in the directory with that label (I would do this with *diff -rq MINE V02*, for instance.  That way I'd know that I had actually learned the material.


The Way Other People Might Do It
================================

For each step, I would look at the changes between what I currently have and what is coming (*diff -rq MINE V02*, for instance),
then I would go ahead and replace my stuff with the next version (*rm -Rf MINE; cp -R V02 MINE*), make sure it deploys and runs, and make 
sure I understand why it runs.


Getting Started
===============
Follow the <a href="V00/README.md">initial setup instructions</a>.

Links to each step's documentation
==================================
* <a href="V00/README.md">Getting started</a>
* <a href="V01/README.md">Version 01</a>
* <a href="V02/README.md">Version 02</a>
* <a href="V03/README.md">Version 03</a>
* <a href="V04/README.md">Version 04</a>
* <a href="V05/README.md">Version 05</a>
* <a href="V06/README.md">Version 06</a>


