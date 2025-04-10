package network;

public class Weight {

    float weight;

    Weight(int weight){
        this.weight = weight;
    }
    Weight(int min, int max){
        //Assign random weight between min and max value
        weight = (float)((max-min)*Math.random()+min);
    }

    public float getWeight(){
        return weight;
    }
    public void setWeight(float weight){
        this.weight = weight;
    }
}
