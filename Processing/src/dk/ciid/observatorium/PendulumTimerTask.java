package dk.ciid.observatorium;

import java.util.TimerTask;

/**
 * Created by kelvyn on 03/03/2017.
 */
public class PendulumTimerTask extends TimerTask {

    ObservatoriumSketch observatoriumSketch;
    long timeLastBeat;
    long beatTimeDuration;

    public PendulumTimerTask(ObservatoriumSketch observatoriumSketch) {
        this.observatoriumSketch = observatoriumSketch;
        timeLastBeat = System.currentTimeMillis();
    }

    @Override
    public void run() {
        try {
            // Calculate time duration of a frmae
            beatTimeDuration = System.currentTimeMillis() - timeLastBeat;
            timeLastBeat = System.currentTimeMillis();
            for (Pendulum pendulum : observatoriumSketch.pendulums) {
                pendulum.calculatePosition(beatTimeDuration);
            }
            // does not work for current pendulum
            // observatoriumSketch.newPendulum.calculatePosition(beatTimeDuration);

        } catch ( IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }
}
