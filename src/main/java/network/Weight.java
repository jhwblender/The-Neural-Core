package network;

public class Weight {

    double weight;
    double rangeMin;
    double rangeMax;

    Weight(int weight){
        this.weight = weight;
    }
    Weight(int min, int max){
        //Assign random weight between min and max value
        weight = ((max-min)*Math.random()+min);
    }

    public double getWeight(){
        return weight;
    }
    public void setWeight(double weight){
        this.weight = weight;
    }

    public void randomize(){
        weight = ((rangeMax-rangeMin)*Math.random()+rangeMin);
    }
    public void setRangeMin(double min){
        rangeMin = min;
    }
    public void setRangeMax(double max){
        rangeMax = max;
    }
    public void setRangeMinMax(double min, double max){
        rangeMin = min;
        rangeMax = max;
    }
    public double getRangeMin(){
        return rangeMin;
    }
    public double getRangeMax(){
        return rangeMax;
    }

    @Override
    public String toString(){
        return Double.toString(weight);
    }
}
