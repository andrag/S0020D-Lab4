package com.example.anders.lab4;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.Random;

/**
 * Created by Anders on 2016-03-04.
 */
public class CircleObject {

    int color, weight, colorDecider, speedX, speedY;
    long changeInterval;
    float x, y, radius;
    double area;
    Random rand;
    GameView gameView;
    Bitmap bitmap;

    public CircleObject(GameView view, float x, float y, int color){
        this.gameView = view;
        this.x = x;
        this.y = y;
        rand = new Random();
        speedX = 0;
        speedY = 0;
        radius = view.dpFromPixel(20);
        weight = 1;
        colorDecider = color;
        setArea();
        setColor(color);
        setNewSpeed();
        setChangeInterval();
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

    public void update(int height, int width){
        changeInterval++;

        if(x > gameView.getWidth() - speedX || x + speedX < 0 || changeInterval > 500) {
            //x = gameView.getWidth() - 2*radius;
            speedX = -speedX;
        }
        x = x + speedX;

        if(y > gameView.getHeight() - speedY || y + speedY < 0 || changeInterval > 500) {
            speedY = -speedY;
        }
        if(changeInterval > 200){
            setChangeInterval();
            setNewSpeed();
        }

        y = y +speedY;
    }


    public int getWeight(){
        return weight;
    }

    public void setArea(){
        area = radius * radius * Math.PI;
    }

    public double getArea(){
        return area;
    }

    public void setSpeedX(int s){
        speedX = s;
    }

    public void setSpeedY(int s){
        speedY = s;
    }

    private void setChangeInterval(){
        //Random rand = new Random();
        changeInterval = rand.nextInt(200);
    }

    private void setNewSpeed(){
        //Random rand = new Random();
        speedX = 4 - rand.nextInt(9);
        speedY = 4 - rand.nextInt(9);
        speedX = (int)gameView.dpFromPixel(speedX)/weight;
        speedY = (int)gameView.dpFromPixel(speedY)/weight;
    }

}
