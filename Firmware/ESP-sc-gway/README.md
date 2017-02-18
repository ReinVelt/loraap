Single Channel LoRaWAN receiver
==============================
This repository contains a proof-of-concept implementation of a single
channel LoRaWAN gateway.

The code is for testing and development purposes only, and is not meant 
for production usage. 

Engine is based on code base of Single Channel gateway for RaspberryPI
which is developed by Thomas Telkamp. Code was ported and extended by Maarten Westenberg to run
on ESP 8266 mcu. 

Written  by Rein Velt (rein@mechanicape.com)

Features
--------
- listen on configurable frequency and spreading factor
- SF7 to SF12



Dependencies
------------

- gBase64 library, The gBase library is actually a base64 library made 
	by Adam Rudd (url=https://github.com/adamvr/arduino-base64). I changed the name because I had
	another base64 library installed on my system and they did not coexist well.
- Time library (http://playground.arduino.cc/code/time)




License
-------
The source files in this repository are made available under the Eclipse
Public License v1.0, except for the base64 implementation, that has been
copied from the Semtech Packet Forwader.
