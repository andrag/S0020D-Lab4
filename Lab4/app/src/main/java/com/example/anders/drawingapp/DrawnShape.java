package com.example.anders.drawingapp;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

import java.util.ArrayList;

/**
 * Created by Anders on 2016-03-07.
 */
public class DrawnShape {

    private ArrayList<PointF> pointList;
    private int opacity, color;
    private Path drawPath;
    private Paint animPaint;
    public boolean isVisible;

    public DrawnShape(ArrayList<PointF> pointList, int color){
        this.pointList = pointList;
        this.color = color;
        initShape();
    }

    private void initShape(){
        opacity = 255;
        isVisible = true;
        drawPath = new Path();
        animPaint = new Paint();

        animPaint.setColor(color);
        animPaint.setAntiAlias(true);
        animPaint.setStrokeWidth(20);
        animPaint.setStyle(Paint.Style.STROKE);
        animPaint.setStrokeJoin(Paint.Join.ROUND);
        animPaint.setStrokeCap(Paint.Cap.ROUND);
        animPaint.setAlpha(opacity);

        drawPath.moveTo(pointList.get(0).x, pointList.get(0).y);

        //Draw the shape
        for(int i = 1; i < pointList.size(); i++){
            drawPath.lineTo(pointList.get(i).x, pointList.get(i).y);
        }
        drawPath.lineTo(pointList.get(0).x, pointList.get(0).y);
    }

    public void decreaseOpacity(){
        if(opacity > 0){
            opacity = opacity - 5;
            animPaint.setAlpha(opacity);
        }
        else isVisible = false;
    }

    public Path getPath(){
        return drawPath;
    }

    public Paint getPaint(){
        return animPaint;
    }
}
