package training;

import network.Network;

public class Training {

    private float[][] getTraining(boolean inOut){
        if(!inOut)
            return charRecognitionIn;
        else
            return charRecognitionOut;
    }

    private final Network network;
    private final int[] dimensions;
    private final float maxError;
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

    public Training(Network network, float trainingMultiplier, float wiggleAdd){
        this.trainingMultiplier = trainingMultiplier;
        this.wiggleAdd = wiggleAdd;
        this.network = network;
        dimensions = network.getLayerSizes();
        maxError = getTraining(true).length * getTraining(true)[0].length;
    }

    public void train(){
        float[] errorAndVariation = calculateError();
        float error = errorAndVariation[0];
        float variation = errorAndVariation[1];
        calculateSlope(error*variation);
        network.descend(trainingMultiplier * (error/maxError));
        System.out.println((int)(1000*(error/maxError))/10f+"%, Error, "+error);          ////////////
    }

    private float[] calculateError(){
        float errorSum = 0;
        float minError = Float.MAX_VALUE; //experimenting
        float maxError = 0; //experimenting
        float variation = 0; //experimenting
        for(int set = 0; set < getTraining(false).length; set++){
            network.feedForward(getTraining(false)[set]);
            float[] errorAndVariation = network.getError(getTraining(true)[set]);
            float error = errorAndVariation[0];
            float nodeMaxError = errorAndVariation[1];
            float nodeMinError = errorAndVariation[2];
            minError = Math.min(Math.min(minError, error), nodeMinError);   //Experimenting
            maxError = Math.max(Math.max(maxError, error), nodeMaxError);   //Experimenting
            variation = maxError - minError;
            errorSum += error;
        }
        return new float[]{errorSum, variation};// * (1 + (maxError-minError)); //multiplier experimental
    }

    private void calculateSlope(float error){
        for(int startLayer = 0; startLayer < dimensions.length - 1; startLayer++){
            int endLayer = startLayer+1;
            for(int endNode = 0; endNode < dimensions[endLayer]; endNode++){
                for(int startNode = 0; startNode < dimensions[startLayer]; startNode++){                                //go through each start node
                    network.setWiggleWeight(startLayer, startNode, endNode, wiggleAdd);
                    float[] errorAndVariation = calculateError();
                    float wiggleError = errorAndVariation[0] * errorAndVariation[1];
                    network.wiggleReset(startLayer, startNode, endNode);
                    float slope = (wiggleError - error)/ wiggleAdd;
                    network.setSlope(startLayer, startNode, endNode, slope);
                }
            }
        }
    }
}
