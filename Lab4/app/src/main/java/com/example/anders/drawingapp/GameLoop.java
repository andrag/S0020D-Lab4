package com.example.anders.drawingapp;

import android.annotation.SuppressLint;
import android.graphics.Canvas;

/**
 * Created by Anders on 2016-03-07.
 */
public class GameLoop extends Thread{
    static final long FPS = 25;
    private GameView gameView;
    private boolean running = false;

    public GameLoop(GameView gameView){
        this.gameView = gameView;
    }

    public void setRunning(boolean run){
        running = run;
    }
    @SuppressLint("WrongCall")
    @Override
    public void run() {
        long ticksPS = 1000/FPS;
        long startTime;
        long sleepTime;
        while(running){
            startTime = System.currentTimeMillis();
            Canvas canvas = null;
            try{
                canvas = gameView.getHolder().lockCanvas();//Use GameViews' canvas exclusively from this thread
                synchronized (gameView.getHolder()){
                    gameView.onDraw(canvas);
                }
            } finally {
                if(canvas != null){
                    gameView.getHolder().unlockCanvasAndPost(canvas);
                }
            }
            sleepTime = ticksPS - (System.currentTimeMillis() -startTime);
            try{
                if(sleepTime > 0){
                    sleep(sleepTime);
                }
                else sleep(10);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

    }
}
