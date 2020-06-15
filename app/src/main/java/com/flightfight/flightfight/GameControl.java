package com.flightfight.flightfight;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

public class GameControl {
    private int screenWidth;
    private int screenHeight;
    private float joystickX;
    private float joystickY;
    private float joystickR;
    private int joyPlateX;
    private int joyPlateY;
    private int joyPlateR;
    private int joyTouchX;
    private int joyTouchY;
    private int joyTouchR;
    private int firePlateX;
    private int firePlateY;
    private int firePlateR;
    private int fireTouchX;
    private int fireTouchY;
    private int fireTouchR;
    private boolean noTouched;
    private boolean joyTouched;
    private boolean fireTouched;
    private int currentDir;


    public GameControl(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        InitTouchArea();
    }

    private void InitTouchArea() {
        joyPlateR = screenWidth / 24;
        joyPlateX = 4 * joyPlateR;
        joyPlateY = screenHeight - 4 * joyPlateR;
        joystickR = screenWidth / 48;
        joystickX = joyPlateX;
        joystickY = joyPlateY;
        joyTouchX = joyPlateX;
        joyTouchY = joyPlateY;
        joyTouchR = screenWidth / 8;
        firePlateR = screenWidth / 24;
        firePlateX = screenWidth - 4 * joyPlateR;
        firePlateY = screenHeight - 4 * joyPlateR;
        fireTouchX = firePlateX;
        fireTouchY = firePlateY;
        fireTouchR = screenWidth / 8;
    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        try {
            paint.setColor(0x20EEEEEE);
            canvas.drawCircle(joyTouchX, joyTouchY, joyTouchR, paint);
            paint.setColor(0x25FFFFFF);
            canvas.drawCircle(joyPlateX, joyPlateY, joyPlateR, paint);
            paint.setColor(0x3000FF00);
            canvas.drawCircle(joystickX, joystickY, joystickR, paint);
            paint.setColor(0x20EEEEEE);
            canvas.drawCircle(fireTouchX, fireTouchY, fireTouchR, paint);
            paint.setColor(0x25FFFFFF);
            canvas.drawCircle(firePlateX, firePlateY, firePlateR, paint);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getTouch(MotionEvent event) {
        joyTouched = false;
        fireTouched = false;
        noTouched = false;
        int joyId = 0;
        float x1 = 0, y1 = 0;
        int pointCount = event.getPointerCount();
        if (pointCount == 1) {
            float x = (int) event.getX(0);
            float y = (int) event.getY(0);
            if (checkPointInCircle(x, y, joyTouchX, joyTouchY, joyTouchR)) {
                joyTouched = true;
                x1 = x;
                y1 = y;
                //Log.d("Point One", joy_ touched+"");
            }
            if (checkPointInCircle(x, y, fireTouchX, fireTouchY, fireTouchR)) {
                fireTouched = true;
                //x2 = x;
                //y2 = y;
            }
        }
        if (pointCount > 1) {
            for (int i = 0; i < pointCount; i++) {
                float x = (int) event.getX(i);
                float y = (int) event.getY(i);
                if (checkPointInCircle(x, y, joyTouchX, joyTouchY, joyTouchR)) {
                    joyTouched = true;
                    x1 = x;
                    y1 = y;
                    joyId = event.getPointerId(i);
                }
                if (checkPointInCircle(x, y, fireTouchX, fireTouchY, fireTouchR)) {
                    fireTouched = true;
                }
            }
        }
        //最后一个手指离开
        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            joystickX = joyPlateX;
            joystickY = joyPlateY;
            noTouched = true;
            joyTouched = false;
        }
        //非最后—个手指离开
        else if (event.getActionMasked() == MotionEvent.ACTION_POINTER_UP) {
            int pointerIndex = event.getActionIndex();
            int pointerId = event.getPointerId(pointerIndex); // get pointer ID
//如果离开的是控制摇杆的手指
            if (pointerId == joyId) {
                joystickX = joyPlateX;
                joystickY = joyPlateY;
                noTouched = false;
                joyTouched = false;
            }
        } else {
            if (joyTouched) {//当触屏区域不在活动范围内
                if (Math.sqrt(Math.pow((joyPlateX - x1), 2) +
                        Math.pow((joyPlateY - y1), 2)) >= joyPlateR) {
                    //得到摇杆与触屏点所形成的角度
                    double tempArc = getTwoPointArc(joyPlateX, joyPlateY, x1, y1);
                    //保证内部小圆运动的长度限制
                    getCircularXY(joyPlateX, joyPlateY, joyPlateR, tempArc);
                } else { //如果小球中心点小于活动区域则随若用户触屏点移动即可
                    joystickX = (int) x1;
                    joystickY = (int) y1;
                }
                double radians = Math.atan2(y1 - joystickY, x1 - joystickX);
                double angle = radians * (180 / Math.PI);
                if (angle <= 0) {
                    angle = angle + 360;
                }
                currentDir = getDirection(angle);
            }
        }
    }

    private int getDirection(double angle) {
        if (angle >= 45 && angle < 135) {
            return GameSprite.DOWN;
        } else if (angle >= 135 && angle < 225) {
            return GameSprite.LEFT;
        } else if (angle >= 225 && angle < 315) {
            return GameSprite.UP;
        } else {
            return GameSprite.RIGHT;
        }
    }

    private boolean checkPointInCircle(float x, float y, int boundX, int boundY, int boundRadius) {
        float dx = Math.abs(x - boundX);
        float dy = Math.abs(y - boundY);
        if (dx + dy <= boundRadius) {
            return true;
        } else if (dx > boundRadius) {
            return false;
        } else if (dy > boundRadius) {
            return false;
        } else if (dx * dx + dy * dy <= boundRadius * boundRadius) {
            return true;
        } else {
            return false;
        }
    }

    public double getTwoPointArc(float px1, float py1, float px2, float py2) {
        //得到两点X 的距离
        float x = px2 - px1;
        //得到两点Y 的距离
        float y = py1 - py2;
        //算出斜边长
        float xie = (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        //得到这个角度的余弦值（通过三角函数中的定理：邻边／斜边＝角度余弦值）
        float cosAngle = x / xie;
        //通过反余弦定理获取到其角度的弧度
        float arc = (float) Math.acos(cosAngle);
        //注意：当触屏的位置Y 坐标＜摇杆的Y 坐标我们要取反值－0~-180
        if (py2 < py1) {
            arc = -arc;
        }
        return arc;
    }

    public void getCircularXY(float centerX, float centerY, float radius, double arc) {
//获取圆周运动的X 坐标
        joystickX = (float) (radius * Math.cos(arc)) + centerX;
//获取圆周运动的Y 坐标
        joystickY = (float) (radius * Math.sin(arc)) + centerY;
    }

    public boolean isNoTouched() {
        return noTouched;
    }

    public boolean isJoyTouched() {
        return joyTouched;
    }

    public boolean isFireTouched() {
        return fireTouched;
    }

    public int getCurrentDir() {
        return currentDir;
    }
}
