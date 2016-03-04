package com.example.anders.drawingapp;

import java.util.ArrayList;

/**
 * Created by Anders on 2016-03-04.
 */
public class CalculatingClass {


    //Calculates a new radius based on the sizes of the CircleObjects that are to be mashed together
    public static float newObjectRadius(int multiplier, ArrayList<CircleObject> involvedObjects){
        double area = 0;
        for(CircleObject object : involvedObjects){
            area += object.getArea();
        }
        double newRadius = Math.sqrt(area/Math.PI);
        return (float) newRadius;
    }
}
