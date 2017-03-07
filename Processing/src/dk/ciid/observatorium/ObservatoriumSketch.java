package dk.ciid.observatorium;


import de.hfkbremen.klang.Synthesizer;
import dk.ciid.observatorium.events.CenterPositionListener;
import dk.ciid.observatorium.events.MaxPositionListener;
import dk.ciid.observatorium.events.MilkwayMenuListener;
import dk.ciid.observatorium.events.PositionEvent;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.event.MouseEvent;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Timer;

public class ObservatoriumSketch extends PApplet implements MaxPositionListener, CenterPositionListener, MilkwayMenuListener {

    public static int SERVER_DEFAULT_SERVER_LISTENING_PORT = 32000;
    OscP5 oscP5;
    long timeLastFrame;
    long frameTimeDuration;
    ArrayList<Pendulum> pendulums = new ArrayList<>();
    ArrayList<CelestialObject> celestialObjects = new ArrayList<>();
    MilkwayMenu milkwayMenu;
    Pendulum newPendulum;
    VelocityBeatDisplay beatVelocityBeatDisplay;
    PendulumTimerTask pendulumTimerTask;
    Timer pendulumTimer;
    final Synthesizer mSynth = Synthesizer.getSynth("midi", "Bus 1"); // name of an available midi out device
    float beatVisualisatzion = 0.0f;
    float maxBeatVisualisatzion = 20.0f;
    boolean mouseIsDown = false;

    ArrayList<ArrayList<Integer>> MIDI_NOTES = new ArrayList<>();

    public int COLOR_VELOCITY_DISPLAY_BAR = color(120);

    public int PENDULUM_COLOR_INSTRUMENT_0 = color(60,250,250);
    public int PENDULUM_COLOR_INSTRUMENT_1 = color(244,110,247);
    public int PENDULUM_COLOR_INSTRUMENT_2 = color(245,201,63);

    public PImage IMAGE_MILKWAY_0;
    public PImage IMAGE_MILKWAY_1;
    public PImage IMAGE_MILKWAY_2;
    public PImage IMAGE_MILKWAY_0_PASSIVE;
    public PImage IMAGE_MILKWAY_1_PASSIVE;
    public PImage IMAGE_MILKWAY_2_PASSIVE;
    public PImage IMAGE_SKY_OBJECT_STAR;
    public PImage IMAGE_SKY_OBJECT_CONST;
    public PImage IMAGE_SKY_OBJECT_PLANET;

    public float METRONOME_CLICK_RADIUS = 10.0f;
    public float PENDULUM_UPDATE_BPM = 10000.0f;

    public int currentInstrument = 1;
    public boolean mouseOverMetronome = false;
    public float rotationAngle = 0.0f;


    public void settings() {
        size(1024, 786);
        // fullScreen();
        oscP5 = new OscP5(this,SERVER_DEFAULT_SERVER_LISTENING_PORT);


        IMAGE_MILKWAY_0 = loadImage("milkway_1.png");
        IMAGE_MILKWAY_1 = loadImage("milkway_2.png");
        IMAGE_MILKWAY_2 = loadImage("milkway_3.png");
        IMAGE_MILKWAY_0_PASSIVE = loadImage("milkway_1_passive.png");
        IMAGE_MILKWAY_1_PASSIVE = loadImage("milkway_2_passive.png");
        IMAGE_MILKWAY_2_PASSIVE = loadImage("milkway_3_passive.png");
        IMAGE_SKY_OBJECT_STAR = loadImage("star.png");
        IMAGE_SKY_OBJECT_CONST = loadImage("const.png");
        IMAGE_SKY_OBJECT_PLANET = loadImage("planet.png");

        milkwayMenu = new MilkwayMenu();
        milkwayMenu.addMilkwayMenuListener(this);
        beatVelocityBeatDisplay = new VelocityBeatDisplay();

    }

