package com.example.anders.lab4.game_engine;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.SurfaceHolder;
import android.graphics.PointF;
import android.support.v4.view.MotionEventCompat;
import android.view.SurfaceView;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Bitmap;
import android.view.MotionEvent;

import com.example.anders.lab4.data.CalculatingClass;
import com.example.anders.lab4.graphics.CircleObject;
import com.example.anders.lab4.graphics.DrawnShape;
import com.example.anders.lab4.R;
import com.example.anders.lab4.data.ScoreObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Random;


/**
 * Created by Anders on 2016-02-27.
 */

public class GameView extends SurfaceView {

    private static GameLoop gameLoop;
    private SurfaceHolder holder;
    private boolean gameStarted = false;
    private boolean gamePaused = false;
    private long startTime;
    private long currentTime;
    private long finishTime;

    //Path will be used to trace the drawing action on the canvas
    private Path drawPath1, drawPath2;//The Path class encapsulates compound geometric paths: straight line segments, curves and so on.

    //Paint objects to represent the canvas AND the drawing on top of it
    private Paint objectPaint, drawPaint1, drawPaint2;//The Paint class holds style and color info on how to draw geometries, texts and bitmaps.

    private int paintColor1 = 0xFF660000;
    private int paintColor2 = 0xFF009600;

    private final int INVALID_ID = -1;
    private int pointer_1_id = INVALID_ID;
    private int pointer_2_id = INVALID_ID;
    private final int MAX_FINGERS = 2;
    private final int NUMBER_OF_OBJECTS = 10;
    private int merges = 0;
    private ArrayList<ScoreObject> highScore;
    private ArrayList<PointF> pointList1, pointList2;
    private ArrayList<CircleObject> circleObjects;
    private ArrayList<DrawnShape> drawnShapes;//Stores drawn shapes for fade animation
    private ArrayList<Integer> shapesToRemove;
    private ArrayList<CircleObject> capturedObjects1, capturedObjects2;
    private int[] objectsLeft;
    private boolean gameFinished = false;

    private Bitmap splashScreen;
    private GameView gameView;

    private SoundPool soundPool;
    private int mergeSoundId, failSoundId;



