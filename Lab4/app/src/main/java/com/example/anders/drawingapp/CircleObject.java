package com.example.anders.drawingapp;

import android.graphics.Color;

/**
 * Created by Anders on 2016-03-04.
 */
public class CircleObject {

    int color, weight, colorDecider;
    float x, y, radius;
    double area;

    public CircleObject(float x, float y, int color){
        this.x = x;
        this.y = y;
        radius = 50;
        weight = 1;
        area = setArea();
        colorDecider = color;
        setColor(color);
    }


    //Method for setting the color from a given random value and setting the type used for comparing colors in the point in polygon algorithm
    private void setColor(int color){
        switch (color){
            case 1:
                this.color = Color.BLUE;
                break;
            case 2:
                this.color = Color.YELLOW;
                break;
            case 3:
                this.color = Color.GREEN;
                break;
            case 4:
                this.color = Color.RED;
                break;
            case 5:
                this.color = Color.BLACK;
                break;
        }
    }


    public int getWeight(){
        return weight;
    }

    public void setWeight(int i){
        weight += i;
    }

    private double setArea(){
        return radius * radius * Math.PI;
    }

    public double getArea(){
        return area;
    }

}
