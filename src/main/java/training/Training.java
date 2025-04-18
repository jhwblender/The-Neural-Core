package training;

import main.Tools;
import network.Network;
import main.Main;
import network.Weight;

import static java.lang.Math.*;

public class Training {

    private final Network network;
    private final Weight[] linearWeights;
    int targetWeightIndex = 0;
    private int numIterations = 0;
    private double avgStabilityReq = 0.00000001; //Sample until this threshold

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


    }

    //------------------ Training Methods -----------------
    public void train(){
        double targetMinWeight = linearWeights[targetWeightIndex].getRangeMin();
        double targetMaxWeight = linearWeights[targetWeightIndex].getRangeMax();
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
            //Choose random point
            for (Weight linearWeight : linearWeights) {
                linearWeight.randomize();
            }
            //Set and test lower target region
            linearWeights[targetWeightIndex].setWeight(Tools.randRange(targetMinWeight, targetMidWeight));
            double lowerRegionError = getTrainingSamplesError();
            lowerRegionErrorSum += lowerRegionError;

            //Set and test upper target region
            linearWeights[targetWeightIndex].setWeight(Tools.randRange(targetMidWeight, targetMaxWeight));
            double upperRegionError = getTrainingSamplesError();
            upperRegionErrorSum += upperRegionError;
            numSamplePoints++;
            if(numSamplePoints%10==0){
                lastUpperRegionErrorAvg = upperRegionErrorAvg;
                lastLowerRegionErrorAvg = lowerRegionErrorAvg;
            }
            lowerRegionErrorAvg = lowerRegionErrorSum/(double)numSamplePoints;
            upperRegionErrorAvg = upperRegionErrorSum/(double)numSamplePoints;
        }while(
                (abs(lowerRegionErrorAvg - lastLowerRegionErrorAvg) > avgStabilityReq) ||
                (abs(upperRegionErrorAvg - lastUpperRegionErrorAvg) > avgStabilityReq)
        );
        System.out.println("numSamplePoints: "+numSamplePoints); //todo: Make this go into the graphics

        double lowerScore = lowerRegionErrorAvg;
        double upperScore = upperRegionErrorAvg;

        if(lowerScore < upperScore){
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
            maxError = max(maxError, setError);   //Experimenting
            minError = min(minError, setError);   //Experimenting
            avgErrorSum  += setError;
        }
        double avgError = avgErrorSum/(double)numSamples;
        double errorRange = maxError - minError;

        //return final error
//        return pow(avgError, errorRange);
        return avgError;// + 0.25 * errorRange; //returns % error and % variation
    }
 }