    //Constructor
    public GameView(Context context){
        super(context);

        drawnShapes = new ArrayList<DrawnShape>();
        shapesToRemove = new ArrayList<Integer>();
        capturedObjects1 = new ArrayList<CircleObject>();
        capturedObjects2 = new ArrayList<CircleObject>();
        circleObjects = new ArrayList<CircleObject>();
        highScore = new ArrayList<ScoreObject>();
        objectsLeft = new int[]{0,0,0,0,0};
        gameView = this;
        setupDrawing();

        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                setupBitmaps();
                initiateSoundPool();
                setupCircleObjects();
                gameLoop = new GameLoop(gameView);
                gameLoop.setRunning(true);
                gameLoop.start();
                startTime = System.currentTimeMillis();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                boolean retry = true;
                gameLoop.setRunning(false);
                while (retry) {
                    try {
                        gameLoop.join();
                        retry = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void setupDrawing() {
        drawPath1 = new Path();
        drawPath2 = new Path();

        objectPaint = new Paint();
        drawPaint1 = new Paint();
        drawPaint2 = new Paint();

        //Set the initial Paint properties
        objectPaint.setStyle(Paint.Style.FILL);
        objectPaint.setTextSize(dpFromPixel(10f));

        drawPaint1.setColor(paintColor1);
        drawPaint1.setAntiAlias(true);
        drawPaint1.setStrokeWidth(dpFromPixel(10));
        drawPaint1.setStyle(Paint.Style.STROKE);
        drawPaint1.setStrokeJoin(Paint.Join.ROUND);
        drawPaint1.setStrokeCap(Paint.Cap.ROUND);

        drawPaint2.setColor(paintColor2);
        drawPaint2.setAntiAlias(true);
        drawPaint2.setStrokeWidth(dpFromPixel(10));
        drawPaint2.setStyle(Paint.Style.STROKE);
        drawPaint2.setStrokeJoin(Paint.Join.ROUND);
        drawPaint2.setStrokeCap(Paint.Cap.ROUND);

        pointList1 = new ArrayList<PointF>();
        pointList2 = new ArrayList<PointF>();
    }

    private void setupBitmaps(){
        splashScreen = BitmapFactory.decodeResource(getResources(), R.drawable.splashscreen2);
        double ratio = (double)getHeight()/(double)splashScreen.getHeight();
        double newWidth = 0.9 * (double)splashScreen.getWidth() * ratio;
        double newHeight = 0.9 * (double) getHeight();
        splashScreen = Bitmap.createScaledBitmap(splashScreen, (int) newWidth, (int) newHeight, false);

    }

    private void setupCircleObjects() {
        if(circleObjects.isEmpty()){//To not create new CircleObjects when we resume the game from a paused state
            Random rand = new Random();
            float x, y;
            int color;

            for (int i = 0; i < NUMBER_OF_OBJECTS; i++) {
                x = (float) rand.nextInt(getWidth());
                y = (float) rand.nextInt(getHeight());

                color = rand.nextInt(5) + 1;
                CircleObject anObject = new CircleObject(this, x, y, color);
                objectsLeft[color-1] += 1;//Increase number of objects of this color
                circleObjects.add(anObject);
            }
        }
    }

    private void initiateSoundPool(){
        //SoundPool is deprecated in level 21 and above, check compatibility.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            AudioAttributes aa = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)//Don't have to define. Default should be ok.
                    .setUsage(AudioAttributes.USAGE_GAME)//Don't have to define. Default should be ok.
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(5)//Maximum number of simultaneous sound streams.
                    .setAudioAttributes(aa)//Might as well have gone with the default and skip aa
                    .build();

            mergeSoundId = soundPool.load(getContext(), R.raw.merge, 1);
            failSoundId = soundPool.load(getContext(), R.raw.fail, 1);
        }

        else{
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 1);
            mergeSoundId = soundPool.load(getContext(), R.raw.merge, 1);
            failSoundId = soundPool.load(getContext(), R.raw.fail, 1);
        }
    }

    public void playSound(int soundId){
        soundPool.play(soundId,1,1,1,0,1);
    }

    private boolean hasFinished(){
        for(Integer i : objectsLeft){
            if(i != 0){
                if(i != 1){
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if(!gamePaused){
            if (gameStarted && !gameFinished) {
                currentTime = System.currentTimeMillis();
                canvas.drawColor(Color.WHITE);
                if (!circleObjects.isEmpty()) {
                    synchronized (circleObjects){
                        for (CircleObject object : circleObjects) {//Concurrent modification exception at finish!
                            objectPaint.setColor(object.color);
                            object.update();
                            canvas.drawCircle(object.x, object.y, object.radius, objectPaint);
                        }
                    }
                }

                drawShapes(canvas);
                if (!drawPath1.isEmpty()) canvas.drawPath(drawPath1, drawPaint1);
                if (!drawPath2.isEmpty()) canvas.drawPath(drawPath2, drawPaint2);
                if (!drawnShapes.isEmpty()) {
                    fadeAnimation(canvas);
                }
                objectPaint.setColor(Color.BLACK);
                objectPaint.setTextSize(dpFromPixel(20));
                canvas.drawText("Merges: " + merges, getWidth() - dpFromPixel(150), 11 * (getHeight() / 12), objectPaint);
                canvas.drawText("Time: " + (currentTime - startTime) / 1000, getWidth() - dpFromPixel(300), 11 * (getHeight() / 12), objectPaint);

                if(hasFinished()){
                    gameFinished = true;
                    addToHighScore();
                }
            }

            if(!gameStarted && !gameFinished) {
                canvas.drawColor(Color.WHITE);
                canvas.drawBitmap(splashScreen, getWidth()/2 - splashScreen.getWidth()/2, 0, objectPaint);
            }
            if(gameFinished && gameStarted){
                drawHighScore(canvas);
            }
            super.onDraw(canvas);
        }
    }


    private void resetGame(){
        objectsLeft = new int[]{0,0,0,0,0};
        circleObjects.clear();
        drawnShapes.clear();
        pointList1.clear();
        pointList2.clear();
        setupCircleObjects();
        startTime = System.currentTimeMillis();
        merges = 0;
        gameFinished = false;
        gameStarted = false;
    }

    private void addToHighScore(){
        finishTime = (System.currentTimeMillis() - startTime)/1000;
        if(finishTime == 0)finishTime = 1;//Avoid having less CircleObjects than 6 since we have six colors.
        int totalScore = (merges*100)/(int)finishTime;
        currentTime = System.currentTimeMillis();
        String time = getCurrentTime();
        ScoreObject scoreObject = new ScoreObject(totalScore, finishTime, merges, time);
        highScore.add(scoreObject);
        Collections.sort(highScore);
    }

    private void drawHighScore(Canvas canvas){

        canvas.drawColor(Color.WHITE);
        objectPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("High Scores", getWidth() / 2, dpFromPixel(20), objectPaint);
        objectPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Score", getWidth() / 10 - dpFromPixel(25), dpFromPixel(60), objectPaint);
        canvas.drawText("Merges", 2*(getWidth()/10), dpFromPixel(60), objectPaint);
        canvas.drawText("Time (sec)", 4*(getWidth()/10), dpFromPixel(60), objectPaint);
        canvas.drawText("Finished", 7*(getWidth()/10), dpFromPixel(60), objectPaint);
        if(highScore.size() >= 10){
            highScore.remove(0);
        }
        int offset = 20;
        int row = 1;
        objectPaint.setColor(Color.GREEN);
            for(int i = highScore.size()-1; i > -1; i--){
                if(dpFromPixel(70 + row * offset) < getHeight() - getHeight()/5){
                    ScoreObject so = highScore.get(i);
                    canvas.drawText(Integer.toString(so.totalScore), getWidth() / 10 - dpFromPixel(25), dpFromPixel(70 + row * offset), objectPaint);
                    canvas.drawText(Integer.toString(so.score), 2 * getWidth() / 10, dpFromPixel(70 + row * offset), objectPaint);
                    canvas.drawText(Long.toString(so.finishTime), 4*(getWidth()/10), dpFromPixel(70 + row * offset), objectPaint);
                    canvas.drawText(so.time, 7*(getWidth()/10), dpFromPixel(70 + row * offset), objectPaint);
                    row++;
                }
            }
        objectPaint.setColor(Color.BLACK);
        canvas.drawRect(dpFromPixel(10), getHeight() - getHeight() / 5, getWidth() - dpFromPixel(10), getHeight() - dpFromPixel(10), objectPaint);

        objectPaint.setColor(Color.WHITE);
        objectPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Restart game", getWidth() / 2, getHeight() - getHeight() / 10, objectPaint);
        objectPaint.setColor(Color.BLACK);
    }

    private synchronized void drawShapes(Canvas canvas) {
        if (!pointList1.isEmpty()) {
            drawPath1.reset();
            drawPath1.moveTo(pointList1.get(0).x, pointList1.get(0).y);
            for (int i = 1; i < pointList1.size(); i++) {
                drawPath1.lineTo(pointList1.get(i).x, pointList1.get(i).y);
            }
        }

        if (!pointList2.isEmpty()) {
            drawPath2.reset();
            drawPath2.moveTo(pointList2.get(0).x, pointList2.get(0).y);
            for (int i = 1; i < pointList2.size(); i++) {
                drawPath2.lineTo(pointList2.get(i).x, pointList2.get(i).y);
            }
        }
    }


    private void fadeAnimation(Canvas canvas) {
        synchronized (drawnShapes){
            for (DrawnShape shape : drawnShapes) {
                if (shape.isVisible) {
                    canvas.drawPath(shape.getPath(), shape.getPaint());
                    shape.decreaseOpacity();
                    if (!shape.isVisible)
                        shapesToRemove.add(drawnShapes.indexOf(shape));
                }
            }
            if (!shapesToRemove.isEmpty()) {
                for (Integer i : shapesToRemove) {
                    drawnShapes.remove(i);
                }
                shapesToRemove.clear();
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gameStarted && !gameFinished) {
            synchronized (gameLoop) {
                int pointerCount = event.getPointerCount();
                int nmbrOfPointersDown = pointerCount >= MAX_FINGERS ? MAX_FINGERS : 1;//We only want to handle 1 or 2 pointers
                int index = MotionEventCompat.getActionIndex(event);//This pointers index
                int activePointerId = event.getPointerId(index);//This is ONLY the primary pointer?
                int action = MotionEventCompat.getActionMasked(event);//Get the action for this event. This makes it compatible with multi touch: http://developer.android.com/training/gestures/multi.html

                switch (action) {

                    //ACTION_DOWN is always the first finger down
                    case MotionEvent.ACTION_DOWN:

                        //ACTION_POINTER_DOWN
                    case MotionEvent.ACTION_POINTER_DOWN:
                        if (pointList1.isEmpty()) {
                            pointer_1_id = event.getPointerId(index);//This should not be ever changing.
                            PointF point = new PointF(event.getX(index), event.getY(index));
                            pointList1.add(point);
                            break;
                        } else if (pointList2.isEmpty()) {
                            pointer_2_id = event.getPointerId(index);//This should not be ever changing.
                            PointF point = new PointF(event.getX(index), event.getY(index));
                            pointList2.add(point);
                            break;
                        }
                        break;


                    //ACTION_MOVE is for any pointer
                    case MotionEvent.ACTION_MOVE:
                        for (int i = 0; i < nmbrOfPointersDown; i++) {
                            if (event.getPointerId(i) == pointer_1_id) {
                                PointF point = new PointF(event.getX(i), event.getY(i));
                                pointList1.add(point);
                                if (CalculatingClass.crossCheck(pointList1, pointList2, false)) {
                                    clearPath(drawPath1, pointList1, 1);
                                    playSound(failSoundId);
                                    break;
                                }
                            } else if (event.getPointerId(i) == pointer_2_id) {
                                PointF point = new PointF(event.getX(i), event.getY(i));
                                pointList2.add(point);
                                if (CalculatingClass.crossCheck(pointList2, pointList1, false)) {
                                    clearPath(drawPath2, pointList2, 2);
                                    playSound(failSoundId);
                                    break;
                                }
                            }
                        }
                        break;

                    //ACTION_POINTER_UP is for non-primary pointers
                    case MotionEvent.ACTION_POINTER_UP:
                        if (activePointerId == pointer_1_id) {
                            if (!pointList1.isEmpty()) {
                                drawPath1.lineTo(pointList1.get(0).x, pointList1.get(0).y);
                                if (CalculatingClass.crossCheck(pointList1, pointList2, true)) {
                                    clearPath(drawPath1, pointList1, 1);
                                    playSound(failSoundId);
                                    break;
                                }
                                capturedObjects1 = CalculatingClass.checkObjects(circleObjects, pointList1, getWidth());
                                if (capturedObjects1 != null) {
                                    mergeObjects(capturedObjects1);
                                } else {
                                    playSound(failSoundId);
                                }
                                DrawnShape newShape = new DrawnShape(pointList1, paintColor1, this);
                                drawnShapes.add(newShape);
                                clearPath(drawPath1, pointList1, 1);
                            }
                            break;
                        } else if (activePointerId == pointer_2_id) {
                            if (!pointList2.isEmpty()) {
                                drawPath2.lineTo(pointList2.get(0).x, pointList2.get(0).y);
                                if (CalculatingClass.crossCheck(pointList2, pointList1, true)) {
                                    clearPath(drawPath2, pointList2, 2);
                                    playSound(failSoundId);
                                    break;
                                }
                                capturedObjects2 = CalculatingClass.checkObjects(circleObjects, pointList2, getWidth());
                                if (capturedObjects2 != null) {
                                    mergeObjects(capturedObjects2);
                                } else {
                                    playSound(failSoundId);
                                }
                                DrawnShape newShape = new DrawnShape(pointList2, paintColor2, this);
                                drawnShapes.add(newShape);
                                clearPath(drawPath2, pointList2, 2);
                            }
                            break;
                        }
                        break;


                    //ACTION_UP is for the last pointer that leaves the screen
                    case MotionEvent.ACTION_UP:
                        if (activePointerId == pointer_1_id) {
                            if (!pointList1.isEmpty()) {
                                drawPath1.lineTo(pointList1.get(0).x, pointList1.get(0).y);
                                if (CalculatingClass.crossCheck(pointList1, pointList2, true)) {
                                    clearPath(drawPath1, pointList1, 1);
                                    playSound(failSoundId);
                                    break;
                                }
                                capturedObjects1 = CalculatingClass.checkObjects(circleObjects, pointList1, getWidth());
                                if (capturedObjects1 != null) {
                                    mergeObjects(capturedObjects1);
                                } else {
                                    playSound(failSoundId);
                                }
                                DrawnShape newShape = new DrawnShape(pointList1, paintColor1, this);
                                drawnShapes.add(newShape);
                                clearPath(drawPath1, pointList1, 1);
                            }
                            break;
                        } else if (activePointerId == pointer_2_id) {
                            if (!pointList2.isEmpty()) {
                                drawPath2.lineTo(pointList2.get(0).x, pointList2.get(0).y);
                                if (CalculatingClass.crossCheck(pointList2, pointList1, true)) {
                                    clearPath(drawPath2, pointList2, 2);
                                    playSound(failSoundId);
                                    break;
                                }
                                capturedObjects2 = CalculatingClass.checkObjects(circleObjects, pointList2, getWidth());
                                if (capturedObjects2 != null) {
                                    mergeObjects(capturedObjects2);
                                } else {
                                    playSound(failSoundId);
                                }
                                DrawnShape newShape = new DrawnShape(pointList2, paintColor2, this);
                                drawnShapes.add(newShape);
                                clearPath(drawPath2, pointList2, 2);
                            }
                            break;
                        }
                        break;
                    default:
                        return false;
                }
            }
        } else if(!gameFinished){
            int action = MotionEventCompat.getActionMasked(event);
            switch (action){
                case MotionEvent.ACTION_DOWN:
                    gameStarted = true;
            }
        } else if (gameStarted && gameFinished){
            int action = MotionEventCompat.getActionMasked(event);
            switch (action){
                case MotionEvent.ACTION_DOWN:
                    if(event.getX() < getWidth() - dpFromPixel(10) && event.getX() > dpFromPixel(10) && event.getY() < getHeight() -dpFromPixel(10) && event.getY() > getHeight() - getHeight()/5){
                        resetGame();
                    }
            }
        }

        return true;
    }



    //This method clears a drawn shape from the UI
    private void clearPath(Path drawnPath, ArrayList<PointF> pointList, int pointerId){
        drawnPath.reset();
        pointList.clear();

        //Since parameters are passed by value we need to modify the original global id and cannot change it by passing it to this method as an argument
        switch (pointerId){
            case 1:
                pointer_1_id = INVALID_ID;
                break;
            case 2:
                pointer_2_id = INVALID_ID;
        }
    }


    private void mergeObjects(ArrayList<CircleObject> involvedObjects){
        System.out.println("Trying to merge");
        if(involvedObjects.size() > 1){
            Object[] newValues = CalculatingClass.newObjectValues(involvedObjects);
            int color = involvedObjects.get(0).colorDecider;//involvedObjects.get(0).color;

            float newRadius = (float)newValues[0];
            float newX = (float)newValues[1];
            float newY = (float)newValues[2];
            int newSpeedX = (int) newValues[3];
            int newSpeedY = (int) newValues[4];
            int newWeight = (int) newValues[5];

            CircleObject newObject = new CircleObject(this, newX, newY, color);
            newObject.radius = newRadius;
            newObject.weight = newWeight;
            newObject.setSpeedX(newSpeedX);
            newObject.setSpeedY(newSpeedY);
            newObject.setArea();

            objectsLeft[color-1] = objectsLeft[color-1] - involvedObjects.size() + 1;


            //Set speed of new object! Get it from two new positions in newValues!

            circleObjects.add(newObject);

            synchronized (circleObjects){
                for(CircleObject object : involvedObjects){
                    circleObjects.remove(object);
                }
            }

            merges += involvedObjects.size();

            playSound(mergeSoundId);
        }
    }

    public void pauseGame(){
        //gameStarted = false;
        gamePaused = true;
        //gameLoop.setRunning(false);
    }

    public void resumeGame(){
        //gameStarted = true;
        gamePaused = false;
        //gameLoop.setRunning(true);
    }

    public float dpFromPixel(float pixelValue){
        final float scale = getResources().getDisplayMetrics().density;
        return pixelValue * scale + 0.5f;
    }


    private String getCurrentTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date now = new Date();
        String dateString = sdf.format(now);
        return dateString;
    }

}