    public void setup() {
        timeLastFrame = System.currentTimeMillis();
        newPendulum = new Pendulum(new PVector(20, 20), new PVector(width/2, height/2));
        newPendulum.addMaxPositionEventListener(this);
        newPendulum.addCenterPositionEventListener(this);

        // Timmer
        pendulumTimer = new Timer();
        pendulumTimerTask = new PendulumTimerTask(this);
        final int pendulumUpdatePeriod = (int) (60.0f / PENDULUM_UPDATE_BPM * 1000.0f);
        pendulumTimer.scheduleAtFixedRate(pendulumTimerTask, 100, pendulumUpdatePeriod);

        // setup notes

        MIDI_NOTES.add(new ArrayList<>());
        MIDI_NOTES.add(new ArrayList<>());
        MIDI_NOTES.add(new ArrayList<>());

        MIDI_NOTES.add(new ArrayList<>());
        MIDI_NOTES.add(new ArrayList<>());
        MIDI_NOTES.add(new ArrayList<>());

        MIDI_NOTES.get(0).add(26);
        MIDI_NOTES.get(0).add(17);
        MIDI_NOTES.get(0).add(16);
        MIDI_NOTES.get(0).add(15);
        MIDI_NOTES.get(0).add(12);

        MIDI_NOTES.get(1).add(26);
        MIDI_NOTES.get(1).add(13);
        MIDI_NOTES.get(1).add(16);
        MIDI_NOTES.get(1).add(25);
        MIDI_NOTES.get(1).add(15);
        MIDI_NOTES.get(1).add(12);
        // 15

        // third instrument with scle
        // public static final int[] MAJOR_CHORD = {0, 4, 7}; // + 12 Octave
        // public static final int[] MAJOR_CHORD_7 = {0, 4, 7, 11};

        MIDI_NOTES.get(2).add(26);
        MIDI_NOTES.get(2).add(30);
        MIDI_NOTES.get(2).add(33);
        MIDI_NOTES.get(2).add(37);
        MIDI_NOTES.get(2).add(49);
        MIDI_NOTES.get(2).add(53);
        MIDI_NOTES.get(2).add(56);
        MIDI_NOTES.get(2).add(60);

        // Sky Objects

        MIDI_NOTES.get(3).add(26);
        MIDI_NOTES.get(3).add(17);
        MIDI_NOTES.get(3).add(16);
        MIDI_NOTES.get(3).add(15);
        MIDI_NOTES.get(3).add(12);

        MIDI_NOTES.get(4).add(26);
        MIDI_NOTES.get(4).add(17);
        MIDI_NOTES.get(4).add(16);
        MIDI_NOTES.get(4).add(15);
        MIDI_NOTES.get(4).add(12);

        MIDI_NOTES.get(5).add(26);
        MIDI_NOTES.get(5).add(17);
        MIDI_NOTES.get(5).add(16);
        MIDI_NOTES.get(5).add(15);
        MIDI_NOTES.get(5).add(12);


    }

    public void draw() {

        // Calculate time duration of a frame
        frameTimeDuration = System.currentTimeMillis() - timeLastFrame;
        timeLastFrame = System.currentTimeMillis();

        background(0);

        // draw center point visual
        if(beatVisualisatzion > 0.0f){
            pushMatrix();
            translate(width/2, height/2);
            fill(160 * (beatVisualisatzion/maxBeatVisualisatzion));
            noStroke();
            ellipse(0, 0, beatVisualisatzion, beatVisualisatzion);
            popMatrix();
            beatVisualisatzion = beatVisualisatzion - (frameTimeDuration * 0.1f);
        }

        for (CelestialObject celestialObject : celestialObjects) {
            celestialObject.draw(this);
        }

        // check mouseover
        mouseOverMetronome = false;
        for (Pendulum pendulum : pendulums) {
            boolean mouseOver = (pendulum.distanceToPendulumEnds(new PVector(mouseX, mouseY)) < METRONOME_CLICK_RADIUS);
            pendulum.setMouseOver(mouseOver);
            if(mouseOver) mouseOverMetronome = true;
        }

        // draw current point
        if(!mouseOverMetronome) { // dont draw if over metronome
            newPendulum.resetPendulum(new PVector(mouseX, mouseY), new PVector(width / 2, height / 2));
            newPendulum.calculatePosition(frameTimeDuration); // TODO: do in Timerk
            newPendulum.draw(this);
            newPendulum.setMute(false);
        }else{
            newPendulum.setMute(true); // mute current metronome if not visible
        }


        for (Pendulum pendulum : pendulums) {
            // pendulum.calculatePosition(frameTimeDuration);
            pendulum.draw(this);
        }

        // draw milkway menu
        milkwayMenu.draw(this);

        // draw velocity bar
        beatVelocityBeatDisplay.draw(this);
        
    }

