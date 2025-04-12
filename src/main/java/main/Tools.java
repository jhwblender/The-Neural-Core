package main;

import java.util.ArrayList;
import java.util.Arrays;

public class Tools {

    public static double randRange(double min, double max){
        return ((max-min)*Math.random()+min);
    }

    public static <T> void shuffleArray(T[] array){
        ArrayList<T> oldList = new ArrayList<>(Arrays.asList(array));
        for(int i = 0; i < array.length; i++){
            int randIndex = (int)(Math.random() * oldList.size());
            array[i] = (oldList.get(randIndex));
            oldList.remove(randIndex);
        }
    }
}
