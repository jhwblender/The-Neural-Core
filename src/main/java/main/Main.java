package main;

import graphics.Graph;
import graphics.Graphics;
import graphics.NetworkVisualizer;
import network.Network;
import processing.core.PApplet;
import training.Training;

public class Main extends PApplet{

    final static int[] dimensions = new int[]{2,5,10,1}; //Network Size
    public final static float trainingMultiplier = 0.1f; //0.1              //Multiplier for direction push
    public final static float wiggleAdd = 0.001f; //0.01                   //surrounding test point distance
    public final static float variationFactor = 0.05f;                     //drop of randomness
    //todo add multiplier for variation penalty
    //todo change color to show network error
    //todo output variation
    //todo output penalty

    Graphics graphics;
    static Network network;
    static NetworkVisualizer networkVisualizer;
    public static Graph graph;
    static Training training;

    public static void main(String[] args) {
        PApplet.main("main.Main"); //Start graphics window (DON'T ADD ANYTHING ELSE!
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
        graph = new Graph();
        training = new Training(network);

        graphics.addDrawable(networkVisualizer);
        graphics.addDrawable(graph);
    }
    public void draw() {
        graphics.draw();
        training.train();
    }
}