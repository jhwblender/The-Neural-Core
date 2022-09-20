package graphics;

import network.Network;
import processing.core.PApplet;

public class NetworkVisualizer implements Drawable {

    Network network;
    PApplet canvas;
    int nodeScale;

    public NetworkVisualizer(Network network){
        this.network = network;
    }

    public void addCanvas(PApplet canvas){
        this.canvas = canvas;
        int vScale = canvas.height / (2 * network.getMaxLayerSize());
        int hScale = canvas.width  / (2 * network.getNumLayers()   );
        nodeScale = Math.min(vScale, hScale);
    }

    public void draw(){
        drawWeights();
        drawNodes();
    }

    private void drawWeights() {
        for (int layer = 0; layer < network.getNumLayers() - 1; layer++) {
            for( int startNode = 0; startNode < network.getLayerSize(layer); startNode++){
                for(int endNode = 0; endNode < network.getLayerSize(layer + 1); endNode++){
                    int[] startPos = getNodePosition(layer, startNode);
                    int[] endPos = getNodePosition(layer+1, endNode);
                    float weightValue = network.getWeightValue(layer, startNode, endNode);
                    int[] color = getColor(weightValue, 6);
                    canvas.strokeWeight(Math.abs(weightValue));
                    canvas.stroke(color[0], color[1], color[2]);
                    canvas.line(startPos[0], startPos[1], endPos[0], endPos[1]);
                }
            }
        }
    }

    private void drawNodes(){
        canvas.noStroke();
        for(int layer = 0; layer < network.getNumLayers(); layer++){
            for(int node = 0; node < network.getLayerSize(layer); node++){
                int[] pos = getNodePosition(layer, node);
                float nodeValue = network.getNodeValue(layer, node);
                int[] color = getColor(nodeValue, 1);
                canvas.fill(color[0],color[1],color[2]);
                canvas.ellipse(pos[0],pos[1], nodeScale, nodeScale);
            }
        }
    }

    private int[] getNodePosition(int layer, int node){
        int[] pos = new int[2];
        int xStep = canvas.width / (network.getNumLayers() * 2);
        int yStep = canvas.height / (network.getLayerSize(layer) * 2);
        pos[0] = (2 * layer) * xStep + xStep;
        pos[1] = (2 * node) * yStep + yStep;
        return pos;
    }

    private int[] getColor(float value, float max){ //assumed -1 to 1
        int[] color = new int[3];
        color[0] = (int)((value <= 0)? 255 : 255 * (max - Math.abs(value)));
        color[1] = (int)((value >= 0)? 255 : 255 * (max - Math.abs(value)));
        color[2] = (int)(255 * (max - Math.abs(value)));
        return color;
    }

}
