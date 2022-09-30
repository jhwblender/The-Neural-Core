package network;

public class WeightMaster {

    private int numLayers;
    private int[] dimensions;
    private Weight[][][] weights; //weights[layerStart][startNode][endNode of nextLayer]

    public void init(int[] dimensions){
        numLayers = dimensions.length;
        this.dimensions = dimensions;

        weights = new Weight[dimensions.length][][];
        for(int layerStart = 0; layerStart < numLayers - 1; layerStart++) {
            int layerEnd = layerStart + 1;
            weights[layerStart] = new Weight[dimensions[layerStart]][];
            for (int startNode = 0; startNode < dimensions[layerStart]; startNode++) {
                weights[layerStart][startNode] = new Weight[dimensions[layerEnd]];
                for (int endNode = 0; endNode < dimensions[layerEnd]; endNode++) {
                    weights[layerStart][startNode][endNode] = new Weight();
                }
            }
        }
    }

    public float getWeight(int layerStart, int startNode, int endNode){
        return weights[layerStart][startNode][endNode].getWeight();
    }

    public void setWiggleWeight(int layerStart, int startNode, int endNode, float value){
        weights[layerStart][startNode][endNode].setWiggleWeight(value);
    }
    public void wiggleReset(int layerStart, int startNode, int endNode){
        weights[layerStart][startNode][endNode].wiggleReset();
    }
    public void setSlope(int layer, int startNode, int endNode, float value) {
        weights[layer][startNode][endNode].setSlope(value);
    }

    public void descend(float multiplier, float variationFactor){
        for(int layerStart = 0; layerStart < numLayers - 1; layerStart++) {
            int layerEnd = layerStart + 1;
            for (int startNode = 0; startNode < dimensions[layerStart]; startNode++) {
                for (int endNode = 0; endNode < dimensions[layerEnd]; endNode++) {
                    weights[layerStart][startNode][endNode].descend(multiplier, variationFactor);
                }
            }
        }
    }
}
