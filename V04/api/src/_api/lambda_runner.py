#!/usr/bin/env python

import logging
import sys

import simplejson

logging.getLogger().setLevel(10)

if len(sys.argv) == 3:
    program_file = sys.argv[1]
    event_file = sys.argv[2]
    logging.debug("Running with program " + program_file + " event " + event_file)
    logging.debug("Reading event file")
    with open(event_file) as json_file:
        logging.debug(("File is " + str(json_file)))
        event_data = simplejson.load(json_file)
    logging.debug("File : " + str(event_data))

    logging.debug("Sys path is " + str(sys.path))
    logging.debug("Importing " + program_file)
    __import__(program_file)
    program = sys.modules[program_file]

    context = {}
    output = program.lambda_handler(event_data, context)

    print "-----------------------------------"
    print str(output)


else:
    print "Usage: lambda_runner program_file event_file (handler name must be my_handler)"
