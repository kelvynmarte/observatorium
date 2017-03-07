package dk.ciid.observatorium;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * Created by kelvyn on 02/03/2017.
 */
public class CelestialObject {
    PVector skyObjectPosition;
    final int objectType;
    long lifeTime;
    long birthTime;
    boolean alive = true;
    boolean playing = false;

    public final static int SKY_OBJECT_STAR = 0;
    public final static int SKY_OBJECT_CONST = 1;
    public final static int SKY_OBJECT_PLANET = 2;

    public final static int SKY_OBJECT_STAR_INSTRUMENT = 3;
    public final static int SKY_OBJECT_CONST_INSTRUMENT = 4;
    public final static int SKY_OBJECT_PLANET_INSTRUMENT = 5;

    public CelestialObject(PVector skyObjectPosition, int objectType, long lifeTimeInMs) {
        this.skyObjectPosition = skyObjectPosition;
        this.objectType = objectType;
        this.lifeTime = lifeTimeInMs;
        this.birthTime = System.currentTimeMillis();
    }

    void draw(ObservatoriumSketch p){
        p.tint(255,  PApplet.map(System.currentTimeMillis()-this.birthTime, 0, 400, 0,255));
        int yOffset = (int)PApplet.map(System.currentTimeMillis()-this.birthTime, 0, 400, 6,0);
        if(yOffset < 0) yOffset = 0;

        if(System.currentTimeMillis()-3000 > this.birthTime + lifeTime){
            p.tint(255, 255 - ((System.currentTimeMillis()-3000-this.birthTime - lifeTime)*255/3000));

        }else if(System.currentTimeMillis() > this.birthTime + lifeTime){
            alive = false;
        }

        switch(objectType){
            case SKY_OBJECT_STAR:
                p.image(p.IMAGE_SKY_OBJECT_STAR, skyObjectPosition.x, skyObjectPosition.y + yOffset);
                break;
            case SKY_OBJECT_CONST:
                p.image(p.IMAGE_SKY_OBJECT_CONST, skyObjectPosition.x, skyObjectPosition.y + yOffset);
                break;
            case SKY_OBJECT_PLANET:
                p.image(p.IMAGE_SKY_OBJECT_PLANET, skyObjectPosition.x, skyObjectPosition.y + yOffset);
                break;

        }
        p.tint(255, 255);
    }

    public int getInstrument(){
        switch(objectType){
            case SKY_OBJECT_STAR:
                return SKY_OBJECT_STAR_INSTRUMENT;
            case SKY_OBJECT_CONST:
                return SKY_OBJECT_CONST_INSTRUMENT;
            case SKY_OBJECT_PLANET:
                return SKY_OBJECT_PLANET_INSTRUMENT;
        }
        return 0;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public long getLifeTime() {
        return lifeTime;
    }

    public PVector getSkyObjectPosition() {
        return skyObjectPosition;
    }
}
