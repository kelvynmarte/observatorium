package dk.ciid.observatorium;

import dk.ciid.observatorium.events.CenterPositionListener;
import dk.ciid.observatorium.events.MaxPositionListener;
import dk.ciid.observatorium.events.PositionEvent;
import processing.core.PVector;

import java.util.ArrayList;

/**
 * Created by kelvyn on 02/03/2017.
 */
public class Pendulum {
    float velocity = 10.0f; // speed
    int beatVelocity = 127; // Beat strength
    PVector currentPositionVector;
    PVector pendulumCenter;
    PVector pendulumMax1;
    PVector pendulumMax2;
    float currentPosition = 1.0f;
    float angle;
    int instrument = 0;
    boolean mouseOver = false;
    boolean mute = false;

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    public boolean isMouseOver() {
        return mouseOver;
    }

    public void setMouseOver(boolean mouseOver) {
        this.mouseOver = mouseOver;
    }

    public int getInstrument() {
        return instrument;
    }

    public void setInstrument(int instrument) {
        this.instrument = instrument;
    }

    ArrayList<MaxPositionListener> maxPositionListeners = new ArrayList<>();
    ArrayList<CenterPositionListener> centerPositionListeners = new ArrayList<>();

    boolean direction = true;
    boolean centerHit = false;

    Pendulum(PVector pendulumPosition, PVector pendulumCenter){
        resetPendulum(pendulumPosition,  pendulumCenter);
    }

    public Pendulum(PVector pendulumPosition, PVector pendulumCenter, int instrument) {
        this.currentPositionVector = pendulumCenter.copy();
        resetPendulum(pendulumPosition,  pendulumCenter);
        this.instrument = instrument;
    }

    void resetPendulum(PVector pendulumPosition, PVector pendulumCenter){
        this.currentPositionVector = pendulumCenter.copy();
        this.pendulumCenter = pendulumCenter;
        this.pendulumMax1 = pendulumPosition;
        this.pendulumMax2 = this.pendulumCenter.copy().sub(pendulumMax1.copy()).copy().add(pendulumCenter.copy());
        this.angle = PVector.angleBetween(this.pendulumCenter, this.pendulumMax1);
    }

    void setPendulumPositionToCenter(){
        currentPosition = 0.5f;
        triggerCenterPositionEvent();
    }

    float distanceToPendulumEnds(PVector messureVector){
        float distance1 = this.getPendulumMax1().dist(messureVector);
        float distance2 = this.getPendulumMax2().dist(messureVector);
        if(distance1 < distance2 ){
            return distance1;
        }else{
            return distance2;
        }
    }

    public void rotatePendulum(float angle)
    {
        Double s = Math.sin(angle);
        Double c = Math.cos(angle);

        // translate point back to origin:
        PVector pendulumPosition = this.pendulumMax1.copy();
        pendulumPosition.x -= pendulumCenter.x;
        pendulumPosition.y -= pendulumCenter.y;

        // rotate point
        Double xnew = pendulumPosition.x * c - pendulumPosition.y * s;
        Double ynew = pendulumPosition.x * s + pendulumPosition.y * c;

        // translate point back:
        pendulumPosition.x = new Double(xnew + pendulumCenter.x).intValue();
        pendulumPosition.y = new Double(ynew + pendulumCenter.y).intValue();

        resetPendulum(pendulumPosition, pendulumCenter);
    }


