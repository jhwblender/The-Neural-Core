package main;

public class Tools {

    public static double randRange(double min, double max){
        return ((max-min)*Math.random()+min);
    }

    public static double getDelta(double[] array){
        double max = array[0];
        double min = array[0];
        for(int i = 1; i < array.length; i++){
            max = Math.max(array[i], max);
            min = Math.min(array[i], min);
        }
        return max-min;
    }

}
