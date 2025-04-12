package training;

import main.Tools;
import network.Network;
import main.Main;
import network.Weight;

import java.util.Arrays;

import static java.lang.Math.*;

public class Training {

    private final Network network;
    private final Weight[] linearWeights;
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

        for (Weight linearWeight : linearWeights) {
            linearWeight.setRangeMinMax(-4, 4);
        }

        //Starting error
        initialSamplePoints = 200 * network.getNumWeights();
    }

    //------------------ Training Methods -----------------
    public void train(){
        double targetMinWeight = linearWeights[targetWeightIndex].getRangeMin();
        double targetMaxWeight = linearWeights[targetWeightIndex].getRangeMax();
        double targetMidWeight = (targetMinWeight + targetMaxWeight) / 2;

        //For each sample point
        int numSamplePoints = (int) ceil(initialSamplePoints / (1 + .25f * numIterations)); //higher the coefficient, the quicker the dropoff
        double[] lowerRegionSamples = new double[numSamplePoints];
        double[] upperRegionSamples = new double[numSamplePoints];
        if(numSamplePoints == 1)
            return;
        System.out.println("numSamplePoints: "+numSamplePoints); //todo: Make this go into the graphics
        double lowerRegionErrorSum = 0;
        double upperRegionErrorSum = 0;
        for(int point = 0; point < numSamplePoints; point++) {
            //Choose random point
            for (Weight linearWeight : linearWeights) {
                linearWeight.randomize();
            }
            //Set and test lower target region
            linearWeights[targetWeightIndex].setWeight(Tools.randRange(targetMinWeight, targetMidWeight));
            double lowerRegionError = getTrainingSamplesError();
            lowerRegionSamples[point] = lowerRegionError;
            lowerRegionErrorSum += lowerRegionError;

            //Set and test upper target region
            linearWeights[targetWeightIndex].setWeight(Tools.randRange(targetMidWeight, targetMaxWeight));
            double upperRegionError = getTrainingSamplesError();
            upperRegionSamples[point] = upperRegionError;
            upperRegionErrorSum += upperRegionError;
        }
        double lowerRegionErrorAvg = lowerRegionErrorSum/(double)numSamplePoints;
        double upperRegionErrorAvg = upperRegionErrorSum/(double)numSamplePoints;
//        Arrays.sort(lowerRegionSamples);
//        double lowerRegionError = lowerRegionSamples[lowerRegionSamples.length/2];
//        Arrays.sort(upperRegionSamples);
//        double upperRegionError = upperRegionSamples[upperRegionSamples.length/2];

//        double lowerRegionError = (lowerRegionErrorAvg + lowerRegionErrorMid)/2;
//        double upperRegionError = (upperRegionErrorAvg + upperRegionErrorMid)/2;

        if(lowerRegionErrorAvg < upperRegionErrorAvg){
            //Lower region wins
            linearWeights[targetWeightIndex].setRangeMax(targetMidWeight);
            Main.graph.addValue(100*lowerRegionErrorAvg);
        }else{
            //Upper region wins
            linearWeights[targetWeightIndex].setRangeMin(targetMidWeight);
            Main.graph.addValue(100*upperRegionErrorAvg);
        }

        //Go to next target
        targetWeightIndex = (targetWeightIndex+1) % linearWeights.length;
        if(targetWeightIndex == 0){
            Tools.shuffleArray(linearWeights);
        }
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
            maxError = Math.max(maxError, setError);   //Experimenting
            minError = Math.min(minError, setError);   //Experimenting
            avgErrorSum  += setError;
        }
        double avgError = avgErrorSum/(double)numSamples;
        double errorRange = maxError - minError;

        //return final error
//        return pow(avgError, errorRange);
        return avgError;
    }
 }