    void draw(ObservatoriumSketch p){
        // draw line
        p.stroke(60);
        p.line(pendulumMax1.x, pendulumMax1.y, pendulumMax2.x, pendulumMax2.y);

        p.pushMatrix();
        p.translate(pendulumMax1.x, pendulumMax1.y);
        if(isMouseOver()){
            p.fill(0);
            p.stroke(120);
            p.strokeWeight(1);
        }else{
            p.fill(120);
            p.noStroke();
        }
        p.ellipse(0, 0, 12, 12);

        p.popMatrix();

        p.pushMatrix();
        p.translate(pendulumMax2.x, pendulumMax2.y);

        p.ellipse(0, 0, 12, 12);

        p.popMatrix();
        p.color(23);

        // draw center point
        p.pushMatrix();
        p.translate(pendulumCenter.x, pendulumCenter.y);
        p.fill(255);
        p.noStroke();
        p.ellipse(0, 0, 5, 5);
        p.popMatrix();

        // current point
        p.pushMatrix();

        // p.println("current position x y " + currentPositionVector.x + " " + currentPositionVector.y);

        p.translate(currentPositionVector.x, currentPositionVector.y);

        switch (instrument){
            case 0:
                p.fill(p.PENDULUM_COLOR_INSTRUMENT_0);
            break;
            case 1:
                p.fill(p.PENDULUM_COLOR_INSTRUMENT_1);
            break;
            case 2:
                p.fill(p.PENDULUM_COLOR_INSTRUMENT_2);
            break;
        }

        p.noStroke();
        p.ellipse(0, 0, 7, 7);
        p.popMatrix();
    }

    void calculatePosition(long timeDiff){

        // System.out.println("time diff: " + timeDiff);

        if(direction){
            currentPosition = currentPosition - (timeDiff/getMaxDistanceToCenter())*velocity*0.02f;
            // trigger center event
            if( !centerHit && currentPosition < 0.51f) triggerCenterPositionEvent();
        }else{
            currentPosition = currentPosition + (timeDiff/getMaxDistanceToCenter())*velocity*0.02f;
            // trigger center event
            if( !centerHit && currentPosition > 0.49f) triggerCenterPositionEvent();
        }
        // println("pos: " + currentPosition);

        this.currentPositionVector = this.pendulumMax1.copy().add(this.pendulumMax2.copy().sub(pendulumMax1.copy()).copy().mult(currentPosition));

        if(currentPosition >= 1.0f || currentPosition <= 0.0f) {
            direction = !direction;
            triggerMaxPositionEvent();
        }
        if(currentPosition >= 1.0f){
            currentPosition = 1.0f;
        }else if(currentPosition <= 0.0f){
            currentPosition = 0.0f;
        }


    }

    public PVector getCurrentPositionVector() {
        return currentPositionVector;
    }

    public PVector getPendulumMax1() {
        return pendulumMax1;
    }

    public PVector getPendulumMax2() {
        return pendulumMax2;
    }

    public float getVelocity() {
        return velocity;
    }


    public float getAngle() {
        float angle = (float) Math.toDegrees(Math.atan2(pendulumCenter.y - pendulumMax1.y, pendulumCenter.x - pendulumMax1.x));

        if(angle < 0){
            angle += 360;
        }

        return angle;
    }

    public float getDistanceToCenter() {
        return this.currentPositionVector.dist(pendulumCenter);
    }

    public float getMaxDistanceToCenter() {
        return this.pendulumMax1.dist(pendulumCenter);
    }

    void triggerMaxPositionEvent(){
        if(!isMute()) {
            for (MaxPositionListener listener : maxPositionListeners) {
                listener.maxPositionReached(new PositionEvent(getMaxDistanceToCenter(), getVelocity(), getBeatVelocity(), getAngle(), instrument));
            }
        }
        centerHit = false; // reset center hit
    }

    void triggerCenterPositionEvent(){
        if(!isMute()){
            for(CenterPositionListener listener : centerPositionListeners){
                listener.centerPositionReached(new PositionEvent(getMaxDistanceToCenter(), getVelocity(), getBeatVelocity(), getAngle(), instrument ));
            }
        }
        centerHit = true;
    }

    void addMaxPositionEventListener(MaxPositionListener listener){
        maxPositionListeners.add(listener);
    }

    void addCenterPositionEventListener(CenterPositionListener listener){
        centerPositionListeners.add(listener);
    }

    void removeMaxPositionEventListener(MaxPositionListener listener){
        maxPositionListeners.remove(listener);
    }

    public int getBeatVelocity() {
        return beatVelocity;
    }

    public void setBeatVelocity(int beatVelocity) {
        this.beatVelocity = beatVelocity;
    }
}