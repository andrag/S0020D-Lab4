package com.example.anders.drawingapp;

import android.graphics.Color;

/**
 * Created by Anders on 2016-03-04.
 */
public class CircleObject {

    int color, timesOriginalSize;
    float x, y, radius;
    double area;
    ObjectType type;

    public enum ObjectType {
        BLUE, YELLOW, GREEN, RED, BLACK
    }

    public CircleObject(float x, float y, int color){
        this.x = x;
        this.y = y;
        radius = 50;
        timesOriginalSize = 1;
        area = setArea();
        setColor(color);
    }


    //Method for setting the color from a given random value and setting the type used for comparing colors in the point in polygon algorithm
    private void setColor(int color){
        switch (color){
            case 1:
                this.color = Color.BLUE;
                type = ObjectType.BLUE;
                break;
            case 2:
                this.color = Color.YELLOW;
                type = ObjectType.YELLOW;
                break;
            case 3:
                this.color = Color.GREEN;
                type = ObjectType.GREEN;
                break;
            case 4:
                this.color = Color.RED;
                type = ObjectType.RED;
                break;
            case 5:
                this.color = Color.BLACK;
                type = ObjectType.BLACK;
                break;
        }
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
