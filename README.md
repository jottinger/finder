finder
======

OSAS Application, meant to be a feedreader with workable classifier for "interesting" feeds.

It's built off of the JBoss Way quickstarts for Java EE 7, deployed normally to
Wildfly. It's very much a work-in-progress; right now, it does a few things, none of them well.

Right now - late February - it uses three entities. 

One is a holdover from the
quickstart, which tracks membership in ... something.

The others are Feed and Entry, which map to an ATOM channel and entry set.

When the user submits a URL to the application, it creates a Feed entity with a "next read"
matching the epoch (i.e., 1970). 

A timer in the application checks for any feed whose "next read" is earlier than "now", and
submits the URLs to a JMS queue.

A message-driven bean then picks up the URL and reads the feed, populating the Feed and Entry
datasets with the ATOM or RSS feed's data.

That's it, right now; there's no way to see the Entry data yet, but that's okay. I'll build a
workable UI soon, I hope.

Idea
====

The idea is that eventually, it'll track classifications for each entry: an automated (unsupervised)
classification, and a supervised classification that serves as training data.

Users will be able to select entries based on classification ("show me interesting entries!") or currency
("Show me the things most recently read.") 

For entries that have not been used as training data, users will be able to set the classification manually,
which will serve as a flag for training.

Eventually, when entries are read, they'll be sent to another JMS queue for asynchronous classification
and/or training.
