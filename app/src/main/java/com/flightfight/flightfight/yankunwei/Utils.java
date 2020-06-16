package com.flightfight.flightfight.yankunwei;

import android.graphics.RectF;

public class Utils {
    public static double calculate2PointAngleArc(float x1, float y1, float x2, float y2) {
        double horizontalX = x2 + 1;
        double vector = (x1 - x2) * (horizontalX - x2) + (y1 - y2) * (y2 - y2);
        double lengthPointerVector = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
        double lengthHorizontalVector = horizontalX - x2;
        double angle = Math.acos(vector / (lengthPointerVector * lengthHorizontalVector));
        if (y1 < y2) {
            angle = 2 * Math.PI - angle;
        }
//        System.out.println("ANGLE: " + angle * 180 / Math.PI);
        return angle;
    }

    public boolean rectCollide(RectF rect1, RectF rect2) {
        return rect1.intersect(rect2);
    }
}
