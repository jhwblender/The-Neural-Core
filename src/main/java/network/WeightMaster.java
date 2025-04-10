package network;

import java.util.ArrayList;
import java.util.HashMap;

public class WeightMaster {

    private final Weight[][][] weights; //weights[layerStart][startNode][endNode of nextLayer]
    private final Weight[] linearWeights;
//    private final ArrayList<Weight> linearWeights;

    public WeightMaster(int[] dimensions, Integer setWeights) {
        //Initialize arrays
        int numLayers   = dimensions.length;
        weights         = new Weight[dimensions.length][][];
        //initialize linear weights
        int numWeights = 0;
        for (int layerStart = 0; layerStart < numLayers - 1; layerStart++) {
            int layerEnd = layerStart + 1;
            numWeights += dimensions[layerStart] * dimensions[layerEnd];
        }
        linearWeights   = new Weight[numWeights];
        //Initialize weights and sub-arrays
        int linearWeightIndex = 0;
        for(int layerStart = 0; layerStart < numLayers - 1; layerStart++) {
            int layerEnd = layerStart + 1;
            weights[layerStart] = new Weight[dimensions[layerStart]][];
            for (int startNode = 0; startNode < dimensions[layerStart]; startNode++) {
                weights[layerStart][startNode] = new Weight[dimensions[layerEnd]];
                for (int endNode = 0; endNode < dimensions[layerEnd]; endNode++) {
                    //Initialize weight
                    Weight weight = ((setWeights != null) ? new Weight(setWeights)
                            : new Weight(-2, 2)); //TODO: make this a parameter
                    weights[layerStart][startNode][endNode] = weight;
                    //Add to linear weightMap
                    linearWeights[linearWeightIndex] = weight;
                    linearWeightIndex++;
                }
            }
        }
    }

    public int getNumWeights(){
        return linearWeights.length;
    }

    public float getWeight(int layerStart, int startNode, int endNode){
        return weights[layerStart][startNode][endNode].getWeight();
    }

    public float getWeight(int num){
        return linearWeights[num].getWeight();
    }

    public Weight[] getLinearWeights(){
        return linearWeights;
    }
}
