import graphics.Drawable;
import graphics.Graphics;
import graphics.NetworkVisualizer;
import network.Network;
import processing.core.PApplet;
import training.Training;

import java.util.ArrayList;

public class Main extends PApplet{

    final static int[] dimensions = new int[]{2,3,3,1}; //Network Size
    final float trainingMultiplier = 0.1f; //0.1              //Multiplier for direction push
    final float wiggleAdd = 0.001f; //0.01                   //surrounding test point distance
    final float variationFactor = 0.05f;                     //drop of randomness
    //todo add multiplier for variation penalty
    //todo change color to show network error
    //todo output variation
    //todo output penalty

    Graphics graphics;
    static Network network;
    static NetworkVisualizer networkVisualizer;
    static Training training;

    public static void main(String[] args) {
        PApplet.main("Main"); //Start graphics window (DON'T ADD ANYTHING ELSE!
    }

    public void settings(){
        graphics = new Graphics();
        graphics.addCanvas(this);
        graphics.settings();
    }
    public void setup() {
        graphics.setup();

        network = new Network(dimensions);
        networkVisualizer = new NetworkVisualizer(network);
        training = new Training(network, trainingMultiplier, wiggleAdd);

        graphics.addDrawable(networkVisualizer);
    }
    public void draw() {
        graphics.draw();
        training.train(variationFactor);
    }
}