    public void mousePressed() {
        if(!mouseIsDown){ // only on pressed
            mouseIsDown = true;
            if (mouseButton == LEFT) {
                if (mouseOverMetronome) { // set pendulum to center
                    ListIterator<Pendulum> pendulumsIterator = pendulums.listIterator();
                    while (pendulumsIterator.hasNext()) {
                        Pendulum pendulum = pendulumsIterator.next();
                        if (pendulum.isMouseOver()) {
                            pendulum.setPendulumPositionToCenter();
                        }
                    }
                } else {

                }
            }else if (mouseButton == RIGHT) { // remove pendulum
                ListIterator<Pendulum> pendulumsIterator =pendulums.listIterator();
                while(pendulumsIterator.hasNext()) {
                    Pendulum pendulum = pendulumsIterator.next();
                    if(pendulum.isMouseOver()){
                        // Best off for removed pendulums
                        int noteIndex = (int) Math.ceil(map(Math.round(pendulum.getAngle()), 0, 360, 0, MIDI_NOTES.get(pendulum.getInstrument()).size() -1 ));
                        int note = MIDI_NOTES.get(pendulum.getInstrument()).get(noteIndex);
                        mSynth.instrument(pendulum.getInstrument());
                        mSynth.noteOn(24 + note, 0);

                        mSynth.noteOff(24 + note);
                        mSynth.noteOff();

                        pendulumsIterator.remove();
                    }
                }
            }

        }


    }

    public void mouseReleased(){
        mouseIsDown = false; // reset mouse down

        if (mouseButton == LEFT) {
            // println("left");
            // Add new pendulum

            if(mouseOverMetronome){

            }else { // add new pendulum
                pendulums.add(newPendulum);
                newPendulum = new Pendulum(new PVector(20, 20), new PVector(width/2, height/2), currentInstrument);
                newPendulum.addMaxPositionEventListener(this);
                newPendulum.addCenterPositionEventListener(this);
            }

        } else if (mouseButton == RIGHT) {

        }
    }

    public void keyPressed()
    {
        if(key == CODED)
        {
            if (keyCode == LEFT)
            {
                for (Pendulum pendulum : pendulums) {
                    pendulum.rotatePendulum(-0.2f);
                }
            }
            if(keyCode == RIGHT)
            {
                for (Pendulum pendulum : pendulums) {
                    pendulum.rotatePendulum(0.2f);
                }
            }
            if(keyCode == UP)
            {
                // Add start

                for(int i = 0; i < MIDI_NOTES.size(); i++){
                    mSynth.instrument(i);
                    for(int note: MIDI_NOTES.get(i)) {
                        mSynth.noteOff(note);
                    }
                }

                switch(Math.round(random(-0.49f,2.49f))){

                    case 0:
                        celestialObjects.add(new CelestialObject(new PVector(random(20, width-20), random(100, height - 120)), CelestialObject.SKY_OBJECT_STAR, (long)random(3000.0f, 5000.0f)));
                    break;
                    case 1:
                        celestialObjects.add(new CelestialObject(new PVector(random(20, width-20), random(100, height - 120)), CelestialObject.SKY_OBJECT_CONST, (long)random(3000.0f, 5000.0f)));
                    break;
                    case 2:
                        celestialObjects.add(new CelestialObject(new PVector(random(20, width-20), random(100, height - 120)), CelestialObject.SKY_OBJECT_PLANET, (long)random(3000.0f, 5000.0f)));
                    break;
                }

                playCelestialObject(celestialObjects.get(celestialObjects.size()-1));
            }
            if(keyCode == DOWN){
                for(int i = 0; i < MIDI_NOTES.size(); i++){
                    mSynth.instrument(i);
                    mSynth.noteOff();
                }
            }
        }
    }

