package main;

import graphics.Graph;
import graphics.Graphics;
import graphics.NetworkVisualizer;
import network.Network;
import processing.core.PApplet;
import training.Training;

import java.util.Arrays;

public class Main extends PApplet{

    final static int[] dimensions = new int[]{9, 8, 7}; //Network Size
    //Sample until this accuracy threshold
    final public static double avgStabilityReq = 0.000001; //0.00001
    final public static double weightSolidifiedReq = 0.03;

    Graphics graphics;
    public static Network network;
    public static NetworkVisualizer networkVisualizer;
    public static Graph graph;
    public static Graph graph2;
    public static Training training;

    public static void main(String[] args) {
        PApplet.main("main.Main"); //Start graphics window (DON'T ADD ANYTHING ELSE!
    }

    //----------------- Processing Methods -----------------
    public void settings(){
        graphics = new Graphics();
        graphics.addCanvas(this);
        graphics.settings();
    }

    public void setup() {
        graphics.setup();

        network = new Network(dimensions);
        networkVisualizer = new NetworkVisualizer(network);
        graph = new Graph(new int[]{19, 100, 132});
        graph2 = new Graph(new int[]{78, 94, 42});
        training = new Training(network);

        graphics.addDrawable(networkVisualizer);
        graphics.addDrawable(graph);
        graphics.addDrawable(graph2);
    }

    public void draw() {
        graphics.draw();
        training.train();
    }
    //------------------------------------------------
}