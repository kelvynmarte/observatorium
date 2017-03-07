package dk.ciid.observatorium;

import dk.ciid.observatorium.events.MilkwayMenuListener;

import java.util.ArrayList;

/**
 * Created by kelvyn on 02/03/2017.
 */
public class MilkwayMenu {

    ArrayList<MilkwayMenuListener> listeners = new ArrayList<>();

    public void draw(ObservatoriumSketch p){
        if(p.mouseY < 40){
            if(p.mouseX < p.width/3){
                changeCurrentInstrument(0);
            }else if(p.mouseX < (p.width*2)/3){
                changeCurrentInstrument(1);
            }else{
                changeCurrentInstrument(2);
            }
        }

        float milkway_width = p.width/3.2f;
        int milkway_button_height = 40;

        if(p.currentInstrument == 0){
            p.image(p.IMAGE_MILKWAY_0, 0, 0, milkway_width, milkway_button_height);
        }else{
            p.image(p.IMAGE_MILKWAY_0_PASSIVE, 0, 0, milkway_width, 20);
        }

        if(p.currentInstrument == 1){
            p.pushMatrix();
            p.translate((p.width/2)-milkway_width/2, 0);
            p.image(p.IMAGE_MILKWAY_1, 0, 0, milkway_width, milkway_button_height);
            p.popMatrix();
        }else{
            p.pushMatrix();
            p.translate((p.width/2)-milkway_width/2, 0);
            p.image(p.IMAGE_MILKWAY_1_PASSIVE, 0, 0, milkway_width, 20);
            p.popMatrix();
        }

        if(p.currentInstrument == 2){
            p.pushMatrix();
            p.translate(p.width-milkway_width, 0);
            p.image(p.IMAGE_MILKWAY_2, 0, 0, milkway_width, milkway_button_height );
            p.popMatrix();
        }else{
            p.pushMatrix();
            p.translate(p.width-milkway_width, 0);
            p.image(p.IMAGE_MILKWAY_2_PASSIVE, 0, 0, milkway_width, 20);
            p.popMatrix();
        }
    }

    public void addMilkwayMenuListener(MilkwayMenuListener listener){
        listeners.add(listener);
    }

    private void changeCurrentInstrument(int instrument){
        for(MilkwayMenuListener listener : this.listeners){
            listener.instrumentChanged(instrument);
        }
    }
}
