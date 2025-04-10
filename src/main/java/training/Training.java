package training;

import main.Tools;
import network.Network;
import main.Main;
import network.Weight;

public class Training {

    private final Network network;
    private final Weight[] linearWeights;
    private final float[] minWeights;
    private final float[] maxWeights;
    int targetWeightIndex = 0;

    //---------------- Training Selector -----------------
    private float[][] getTraining(boolean inOut){
        if(!inOut)
            return binary3AddIn;
        else
            return binary3AddOut;
    }

    //------------------ Training Data -----------------
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

    //------------------ Constructor -----------------
    public Training(Network network){
        this.network = network;
        this.linearWeights = network.getLinearWeights();
        minWeights = new float[network.getNumWeights()];
        maxWeights = new float[network.getNumWeights()];

        for(int i = 0; i < linearWeights.length; i++){
            minWeights[i] = -2;
            maxWeights[i] = 2;
        }

        //Starting error
        Main.graph.addValue(getTrainingSamplesError());
    }

    //------------------ Training Methods -----------------
    public void train(){
        float targetMinWeight = minWeights[targetWeightIndex];
        float targetMaxWeight = maxWeights[targetWeightIndex];
        float targetMidWeight = (targetMinWeight + targetMaxWeight) / 2;

        //For each sample point
        int numSamplePoints = 50 * network.getNumWeights();
        float lowerRegionErrorSum = 0;
        float upperRegionErrorSum = 0;
        for(int point = 0; point < numSamplePoints; point++) {
            //Choose random point
            for (int i = 0; i < linearWeights.length; i++) {
                float minWeight = minWeights[i];
                float maxWeight = maxWeights[i];
                linearWeights[i].setWeight(Tools.randRange(minWeight, maxWeight));
            }
            //Set and test lower target region
            linearWeights[targetWeightIndex].setWeight(Tools.randRange(targetMinWeight, targetMidWeight));
            float lowerRegionError = getTrainingSamplesError();
            lowerRegionErrorSum += lowerRegionError;

            //Set and test upper target region
            linearWeights[targetWeightIndex].setWeight(Tools.randRange(targetMidWeight, targetMaxWeight));
            float upperRegionError = getTrainingSamplesError();
            upperRegionErrorSum += upperRegionError;
        }
        float lowerRegionError = lowerRegionErrorSum/numSamplePoints;
        float upperRegionError = upperRegionErrorSum/numSamplePoints;

        if(lowerRegionError < upperRegionError){
            //Lower region wins
            maxWeights[targetWeightIndex] = targetMidWeight;
            Main.graph.addValue(lowerRegionError);
        }else{
            //Upper region wins
            minWeights[targetWeightIndex] = targetMidWeight;
            Main.graph.addValue(100*upperRegionError);
        }

        //Go to next dimension
        targetWeightIndex = (targetWeightIndex+1) % linearWeights.length;
    }
    //----------------------------------------------------------------------

    //------------ Calculate the average error of the training set------------
    //returns array: [avgError, avgMaxError, maxError]
    private float getTrainingSamplesError(){
        float avgErrorSum = 0;
        float maxErrorSum = 0;
        float maxError = 0;
        int numSamples = getTraining(false).length;
        for(int set = 0; set < numSamples; set++){
            network.feedForward(getTraining(false)[set]);
            float[] avgAndMaxError = network.getAvgAndMaxError(getTraining(true)[set]);
            float avgSetError = avgAndMaxError[0];
            float maxSetError = avgAndMaxError[1];
            maxErrorSum += maxSetError;
            maxError = Math.max(maxError, maxSetError);   //Experimenting
            avgErrorSum  += avgSetError;
        }
        float avgError = avgErrorSum/numSamples;
        float avgMaxError = maxErrorSum/numSamples;

        //return final error
        return (avgMaxError + (0.8f * avgError + 0.2f * maxError)) / 2f;
    }
 }
