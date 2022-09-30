package network;

public class Network {

    final float activationMax = 1;
    final float activationMin = 0;
    final int numLayers;
    int[] dimensions;
    WeightMaster weights;
    NodeMaster nodes;
    float currentOverallError;

    public Network(int[] dimensions){
        this.dimensions = dimensions;
        numLayers = dimensions.length;

        nodes = new NodeMaster();
        nodes.init(dimensions);
        weights = new WeightMaster();
        weights.init(dimensions);
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

    public float[] getErrorAndVariation(float[] desired){
        int lastLayer = numLayers-1;
        int lastLayerSize = dimensions[lastLayer];
        assert(desired.length == lastLayerSize);

        float minError = Float.MAX_VALUE; //experimenting
        float maxError = 0; //experimenting
        float errorSum = 0; //experimenting
        for(int node = 0; node < lastLayerSize; node++){
            float error = Math.abs(desired[node] - getNodeValue(lastLayer, node));
            minError = Math.min(minError, error);   //Experimenting
            maxError = Math.max(maxError, error);   //Experimenting
            errorSum += error;
        }
        errorSum /= activationMax - activationMin;
        return new float[]{errorSum/lastLayerSize, (maxError - minError)/(activationMax - activationMin)};// returns % error and % variation
    }

    //REMEMBER to change activationMax and activationMin
    private float activation(float x){
        return sigmoidActivation(x);
    }

    private float modifiedSigmoidActivation(float x){
        return (float)(2/(1+Math.pow(Math.E,-x))-1); //modified to be -1 to 1, with a .995 range by -2 to 2.
    }
    private float sigmoidActivation(float x){
        return (float)(1/(1+Math.pow(Math.E,-x)));
    }

    public float getNodeValue(int layer, int node){
        return nodes.getValue(layer, node);
    }

    public float getWeightValue(int layer, int startNode, int endNode){
        return weights.getWeight(layer, startNode, endNode);
    }
    public void setWiggleWeight(int layer, int startNode, int endNode, float value) {
        weights.setWiggleWeight(layer, startNode, endNode, value);
    }
    public void wiggleReset(int layer, int startNode, int endNode){
        weights.wiggleReset(layer, startNode, endNode);
    }
    public void setSlope(int layer, int startNode, int endNode, float value){
        weights.setSlope(layer, startNode, endNode, value);
    }
    public void descend(float multiplier, float variationFactor){
        weights.descend(multiplier, variationFactor);
    }

    public int getNumLayers(){
        return numLayers;
    }
    public int getLayerSize(int layer){
        return dimensions[layer];
    }
    public int[] getLayerSizes(){
        return dimensions;
    }
    public int getMaxLayerSize(){
        int max = 0;
        for(int layer = 0; layer < numLayers; layer++){
            max = Math.max(getLayerSize(layer), max);
        }
        return max;
    }
}
