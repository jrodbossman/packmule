package com.jared.packmule;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

class JoyStick extends AppCompatActivity {
    static final int STICK_NONE = 0;
    static final int STICK_UP = 1;
    static final int STICK_UPRIGHT = 2;
    static final int STICK_RIGHT = 3;
    static final int STICK_DOWNRIGHT = 4;
    static final int STICK_DOWN = 5;
    static final int STICK_DOWNLEFT = 6;
    static final int STICK_LEFT = 7;
    static final int STICK_UPLEFT = 8;

    private int OFFSET = 0;

    private final ViewGroup mLayout;
    private int stick_width, stick_height;

    int position_x = 0, position_y = 0, min_distance = 0;
    private float distance = 0, angle = 0;

    private final DrawCanvas draw;
    private final Paint paint;
    private Bitmap stick;

    private boolean touch_state = false;
    int STICK_ALPHA = 200;
    int LAYOUT_ALPHA = 200;
    float width;

    JoyStick(Context context, ViewGroup layout) {
        stick = BitmapFactory.decodeResource(context.getResources(), R.drawable.image_button);

        stick_width =  stick.getWidth();
        stick_height = stick.getHeight();

        draw = new DrawCanvas(context);
        paint = new Paint();
        mLayout = layout;
    }

    void drawStick(MotionEvent arg1) {
        width = mLayout.getMeasuredWidth();
        position_x = (int) (arg1.getX() - (width / 2));
        position_y = (int) (arg1.getY() - (width / 2));
        distance = (float) Math.sqrt(Math.pow(position_x, 2) + Math.pow(position_y, 2));
        angle = (float) cal_angle(position_x, position_y);


        if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
            if (distance <= (width / 2) - OFFSET) {
                draw.position(arg1.getX(), arg1.getY());
                draw();
                touch_state = true;
            }
        } else if (arg1.getAction() == MotionEvent.ACTION_MOVE && touch_state) {
            if (distance <= (width / 2) - OFFSET) {
                draw.position(arg1.getX(), arg1.getY());
                draw();
            } else if (distance > (width / 2) - OFFSET) {
                float x = (float) (Math.cos(Math.toRadians(cal_angle(position_x, position_y)))
                        * ((width / 2) - OFFSET));
                float y = (float) (Math.sin(Math.toRadians(cal_angle(position_x, position_y)))
                        * ((width / 2) - OFFSET));
                x += (width / 2);
                y += (width / 2);
                draw.position(x, y);
                draw();
            } else {
                mLayout.removeView(draw);
            }
        } else if (arg1.getAction() == MotionEvent.ACTION_UP) {
            mLayout.removeView(draw);
            touch_state = false;
        }
    }
    public int getY() {
        if (distance > min_distance && touch_state) {
            return position_y;
        }
        return 0;
    }

    public float getAngle() {
        if (distance > min_distance && touch_state) {
            return angle;
        }
        return 0;
    }

    public float getDistance() {
        width = mLayout.getMeasuredWidth();
        if (distance > min_distance && touch_state) {
            if (distance > width / 2)
                return width / 2;
            return distance;
        }
        return 0;
    }

    public float getWidth() {
        return width;
    }
    void setMinimumDistance() {
        min_distance = 10;
    }

    int get8Direction() {
        if (distance > min_distance && touch_state) {
            if (angle >= 247.5 && angle < 292.5) {
                return STICK_UP;
            } else if (angle >= 292.5 && angle < 337.5) {
                return STICK_UPRIGHT;
            } else if (angle >= 337.5 || angle < 22.5) {
                return STICK_RIGHT;
            } else if (angle >= 22.5 && angle < 67.5) {
                return STICK_DOWNRIGHT;
            } else if (angle >= 67.5 && angle < 112.5) {
                return STICK_DOWN;
            } else if (angle >= 112.5 && angle < 157.5) {
                return STICK_DOWNLEFT;
            } else if (angle >= 157.5 && angle < 202.5) {
                return STICK_LEFT;
            } else if (angle >= 202.5 && angle < 247.5) {
                return STICK_UPLEFT;
            }
        } else if (distance <= min_distance && touch_state) {
            return STICK_NONE;
        }
        return 0;
    }

    void setOffset(float density) {
        OFFSET = (int) (30 * density);
    }

    void setStickAlpha() {
        STICK_ALPHA = 100;
        paint.setAlpha(100);
    }

    void setLayoutAlpha() {
        LAYOUT_ALPHA = 150;
        mLayout.getBackground().setAlpha(150);
    }

    void setStickSize(float density) {
        stick = Bitmap.createScaledBitmap(stick, (int) (50*density), (int) (50*density), false);
        stick_width = stick.getWidth();
        stick_height = stick.getHeight();
    }

    private double cal_angle(float x, float y) {
        if (x >= 0 && y >= 0)
            return Math.toDegrees(Math.atan(y / x));
        else if (x < 0 && y >= 0)
            return Math.toDegrees(Math.atan(y / x)) + 180;
        else if (x < 0 && y < 0)
            return Math.toDegrees(Math.atan(y / x)) + 180;
        else if (x >= 0 && y < 0)
            return Math.toDegrees(Math.atan(y / x)) + 360;
        return 0;
    }

    private void draw() {
        try {
            mLayout.removeView(draw);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mLayout.addView(draw);
    }

    private class DrawCanvas extends View {
        float x, y;

        private DrawCanvas(Context mContext) {
            super(mContext);
        }

        public void onDraw(Canvas canvas) {
            canvas.drawBitmap(stick, x, y, paint);
        }

        private void position(float pos_x, float pos_y) {
            x = pos_x - (stick_width / 2);
            y = pos_y - (stick_height / 2);
        }
    }
}