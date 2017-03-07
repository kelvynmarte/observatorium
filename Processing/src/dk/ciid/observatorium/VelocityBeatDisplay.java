package dk.ciid.observatorium;

import processing.core.PApplet;

/**
 * Created by kelvyn on 03/03/2017.
 */
public class VelocityBeatDisplay {

    public static int VELOCITY_LEVEL_MAX_MAPPING = 127;
    public static int VELOCITY_LEVEL_MIN_MAPPING = 1;

    public static int VELOCITY_LEVEL_MAX = 4;
    public static int VELOCITY_LEVEL_MIN = 1;

    int currentVelocityLevel = 4;

    public void draw(ObservatoriumSketch p){
        int velocityBarSpacing = 2;
        int velocityBarHeight = 2;
        int velocityBarWith = (p.width/VELOCITY_LEVEL_MAX)-velocityBarSpacing + velocityBarSpacing/VELOCITY_LEVEL_MAX;
        p.noStroke();
        p.fill(p.COLOR_VELOCITY_DISPLAY_BAR);
        for(int i = 0; i < currentVelocityLevel; i++){
            p.rect((velocityBarWith + velocityBarSpacing) * i, p.height - velocityBarHeight, velocityBarWith, velocityBarHeight);
        }

    }

    public int getCurrentBeatVelocityLevel() {
        return (int) PApplet.map(currentVelocityLevel, VELOCITY_LEVEL_MIN, VELOCITY_LEVEL_MAX, VELOCITY_LEVEL_MIN_MAPPING, VELOCITY_LEVEL_MAX_MAPPING);
    }

    public void increaseVelocityLevel() {
        if(currentVelocityLevel < VELOCITY_LEVEL_MAX) currentVelocityLevel++;
    }


    public void decreaseVelocityLevel() {
        if(currentVelocityLevel > VELOCITY_LEVEL_MIN) currentVelocityLevel--;
    }

}
