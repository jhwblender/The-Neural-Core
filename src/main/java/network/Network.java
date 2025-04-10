package network;

import main.Tools;

public class Network {

    final int numLayers;
    int[] dimensions;
    WeightMaster weights, minWeights, maxWeights;
    NodeMaster nodes;
    float currentOverallError;

    public Network(int[] dimensions){
        this.dimensions = dimensions;
        numLayers = dimensions.length;

        nodes = new NodeMaster();
        nodes.init(dimensions);

        weights = new WeightMaster(dimensions, null);
    }

    public void feedForward(float[] input){
        nodes.setLayer(0, input);
        for(int startLayer = 0; startLayer < numLayers-1; startLayer++){
            int endLayer = startLayer+1;
            for(int endNode = 0; endNode < dimensions[endLayer]; endNode++){
                float sum = 0;                                                                                          //reset sum
                for(int startNode = 0; startNode < dimensions[startLayer]; startNode++){                                //go through each start node
                    sum += nodes.getValue(startLayer, startNode) * weights.getWeight(startLayer, startNode, endNode);    //sum the start node value with the weight
                }
                nodes.setValue(endLayer, endNode, activation(sum));                                                     //set the end node to the f(sum) with f being the activation function
            }
        }
    }

    public float[] getAvgAndMaxError(float[] desired){
        int lastLayer = numLayers-1;
        int lastLayerSize = dimensions[lastLayer];
        assert(desired.length == lastLayerSize);

        float maxError = 0;
        float errorSum = 0;
        for(int node = 0; node < lastLayerSize; node++){
            float error = Math.abs(desired[node] - getNodeValue(lastLayer, node));
            maxError = Math.max(maxError, error);   //Experimenting
            errorSum += error;
        }
        float avgError = errorSum/lastLayerSize;
        //Get range for normalizing to %
        float desiredDelta = Tools.getDelta(desired);
        //Convert to %
        avgError = avgError;///desiredDelta;
        maxError = maxError;///desiredDelta;
//        System.out.println("avgError: "+avgError+", maxError: "+maxError);
        return new float[]{avgError, maxError};// returns % error and % variation
    }

    private float activation(float x){
        return modifiedSigmoidActivation(x);
    }
    //---------- Activation Functions ----------
    private float modifiedSigmoidActivation(float x){
        return (float)(2/(1+Math.pow(Math.E,-3*x))-1); //RANGE: y[-1, 1] with a .995 range x[-2, 2].
    }
    private float standardSigmoidActivation(float x){
        return (float)(1/(1+Math.pow(Math.E,-x))); //RANGE: y[0, 1] with a .995 range x[-6, 6].
    }
    //--------------------------------------------

    //------------- Node Getters -------------------------
    public float getNodeValue(int layer, int node){
        return nodes.getValue(layer, node);
    }
    //-----------------------------------------------------
    //------------- Weight Getters -------------------------
    public int getNumWeights(){
        return weights.getNumWeights();
    }

    public Weight[] getLinearWeights(){
        return weights.getLinearWeights();
    }

    public float getWeightValue(int layer, int startNode, int endNode) {
        return weights.getWeight(layer, startNode, endNode);
    }
    //----------------------------------------------------
    //------------- Layer Getters -------------------------
    public int getNumLayers(){
        return numLayers;
    }
    public int getLayerSize(int layer){
        return dimensions[layer];
    }
    public int getMaxLayerSize(){
        int max = 0;
        for(int layer = 0; layer < numLayers; layer++){
            max = Math.max(getLayerSize(layer), max);
        }
        return max;
    }
    //-----------------------------------------------------
}
