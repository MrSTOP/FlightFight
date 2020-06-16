package com.flightfight.flightfight;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.flightfight.flightfight.yankunwei.Utils;

public class GameControl {
    private int screenWidth;
    private int screenHeight;
    private int joyPlateX;
    private int joyPlateY;
    private int joyPlateR;
    private int firePlateX;
    private int firePlateY;
    private int firePlateR;
    private int fireTouchX;
    private int fireTouchY;
    private int fireTouchR;
    private boolean noTouched;
    private boolean fireTouched;
    private int currentDir;

    private RectF playerRect;
    private boolean playerTouched;
    private double playerAngleArc;
    private float playerDestinationX;
    private float playerDestinationY;

    public GameControl(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        InitTouchArea();
    }

    private void InitTouchArea() {
        joyPlateR = screenWidth / 24;
        joyPlateX = 4 * joyPlateR;
        joyPlateY = screenHeight - 4 * joyPlateR;
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
            canvas.drawCircle(fireTouchX, fireTouchY, fireTouchR, paint);
            paint.setColor(0x25FFFFFF);
            canvas.drawCircle(firePlateX, firePlateY, firePlateR, paint);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getTouch(MotionEvent event) {
        fireTouched = false;
        noTouched = false;
        int playerTouchID = 0;
        float x = 0;
        float y = 0;
        int pointCount = event.getPointerCount();
        int pointerId = -1;
        ///////////////////////////////////////////////////////
        for (int i = 0; i < pointCount; i++) {
            x = event.getX(i);
            y = event.getY(i);
            pointerId = event.getPointerId(i);
            if (!playerTouched) {
                if (checkPointInPlayerRectF(x, y)) {
                    playerTouchID = pointerId;
                    playerTouched = true;
                    playerDestinationX = x;
                    playerDestinationY = y;
//                    Log.d("PLAYER_MOVE", "DX: " + playerDestinationX + "  DY: " + playerDestinationY);
                }
            } else {
                if (playerTouchID == event.getPointerId(i)) {
                    playerAngleArc = Utils.calculate2PointAngleArc(x, y, playerRect.centerX(), playerRect.centerY());
                    playerDestinationX = x;
                    playerDestinationY = y;
//                    Log.d("PLAYER_MOVE", "DX: " + playerDestinationX + "  DY: " + playerDestinationY);
                }
            }
        }
        ///////////////////////////////////////////////////////
        if (pointCount == 1) {
            x = event.getX(0);
            y = event.getY(0);
            if (checkPointInCircle(x, y, fireTouchX, fireTouchY, fireTouchR)) {
                fireTouched = true;
                //x2 = x;
                //y2 = y;
            }
        } else if (pointCount > 1) {
            for (int i = 0; i < pointCount; i++) {
                x = event.getX(i);
                y = event.getY(i);
                if (checkPointInCircle(x, y, fireTouchX, fireTouchY, fireTouchR)) {
                    fireTouched = true;
                }
            }
        }
        //最后一个手指离开
        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            noTouched = true;
            playerTouched = false;
        } else if (event.getActionMasked() == MotionEvent.ACTION_POINTER_UP) {
            //非最后—个手指离开
            int pointerIndex = event.getActionIndex();
            pointerId = event.getPointerId(pointerIndex); // get pointer ID

            if (pointerId == playerTouchID) {
                playerTouched = false;
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

    private boolean checkPointInPlayerRectF(float x, float y) {
        return this.playerRect.contains(x, y);
    }

    public double getPlayerAngleArc() {
        return playerAngleArc;
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


    public boolean isNoTouched() {
        return noTouched;
    }


    public boolean isFireTouched() {
        return fireTouched;
    }

    public int getCurrentDir() {
        return currentDir;
    }

    public void setPlayerRect(RectF playerRect) {
        this.playerRect = playerRect;
    }

    public boolean isPlayerTouched() {
        return playerTouched;
    }

    public float getPlayerDestinationY() {
        return playerDestinationY;
    }

    public float getPlayerDestinationX() {
        return playerDestinationX;
    }
}
