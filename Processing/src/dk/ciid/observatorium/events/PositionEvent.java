package dk.ciid.observatorium.events;

/**
 * Created by kelvyn on 02/03/2017.
 */
public class PositionEvent {
    float distanceToCenter;
    float velocity;
    int beatVelocity;
    float angle;
    int instrument;

    public PositionEvent(float distanceToCenter, float velocity, int beatVelocity, float angle, int instrument) {
        this.distanceToCenter = distanceToCenter;
        this.velocity = velocity;
        this.beatVelocity = beatVelocity;
        this.angle = angle;
        this.instrument = instrument;
    }

    public float getDistanceToCenter() {
        return distanceToCenter;
    }

    public float getVelocity() {
        return velocity;
    }

    public int getBeatVelocity() {
        return beatVelocity;
    }

    public float getAngle() {
        return angle;
    }

    public int getInstrument() {
        return instrument;
    }
}