    @Override
    public void maxPositionReached(PositionEvent event) {
        //println("max position reached");
        // mSynth.noteOn(24 + 17, 127, 3);
    }

    public void playCelestialObject(CelestialObject co){
        mSynth.instrument(co.getInstrument());
        int noteIndex = (int) Math.ceil(map(co.getSkyObjectPosition().x, 0, width, 0, MIDI_NOTES.get(co.getInstrument()).size() -1 ));

        mSynth.noteOn(24 + noteIndex, 127, co.getLifeTime()/1000);
        co.setPlaying(true);

    }

    public static void main(String[] args) {
        PApplet.main(ObservatoriumSketch.class.getName());
    }

    @Override
    public void centerPositionReached(PositionEvent event) {
        // println("Center position reached " + event.angle);

        int noteIndex = (int) Math.ceil(map(Math.round(event.getAngle()), 0, 360, 0, MIDI_NOTES.get(event.getInstrument()).size() -1 ));
        int note = MIDI_NOTES.get(event.getInstrument()).get(noteIndex);
        mSynth.instrument(event.getInstrument());
        mSynth.noteOn(24 + note, event.getBeatVelocity());
        beatVisualisatzion = maxBeatVisualisatzion;
    }

    public void oscEvent(OscMessage theOscMessage) {
        print("### received an osc message.");
        print(" addrpattern: "+theOscMessage.addrPattern());
        println(" typetag: "+theOscMessage.typetag());
        println(" typetag: "+theOscMessage.arguments()[0]);
        // accelX = (float) theOscMessage.arguments()[0];

        switch (theOscMessage.addrPattern()){
            case "/rotationAngle":
                Float newRotationAngle = new Float((Float)theOscMessage.arguments()[0]);
                for (Pendulum pendulum : pendulums) {
                    pendulum.rotatePendulum(newRotationAngle - this.rotationAngle );
                }
                this.rotationAngle = newRotationAngle;
                break;

            case "/celestialObject":
                String[] msg = ((String)theOscMessage.arguments()[0]).split(";");
                Float celestialObjectX = Float.parseFloat(msg[1]);

                switch(msg[0]){
                    case "STAR":
                        celestialObjects.add(new CelestialObject(new PVector(celestialObjectX*width, random(60, height - 20)), CelestialObject.SKY_OBJECT_STAR, (long)random(3000.0f, 5000.0f)));
                        break;
                    case "CONST":
                        celestialObjects.add(new CelestialObject(new PVector(celestialObjectX*width, random(60, height - 20)), CelestialObject.SKY_OBJECT_CONST, (long)random(3000.0f, 5000.0f)));
                        break;
                    case "PLANET":
                        celestialObjects.add(new CelestialObject(new PVector(celestialObjectX*width, random(60, height - 20)), CelestialObject.SKY_OBJECT_PLANET, (long)random(3000.0f, 5000.0f)));
                        break;
                }

                playCelestialObject(celestialObjects.get(celestialObjects.size()-1));
                break;
        }
    }

    @Override
    public void instrumentChanged(int instriment) {
        this.currentInstrument = instriment;
        this.newPendulum.setInstrument(instriment);
    }

    public void mouseWheel(MouseEvent event) {
        float e = event.getCount();
        if(event.getCount() > 0){
            beatVelocityBeatDisplay.increaseVelocityLevel();
        }else{
            beatVelocityBeatDisplay.decreaseVelocityLevel();
        }
        newPendulum.setBeatVelocity(beatVelocityBeatDisplay.getCurrentBeatVelocityLevel());
    }

}


