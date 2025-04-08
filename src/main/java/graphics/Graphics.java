package graphics;

import network.Network;
import processing.core.PApplet;

import java.util.ArrayList;

public class Graphics implements Drawable{

    PApplet canvas;
    static ArrayList<Drawable> drawables;

    @Override
    public void addCanvas(PApplet canvas) {
        this.canvas = canvas;
    }

    public void settings(){
        canvas.size(canvas.displayWidth*3/8, canvas.displayHeight*3/8);
    }

    public void setup(){
        drawables = new ArrayList<>();
    }

    public void draw(){
        canvas.background(123,50,79);
        for (Drawable item : drawables) {
            item.draw();
        }
    }

    public void addDrawable(Drawable drawable){
        drawables.add(drawable);
        drawable.addCanvas(canvas);
    }
}
