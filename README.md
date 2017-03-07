# Observatorium

# Processing

The processing sketch is sending midi data a midi software (Ableton Live, Garageband …) running on you localhost. 
In order to do that you need to setup a midi IAC Driver with the name "Bus 1" on your localhost.

An the other hand it is listening on the port 3200 for OSC messages, that way it can be connected 

**Dependencies**
Processing Core
Processing Serial
controlIP5
oscP5
klang

# Node OSC Forward Server

Forwards http data received at port 3000 as OSC message to port 3200 on localhost.

# Arduino Knob

A very simple small sketch to turn the universe in the Processing sketch.
In order to run this the **ArduinoPotSerial** needs to be running.