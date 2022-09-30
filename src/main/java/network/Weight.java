package network;

public class Weight {

    final float momentumFactor = 0.2f;
    final float frictionFactor = 0.9f;
    final float minValue = -6;
    final float maxValue = 6;
    float weight;
    float wiggleWeight;
    float slope;

    //Experimental
    float momentum = 0;

    Weight(){
        //Assign random weight between min and max value
        weight = (float)((maxValue-minValue)*Math.random()+minValue);
    }

    public float getWeight(){
        return weight;
    }

    public void setWiggleWeight(float wiggleDiff){
        wiggleWeight = weight;
        weight -= wiggleDiff;
    }
    public void wiggleReset(){
        weight = wiggleWeight;
    }
    public void setSlope(float slope){
        this.slope = slope;
    }
    public void descend(float multiplier, float variationFactor){
        momentum += multiplier * slope;
        momentum *= frictionFactor;
//        float newValue = weight + multiplier * slope * ((float)(2 * Math.random()-1) * (1+variationFactor)) + (momentumFactor * momentum);// * 2*(float)Math.random(); //attempt at variation
        float newValue = weight + multiplier * slope * getVariation(variationFactor);
        if(newValue >= minValue && newValue <= maxValue){
            weight = newValue;
        }
    }

    private float getVariation(float variationFactor){
        float factor = 1 + (2 * (float)Math.random() - 1) * variationFactor;
        return factor;
    }
}
