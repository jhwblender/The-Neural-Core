import graphics.Drawable;
import graphics.Graphics;
import graphics.NetworkVisualizer;
import network.Network;
import processing.core.PApplet;
import training.Training;

import java.util.ArrayList;

public class Main extends PApplet{

    final static int[] dimensions = new int[]{9,8,8,8,8,7};
    final float trainingMultiplier = 0.4f; //0.1
    final float wiggleAdd = 0.01f; //0.01
    //todo add multiplier for error difference punisher

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
        training.train();
    }
}