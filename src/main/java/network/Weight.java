package network;

public class Weight {

    double weight;

    Weight(int weight){
        this.weight = weight;
    }
    Weight(int min, int max){
        //Assign random weight between min and max value
        weight = (double)((max-min)*Math.random()+min);
    }

    public double getWeight(){
        return weight;
    }
    public void setWeight(double weight){
        this.weight = weight;
    }

    @Override
    public String toString(){
        return Double.toString(weight);
    }
}
