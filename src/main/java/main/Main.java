package main;

import graphics.Graph;
import graphics.Graphics;
import graphics.NetworkVisualizer;
import network.Network;
import processing.core.PApplet;
import training.Training;

public class Main extends PApplet{

    final static int[] dimensions = new int[]{3, 3, 2}; //Network Size

    Graphics graphics;
    public static Network network;
    public static NetworkVisualizer networkVisualizer;
    public static Graph graph;
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
        graph = new Graph();
        training = new Training(network);

        graphics.addDrawable(networkVisualizer);
        graphics.addDrawable(graph);
    }

    public void draw() {
        graphics.draw();
        training.train();
    }
    //------------------------------------------------
}