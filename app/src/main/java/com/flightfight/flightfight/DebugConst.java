package com.flightfight.flightfight;

import android.graphics.Color;
import android.graphics.Paint;

public class DebugConst {
    public static final boolean DEBUG_DRAW_SPRITE_BOUND_RECT = false;
    public static final Paint boundPaint = new Paint();

    static {
        boundPaint.setColor(Color.argb(64, 0x66, 0xCC, 0xFF));
    }
}
