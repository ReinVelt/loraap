Single Channel LoRaWAN Receiver
==============================
This repository contains a proof-of-concept implementation of a single
channel LoRaWAN receiver connected to an Android device. It has been tested on the ESP 12E DevKit using a 
Semtech SX1276 transceiver (HopeRF RFM95W).

The code is for testing and development purposes only, and is not meant 
for production usage. 

Engine is based on code base of Single Channel gateway for RaspberryPI
which is developed by Thomas Telkamp. Code was ported and extended to run
on ESP 8266 mcu and provide RTC, Webserver and DNS services by Maarten Westenberg .
This code is stripped and refactored for its new purpose by Rein Velt (rein@mechanicape.com
