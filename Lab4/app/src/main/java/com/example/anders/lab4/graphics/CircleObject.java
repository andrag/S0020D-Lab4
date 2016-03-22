package com.example.anders.lab4.graphics;

import android.graphics.Color;
import com.example.anders.lab4.game_engine.GameView;
import java.util.Random;

/**
 * Created by Anders on 2016-03-04.
 */
public class CircleObject {

    public int color, weight, colorDecider, speedX, speedY;
    public long changeInterval;
    public float x, y, radius;
    public double area;
    Random rand;
    GameView gameView;


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


    //Method for setting the color from a given random value
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

    public void update(){
        changeInterval++;

        if(x > gameView.getWidth() - speedX || x + speedX < 0 || changeInterval > 500) {
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
        changeInterval = rand.nextInt(200);
    }

    private void setNewSpeed(){
        speedX = 4 - rand.nextInt(9);
        speedY = 4 - rand.nextInt(9);
        speedX = (int)gameView.dpFromPixel(speedX)/weight;
        speedY = (int)gameView.dpFromPixel(speedY)/weight;
    }

}
