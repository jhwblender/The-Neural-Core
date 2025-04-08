package graphics;

import processing.core.PApplet;

import java.awt.*;
import java.util.ArrayList;

public class Graph implements Drawable{

    PApplet canvas;
    ArrayList<Float> values;
    float minY = Float.MAX_VALUE;
    float maxY = 0;

    public Graph(){
        values = new ArrayList<>();
    }

    @Override
    public void draw() {
        drawGraph();
    }

    @Override
    public void addCanvas(PApplet canvas) {
        this.canvas = canvas;
    }

    public void addValue(float value){
        values.add(value);
        if (value > maxY) {
            maxY = value;
        }
        if (value < minY) {
            minY = value;
        }
    }

    private void drawGraph(){
        if(values.isEmpty()){
            return;
        }

        canvas.strokeWeight(3);
        canvas.stroke(0, 0, 255);
        canvas.fill(255);

        //Draw Graph
        int lastX = 0;
        float lastY = (1f - ((values.get(0) - minY) / (maxY - minY))) * canvas.height;;

        boolean drawEvery = values.size() <= canvas.width;
        int maxI = Math.min(values.size(), canvas.width);

        for(int i = 1; i < maxI; i++) {
            int index = drawEvery ? i : (int)(i * ((float)values.size()/canvas.width));
            float value = values.get(index);
            int x = (int)((canvas.width * index) / (float)values.size());
            float y = (1f - ((value - minY) / (maxY - minY))) * canvas.height;
            canvas.line(lastX, lastY, x, y);
            lastX = x;
            lastY = y;
        }

        //Y-axis max/min text
        canvas.text("iterations: " + values.size(), 5, canvas.height - 35);
        canvas.text("max: " + maxY + "%",  5, canvas.height - 20);
        canvas.text("min: " + minY + "%", 5, canvas.height - 5);
    }
}
