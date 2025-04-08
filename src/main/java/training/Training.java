package training;

import network.Network;
import main.Main;

public class Training {

    private float[][] getTraining(boolean inOut){
        if(!inOut)
            return xorIn;
        else
            return xorOut;
    }

    private final Network network;
    private final int[] dimensions;
    final float trainingMultiplier;
    final float wiggleAdd;

    private final float[][] xorIn = new float[][]{
            {0,0},
            {0,1},
            {1,0},
            {1,1}
    };
    private final float[][] xorOut = new float[][]{
            {0},
            {1},
            {1},
            {0}
    };

    private final float[][] orIn = new float[][]{
            {0,0},
            {0,1},
            {1,0},
            {1,1}
    };
    private final float[][] orOut = new float[][]{
            {0},
            {1},
            {1},
            {1}
    };

    private final float[][] andIn = new float[][]{
            {0,0},
            {0,1},
            {1,0},
            {1,1}
    };
    private final float[][] andOut = new float[][]{
            {0},
            {0},
            {0},
            {1}
    };

    private final float[][] binary3AddIn = new float[][]{
            {0,0,0},
            {0,0,1},
            {0,1,0},
            {0,1,1},
            {1,0,0},
            {1,0,1},
            {1,1,0},
            {1,1,1}
    };
    private final float[][] binary3AddOut = new float[][]{
            {0,0},
            {0,1},
            {0,1},
            {1,0},
            {0,1},
            {1,0},
            {1,0},
            {1,1}
    };

    private final float[][] charRecognitionIn = new float[][]{
            {0,1,0,1,1,1,0,1,0},
            {0,0,0,1,1,1,0,0,0},
            {1,1,1,0,1,0,1,1,1},
            {1,0,1,1,0,1,1,1,1},
            {1,1,1,0,1,0,0,1,0},
            {1,0,0,1,0,0,1,1,1},
            {1,0,1,1,1,1,1,0,1}
    };
    private final float[][] charRecognitionOut = new float[][]{
            {0,0,0,0,0,0,1},
            {0,0,0,0,0,1,0},
            {0,0,0,0,1,0,0},
            {0,0,0,1,0,0,0},
            {0,0,1,0,0,0,0},
            {0,1,0,0,0,0,0},
            {1,0,0,0,0,0,0}
    };

    public Training(Network network){
        this.trainingMultiplier = Main.trainingMultiplier;
        this.wiggleAdd = Main.wiggleAdd;
        this.network = network;
        dimensions = network.getLayerSizes();
    }

    public void train(){
        float[] errorAndVariation = calculateError();
        float error = errorAndVariation[0];
        calculateSlope(getPenalty(errorAndVariation));
        network.descend(trainingMultiplier * getPenalty(errorAndVariation), Main.variationFactor);
        Main.graph.addValue(100*error);
    }

    private float getPenalty(float[] errorAndVariation){
        //errorAndVariation[0] = Error
        //errorAndVariation[1] = Variation
        return (errorAndVariation[0]*0.5f + errorAndVariation[1]*0.5f);
    }

    private float[] calculateError(){
        float errorSum = 0;
        float minError = Float.MAX_VALUE; //experimenting
        float maxError = 0; //experimenting
        float variation = 0; //experimenting
        int numSamples = getTraining(false).length;
        for(int set = 0; set < numSamples; set++){
            network.feedForward(getTraining(false)[set]);
            float[] errorAndVariation = network.getErrorAndVariation(getTraining(true)[set]);
            float error         = errorAndVariation[0];
            float nodeVariation = errorAndVariation[1];
            minError = Math.min(minError, error);   //Experimenting
            maxError = Math.max(maxError, error);   //Experimenting
            variation += (maxError - minError + nodeVariation)/2; //This 2 is okay because it's averaging 2 %s
            errorSum  += error;
        }
        return new float[]{errorSum/numSamples, variation/numSamples};// * (1 + (maxError-minError)); //multiplier experimental
    }

    private void calculateSlope(float baseError){
        for(int startLayer = 0; startLayer < dimensions.length - 1; startLayer++){
            int endLayer = startLayer+1;
            for(int endNode = 0; endNode < dimensions[endLayer]; endNode++){
                for(int startNode = 0; startNode < dimensions[startLayer]; startNode++){                                //go through each start node
                    network.setWiggleWeight(startLayer, startNode, endNode, wiggleAdd);
                    float wiggleError = getPenalty(calculateError());
                    network.wiggleReset(startLayer, startNode, endNode);
                    float slope = (wiggleError - baseError) / wiggleAdd;
                    network.setSlope(startLayer, startNode, endNode, slope);
                }
            }
        }
    }
}
