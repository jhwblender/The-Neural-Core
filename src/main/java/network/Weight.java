package network;

public class Weight {

    double weight;
    double rangeMin;
    double rangeMax;
    int index;

    //For rendering
    public boolean isSpecial = false;

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

    public void setIndex(int index){
        this.index = index;
    }
    public int getIndex(){
        return index;
    }

    public void randomize(){
        weight = ((rangeMax-rangeMin)*Math.random()+rangeMin);
    }
    public void setRangeToUpper(){
        rangeMin = (rangeMin + rangeMax)/2f;
    }
    public void setRangeToLower(){
        rangeMax = (rangeMin + rangeMax)/2f;
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
    public double getRange(){
        return rangeMax - rangeMin;
    }

    @Override
    public String toString(){
        return Double.toString(weight);
    }
}
