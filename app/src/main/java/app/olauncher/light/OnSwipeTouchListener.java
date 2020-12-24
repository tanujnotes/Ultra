package app.olauncher.light;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.abs;

public class OnSwipeTouchListener implements View.OnTouchListener {

    private boolean longPressOn = false;
    private final GestureDetector gestureDetector;

    public OnSwipeTouchListener(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) longPressOn = false;
        return gestureDetector.onTouchEvent(motionEvent);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private final int SWIPE_THRESHOLD = 100;
        private final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            return super.onSingleTapUp(motionEvent);
        }

        @Override
        public boolean onDoubleTap(MotionEvent motionEvent) {
            onDoubleClick();
            return super.onDoubleTap(motionEvent);
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {
            longPressOn = true;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (longPressOn) onLongClick();
                }
            }, 500);
            super.onLongPress(motionEvent);
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            try {
                float diffY = event2.getY() - event1.getY();
                float diffX = event2.getX() - event1.getX();
                if (abs(diffX) > abs(diffY)) {
                    if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD)
                        if (diffX > 0) onSwipeRight();
                        else onSwipeLeft();
                } else {
                    if (abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD)
                        if (diffY < 0) onSwipeUp();
                        else onSwipeDown();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public void onSwipeRight() {
    }

    public void onSwipeLeft() {
    }

    public void onSwipeUp() {
    }

    public void onSwipeDown() {
    }

    public void onLongClick() {
    }

    public void onDoubleClick() {
    }

    public void onTripleClick() {
    }

    public void onClick() {
    }

}

//internal open class OnSwipeTouchListener(c: Context?) : OnTouchListener {
//        private var longPressOn = false
//        private var doubleTapOn = false
//        private val gestureDetector: GestureDetector
//
//        override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
//        if (motionEvent.action == MotionEvent.ACTION_UP)
//        longPressOn = false
//        return gestureDetector.onTouchEvent(motionEvent)
//        }
//
//        private inner class GestureListener : SimpleOnGestureListener() {
//        private val SWIPE_THRESHOLD: Int = 100
//        private val SWIPE_VELOCITY_THRESHOLD: Int = 100
//
//        override fun onDown(e: MotionEvent): Boolean {
//        return true
//        }
//
//        override fun onSingleTapUp(e: MotionEvent): Boolean {
//        if (doubleTapOn) {
//        doubleTapOn = false
//        onTripleClick()
//        }
//        return super.onSingleTapUp(e)
//        }
//
//        override fun onDoubleTap(e: MotionEvent): Boolean {
//        doubleTapOn = true
//        Timer().schedule(Constants.TRIPLE_TAP_DELAY_MS.toLong()) {
//        if (doubleTapOn) {
//        doubleTapOn = false
//        onDoubleClick()
//        }
//        }
//        return super.onDoubleTap(e)
//        }
//
//        override fun onLongPress(e: MotionEvent) {
//        longPressOn = true
//        Timer().schedule(Constants.LONG_PRESS_DELAY_MS.toLong()) {
//        if (longPressOn) onLongClick()
//        }
//        super.onLongPress(e)
//        }
//
//        override fun onFling(
//        event1: MotionEvent,
//        event2: MotionEvent,
//        velocityX: Float,
//        velocityY: Float
//        ): Boolean {
//        try {
//        val diffY = event2.y - event1.y
//        val diffX = event2.x - event1.x
//        if (abs(diffX) > abs(diffY)) {
//        if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
//        if (diffX > 0) onSwipeRight() else onSwipeLeft()
//        }
//        } else {
//        if (abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
//        if (diffY < 0) onSwipeUp() else onSwipeDown()
//        }
//        }
//        } catch (exception: Exception) {
//        exception.printStackTrace()
//        }
//        return false
//        }
//        }
//
//        open fun onSwipeRight() {}
//        open fun onSwipeLeft() {}
//        open fun onSwipeUp() {}
//        open fun onSwipeDown() {}
//        open fun onLongClick() {}
//        open fun onDoubleClick() {}
//        open fun onTripleClick() {}
//        private fun onClick() {}
//
//        init {
//        gestureDetector = GestureDetector(c, GestureListener())
//        }
//        }