package com.example.anders.drawingapp;

import android.graphics.Point;
import android.graphics.PointF;

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


    //Line intersection algorithm
    /*  1. Take the two arrays containing the points
        2. Access them synchronized? Must be in onTouch as well I guess
        3. Implement the intersection detection algorithm with the last line added against all others.
            1. currentLine = Last point and next last point
            2. Make a boundingbox of this? Can this be made someplace else?
            3. Algorithm:
                1. For-loop through array 1
                    if lineIntersects(currentLine, array.get(i).x, array.get(i).y){
                        return true;
                    }
                    return false;
     */

    //Unit test this method!
    public static synchronized boolean crossCheck(ArrayList<PointF> currentList, ArrayList<PointF> otherList, boolean lastLine){
        if(currentList.size() > 2 ) {//Need at least one segment stored

            PointF testPoint1;
            PointF testPoint2;

            //Test the latest line or the very last line of the draw shape
            if(!lastLine){
                testPoint1 = currentList.get(currentList.size() - 1);
                testPoint2 = currentList.get(currentList.size() - 2);
            }
            else{
                testPoint1 = currentList.get(currentList.size() - 1);
                testPoint2 = currentList.get(0);//The last point is the same as the first
            }


            int index = 1;
            int stop = currentList.size();

            //Avoid testing the last line against a neighbouring line since they intersect in their endpoints
            if(lastLine){
                index++;
                stop -= 1;
            }

            //Test against lines in the currentList
            while(index < stop) {
                //Fetch the line
                PointF linePoint1 = currentList.get(index-1);
                PointF linePoint2 = currentList.get(index);

                //Avoid neighbouring segments
                if(!testPoint1.equals(linePoint1) && !testPoint1.equals(linePoint2) && !testPoint2.equals(linePoint1) && !testPoint2.equals(linePoint2)){
                    //Make intersection test
                    if(isIntersecting(testPoint1, testPoint2, linePoint1, linePoint2)){
                        return true;
                    }
                }
                index++;
            }

            //Test against lines in the other players list of points
            for (int i = 1; i < otherList.size(); i++) {
                //Fetch the line
                PointF linePoint1 = otherList.get(i-1);
                PointF linePoint2 = otherList.get(i);

                //Avoid neighbouring segments
                if(!testPoint1.equals(linePoint1) && !testPoint1.equals(linePoint2) && !testPoint2.equals(linePoint1) && !testPoint2.equals(linePoint2)){
                    //Make intersection test
                    if(isIntersecting(testPoint1, testPoint2, linePoint1, linePoint2)){
                        return true;
                    }
                }
            }
        }
        return false;
    }





    //Need a unit test for this one!
    private static boolean isIntersecting(PointF a, PointF b, PointF c, PointF d){
        float denominator = (a.x - b.x)*(c.y - d.y) - (a.y - b.y)*(c.x - d.x);
        if(denominator == 0) return false; //The lines are parallel. What if they are on the same place?

        //Calculate point of intersection: http://www.ahristov.com/tutorial/geometry-games/intersection-segments.html
        float point_x = ((c.x - d.x)*(a.x * b.y - a.y * b.x) - (a.x - b.x) * (c.x * d.y - c.y * d.x))/denominator;
        float point_y = ((c.y - d.y)*(a.x * b.y - a.y * b.x) - (a.y - b.y) * (c.x * d.y - c.y * d.x))/denominator;
        PointF point = new PointF(point_x, point_y);

        //Check if any of the lines are vertical
        if(a.x == b.x || c.x == d.x){
            //Check if the intersection point lies outside any of the segment in the y direction
            if(point.y < Math.min(a.y, b.y) || point.y > Math.max(a.y, b.y)) return false;
            if(point.y < Math.min(c.y, d.y) || point.y > Math.max(c.y, d.y)) return false;
        }

        //If no element is vertical, it is enough to check the spanning of the segment in the x direction against the intersection point
        if(point_x < Math.min(a.x, b.x) || point_x > Math.max(a.x, b.x)) return false;
        if(point_x < Math.min(c.x, d.x) || point_x > Math.max(c.x, d.x)) return false;

        return true;
    }


    public static boolean isPointInPolygon(CircleObject object, ArrayList<PointF> list, int screenWidth){
        /* The Algorithm
            1. Get the objects y-coordinate
            2. Construct two horizontal lines that stretches from the object to each side of the screen
            3. Calculate how many times each line intersects with lines contained in the list
            4. If it intersects an odd number of times, the point lies within the drawn shape
            5 (only test edges for intersection if they contain the specific y-coordinate
            6. I believe it should be enough to check only one side of the object.... Try it.
         */
        PointF objectPosition = new PointF(object.x, object.y);
        PointF screenLeft = new PointF(0, object.y);
        PointF screenRight = new PointF(screenWidth, object.y);

        int intersectionsLeft = 0;
        int intersectionsRight = 0;

        for(int i = 0; i < list.size()-1; i++){
            //Check if the objects y-coordinate is within the interval of the line segment that is checked for intersection
            if(object.y >= Math.min(list.get(i).y, list.get(i+1).y) && object.y <= Math.max(list.get(i).y, list.get(i).y)){
                if(isIntersecting(objectPosition, screenLeft, list.get(i), list.get(i+1))) intersectionsLeft++;
                else if(isIntersecting(objectPosition, screenRight, list.get(i), list.get(i+1))) intersectionsRight++;
            }
        }

        if(intersectionsLeft == 0 && intersectionsRight == 0) return false;
        else if(intersectionsLeft % 2 == 0 && intersectionsRight % 2 == 0) return false;
        else return true;
    }
}
