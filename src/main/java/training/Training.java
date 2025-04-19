package training;

import main.Tools;
import network.Network;
import main.Main;
import network.Weight;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

public class Training {

    private final Network network;
    private final ArrayList<Weight> linearWeights;
//    int targetWeightIndex = 0;
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
        this.linearWeights = new ArrayList<>(List.of(network.getLinearWeights()));

        for (Weight linearWeight : linearWeights) {
            linearWeight.setRangeMinMax(-4, 4);
        }
    }

    //------------------ Training Methods -----------------
    public void train(){
        //Check if the training is done
        if(linearWeights.isEmpty())
            return;
        int totalNumSamples = 0;
        double lowestError = Double.MAX_VALUE;
        Weight lowestErrorWeight = linearWeights.get(0);
        boolean lowestErrorLowerOrUpper = false;
        for(Weight currentWeight : linearWeights){
//        for (int curWeightIndex = 0; curWeightIndex < linearWeights.size(); curWeightIndex++) {
            double targetMinWeight = currentWeight.getRangeMin();
            double targetMaxWeight = currentWeight.getRangeMax();
            double targetMidWeight = (targetMinWeight + targetMaxWeight) / 2;

            //For each sample point
            int numSamplePoints = 0; //Tracking
            double lowerRegionErrorSum = 0;
            double upperRegionErrorSum = 0;
            double upperRegionErrorAvg = 0;
            double lowerRegionErrorAvg = 0;
            double lastLowerRegionErrorAvg = 0;
            double lastUpperRegionErrorAvg = 0;

            do {
                //Choose random nD point
                for (Weight linearWeight : linearWeights) {
                    if(linearWeight != null)
                        linearWeight.randomize();
                }
                //Set and test lower target region
                currentWeight.setWeight(Tools.randRange(targetMinWeight, targetMidWeight));
                double lowerRegionError = getTrainingSamplesError();
                lowerRegionErrorSum += lowerRegionError;

                //Set and test upper target region
                currentWeight.setWeight(Tools.randRange(targetMidWeight, targetMaxWeight));
                double upperRegionError = getTrainingSamplesError();
                upperRegionErrorSum += upperRegionError;

                numSamplePoints++;
                totalNumSamples++;

                //Makes sample points in batches of 10
                if (numSamplePoints % 10 == 0) {
                    lastUpperRegionErrorAvg = upperRegionErrorAvg;
                    lastLowerRegionErrorAvg = lowerRegionErrorAvg;
                }

                //Find average error
                lowerRegionErrorAvg = lowerRegionErrorSum / (double) numSamplePoints;
                upperRegionErrorAvg = upperRegionErrorSum / (double) numSamplePoints;

                //Check if average error is stable
            } while (
                    (abs(lowerRegionErrorAvg - lastLowerRegionErrorAvg) > Main.avgStabilityReq) ||
                            (abs(upperRegionErrorAvg - lastUpperRegionErrorAvg) > Main.avgStabilityReq)
            );

            double lowerError = lastLowerRegionErrorAvg;
            double upperError = lastUpperRegionErrorAvg;

            if (lowerError < lowestError) {
                lowestError = lowerError;
                lowestErrorWeight = currentWeight;
                lowestErrorLowerOrUpper = false;
            }
            if (upperError < lowestError) {
                lowestError = upperError;
                lowestErrorWeight = currentWeight;
                lowestErrorLowerOrUpper = true;
            }
        }
        assert lowestErrorWeight != null;
        lowestErrorWeight.isSpecial = true;
        if(!lowestErrorLowerOrUpper){
            //Lower region wins
            lowestErrorWeight.setRangeToLower();
        }else{
            //Upper region wins
            lowestErrorWeight.setRangeToUpper();
        }

        //Check if the weight is solidified
        if(lowestErrorWeight.getRange() <= Main.weightSolidifiedReq){
            linearWeights.remove(lowestErrorWeight);
            System.out.println("Weight " + lowestErrorWeight.getIndex() + " solidified: " + lowestErrorWeight.getWeight());
        }
        System.out.println("Total number of samples: " + totalNumSamples);
        Main.graph.addValue(100*lowestError);

        numIterations += 1;
    }
    //----------------------------------------------------------------------

    //------------ Calculate the average error of the training set------------
    //returns array: [avgError, avgMaxError, maxError]
    private double getTrainingSamplesError(){
        double avgErrorSum = 0;
        double maxError = 0;
        double minError = Double.MAX_VALUE;
        int numSamples = getTraining(false).length;
        for(int set = 0; set < numSamples; set++){
            network.feedForward(getTraining(false)[set]);
            double setError = network.getSetError(getTraining(true)[set]);
            maxError = max(maxError, setError);   //Experimenting
            minError = min(minError, setError);   //Experimenting
            avgErrorSum  += setError;
        }
        double avgError = avgErrorSum/(double)numSamples;
        double errorRange = maxError - minError;

        //return final error
//        return pow(errorRange, 1 + avgError);
        return pow(avgError, 1 + errorRange);
//        return avgError;// + 0.25 * errorRange; //returns % error and % variation
    }
 }
