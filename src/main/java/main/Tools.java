package main;

public class Tools {

    public static float randRange(float min, float max){
        return (float)((max-min)*Math.random()+min);
    }

    public static float getDelta(float[] array){
        float max = array[0];
        float min = array[0];
        for(int i = 1; i < array.length; i++){
            max = Math.max(array[i], max);
            min = Math.min(array[i], min);
        }
        return max-min;
    }

}
