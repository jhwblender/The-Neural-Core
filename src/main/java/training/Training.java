package training;

import main.Tools;
import network.Network;
import main.Main;
import network.Weight;

import java.util.Arrays;

import static java.lang.Math.ceil;
import static java.lang.Math.exp;

public class Training {

    private final Network network;
    private final Weight[] linearWeights;
    private final double[] minWeights;
    private final double[] maxWeights;
    int targetWeightIndex = 0;
    private int initialSamplePoints;
    private int numIterations = 0;

    //---------------- Training Selector -----------------
    private double[][] getTraining(boolean inOut){
        if(!inOut)
            return charRecognitionIn;
        else
            return charRecognitionOut;
    }

    //------------------ Training Data -----------------
    private final double[][] xorIn = new double[][]{
            {0,0},
            {0,1},
            {1,0},
            {1,1}
    };
    private final double[][] xorOut = new double[][]{
            {0},
            {1},
            {1},
            {0}
    };

    private final double[][] orIn = new double[][]{
            {0,0},
            {0,1},
            {1,0},
            {1,1}
    };
    private final double[][] orOut = new double[][]{
            {0},
            {1},
            {1},
            {1}
    };

    private final double[][] andIn = new double[][]{
            {0,0},
            {0,1},
            {1,0},
            {1,1}
    };
    private final double[][] andOut = new double[][]{
            {0},
            {0},
            {0},
            {1}
    };

    private final double[][] binary3AddIn = new double[][]{
            {0,0,0},
            {0,0,1},
            {0,1,0},
            {0,1,1},
            {1,0,0},
            {1,0,1},
            {1,1,0},
            {1,1,1}
    };
    private final double[][] binary3AddOut = new double[][]{
            {0,0},
            {0,1},
            {0,1},
            {1,0},
            {0,1},
            {1,0},
            {1,0},
            {1,1}
    };

    private final double[][] charRecognitionIn = new double[][]{
            {0,1,0,1,1,1,0,1,0},
            {0,0,0,1,1,1,0,0,0},
            {1,1,1,0,1,0,1,1,1},
            {1,0,1,1,0,1,1,1,1},
            {1,1,1,0,1,0,0,1,0},
            {1,0,0,1,0,0,1,1,1},
            {1,0,1,1,1,1,1,0,1}
    };
    private final double[][] charRecognitionOut = new double[][]{
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
        minWeights = new double[network.getNumWeights()];
        maxWeights = new double[network.getNumWeights()];

        for(int i = 0; i < linearWeights.length; i++){
            minWeights[i] = -2;
            maxWeights[i] = 2;
        }

        //Starting error
        double startingError = getTrainingSamplesError();
        Main.graph.addValue(getTrainingSamplesError());
        System.out.println(startingError);
        System.exit(0);
        initialSamplePoints = 50 * network.getNumWeights();
    }

    //------------------ Training Methods -----------------
    public void train(){
        double targetMinWeight = minWeights[targetWeightIndex];
        double targetMaxWeight = maxWeights[targetWeightIndex];
        double targetMidWeight = (targetMinWeight + targetMaxWeight) / 2;

        //For each sample point
//        int numSamplePoints = (int) ceil(initialSamplePoints * exp(-0.3 * numIterations));
        int numSamplePoints = 500;
        if(numSamplePoints == 1)
            return;
        System.out.println("numSamplePoints: "+numSamplePoints);
        double lowerRegionErrorSum = 0;
        double upperRegionErrorSum = 0;
        for(int point = 0; point < numSamplePoints; point++) {
            //Choose random point
            for (int i = 0; i < linearWeights.length; i++) {
                double minWeight = minWeights[i];
                double maxWeight = maxWeights[i];
                linearWeights[i].setWeight(Tools.randRange(minWeight, maxWeight));
            }
            //Set and test lower target region
            linearWeights[targetWeightIndex].setWeight(Tools.randRange(targetMinWeight, targetMidWeight));
            double lowerRegionError = getTrainingSamplesError();
            lowerRegionErrorSum += lowerRegionError;

            //Set and test upper target region
            linearWeights[targetWeightIndex].setWeight(Tools.randRange(targetMidWeight, targetMaxWeight));
            double upperRegionError = getTrainingSamplesError();
            upperRegionErrorSum += upperRegionError;
        }
        double lowerRegionError = lowerRegionErrorSum/(double)numSamplePoints;
        double upperRegionError = upperRegionErrorSum/(double)numSamplePoints;

        if(lowerRegionError < upperRegionError){
            //Lower region wins
            maxWeights[targetWeightIndex] = targetMidWeight;
            Main.graph.addValue(100*lowerRegionError);
        }else{
            //Upper region wins
            minWeights[targetWeightIndex] = targetMidWeight;
            Main.graph.addValue(100*upperRegionError);
        }

        //Go to next target
        targetWeightIndex = (targetWeightIndex+1) % linearWeights.length;
        numIterations += 1;
    }
    //----------------------------------------------------------------------

    //------------ Calculate the average error of the training set------------
    //returns array: [avgError, avgMaxError, maxError]
    private double getTrainingSamplesError(){
        double avgErrorSum = 0;
        double maxErrorSum = 0;
        double maxError = 0;
        int numSamples = getTraining(false).length;
        for(int set = 0; set < numSamples; set++){
            network.feedForward(getTraining(false)[set]);
            double[] avgAndMaxError = network.getAvgAndMaxError(getTraining(true)[set]);
            double avgSetError = avgAndMaxError[0];
            double maxSetError = avgAndMaxError[1];
            maxErrorSum += maxSetError;
            maxError = Math.max(maxError, maxSetError);   //Experimenting
            avgErrorSum  += avgSetError;
        }
        double avgError = avgErrorSum/(double)numSamples;
        double avgMaxError = maxErrorSum/(double)numSamples;

        //return final error
        return avgError;
//        return (avgMaxError + (0.8f * avgError + 0.2f * maxError)) / 2f; todo UNCOMMENT THIS!
    }
 }
