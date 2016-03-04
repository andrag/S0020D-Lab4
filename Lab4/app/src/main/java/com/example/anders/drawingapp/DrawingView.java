package com.example.anders.drawingapp;

import android.graphics.Color;
import android.graphics.Point;
import android.support.v4.view.MotionEventCompat;
import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Bitmap;
import android.view.MotionEvent;
import java.util.ArrayList;


/**
 * Created by Anders on 2016-02-27.
 */
public class DrawingView extends View {

    //Path will be used to trace the drawing action on the canvas
    private Path drawPath1, drawPath2;//The Path class encapsulates compound geometric paths: straight line segments, curves and so on.

    //Paint objects to represent the canvas AND the drawing on top of it
    private Paint canvasPaint, objectPaint, drawPaint1, drawPaint2;//The Paint class holds style and color info on how to draw geometries, texts and bitmaps.

    private int paintColor1 = 0xFF660000;
    private int paintColor2 = 0xFF009600;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;

    private int pointer_1_id, pointer_2_id;
    private final int MAX_FINGERS = 2;
    private ArrayList<Point> pointList1, pointList2;
    private ArrayList<CircleObject> circleObjects;


    //Constructor
    public DrawingView(Context context, AttributeSet attrSet){
        super(context, attrSet);
        setupDrawing();
        setupCircleObjects();
    }

    private void setupDrawing() {
        drawPath1 = new Path();
        drawPath2 = new Path();

        objectPaint = new Paint();
        drawPaint1 = new Paint();
        drawPaint2 = new Paint();

        //Set the initial Paint properties
        objectPaint.setStyle(Paint.Style.FILL);

        drawPaint1.setColor(paintColor1);
        drawPaint1.setAntiAlias(true);
        drawPaint1.setStrokeWidth(20);
        drawPaint1.setStyle(Paint.Style.STROKE);
        drawPaint1.setStrokeJoin(Paint.Join.ROUND);
        drawPaint1.setStrokeCap(Paint.Cap.ROUND);

        drawPaint2.setColor(paintColor2);
        drawPaint2.setAntiAlias(true);
        drawPaint2.setStrokeWidth(20);
        drawPaint2.setStyle(Paint.Style.STROKE);
        drawPaint2.setStrokeJoin(Paint.Join.ROUND);
        drawPaint2.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);//Set the dithering as a parameter for this one. Why? Don't know.

        pointList1 = new ArrayList<Point>();
        pointList2 = new ArrayList<Point>();
    }

    private void setupCircleObjects(){
        circleObjects = new ArrayList<CircleObject>();
        CircleObject anObject = new CircleObject(300, 300, Color.BLUE);
        circleObjects.add(anObject);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);//Each pixel is stored on 4 bytes.
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath1, drawPaint1);
        canvas.drawPath(drawPath2, drawPaint2);
        if(!circleObjects.isEmpty()){
            for(CircleObject object : circleObjects){
                objectPaint.setColor(object.color);
                canvas.drawCircle(object.x, object.y, object.radius, objectPaint);
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointerCount = event.getPointerCount();
        int nmbrOfPointersDown = pointerCount >= MAX_FINGERS ? MAX_FINGERS : 1;//We only want to handle 1 or 2 pointers
        int index = MotionEventCompat.getActionIndex(event);//This pointers index
        int activePointerId = event.getPointerId(index);//This is ONLY the primary pointer?
        int action = MotionEventCompat.getActionMasked(event);//Get the action for this event. This makes it compatible with multi touch: http://developer.android.com/training/gestures/multi.html

        switch (action){

            //ACTION_DOWN is always the first finger down
            case MotionEvent.ACTION_DOWN:

            //ACTION_POINTER_DOWN
            case MotionEvent.ACTION_POINTER_DOWN:
                if(drawPath1.isEmpty()){
                    pointer_1_id = event.getPointerId(index);//This should not be ever changing.
                    drawPath1.moveTo((int)event.getX(index), (int)event.getY(index));
                    Point point = new Point((int)event.getX(index), (int)event.getY(index));
                    pointList1.add(point);
                    break;
                }
                else if(drawPath2.isEmpty()){
                    //activePointerId = event.getPointerId(index);
                    pointer_2_id = event.getPointerId(index);//This should not be ever changing.
                    drawPath2.moveTo((int)event.getX(index), (int)event.getY(index));
                    Point point = new Point((int)event.getX(index), (int)event.getY(index));
                    pointList2.add(point);
                    break;
                }


            //ACTION_MOVE is for any pointer
            case MotionEvent.ACTION_MOVE:
                for(int i = 0; i < nmbrOfPointersDown; i++){
                    if(event.getPointerId(i)==pointer_1_id){
                        //Move operations on the first
                        drawPath1.lineTo((int) event.getX(i), (int) event.getY(i));
                        Point point = new Point((int) event.getX(i), (int) event.getY(i));
                        pointList1.add(point);
                    }
                    else if(event.getPointerId(i)==pointer_2_id){
                        //Move operations on the second
                        drawPath2.lineTo((int)event.getX(i), (int)event.getY(i));
                        Point point = new Point((int) event.getX(i), (int) event.getY(i));
                        pointList2.add(point);
                    }
                }
                break;

            //Need to call the algorithms in a thread-safe manner? Probably not since they are working on their own lists.
            //But maybe to not block graphics?
            //ACTION_POINTER_UP is for non-primary pointers
            case MotionEvent.ACTION_POINTER_UP:
                if(activePointerId == pointer_1_id){
                    drawPath1.lineTo(pointList1.get(0).x, pointList1.get(0).y);
                    drawCanvas.drawPath(drawPath1, drawPaint1);//Draw the Path onto the canvas
                    //Call the intersection algorithm
                    //Call the containing algorithm
                    pointList1.clear();
                    drawPath1.reset();
                    break;
                }
                else if(activePointerId == pointer_2_id){
                    drawPath2.lineTo(pointList2.get(0).x, pointList2.get(0).y);
                    drawCanvas.drawPath(drawPath2, drawPaint2);
                    //Call the intersection algorithm
                    //Call the containing algorithm
                    pointList2.clear();
                    drawPath2.reset();
                    break;
                }


            //ACTION_UP is for the last pointer that leaves the screen
            case MotionEvent.ACTION_UP:
                //Track the last position here and draw a line to the first point.
                index = MotionEventCompat.getActionIndex(event);//This pointers index
                activePointerId = MotionEventCompat.getPointerId(event, index);
                if(activePointerId == pointer_1_id){
                    drawPath1.lineTo(pointList1.get(0).x, pointList1.get(0).y);
                    drawCanvas.drawPath(drawPath1, drawPaint1);//Draw the Path onto the canvas
                    //Call the intersection algorithm
                    //Call the containing algorithm
                    pointList1.clear();
                    drawPath1.reset();
                    break;
                }
                else if(activePointerId == pointer_2_id){
                    drawPath2.lineTo(pointList2.get(0).x, pointList2.get(0).y);
                    drawCanvas.drawPath(drawPath2, drawPaint2);
                    //Call the intersection algorithm
                    //Call the containing algorithm
                    pointList2.clear();
                    drawPath2.reset();
                    break;
                }
            default:
                return false;
        }

        invalidate();
        return true;
    }
}
