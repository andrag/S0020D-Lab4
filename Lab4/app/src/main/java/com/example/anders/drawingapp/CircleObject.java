package com.example.anders.drawingapp;

/**
 * Created by Anders on 2016-03-04.
 */
public class CircleObject {

    int color, timesOriginalSize;
    float x, y, radius;
    double area;

    public CircleObject(float x, float y, int color){
        this.x = x;
        this.y = y;
        this.color = color;
        radius = 50;
        timesOriginalSize = 1;
        area = setArea();
    }


    public int getTimesOriginalSize(){
        return timesOriginalSize;
    }

    public void setTimesOriginalSize(int i){
        timesOriginalSize += i;
    }

    private double setArea(){
        return radius * radius * Math.PI;
    }

    public double getArea(){
        return area;
    }
}
