package dk.ciid.observatorium;

import processing.serial.*;


import processing.core.PApplet;

import java.util.Arrays;

import oscP5.*;
import netP5.*;


public class ArduinoPotSerial extends PApplet {

    Serial serialPort;
    OscP5 oscP5;
    NetAddress myRemoteLocation;
    int previousValue;
    int LF = 10;

    public void settings() {
        size(640, 480);
    }

    public void setup() {

        String portName = Serial.list()[3];
        print(Serial.list());
        serialPort = new Serial(this, portName, 9600);

        // oscP5 = new OscP5(this,12000);

        myRemoteLocation = new NetAddress("127.0.0.1",32000);


    }

    public void draw() {

        background(255);

        // println(serialPort.available());

        if ( serialPort.available() > 0) {  // If data is available,
            try{
                String stringValue = serialPort.readStringUntil(LF);
                if (stringValue != null) {
                    print(stringValue);
                    int value = Integer.parseInt(stringValue.trim());

                    if(value != previousValue){
                        previousValue = value;
                        println(value);
                        /* create a new OscMessage with an address pattern, in this case /test. */
                        OscMessage myOscMessage = new OscMessage("/rotationAngle");
                        /* add a value (an integer) to the OscMessage */
                        myOscMessage.add(map((float)value, 0.0f , 360.0f, 0.0f, (float)Math.PI*2));
                        /* send the OscMessage to a remote location specified in myNetAddress */
                        // oscP5.send(myOscMessage, myBroadcastLocation);
                        OscP5.flush(myOscMessage,myRemoteLocation);
                    }
                }

            }catch(Exception ex){
                println("error");
                System.out.println(ex);

            }

        }
        delay(20);
    }

    void serialEvent(Serial myPort) {
        /*
        byte[] bytes = serialPort.readBytes();
        println(bytes.length);
        println(bytes); */

        // read a byte from the serial port:
        // int inByte = myPort.read();
        // if this is the first byte received, and it's an A,
        // clear the serial buffer and note that you've
        // had first contact from the microcontroller.
        // Otherwise, add the incoming byte to the array:

    }

    public static void main(String[] args) {
        PApplet.main(ArduinoPotSerial.class.getName());
    }
}